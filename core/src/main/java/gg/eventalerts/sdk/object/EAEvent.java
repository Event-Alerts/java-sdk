package gg.eventalerts.sdk.object;

import gg.eventalerts.sdk.ExampleUtility;
import org.bson.types.ObjectId;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;


public class EAEvent extends EAObject {
    // BUILDER/CUSTOM
    @Nullable public ObjectId id;
    @Nullable public Type type;
    @Nullable public Long channel;
    @Nullable public Long message;
    @Nullable public Long controlPanel;
    @Nullable public Cancellation cancellation;
    @Nullable public Set<Review> reviews;
    @Nullable public Boolean custom;
    @Nullable public Date created;
    @Nullable public String title;
    @Nullable public Long host;
    @Nullable public String description;
    @Nullable public Set<Long> roles;
    @Nullable public Set<PingRole> rolesNamed;
    @Nullable public ObjectId server;
    @Nullable public Media media;
    @Nullable public Source source;

    // BUILDER
    @Nullable public Mode mode;
    @Nullable public String ip;
    @Nullable public Set<Platform> platforms;
    @Nullable public String platform;
    @Nullable public String version;
    @Nullable public String prize;
    @Nullable public Integer maxPlayers;
    /**
     * The time the event starts
     */
    @Nullable public Date time;
    @Nullable public Set<Long> subscribers;

    public EAEvent() {}

    public EAEvent(@Nullable ObjectId id, @Nullable Type type, @Nullable Long channel, @Nullable Long message, @Nullable Long controlPanel, @Nullable Cancellation cancellation, @Nullable Set<Review> reviews, @Nullable Boolean custom, @Nullable Date created, @Nullable String title, @Nullable Long host, @Nullable String description, @Nullable Set<Long> roles, @Nullable Set<PingRole> rolesNamed, @Nullable ObjectId server, @Nullable Media media, @Nullable Source source, @Nullable Mode mode, @Nullable String ip, @Nullable Set<Platform> platforms, @Nullable String platform, @Nullable String version, @Nullable String prize, @Nullable Integer maxPlayers, @Nullable Date time, @Nullable Set<Long> subscribers) {
        this.id = id;
        this.type = type;
        this.channel = channel;
        this.message = message;
        this.controlPanel = controlPanel;
        this.cancellation = cancellation;
        this.reviews = reviews;
        this.custom = custom;
        this.created = created;
        this.title = title;
        this.host = host;
        this.description = description;
        this.roles = roles;
        this.rolesNamed = rolesNamed;
        this.server = server;
        this.media = media;
        this.source = source;
        this.mode = mode;
        this.ip = ip;
        this.platforms = platforms;
        this.platform = platform;
        this.version = version;
        this.prize = prize;
        this.maxPlayers = maxPlayers;
        this.time = time;
        this.subscribers = subscribers;
    }

    @Override
    public boolean equals(@Nullable Object object) {
        if (this == object) return true;
        if (!(object instanceof EAEvent)) return false;
        return id != null && id.equals(((EAEvent) object).id);
    }

    @Override
    public int hashCode() {
        if (id == null) return super.hashCode();
        return id.hashCode();
    }

    @NotNull
    public static EAEvent getExample() {
        return new EAEvent(
                new ObjectId(),
                Type.PARTNER,
                ExampleUtility.Random.discordId(),
                ExampleUtility.Random.discordId(),
                ExampleUtility.Random.discordId(),
                Cancellation.getExample(),
                Collections.singleton(Review.getExample()),
                false,
                new Date(),
                "Example Event",
                ExampleUtility.User.SRNYX_ID,
                "This is an example event description.",
                new HashSet<>(Arrays.asList(ExampleUtility.Role.PARTNER_EVENTS_ID, ExampleUtility.Role.MONEY_EVENTS_ID)),
                new HashSet<>(Arrays.asList(PingRole.PARTNER, PingRole.MONEY)),
                new ObjectId(),
                Media.getExample(),
                Source.DISCORD,
                Mode.TEXT,
                "play.eventalerts.gg",
                Collections.singleton(Platform.JAVA),
                "Java",
                "1.21.11",
                "$10 USD",
                100,
                new Date(),
                new HashSet<>(Arrays.asList(ExampleUtility.User.RAME_ID, ExampleUtility.User.REECE_ID)));
    }

