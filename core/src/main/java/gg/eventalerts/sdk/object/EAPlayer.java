package gg.eventalerts.sdk.object;

import com.google.gson.annotations.SerializedName;
import gg.eventalerts.sdk.ExampleUtility;
import org.bson.types.ObjectId;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;


public class EAPlayer extends EAObject {
    @NotNull public static final String KEY_ID = "id";
    @NotNull public static final String KEY_DISCORD = "discord";
    @NotNull public static final String KEY_MINECRAFT = "minecraft";
    @NotNull public static final String KEY_LINK_METHOD = "linkMethod";
    @NotNull public static final String KEY_ANNIVERSARIES = "anniversaries";
    @NotNull public static final String KEY_BOOSTER_PASSES = "boosterPasses";
    @NotNull public static final String KEY_DEFAULT_PRESET = "defaultPreset";
    @NotNull public static final String KEY_RATING = "rating";
    @NotNull public static final String KEY_SUBSCRIPTION = "subscription";

    @SerializedName(KEY_ID) @Nullable public ObjectId id;
    @SerializedName(KEY_DISCORD) @Nullable public Discord discord;
    @SerializedName(KEY_MINECRAFT) @Nullable public Minecraft minecraft;
    @SerializedName(KEY_LINK_METHOD) @Nullable public LinkMethod linkMethod;
    @SerializedName(KEY_ANNIVERSARIES) @Nullable public Set<Integer> anniversaries;
    @SerializedName(KEY_BOOSTER_PASSES) @Nullable public Set<Long> boosterPasses;
    @SerializedName(KEY_DEFAULT_PRESET) @Nullable public ObjectId defaultPreset;
    @SerializedName(KEY_RATING) @Nullable public Double rating;
    @SerializedName(KEY_SUBSCRIPTION) @Nullable public Subscription subscription;

    public EAPlayer() {}

    public EAPlayer(@Nullable ObjectId id, @Nullable Discord discord, @Nullable Minecraft minecraft, @Nullable LinkMethod linkMethod, @Nullable Collection<Integer> anniversaries, @Nullable Collection<Long> boosterPasses, @Nullable ObjectId defaultPreset, @Nullable Double rating, @Nullable Subscription subscription) {
        this.id = id;
        this.discord = discord;
        this.minecraft = minecraft;
        this.linkMethod = linkMethod;
        this.anniversaries = anniversaries == null ? null : new HashSet<>(anniversaries);
        this.boosterPasses = boosterPasses == null ? null : new HashSet<>(boosterPasses);
        this.defaultPreset = defaultPreset;
        this.rating = rating;
        this.subscription = subscription;
    }

    public EAPlayer(@NotNull EAPlayer player) {
        this(player.id, player.discord, player.minecraft, player.linkMethod, player.anniversaries, player.boosterPasses, player.defaultPreset, player.rating, player.subscription);
    }

    @Override
    public boolean equals(@Nullable Object object) {
        if (this == object) return true;
        if (!(object instanceof EAPlayer)) return false;
        return id != null && id.equals(((EAPlayer) object).id);
    }

    @Override
    public int hashCode() {
        if (id == null) return super.hashCode();
        return id.hashCode();
    }

    @NotNull
    public static EAPlayer getExample() {
        return new EAPlayer(
                new ObjectId(),
                Discord.getExample(),
                Minecraft.getExample(),
                EAPlayer.LinkMethod.MICROSOFT_OAUTH,
                Arrays.asList(2024, 2025),
                Collections.singleton(ExampleUtility.User.OIIINK_ID),
                new ObjectId(),
                4.5,
                Subscription.getExample());
    }

    public enum LinkMethod {
        LEGACY,
        MICROSOFT_OAUTH,
        DISCORD_OAUTH,
        CODE_FROM_DISCORD,
        CODE_FROM_MINECRAFT,
        CROSS_BAN
    }

    public static class Discord extends EAObject {
        @NotNull public static final String KEY_ID = "id";
        @NotNull public static final String KEY_USERNAME = "username";
        @NotNull public static final String KEY_ROLES = "roles";

        @SerializedName(KEY_ID) @Nullable public Long id;
        @SerializedName(KEY_USERNAME) @Nullable public String username;
        @SerializedName(KEY_ROLES) @Nullable public Set<EAPlayer.Discord.Role> roles;

        public Discord() {}

