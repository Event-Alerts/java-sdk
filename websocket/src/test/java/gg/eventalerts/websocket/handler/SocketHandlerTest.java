package gg.eventalerts.websocket.handler;

import com.google.gson.JsonObject;
import gg.eventalerts.sdk.json.GSONProvider;
import gg.eventalerts.sdk.object.EAEvent;
import gg.eventalerts.websocket.support.JsonRoundTripSupport;
import gg.eventalerts.sdk.websocket.handler.EventPostedHandler;
import gg.eventalerts.sdk.websocket.SocketEventName;
import gg.eventalerts.sdk.websocket.message.event.SocketEvent;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


class SocketHandlerTest {
    @Test
    void onMessageParsesTheEntireEnvelope() {
        final CapturingHandler handler = new CapturingHandler();
        final EAEvent payload = EAEvent.getExample();

        final SocketEvent<EAEvent> envelope = new SocketEvent<>();
        envelope.event = SocketEventName.EVENT_POSTED;
        envelope.sequence = 9;
        envelope.timestamp = new Date(1_700_000_000_123L);
        envelope.data = payload;

        final JsonObject json = GSONProvider.GSON.toJsonTree(
                envelope,
                JsonRoundTripSupport.typeOf(SocketEvent.class, EAEvent.class)).getAsJsonObject();

        handler.onMessage(json);

        final SocketEvent<EAEvent> parsed = handler.captured.get();
        assertNotNull(parsed);
        assertEquals(SocketEventName.EVENT_POSTED, parsed.event);
        assertEquals(Integer.valueOf(9), parsed.sequence);
        assertEquals(envelope.timestamp, parsed.timestamp);
        assertNotNull(parsed.data);
        assertEquals(payload.id, parsed.data.id);
        assertEquals(payload.title, parsed.data.title);
    }

    private static final class CapturingHandler extends EventPostedHandler {
        private final AtomicReference<SocketEvent<EAEvent>> captured = new AtomicReference<>();

        @Override
        public void onMessage(@NotNull SocketEvent<EAEvent> object) {
            captured.set(object);
        }
    }
}
