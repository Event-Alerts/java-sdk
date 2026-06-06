package gg.eventalerts.sdk.json;

import gg.eventalerts.sdk.object.EACrossBan;
import gg.eventalerts.sdk.object.EAEvent;
import gg.eventalerts.sdk.object.EAEventThreadMessage;
import gg.eventalerts.sdk.object.EAFamousEvent;
import gg.eventalerts.sdk.object.EAPlayer;
import gg.eventalerts.sdk.object.EAServerApplication;
import gg.eventalerts.sdk.support.JsonRoundTripSupport;
import gg.eventalerts.sdk.websocket.SocketActionName;
import gg.eventalerts.sdk.websocket.SocketEventName;
import gg.eventalerts.sdk.websocket.message.action.EAPlayerConnectionAction;
import gg.eventalerts.sdk.websocket.message.action.SocketAction;
import gg.eventalerts.sdk.websocket.message.event.EACrossBanEvent;
import gg.eventalerts.sdk.websocket.message.event.EALinkEvent;
import gg.eventalerts.sdk.websocket.message.event.SocketEvent;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Date;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


class AdditionalModelRoundTripTest {
    @Test
    void crossBanRoundTripPreservesFields() {
        final EACrossBan original = EACrossBan.getExample();
        final EACrossBan parsed = JsonRoundTripSupport.roundTrip(original, EACrossBan.class);

        assertNotNull(parsed);
        assertEquals(original.id, parsed.id);
        assertEquals(original.discordId, parsed.discordId);
        assertEquals(original.minecraftUuid, parsed.minecraftUuid);
        assertEquals(original.reason, parsed.reason);
        assertEquals(original.expiration, parsed.expiration);
        assertEquals(original.created, parsed.created);
    }

    @Test
    void famousEventRoundTripPreservesFields() {
        final EAFamousEvent original = EAFamousEvent.getExample();
        final EAFamousEvent parsed = JsonRoundTripSupport.roundTrip(original, EAFamousEvent.class);

        assertNotNull(parsed);
        assertEquals(original.type, parsed.type);
        assertEquals(original.channel, parsed.channel);
        assertEquals(original.message, parsed.message);
        assertEquals(original.user, parsed.user);
    }

    @Test
    void serverApplicationRoundTripPreservesNestedData() {
        final EAServerApplication original = EAServerApplication.getExample();
        final EAServerApplication parsed = JsonRoundTripSupport.roundTrip(original, EAServerApplication.class);

        assertNotNull(parsed);
        assertEquals(original.id, parsed.id);
        assertEquals(original.applicant, parsed.applicant);
        assertEquals(original.channel, parsed.channel);
        assertEquals(original.created, parsed.created);
        assertEquals(original.message, parsed.message);
        assertEquals(original.approvedBy, parsed.approvedBy);
        assertNotNull(parsed.data);
        assertNotNull(original.data);
        assertEquals(original.data.name, parsed.data.name);
        assertEquals(original.data.description, parsed.data.description);
        assertEquals(original.data.tags, parsed.data.tags);
        assertEquals(original.data.color, parsed.data.color);
    }

    @Test
    void eventThreadMessageRoundTripPreservesDeeplyNestedObjects() {
        final EAEvent event = EAEvent.getExample();
        final EAEventThreadMessage original = new EAEventThreadMessage(
                event,
                new EAEventThreadMessage.Channel(123, "event-thread"),
                new EAEventThreadMessage.Author(
                        456,
                        "tester",
                        "Tester",
                        EAPlayer.getExample()),
                new EAEventThreadMessage.Message(
                        789,
                        new EAEventThreadMessage.Message.Content("raw", "display", "stripped"),
                        Collections.singletonList(new EAEventThreadMessage.Message.Attachment(
                                321,
                                "file.png",
                                "https://example.invalid/file.png",
                                "https://proxy.invalid/file.png"))));

        final EAEventThreadMessage parsed = JsonRoundTripSupport.roundTrip(original, EAEventThreadMessage.class);

        assertNotNull(parsed);
        assertNotNull(parsed.event);
        assertEquals(event.id, parsed.event.id);
        assertEquals(event.title, parsed.event.title);
        assertNotNull(parsed.channel);
        assertNotNull(original.channel);
        assertEquals(original.channel.id, parsed.channel.id);
        assertEquals(original.channel.name, parsed.channel.name);
        assertNotNull(parsed.author);
        assertNotNull(original.author);
        assertEquals(original.author.id, parsed.author.id);
        assertEquals(original.author.name, parsed.author.name);
        assertEquals(original.author.effectiveName, parsed.author.effectiveName);
        assertNotNull(parsed.author.player);
        assertNotNull(original.author.player);
        assertEquals(original.author.player.id, parsed.author.player.id);
        assertNotNull(parsed.message);
        assertNotNull(original.message);
        assertEquals(original.message.id, parsed.message.id);
        assertNotNull(parsed.message.content);
        assertNotNull(original.message.content);
        assertEquals(original.message.content.raw, parsed.message.content.raw);
        assertEquals(original.message.content.display, parsed.message.content.display);
        assertEquals(original.message.content.stripped, parsed.message.content.stripped);
        assertNotNull(original.message.attachments);
        assertNotNull(parsed.message.attachments);
        assertEquals(original.message.attachments.size(), parsed.message.attachments.size());
        assertEquals(original.message.attachments.get(0).name, parsed.message.attachments.get(0).name);
    }

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
