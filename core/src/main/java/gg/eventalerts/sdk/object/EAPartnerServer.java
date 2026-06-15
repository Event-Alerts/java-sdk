package gg.eventalerts.sdk.object;

import gg.eventalerts.sdk.ExampleUtility;
import org.bson.types.ObjectId;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;


public class EAPartnerServer extends EAObject {
    @Nullable public ObjectId id;
    @Nullable public Long serverId;
    @Nullable public Date created;
    @Nullable public Set<Long> representatives;
    @Nullable public Date enabled;
    @Nullable public String name;
    @Nullable public String description;
    @Nullable public String invite;
    @Nullable public Set<Tag> tags;
    @Nullable public Integer color;
    @Nullable public String thumbnail;
    @Nullable public Long message;
    @Nullable public Map<Long, Integer> gets;
    @Nullable public DisableData disableData;
    @Nullable public String apiKey;

    public EAPartnerServer() {}

    public EAPartnerServer(@Nullable ObjectId id, @Nullable Long serverId, @Nullable Date created, @Nullable Collection<Long> representatives, @Nullable Date enabled, @Nullable String name, @Nullable String description, @Nullable String invite, @Nullable Collection<Tag> tags, @Nullable Integer color, @Nullable String thumbnail, @Nullable Long message, @Nullable Map<Long, Integer> gets, @Nullable DisableData disableData, @Nullable String apiKey) {
        this.id = id;
        this.serverId = serverId;
        this.created = created;
        this.representatives = representatives == null ? null : new HashSet<>(representatives);
        this.enabled = enabled;
        this.name = name;
        this.description = description;
        this.invite = invite;
        this.tags = tags == null ? null : new HashSet<>(tags);
        this.color = color;
        this.thumbnail = thumbnail;
        this.message = message;
        this.gets = gets;
        this.disableData = disableData;
        this.apiKey = apiKey;
    }

    public EAPartnerServer(@NotNull EAPartnerServer partnerServer) {
        this(partnerServer.id, partnerServer.serverId, partnerServer.created, partnerServer.representatives, partnerServer.enabled, partnerServer.name, partnerServer.description, partnerServer.invite, partnerServer.tags, partnerServer.color, partnerServer.thumbnail, partnerServer.message, partnerServer.gets, partnerServer.disableData, partnerServer.apiKey);
    }

    @Override
    public boolean equals(@Nullable Object object) {
        if (this == object) return true;
        if (!(object instanceof EAPartnerServer)) return false;
        return id != null && id.equals(((EAPartnerServer) object).id);
    }

    @Override
    public int hashCode() {
        if (id == null) return super.hashCode();
        return id.hashCode();
    }

    @NotNull
    public static EAPartnerServer getExample() {
        final Map<Long, Integer> gets = new HashMap<>();
        gets.put(ExampleUtility.User.SRNYX_ID, 10);
        gets.put(ExampleUtility.User.OIIINK_ID, 5);
        return new EAPartnerServer(
                new ObjectId(),
                ExampleUtility.Guild.EVENT_ALERTS_ID,
                new Date(),
                Arrays.asList(ExampleUtility.User.SRNYX_ID, ExampleUtility.User.OIIINK_ID),
                null,
                "Example Server",
                "This is an example partner server.",
                "skeppy",
                Arrays.asList(Tag.FUN, Tag.PVP),
                0xFF5733,
                "https://us-east-1.tixte.net/uploads/img.venox.network/bee.png",
                null,
                gets,
                DisableData.getExample(),
                "EA.Player." + ExampleUtility.Random.base64(32));
    }

    public enum Tag {
        CIVILIZATION,
        FUN,
        HYPIXEL,
        LUCK,
        MINEHUT,
        MONEY,
        PVP,
        SKILL,
        STREAMS,
        VIDEOS
    }

    public static class DisableData extends EAObject {
        @Nullable public String reason;
        /**
         * When the server was disabled
         */
        @Nullable public Date time;

        public DisableData() {}

        public DisableData(@NotNull String reason, @NotNull Date time) {
            this.reason = reason;
            this.time = time;
        }

        @NotNull
        public static DisableData getExample() {
            return new DisableData("Violation of terms", new Date());
        }
    }
}
