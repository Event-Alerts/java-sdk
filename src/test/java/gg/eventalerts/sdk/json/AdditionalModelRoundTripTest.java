package gg.eventalerts.sdk.json;

import gg.eventalerts.sdk.object.CrossBan;
import gg.eventalerts.sdk.object.Event;
import gg.eventalerts.sdk.object.EventThreadMessage;
import gg.eventalerts.sdk.object.FamousEvent;
import gg.eventalerts.sdk.object.Player;
import gg.eventalerts.sdk.object.ServerApplication;
import gg.eventalerts.sdk.support.JsonRoundTripSupport;
import gg.eventalerts.sdk.websocket.SocketActionName;
import gg.eventalerts.sdk.websocket.SocketEventName;
import gg.eventalerts.sdk.websocket.message.action.PlayerConnectionAction;
import gg.eventalerts.sdk.websocket.message.action.SocketAction;
import gg.eventalerts.sdk.websocket.message.event.CrossBanEvent;
import gg.eventalerts.sdk.websocket.message.event.LinkEvent;
import gg.eventalerts.sdk.websocket.message.event.SocketEvent;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


class AdditionalModelRoundTripTest {
    @Test
    void crossBanRoundTripPreservesFields() {
        final CrossBan original = CrossBan.getExample();
        final CrossBan parsed = JsonRoundTripSupport.roundTrip(original, CrossBan.class);

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
        final FamousEvent original = FamousEvent.getExample();
        final FamousEvent parsed = JsonRoundTripSupport.roundTrip(original, FamousEvent.class);

        assertNotNull(parsed);
        assertEquals(original.type, parsed.type);
        assertEquals(original.channel, parsed.channel);
        assertEquals(original.message, parsed.message);
        assertEquals(original.user, parsed.user);
    }

    @Test
    void serverApplicationRoundTripPreservesNestedData() {
        final ServerApplication original = ServerApplication.getExample();
        final ServerApplication parsed = JsonRoundTripSupport.roundTrip(original, ServerApplication.class);

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
        final Event event = Event.getExample();
        final EventThreadMessage original = new EventThreadMessage(
                event,
                new EventThreadMessage.Channel("123", "event-thread"),
                new EventThreadMessage.Author(
                        "456",
                        "tester",
                        "Tester",
                        Player.getExample()),
                new EventThreadMessage.Message(
                        "789",
                        new EventThreadMessage.Message.Content("raw", "display", "stripped"),
                        List.of(new EventThreadMessage.Message.Attachment(
                                "att-1",
                                "file.png",
                                "https://example.invalid/file.png",
                                "https://proxy.invalid/file.png"))));

        final EventThreadMessage parsed = JsonRoundTripSupport.roundTrip(original, EventThreadMessage.class);

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
        final LinkEvent linkEvent = JsonRoundTripSupport.roundTrip(LinkEvent.getExample(), LinkEvent.class);
        final CrossBanEvent crossBanEvent = JsonRoundTripSupport.roundTrip(CrossBanEvent.getExample(), CrossBanEvent.class);

        assertNotNull(linkEvent);
        assertEquals(LinkEvent.LinkStatus.ADDED, linkEvent.linkStatus);
        assertNotNull(linkEvent.discord);
        assertNotNull(Player.getExample().discord);
        assertEquals(Player.getExample().discord.id, linkEvent.discord.id);

        assertNotNull(crossBanEvent);
        assertEquals(CrossBanEvent.Status.ADDED, crossBanEvent.status);
        assertEquals(CrossBan.getExample().reason, crossBanEvent.reason);
    }

    @Test
    void socketEnvelopeAndActionRoundTripWithSubtypePayloads() {
        final Player player = Player.getExample();

        final SocketEvent<Player> eventEnvelope = new SocketEvent<>(
                SocketEventName.LINK,
                7,
                player);
        final SocketAction<PlayerConnectionAction> actionEnvelope = new SocketAction<>(
                SocketActionName.PLAYER_CONNECTION,
                new PlayerConnectionAction(
                        UUID.fromString("aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee"),
                        "tester",
                        new Date(1_700_000_000_123L),
                        PlayerConnectionAction.Type.JOIN));

        final Type eventType = JsonRoundTripSupport.typeOf(SocketEvent.class, Player.class);
        final Type actionType = JsonRoundTripSupport.typeOf(SocketAction.class, PlayerConnectionAction.class);

        final SocketEvent<Player> parsedEvent = JsonRoundTripSupport.roundTrip(eventEnvelope, eventType);
        final SocketAction<PlayerConnectionAction> parsedAction = JsonRoundTripSupport.roundTrip(actionEnvelope, actionType);

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