        public Discord(@Nullable Long id, @Nullable String username, @Nullable Set<EAPlayer.Discord.Role> roles) {
            this.id = id;
            this.username = username;
            this.roles = roles;
        }

        @NotNull
        public Set<EAPlayer.Discord.Role> getRoles() {
            return roles == null ? Collections.emptySet() : roles;
        }

        @NotNull
        public static Discord getExample() {
            return new Discord(
                    ExampleUtility.User.SRNYX_ID,
                    ExampleUtility.User.SRNYX_USERNAME,
                    new HashSet<>(Arrays.asList(EAPlayer.Discord.Role.ADMIN, EAPlayer.Discord.Role.STAFF)));
        }

        public enum Role {
            ADMIN,
            STAFF,
            CREATOR,
            CONTRIBUTOR,
        }
    }

    public static class Minecraft extends EAObject {
        @NotNull public static final String KEY_UUID = "uuid";
        @NotNull public static final String KEY_USERNAME = "username";
        @NotNull public static final String KEY_EVENT_UTILS = "eventUtils";

        @SerializedName(KEY_UUID) @Nullable public UUID uuid;
        @SerializedName(KEY_USERNAME) @Nullable public String username;
        @SerializedName(KEY_EVENT_UTILS) @Nullable public EventUtils eventUtils;

        public Minecraft() {}

        public Minecraft(@Nullable UUID uuid, @Nullable String username, @Nullable EventUtils eventUtils) {
            this.uuid = uuid;
            this.username = username;
            this.eventUtils = eventUtils;
        }

        @NotNull
        public static Minecraft getExample() {
            return new Minecraft(
                    ExampleUtility.Minecraft.SRNYX_UUID,
                    ExampleUtility.Minecraft.SRNYX_USERNAME,
                    EventUtils.getExample());
        }

        public static class EventUtils extends EAObject {
            @NotNull public static final String KEY_TOKEN_HASH = "tokenHash";
            @NotNull public static final String KEY_CREATED = "created";
            @NotNull public static final String KEY_EXPIRES = "expires";
            @NotNull public static final String KEY_LAST_HEARTBEAT = "lastHeartbeat";
            @NotNull public static final String KEY_MOD_VERSION = "modVersion";
            @NotNull public static final String KEY_MINECRAFT_VERSION = "minecraftVersion";

            @SerializedName(KEY_TOKEN_HASH) @Nullable public String tokenHash;
            @SerializedName(KEY_CREATED) @Nullable public Date created;
            @SerializedName(KEY_EXPIRES) @Nullable public Date expires;
            @SerializedName(KEY_LAST_HEARTBEAT) @Nullable public Date lastHeartbeat;
            @SerializedName(KEY_MOD_VERSION) @Nullable public String modVersion;
            @SerializedName(KEY_MINECRAFT_VERSION) @Nullable public String minecraftVersion;

            public EventUtils() {}

            public EventUtils(@Nullable String tokenHash, @Nullable Date created, @Nullable Date expires, @Nullable Date lastHeartbeat, @Nullable String modVersion, @Nullable String minecraftVersion) {
                this.tokenHash = tokenHash;
                this.created = created;
                this.expires = expires;
                this.lastHeartbeat = lastHeartbeat;
                this.modVersion = modVersion;
                this.minecraftVersion = minecraftVersion;
            }

            @NotNull
            public static EventUtils getExample() {
                return new EventUtils(
                        "exampleToken",
                        new Date(),
                        new Date(),
                        new Date(),
                        "3.0.0",
                        "1.21.11");
            }
        }
    }

    public static class Subscription extends EAObject {
        @NotNull public static final String KEY_TIER = "tier";
        @NotNull public static final String KEY_SERVERS = "servers";

        @SerializedName(KEY_TIER) @Nullable public EAPlayer.Subscription.Tier tier;
        @SerializedName(KEY_SERVERS) @Nullable public List<Long> servers;

        public Subscription() {}

        public Subscription(@Nullable EAPlayer.Subscription.Tier tier, @Nullable List<Long> servers) {
            this.tier = tier;
            this.servers = servers;
        }

        @NotNull
        public List<Long> getServers() {
            return servers == null ? Collections.emptyList() : servers;
        }

        @NotNull
        public static Subscription getExample() {
            return new Subscription(
                    Tier.BEE,
                    Collections.singletonList(ExampleUtility.Guild.EVENT_ALERTS_ID));
        }

        public enum Tier {
            BEE,
            WASP,
            HORNET
        }
    }
}
