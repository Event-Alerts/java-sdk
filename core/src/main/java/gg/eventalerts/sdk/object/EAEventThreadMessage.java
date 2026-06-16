package gg.eventalerts.sdk.object;

import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;


public class EAEventThreadMessage extends EAObject {
    @NotNull public static final String KEY_EVENT = "event";
    @NotNull public static final String KEY_CHANNEL = "channel";
    @NotNull public static final String KEY_AUTHOR = "author";
    @NotNull public static final String KEY_MESSAGE = "message";

    @SerializedName(KEY_EVENT) @Nullable public EAEvent event;
    @SerializedName(KEY_CHANNEL) @Nullable public Channel channel;
    @SerializedName(KEY_AUTHOR) @Nullable public Author author;
    @SerializedName(KEY_MESSAGE) @Nullable public EAEventThreadMessage.Message message;

    public EAEventThreadMessage() {}

    public EAEventThreadMessage(@Nullable EAEvent event, @Nullable Channel channel, @Nullable Author author, @Nullable EAEventThreadMessage.Message message) {
        this.event = event;
        this.channel = channel;
        this.author = author;
        this.message = message;
    }

    public EAEventThreadMessage(@NotNull EAEventThreadMessage eventThreadMessage) {
        this(eventThreadMessage.event, eventThreadMessage.channel, eventThreadMessage.author, eventThreadMessage.message);
    }

    public static class Channel extends EAObject{
        @NotNull public static final String KEY_ID = "id";
        @NotNull public static final String KEY_NAME = "name";

        @SerializedName(KEY_ID) @Nullable public Long id;
        @SerializedName(KEY_NAME) @Nullable public String name;

        public Channel() {}

        public Channel(@Nullable Long id, @Nullable String name) {
            this.id = id;
            this.name = name;
        }

        public Channel(@NotNull EAEventThreadMessage.Channel channel) {
            this(channel.id, channel.name);
        }
    }

    public static class Author extends EAObject {
        @NotNull public static final String KEY_ID = "id";
        @NotNull public static final String KEY_NAME = "name";
        @NotNull public static final String KEY_EFFECTIVE_NAME = "effectiveName";
        @NotNull public static final String KEY_PLAYER = "player";

        @SerializedName(KEY_ID) @Nullable public Long id;
        @SerializedName(KEY_NAME) @Nullable public String name;
        @SerializedName(KEY_EFFECTIVE_NAME) @Nullable public String effectiveName;
        @SerializedName(KEY_PLAYER) @Nullable public EAPlayer player;

        public Author() {}

        public Author(@Nullable Long id, @Nullable String name, @Nullable String effectiveName, @Nullable EAPlayer player) {
            this.id = id;
            this.name = name;
            this.effectiveName = effectiveName;
            this.player = player;
        }

        public Author(@NotNull EAEventThreadMessage.Author author) {
            this(author.id, author.name, author.effectiveName, author.player);
        }
    }

    public static class Message extends EAObject {
        @NotNull public static final String KEY_ID = "id";
        @NotNull public static final String KEY_CONTENT = "content";
        @NotNull public static final String KEY_ATTACHMENTS = "attachments";

        @SerializedName(KEY_ID) @Nullable public Long id;
        @SerializedName(KEY_CONTENT) @Nullable public Content content;
        @SerializedName(KEY_ATTACHMENTS) @Nullable public List<Attachment> attachments;

        public Message() {}

        public Message(@Nullable Long id, @Nullable Content content, @Nullable List<Attachment> attachments) {
            this.id = id;
            this.content = content;
            this.attachments = attachments;
        }

        public Message(@NotNull EAEventThreadMessage.Message message) {
            this(message.id, message.content, message.attachments);
        }

        public static class Content extends EAObject {
            @NotNull public static final String KEY_RAW = "raw";
            @NotNull public static final String KEY_DISPLAY = "display";
            @NotNull public static final String KEY_STRIPPED = "stripped";

            @SerializedName(KEY_RAW) @Nullable public String raw;
            @SerializedName(KEY_DISPLAY) @Nullable public String display;
            @SerializedName(KEY_STRIPPED) @Nullable public String stripped;

            public Content() {}

            public Content(@Nullable String raw, @Nullable String display, @Nullable String stripped) {
                this.raw = raw;
                this.display = display;
                this.stripped = stripped;
            }

            public Content(@NotNull EAEventThreadMessage.Message.Content content) {
                this(content.raw, content.display, content.stripped);
            }
        }

        public static class Attachment extends EAObject {
            @NotNull public static final String KEY_ID = "id";
            @NotNull public static final String KEY_NAME = "name";
            @NotNull public static final String KEY_URL = "url";
            @NotNull public static final String KEY_PROXY_URL = "proxyUrl";

            @SerializedName(KEY_ID) @Nullable public Long id;
            @SerializedName(KEY_NAME) @Nullable public String name;
            @SerializedName(KEY_URL) @Nullable public String url;
            @SerializedName(KEY_PROXY_URL) @Nullable public String proxyUrl;

            public Attachment() {}

            public Attachment(@Nullable Long id, @Nullable String name, @Nullable String url, @Nullable String proxyUrl) {
                this.id = id;
                this.name = name;
                this.url = url;
                this.proxyUrl = proxyUrl;
            }

            public Attachment(@NotNull EAEventThreadMessage.Message.Attachment attachment) {
                this(attachment.id, attachment.name, attachment.url, attachment.proxyUrl);
            }
        }
    }
}
