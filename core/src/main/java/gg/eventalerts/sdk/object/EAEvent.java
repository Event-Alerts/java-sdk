package gg.eventalerts.sdk.object;

import com.google.gson.annotations.SerializedName;
import gg.eventalerts.sdk.ExampleUtility;
import org.bson.types.ObjectId;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;


public class EAEvent extends EAObject {
    @NotNull public static final String KEY_ID = "id";
    @NotNull public static final String KEY_TYPE = "type";
    @NotNull public static final String KEY_CHANNEL = "channel";
    @NotNull public static final String KEY_MESSAGE = "message";
    @NotNull public static final String KEY_CONTROL_PANEL = "controlPanel";
    @NotNull public static final String KEY_CANCELLATION = "cancellation";
    @NotNull public static final String KEY_REVIEWS = "reviews";
    @NotNull public static final String KEY_CUSTOM = "custom";
    @NotNull public static final String KEY_CREATED = "created";
    @NotNull public static final String KEY_TITLE = "title";
    @NotNull public static final String KEY_HOST = "host";
    @NotNull public static final String KEY_DESCRIPTION = "description";
    @NotNull public static final String KEY_ROLES = "roles";
    @NotNull public static final String KEY_ROLES_NAMED = "rolesNamed";
    @NotNull public static final String KEY_SERVER = "server";
    @NotNull public static final String KEY_MEDIA = "media";
    @NotNull public static final String KEY_SOURCE = "source";
    @NotNull public static final String KEY_MODE = "mode";
    @NotNull public static final String KEY_IP = "ip";
    @NotNull public static final String KEY_PLATFORMS = "platforms";
    @NotNull public static final String KEY_PLATFORM = "platform";
    @NotNull public static final String KEY_VERSION = "version";
    @NotNull public static final String KEY_PRIZE = "prize";
    @NotNull public static final String KEY_MAX_PLAYERS = "maxPlayers";
    @NotNull public static final String KEY_TIME = "time";
    @NotNull public static final String KEY_SUBSCRIBERS = "subscribers";

    // BUILDER/CUSTOM
    @SerializedName(KEY_ID) @Nullable public ObjectId id;
    @SerializedName(KEY_TYPE) @Nullable public Type type;
    @SerializedName(KEY_CHANNEL) @Nullable public Long channel;
    @SerializedName(KEY_MESSAGE) @Nullable public Long message;
    @SerializedName(KEY_CONTROL_PANEL) @Nullable public Long controlPanel;
    @SerializedName(KEY_CANCELLATION) @Nullable public Cancellation cancellation;
    @SerializedName(KEY_REVIEWS) @Nullable public Set<Review> reviews;
    @SerializedName(KEY_CUSTOM) @Nullable public Boolean custom;
    @SerializedName(KEY_CREATED) @Nullable public Date created;
    @SerializedName(KEY_TITLE) @Nullable public String title;
    @SerializedName(KEY_HOST) @Nullable public Long host;
    @SerializedName(KEY_DESCRIPTION) @Nullable public String description;
    @SerializedName(KEY_ROLES) @Nullable public Set<Long> roles;
    @SerializedName(KEY_ROLES_NAMED) @Nullable public Set<PingRole> rolesNamed;
    @SerializedName(KEY_SERVER) @Nullable public ObjectId server;
    @SerializedName(KEY_MEDIA) @Nullable public Media media;
    @SerializedName(KEY_SOURCE) @Nullable public Source source;

    // BUILDER
    @SerializedName(KEY_MODE) @Nullable public Mode mode;
    @SerializedName(KEY_IP) @Nullable public String ip;
    @SerializedName(KEY_PLATFORMS) @Nullable public Set<Platform> platforms;
    @SerializedName(KEY_PLATFORM) @Nullable public String platform;
    @SerializedName(KEY_VERSION) @Nullable public String version;
    @SerializedName(KEY_PRIZE) @Nullable public String prize;
    @SerializedName(KEY_MAX_PLAYERS) @Nullable public Integer maxPlayers;
    /**
     * The time the event starts
     */
    @SerializedName(KEY_TIME) @Nullable public Date time;
    @SerializedName(KEY_SUBSCRIBERS) @Nullable public Set<Long> subscribers;

    public EAEvent() {}

