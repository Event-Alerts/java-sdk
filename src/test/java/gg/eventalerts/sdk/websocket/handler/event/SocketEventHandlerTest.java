package gg.eventalerts.sdk.websocket.handler.event;

import com.google.gson.JsonObject;
import gg.eventalerts.sdk.json.GSONProvider;
import gg.eventalerts.sdk.object.Event;
import gg.eventalerts.sdk.support.JsonRoundTripSupport;
import gg.eventalerts.sdk.websocket.object.event.SocketEvent;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


class SocketEventHandlerTest {
    @Test
    void onMessageParsesTheEntireEnvelope() {
        final CapturingHandler handler = new CapturingHandler();
        final Event payload = Event.getExample();

        final SocketEvent<Event> envelope = new SocketEvent<>();
        envelope.event = SocketEventName.EVENT_POSTED;
        envelope.sequence = 9;
        envelope.timestamp = new Date(1_700_000_000_123L);
        envelope.data = payload;

        final JsonObject json = GSONProvider.GSON.toJsonTree(
                envelope,
                JsonRoundTripSupport.typeOf(SocketEvent.class, Event.class)).getAsJsonObject();

        handler.onMessage(json);

        final SocketEvent<Event> parsed = handler.captured.get();
        assertNotNull(parsed);
        assertEquals(SocketEventName.EVENT_POSTED, parsed.event);
        assertEquals(Integer.valueOf(9), parsed.sequence);
        assertEquals(envelope.timestamp, parsed.timestamp);
        assertNotNull(parsed.data);
        assertEquals(payload.id, parsed.data.id);
        assertEquals(payload.title, parsed.data.title);
    }

    private static final class CapturingHandler extends EventPostedEventHandler {
        private final AtomicReference<SocketEvent<Event>> captured = new AtomicReference<>();

        @Override
        public void onMessage(@NonNull SocketEvent<Event> object) {
            captured.set(object);
        }
    }
}
