package gg.eventalerts.sdk.json;

import gg.eventalerts.sdk.object.EAEvent;
import gg.eventalerts.sdk.object.EAPartnerServer;
import gg.eventalerts.sdk.object.EAPlayer;
import gg.eventalerts.sdk.support.JsonRoundTripSupport;
import gg.eventalerts.sdk.websocket.SocketActionName;
import gg.eventalerts.sdk.websocket.SocketEventName;
import gg.eventalerts.sdk.websocket.message.action.SocketAction;
import gg.eventalerts.sdk.websocket.message.action.EAUpdateSubscriptionAction;
import gg.eventalerts.sdk.websocket.message.event.SocketEvent;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


class GsonRoundTripTest {
    @Test
    void eventRoundTripPreservesCoreFields() {
        final EAEvent original = EAEvent.getExample();
        final EAEvent parsed = JsonRoundTripSupport.roundTrip(original, EAEvent.class);

        assertNotNull(parsed);
        assertEquals(original.id, parsed.id);
        assertEquals(original.type, parsed.type);
        assertEquals(original.title, parsed.title);
        assertEquals(original.created, parsed.created);
        assertEquals(original.time, parsed.time);
        assertEquals(original.roles, parsed.roles);
        assertEquals(original.rolesNamed, parsed.rolesNamed);
        assertNotNull(parsed.cancellation);
        assertNotNull(original.cancellation);
        assertEquals(original.cancellation.reason, parsed.cancellation.reason);
        assertNotNull(parsed.media);
        assertNotNull(original.media);
        assertEquals(original.media.url, parsed.media.url);
        assertEquals(original.media.name, parsed.media.name);
    }

    @Test
    void playerRoundTripPreservesNestedObjects() {
        final EAPlayer original = EAPlayer.getExample();
        final EAPlayer parsed = JsonRoundTripSupport.roundTrip(original, EAPlayer.class);

        assertNotNull(parsed);
        assertEquals(original.id, parsed.id);
        assertEquals(original.linkMethod, parsed.linkMethod);
        assertEquals(original.anniversaries, parsed.anniversaries);
        assertEquals(original.boosterPasses, parsed.boosterPasses);
        assertEquals(original.defaultPreset, parsed.defaultPreset);
        assertEquals(original.rating, parsed.rating);
        assertNotNull(parsed.discord);
        assertNotNull(original.discord);
        assertEquals(original.discord.id, parsed.discord.id);
        assertEquals(original.discord.username, parsed.discord.username);
        assertEquals(original.discord.getRoles(), parsed.discord.getRoles());
        assertNotNull(parsed.minecraft);
        assertNotNull(original.minecraft);
        assertEquals(original.minecraft.uuid, parsed.minecraft.uuid);
        assertEquals(original.minecraft.username, parsed.minecraft.username);
        assertNotNull(parsed.subscription);
        assertNotNull(original.subscription);
        assertEquals(original.subscription.tier, parsed.subscription.tier);
        assertEquals(original.subscription.getServers(), parsed.subscription.getServers());
    }

    @Test
    void partnerServerRoundTripPreservesNestedObjects() {
        final EAPartnerServer original = EAPartnerServer.getExample();
        final EAPartnerServer parsed = JsonRoundTripSupport.roundTrip(original, EAPartnerServer.class);

        assertNotNull(parsed);
        assertEquals(original.id, parsed.id);
        assertEquals(original.serverId, parsed.serverId);
        assertEquals(original.created, parsed.created);
        assertEquals(original.representatives, parsed.representatives);
        assertEquals(original.name, parsed.name);
        assertEquals(original.description, parsed.description);
        assertEquals(original.invite, parsed.invite);
        assertEquals(original.tags, parsed.tags);
        assertEquals(original.color, parsed.color);
        assertEquals(original.thumbnail, parsed.thumbnail);
        assertEquals(original.gets, parsed.gets);
        assertNotNull(parsed.disableData);
        assertNotNull(original.disableData);
        assertEquals(original.disableData.reason, parsed.disableData.reason);
        assertEquals(original.disableData.time, parsed.disableData.time);
        assertNotNull(parsed.apiKey);
        assertEquals(original.apiKey, parsed.apiKey);
    }

    @Test
    void primitiveAdaptersRoundTripValues() {
        final Date date = new Date(1_700_000_000_123L);
        final UUID uuid = UUID.fromString("aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee");
        final ObjectId objectId = new ObjectId("507f1f77bcf86cd799439011");

        assertEquals(date, JsonRoundTripSupport.roundTrip(date, Date.class));
        assertEquals(uuid, JsonRoundTripSupport.roundTrip(uuid, UUID.class));
        assertEquals(objectId, JsonRoundTripSupport.roundTrip(objectId, ObjectId.class));
    }

    @Test
    void socketEventRoundTripPreservesEnvelopeAndPayload() {
        final SocketEvent<EAEvent> original = new SocketEvent<>();
        original.event = SocketEventName.EVENT_POSTED;
        original.sequence = 5;
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
