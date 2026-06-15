package gg.eventalerts.websocket.json;

import gg.eventalerts.sdk.object.EAEvent;
import gg.eventalerts.sdk.websocket.SocketActionName;
import gg.eventalerts.sdk.websocket.SocketEventName;
import gg.eventalerts.sdk.websocket.message.action.EAUpdateSubscriptionAction;
import gg.eventalerts.sdk.websocket.message.action.SocketAction;
import gg.eventalerts.sdk.websocket.message.event.SocketEvent;
import gg.eventalerts.websocket.support.JsonRoundTripSupport;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


class GsonRoundTripTest {
    @Test
    void socketEventRoundTripPreservesEnvelopeAndPayload() {
        final SocketEvent<EAEvent> original = new SocketEvent<>();
        original.event = SocketEventName.EVENT_POSTED;
        original.sequence = 5L;
        original.timestamp = new Date(1_700_000_000_123L);
        original.data = EAEvent.getExample();

        final Type type = JsonRoundTripSupport.typeOf(SocketEvent.class, EAEvent.class);
        final SocketEvent<EAEvent> parsed = JsonRoundTripSupport.roundTrip(original, type);

        assertNotNull(parsed);
        assertEquals(original.event, parsed.event);
        assertEquals(original.sequence, parsed.sequence);
        assertEquals(original.timestamp, parsed.timestamp);
        assertNotNull(parsed.data);
        assertEquals(original.data.id, parsed.data.id);
        assertEquals(original.data.title, parsed.data.title);
    }

    @Test
    void socketActionRoundTripPreservesActionAndPayload() {
        final SocketAction<EAUpdateSubscriptionAction> original = new SocketAction<>(
                SocketActionName.UPDATE_SUBSCRIPTION,
                new EAUpdateSubscriptionAction(
                        new HashSet<>(Arrays.asList(SocketEventName.EVENT_POSTED, SocketEventName.LINK)),
                        Collections.singleton(SocketEventName.EVENT_CANCELLED)));

        final Type type = JsonRoundTripSupport.typeOf(SocketAction.class, EAUpdateSubscriptionAction.class);
        final SocketAction<EAUpdateSubscriptionAction> parsed = JsonRoundTripSupport.roundTrip(original, type);

        assertNotNull(parsed);
        assertEquals(original.action, parsed.action);
        assertNotNull(parsed.data);
        assertNotNull(original.data);
        assertEquals(original.data.getSubscribe(), parsed.data.getSubscribe());
        assertEquals(original.data.getUnsubscribe(), parsed.data.getUnsubscribe());
    }
}
