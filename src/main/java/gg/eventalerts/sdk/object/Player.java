package gg.eventalerts.sdk.object;

import gg.eventalerts.sdk.ExampleUtility;
import org.bson.types.ObjectId;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;
import java.util.UUID;


public class Player extends EAObject {
    @Nullable public ObjectId id;
    @Nullable public Discord discord;
    @Nullable public Minecraft minecraft;
    @Nullable public LinkMethod linkMethod;
    @Nullable public Set<Integer> anniversaries;
    @Nullable public Set<Long> boosterPasses;
    @Nullable public ObjectId defaultPreset;
    @Nullable public Double rating;
    @Nullable public Subscription subscription;

    public Player() {}

    public Player(@Nullable ObjectId id, @Nullable Discord discord, @Nullable Minecraft minecraft, @Nullable LinkMethod linkMethod, @Nullable Set<Integer> anniversaries, @Nullable Set<Long> boosterPasses, @Nullable ObjectId defaultPreset, @Nullable Double rating, @Nullable Subscription subscription) {
        this.id = id;
        this.discord = discord;
        this.minecraft = minecraft;
        this.linkMethod = linkMethod;
        this.anniversaries = anniversaries;
        this.boosterPasses = boosterPasses;
        this.defaultPreset = defaultPreset;
        this.rating = rating;
        this.subscription = subscription;
    }

    public Player(@NotNull Player player) {
        this(player.id, player.discord, player.minecraft, player.linkMethod, player.anniversaries, player.boosterPasses, player.defaultPreset, player.rating, player.subscription);
    }

    @Override
    public boolean equals(@Nullable Object object) {
        if (this == object) return true;
        if (!(object instanceof final Player player)) return false;
        return id != null && id.equals(player.id);
    }

    @Override
    public int hashCode() {
        if (id == null) return super.hashCode();
        return id.hashCode();
    }

    @NotNull
    public static Player getExample() {
        return new Player(
                new ObjectId(),
                Discord.getExample(),
                Minecraft.getExample(),
                Player.LinkMethod.MICROSOFT_OAUTH,
                Set.of(2024, 2025),
                Set.of(ExampleUtility.User.OIIINK_ID),
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
        @Nullable public String id;
        @Nullable public String username;
        @Nullable public Set<Player.Discord.Role> roles;

        public Discord() {}

        public Discord(long id, @Nullable String username, @Nullable Set<Player.Discord.Role> roles) {
            this.id = String.valueOf(id);
            this.username = username;
            this.roles = roles;
        }

        @NotNull
        public Set<Player.Discord.Role> getRoles() {
            return roles == null ? Set.of() : roles;
        }

        @NotNull
        public static Discord getExample() {
            return new Discord(
                    ExampleUtility.User.SRNYX_ID,
                    "srnyx",
                    Set.of(Player.Discord.Role.ADMIN, Player.Discord.Role.STAFF));
        }

        public enum Role {
            ADMIN,
            STAFF,
            CREATOR,
            CONTRIBUTOR,
        }
    }

    public static class Minecraft extends EAObject {
        @Nullable public UUID uuid;
        @Nullable public String username;

        public Minecraft() {}

        public Minecraft(@NotNull UUID uuid, @Nullable String username) {
            this.uuid = uuid;
            this.username = username;
        }

        @NotNull
        public static Minecraft getExample() {
            return new Minecraft(
                    ExampleUtility.Minecraft.SRNYX_UUID,
                    "srnyx");
        }
    }

    public static class Subscription extends EAObject {
        @Nullable public Player.Subscription.Tier tier;
        @Nullable public List<Long> servers;

        public Subscription() {}

        public Subscription(@NotNull Player.Subscription.Tier tier, @Nullable List<Long> servers) {
            this.tier = tier;
            this.servers = servers;
        }

        @NotNull
        public List<Long> getServers() {
            return servers == null ? List.of() : servers;
        }

        @NotNull
        public static Subscription getExample() {
            return new Subscription(
                    Tier.BEE,
                    List.of(ExampleUtility.Guild.EVENT_ALERTS_ID));
        }

        public enum Tier {
            BEE,
            WASP,
            HORNET
        }
    }
}
