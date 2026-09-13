package gg.eventalerts.sdk.object;

import com.google.gson.annotations.SerializedName;
import gg.eventalerts.sdk.ExampleUtility;
import org.bson.types.ObjectId;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;


public class EAEventPreset extends EAObject {
    @NotNull public static final String KEY_ID = "id";
    @NotNull public static final String KEY_USER = "user";
    @NotNull public static final String KEY_NAME = "name";
    @NotNull public static final String KEY_DESCRIPTION = "description";
    @NotNull public static final String KEY_LAST_USED = "lastUsed";
    @NotNull public static final String KEY_DATA = "data";

    @SerializedName(KEY_ID) @Nullable public ObjectId id;
    @SerializedName(KEY_USER) @Nullable public Long user;
    @SerializedName(KEY_NAME) @Nullable public String name;
    @SerializedName(KEY_DESCRIPTION) @Nullable public String description;
    @SerializedName(KEY_LAST_USED) @Nullable public Date lastUsed;
    @SerializedName(KEY_DATA) @Nullable public Data data;

    public EAEventPreset() {}

    public EAEventPreset(@Nullable ObjectId id, @Nullable Long user, @Nullable String name, @Nullable String description, @Nullable Date lastUsed, @Nullable Data data) {
        this.id = id;
        this.user = user;
        this.name = name;
        this.description = description;
        this.lastUsed = lastUsed;
        this.data = data;
    }

    @NotNull
    public static EAEventPreset getExample() {
        return new EAEventPreset(
                new ObjectId(),
                ExampleUtility.User.SRNYX_ID,
                "Example Preset",
                "This is an example preset.",
                new Date(),
                Data.getExample());
    }

    public static class Data {
        @NotNull public static final String KEY_TITLE = "title";
        @NotNull public static final String KEY_ROLES = "roles";
        @NotNull public static final String KEY_DESCRIPTION = "description";
        @NotNull public static final String KEY_TIME = "time";
        @NotNull public static final String KEY_PRIZE = "prize";
        @NotNull public static final String KEY_IP = "ip";
        @NotNull public static final String KEY_PLATFORMS = "platforms";
        @NotNull public static final String KEY_VERSION = "version";
        @NotNull public static final String KEY_MAX_PLAYERS = "maxPlayers";
        @NotNull public static final String KEY_MEDIA = "media";

        @SerializedName(KEY_TITLE) @Nullable public String title;
        @SerializedName(KEY_ROLES) @Nullable public Set<EAEvent.PingRole> roles;
        @SerializedName(KEY_DESCRIPTION) @Nullable public String description;
        @SerializedName(KEY_TIME) @Nullable public String time;
        @SerializedName(KEY_PRIZE) @Nullable public String prize;
        @SerializedName(KEY_IP) @Nullable public String ip;
        @SerializedName(KEY_PLATFORMS) @Nullable public Set<EAEvent.Platform> platforms;
        @SerializedName(KEY_VERSION) @Nullable public String version;
        @SerializedName(KEY_MAX_PLAYERS) @Nullable public Integer maxPlayers;
        @SerializedName(KEY_MEDIA) @Nullable public EAEvent.Media media;

        public Data() {}

        public Data(@Nullable String title, @Nullable Collection<EAEvent.PingRole> roles, @Nullable String description, @Nullable String time, @Nullable String prize, @Nullable String ip, @Nullable Collection<EAEvent.Platform> platforms, @Nullable String version, @Nullable Integer maxPlayers, @Nullable EAEvent.Media media) {
            this.title = title;
            this.roles = roles == null ? null : new HashSet<>(roles);
            this.description = description;
            this.time = time;
            this.prize = prize;
            this.ip = ip;
            this.platforms = platforms == null ? null : new HashSet<>(platforms);
            this.version = version;
            this.maxPlayers = maxPlayers;
            this.media = media;
        }

        @NotNull
        public static Data getExample() {
            return new Data(
                    "Example Event",
                    Collections.singleton(EAEvent.PingRole.PARTNER),
                    "This is an example event description.",
                    "30m",
                    "Example Prize",
                    "example.com",
                    Collections.singleton(EAEvent.Platform.JAVA),
                    "1.21.11",
                    100,
                    EAEvent.Media.getExample());
        }
    }
}
