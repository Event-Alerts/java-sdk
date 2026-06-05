package gg.eventalerts.sdk.object;

import gg.eventalerts.sdk.ExampleUtility;
import org.bson.types.ObjectId;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Date;
import java.util.Map;
import java.util.Set;


public class Event extends EAObject {
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

    public Event() {}

    public Event(@Nullable ObjectId id, @Nullable Type type, @Nullable Long channel, @Nullable Long message, @Nullable Long controlPanel, @Nullable Cancellation cancellation, @Nullable Set<Review> reviews, @Nullable Boolean custom, @Nullable Date created, @Nullable String title, @Nullable Long host, @Nullable String description, @Nullable Set<Long> roles, @Nullable Set<PingRole> rolesNamed, @Nullable ObjectId server, @Nullable Media media, @Nullable Source source, @Nullable Mode mode, @Nullable String ip, @Nullable Set<Platform> platforms, @Nullable String platform, @Nullable String version, @Nullable String prize, @Nullable Integer maxPlayers, @Nullable Date time, @Nullable Set<Long> subscribers) {
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
        if (!(object instanceof final Event event)) return false;
        return id != null && id.equals(event.id);
    }

    @Override
    public int hashCode() {
        if (id == null) return super.hashCode();
        return id.hashCode();
    }

    @NotNull
    public static Event getExample() {
        return new Event(
                new ObjectId(),
                Type.PARTNER,
                ExampleUtility.Random.discordId(),
                ExampleUtility.Random.discordId(),
                ExampleUtility.Random.discordId(),
                Cancellation.getExample(),
                Set.of(Review.getExample()),
                false,
                new Date(),
                "Example Event",
                ExampleUtility.User.SRNYX_ID,
                "This is an example event description.",
                Set.of(ExampleUtility.Role.PARTNER_EVENTS_ID, ExampleUtility.Role.MONEY_EVENTS_ID),
                Set.of(PingRole.PARTNER, PingRole.MONEY),
                new ObjectId(),
                Media.getExample(),
                Source.DISCORD,
                Mode.TEXT,
                "play.eventalerts.gg",
                Set.of(Platform.JAVA),
                "Java",
                "1.21.11",
                "$10 USD",
                100,
                new Date(),
                Set.of(ExampleUtility.User.RAME_ID, ExampleUtility.User.REECE_ID));
    }

    public enum Mode {
        TEXT,
        IMAGE
    }

    public enum Type {
        COMMUNITY,
        PARTNER
    }

    public enum PingRole {
        COMMUNITY,
        PARTNER,
        BIG_MONEY,
        MONEY,
        FUN,
        HOUSING,
        CIVILIZATION
    }

    public enum Source {
        DISCORD,
        API
    }

    public enum Platform {
        JAVA,
        BEDROCK
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
            return new Review(
                    ExampleUtility.User.OIIINK_ID,
                    new Date(),
                    "This event was really fun and well organized! I look forward to future events.",
                    Map.of(
                            Category.ORGANIZATION, 5,
                            Category.COMMUNICATION, 5,
                            Category.ENJOYMENT, 4));
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
