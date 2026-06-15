package gg.eventalerts.core.json;

import gg.eventalerts.sdk.object.EACrossBan;
import gg.eventalerts.sdk.object.EAEvent;
import gg.eventalerts.sdk.object.EAEventThreadMessage;
import gg.eventalerts.sdk.object.EAFamousEvent;
import gg.eventalerts.sdk.object.EAPlayer;
import gg.eventalerts.sdk.object.EAServerApplication;
import gg.eventalerts.core.support.JsonRoundTripSupport;
import org.junit.jupiter.api.Test;

import java.util.Collections;

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
                new EAEventThreadMessage.Channel(123L, "event-thread"),
                new EAEventThreadMessage.Author(
                        456L,
                        "tester",
                        "Tester",
                        EAPlayer.getExample()),
                new EAEventThreadMessage.Message(
                        789L,
                        new EAEventThreadMessage.Message.Content("raw", "display", "stripped"),
                        Collections.singletonList(new EAEventThreadMessage.Message.Attachment(
                                321L,
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
}
