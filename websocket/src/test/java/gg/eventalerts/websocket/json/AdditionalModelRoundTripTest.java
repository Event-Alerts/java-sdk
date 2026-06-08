package gg.eventalerts.websocket.json;

import gg.eventalerts.sdk.object.EACrossBan;
import gg.eventalerts.sdk.object.EAPlayer;
import gg.eventalerts.websocket.support.JsonRoundTripSupport;
import gg.eventalerts.sdk.websocket.SocketActionName;
import gg.eventalerts.sdk.websocket.SocketEventName;
import gg.eventalerts.sdk.websocket.message.action.EAPlayerConnectionAction;
import gg.eventalerts.sdk.websocket.message.action.SocketAction;
import gg.eventalerts.sdk.websocket.message.event.EACrossBanEvent;
import gg.eventalerts.sdk.websocket.message.event.EALinkEvent;
import gg.eventalerts.sdk.websocket.message.event.SocketEvent;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


class AdditionalModelRoundTripTest {
    @Test
    void linkAndCrossBanEventRoundTripPreserveWrapperFields() {
        final EALinkEvent linkEvent = JsonRoundTripSupport.roundTrip(EALinkEvent.getExample(), EALinkEvent.class);
        final EACrossBanEvent crossBanEvent = JsonRoundTripSupport.roundTrip(EACrossBanEvent.getExample(), EACrossBanEvent.class);

        assertNotNull(linkEvent);
        assertEquals(EALinkEvent.LinkStatus.ADDED, linkEvent.linkStatus);
        assertNotNull(linkEvent.discord);
        assertNotNull(EAPlayer.getExample().discord);
        assertEquals(EAPlayer.getExample().discord.id, linkEvent.discord.id);

        assertNotNull(crossBanEvent);
        assertEquals(EACrossBanEvent.Status.ADDED, crossBanEvent.status);
        assertEquals(EACrossBan.getExample().reason, crossBanEvent.reason);
    }

    @Test
    void socketEnvelopeAndActionRoundTripWithSubtypePayloads() {
        final EAPlayer player = EAPlayer.getExample();

        final SocketEvent<EAPlayer> eventEnvelope = new SocketEvent<>(
                SocketEventName.LINK,
                7,
                player);
        final SocketAction<EAPlayerConnectionAction> actionEnvelope = new SocketAction<>(
                SocketActionName.PLAYER_CONNECTION,
                new EAPlayerConnectionAction(
                        UUID.fromString("aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee"),
                        "tester",
                        new Date(1_700_000_000_123L),
                        EAPlayerConnectionAction.Type.JOIN));

        final Type eventType = JsonRoundTripSupport.typeOf(SocketEvent.class, EAPlayer.class);
        final Type actionType = JsonRoundTripSupport.typeOf(SocketAction.class, EAPlayerConnectionAction.class);

        final SocketEvent<EAPlayer> parsedEvent = JsonRoundTripSupport.roundTrip(eventEnvelope, eventType);
        final SocketAction<EAPlayerConnectionAction> parsedAction = JsonRoundTripSupport.roundTrip(actionEnvelope, actionType);

        assertNotNull(parsedEvent);
        assertEquals(eventEnvelope.event, parsedEvent.event);
        assertEquals(eventEnvelope.sequence, parsedEvent.sequence);
        assertEquals(eventEnvelope.timestamp, parsedEvent.timestamp);
        assertNotNull(parsedEvent.data);
        assertEquals(player.id, parsedEvent.data.id);

        assertNotNull(parsedAction);
        assertEquals(actionEnvelope.action, parsedAction.action);
        assertNotNull(parsedAction.data);
        assertNotNull(actionEnvelope.data);
        assertEquals(actionEnvelope.data.uuid, parsedAction.data.uuid);
        assertEquals(actionEnvelope.data.username, parsedAction.data.username);
        assertEquals(actionEnvelope.data.type, parsedAction.data.type);
    }
}
