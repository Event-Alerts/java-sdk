package gg.eventalerts.sdk.object;

import com.google.gson.annotations.SerializedName;
import gg.eventalerts.sdk.ExampleUtility;
import org.bson.types.ObjectId;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;


public class EAPartnerServer extends EAObject {
    @NotNull public static final String KEY_ID = "id";
    @NotNull public static final String KEY_SERVER_ID = "serverId";
    @NotNull public static final String KEY_CREATED = "created";
    @NotNull public static final String KEY_REPRESENTATIVES = "representatives";
    @NotNull public static final String KEY_ENABLED = "enabled";
    @NotNull public static final String KEY_NAME = "name";
    @NotNull public static final String KEY_DESCRIPTION = "description";
    @NotNull public static final String KEY_INVITE = "invite";
    @NotNull public static final String KEY_TAGS = "tags";
    @NotNull public static final String KEY_COLOR = "color";
    @NotNull public static final String KEY_THUMBNAIL = "thumbnail";
    @NotNull public static final String KEY_MESSAGE = "message";
    @NotNull public static final String KEY_GETS = "gets";
    @NotNull public static final String KEY_DISABLE_DATA = "disableData";
    @NotNull public static final String KEY_API_KEY = "apiKey";

    @SerializedName(KEY_ID) @Nullable public ObjectId id;
    @SerializedName(KEY_SERVER_ID) @Nullable public Long serverId;
    @SerializedName(KEY_CREATED) @Nullable public Date created;
    @SerializedName(KEY_REPRESENTATIVES) @Nullable public Set<Long> representatives;
    @SerializedName(KEY_ENABLED) @Nullable public Date enabled;
    @SerializedName(KEY_NAME) @Nullable public String name;
    @SerializedName(KEY_DESCRIPTION) @Nullable public String description;
    @SerializedName(KEY_INVITE) @Nullable public String invite;
    @SerializedName(KEY_TAGS) @Nullable public Set<Tag> tags;
    @SerializedName(KEY_COLOR) @Nullable public Integer color;
    @SerializedName(KEY_THUMBNAIL) @Nullable public String thumbnail;
    @SerializedName(KEY_MESSAGE) @Nullable public Long message;
    @SerializedName(KEY_GETS) @Nullable public Map<Long, Integer> gets;
    @SerializedName(KEY_DISABLE_DATA) @Nullable public DisableData disableData;
    @SerializedName(KEY_API_KEY) @Nullable public String apiKey;

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
        @NotNull public static final String KEY_REASON = "reason";
        @NotNull public static final String KEY_TIME = "time";

        @SerializedName(KEY_REASON) @Nullable public String reason;
        /**
         * When the server was disabled
         */
        @SerializedName(KEY_TIME) @Nullable public Date time;

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