    public EAEvent(@Nullable ObjectId id, @Nullable Type type, @Nullable Long channel, @Nullable Long message, @Nullable Long controlPanel, @Nullable Cancellation cancellation, @Nullable Collection<Review> reviews, @Nullable Boolean custom, @Nullable Date created, @Nullable String title, @Nullable Long host, @Nullable String description, @Nullable Collection<Long> roles, @Nullable Collection<PingRole> rolesNamed, @Nullable ObjectId server, @Nullable Media media, @Nullable Source source, @Nullable Mode mode, @Nullable String ip, @Nullable Collection<Platform> platforms, @Nullable String platform, @Nullable String version, @Nullable String prize, @Nullable Integer maxPlayers, @Nullable Date time, @Nullable Collection<Long> subscribers) {
        this.id = id;
        this.type = type;
        this.channel = channel;
        this.message = message;
        this.controlPanel = controlPanel;
        this.cancellation = cancellation;
        this.reviews = reviews == null ? null : new HashSet<>(reviews);
        this.custom = custom;
        this.created = created;
        this.title = title;
        this.host = host;
        this.description = description;
        this.roles = roles == null ? null : new HashSet<>(roles);
        this.rolesNamed = rolesNamed == null ? null : new HashSet<>(rolesNamed);
        this.server = server;
        this.media = media;
        this.source = source;
        this.mode = mode;
        this.ip = ip;
        this.platforms = platforms == null ? null : new HashSet<>(platforms);
        this.platform = platform;
        this.version = version;
        this.prize = prize;
        this.maxPlayers = maxPlayers;
        this.time = time;
        this.subscribers = subscribers == null ? null : new HashSet<>(subscribers);
    }

    public EAEvent(@NotNull EAEvent event) {
        this(event.id, event.type, event.channel, event.message, event.controlPanel, event.cancellation, event.reviews, event.custom, event.created, event.title, event.host, event.description, event.roles, event.rolesNamed, event.server, event.media, event.source, event.mode, event.ip, event.platforms, event.platform, event.version, event.prize, event.maxPlayers, event.time, event.subscribers);
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
                Arrays.asList(ExampleUtility.Role.PARTNER_EVENTS_ID, ExampleUtility.Role.MONEY_EVENTS_ID),
                Arrays.asList(PingRole.PARTNER, PingRole.MONEY),
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
                Arrays.asList(ExampleUtility.User.RAME_ID, ExampleUtility.User.REECE_ID));
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
        /**
         * Whether this role can be toggled by Partners for their events
         */
        public final boolean partnerToggleable;

        PingRole(@NotNull String displayName, boolean partnerToggleable) {
            this.displayName = displayName;
            this.partnerToggleable = partnerToggleable;
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
        @NotNull public static final String KEY_TIMESTAMP = "timestamp";
        @NotNull public static final String KEY_USER = "user";
        @NotNull public static final String KEY_REASON = "reason";

        @SerializedName(KEY_TIMESTAMP) @Nullable public Date timestamp;
        @SerializedName(KEY_USER) @Nullable public Long user;
        @SerializedName(KEY_REASON) @Nullable public String reason;

        public Cancellation() {}

        public Cancellation(@Nullable Date timestamp, @Nullable Long user, @Nullable String reason) {
            this.timestamp = timestamp;
            this.user = user;
            this.reason = reason;
        }

        public Cancellation(@NotNull EAEvent.Cancellation cancellation) {
            this(cancellation.timestamp, cancellation.user, cancellation.reason);
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
        @NotNull public static final String KEY_REVIEWER = "reviewer";
        @NotNull public static final String KEY_TIMESTAMP = "timestamp";
        @NotNull public static final String KEY_COMMENTS = "comments";
        @NotNull public static final String KEY_CATEGORIES = "categories";

        @SerializedName(KEY_REVIEWER) @Nullable public Long reviewer;
        @SerializedName(KEY_TIMESTAMP) @Nullable public Date timestamp;
        @SerializedName(KEY_COMMENTS) @Nullable public String comments;
        @SerializedName(KEY_CATEGORIES) @Nullable public Map<Category, Integer> categories;

        public Review() {}

        public Review(@Nullable Long reviewer, @Nullable Date timestamp, @Nullable String comments, @Nullable Map<Category, Integer> categories) {
            this.reviewer = reviewer;
            this.timestamp = timestamp;
            this.comments = comments;
            this.categories = categories;
        }

        public Review(@NotNull EAEvent.Review review) {
            this(review.reviewer, review.timestamp, review.comments, review.categories);
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
        @NotNull public static final String KEY_URL = "url";
        @NotNull public static final String KEY_NAME = "name";

        @SerializedName(KEY_URL) @Nullable public String url;
        @SerializedName(KEY_NAME) @Nullable public String name;

        public Media() {}

        public Media(@Nullable String url, @Nullable String name) {
            this.url = url;
            this.name = name;
        }

        public Media(@NotNull EAEvent.Media media) {
            this(media.url, media.name);
        }

        @NotNull
        public static Media getExample() {
            return new Media(
                    "https://us-east-1.tixte.net/uploads/img.venox.network/bee.png",
                    "bee.png");
        }
    }
}