    @Nullable
    public Long getTimeUntil() {
        return time == null ? null : time.getTime() - System.currentTimeMillis();
    }

    public enum Mode {
        TEXT,
        IMAGE
    }

    public enum Type {
        COMMUNITY("Community"),
        PARTNER("Partner");

        @NotNull public final String displayName;

        Type(@NotNull String displayName) {
            this.displayName = displayName;
        }
    }

    public enum PingRole {
        COMMUNITY("Community"),
        PARTNER("Partner"),
        BIG_MONEY("Big Money", true),
        MONEY("Money", true),
        FUN("Fun", true),
        HOUSING("Housing", true),
        CIVILIZATION("Civilization", true);

        @NotNull public final String displayName;
        public final boolean partnerPingable;

        PingRole(@NotNull String displayName, boolean partnerPingable) {
            this.displayName = displayName;
            this.partnerPingable = partnerPingable;
        }

        PingRole(@NotNull String displayName) {
            this(displayName, false);
        }
    }

    public enum Source {
        DISCORD,
        API
    }

    public enum Platform {
        JAVA("Java"),
        BEDROCK("Bedrock");

        @NotNull public final String displayName;

        Platform(@NotNull String displayName) {
            this.displayName = displayName;
        }

        @NotNull
        public static String toLegacyString(@NotNull Collection<Platform> platforms) {
            final boolean hasJava = platforms.contains(JAVA);
            final boolean hasBedrock = platforms.contains(BEDROCK);
            if (hasJava && hasBedrock) return "Java/Bedrock";
            if (hasJava) return "Java";
            if (hasBedrock) return "Bedrock";
            return "";
        }
    }

    public static class Cancellation extends EAObject {
        @Nullable public Date timestamp;
        @Nullable public Long user;
        @Nullable public String reason;

        public Cancellation() {}

        public Cancellation(@Nullable Date timestamp, @Nullable Long user, @Nullable String reason) {
            this.timestamp = timestamp;
            this.user = user;
            this.reason = reason;
        }

        @NotNull
        public static Cancellation getExample() {
            return new Cancellation(
                    new Date(),
                    ExampleUtility.User.SRNYX_ID,
                    "This event was cancelled due to a server issue.");
        }
    }

    public static class Review extends EAObject {
        @Nullable public Long reviewer;
        @Nullable public Date timestamp;
        @Nullable public String comments;
        @Nullable public Map<Category, Integer> categories;

        public Review() {}

        public Review(long reviewer, @NotNull Date timestamp, @NotNull String comments, @NotNull Map<Category, Integer> categories) {
            this.reviewer = reviewer;
            this.timestamp = timestamp;
            this.comments = comments;
            this.categories = categories;
        }

        public enum Category {
            ORGANIZATION,
            COMMUNICATION,
            ENJOYMENT
        }

        @NotNull
        public static Review getExample() {
            final Map<Category, Integer> categories = new EnumMap<>(Category.class);
            categories.put(Category.ORGANIZATION, 5);
            categories.put(Category.COMMUNICATION, 5);
            categories.put(Category.ENJOYMENT, 4);
            return new Review(
                    ExampleUtility.User.OIIINK_ID,
                    new Date(),
                    "This event was really fun and well organized! I look forward to future events.",
                    categories);
        }
    }

    public static class Media extends EAObject {
        @Nullable public String url;
        @Nullable public String name;

        public Media() {}

        public Media(@NotNull String url, @NotNull String name) {
            this.url = url;
            this.name = name;
        }

        @NotNull
        public static Media getExample() {
            return new Media(
                    "https://us-east-1.tixte.net/uploads/img.venox.network/bee.png",
                    "bee.png");
        }
    }
}
