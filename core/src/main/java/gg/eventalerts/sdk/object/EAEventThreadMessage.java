package gg.eventalerts.sdk.object;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;


public class EAEventThreadMessage extends EAObject {
    @Nullable public EAEvent event;
    @Nullable public Channel channel;
    @Nullable public Author author;
    @Nullable public EAEventThreadMessage.Message message;

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
        @Nullable public Long id;
        @Nullable public String name;

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
        @Nullable public Long id;
        @Nullable public String name;
        @Nullable public String effectiveName;
        @Nullable public EAPlayer player;

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
        @Nullable public Long id;
        @Nullable public Content content;
        @Nullable public List<Attachment> attachments;

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
            @Nullable public String raw;
            @Nullable public String display;
            @Nullable public String stripped;

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
            @Nullable public Long id;
            @Nullable public String name;
            @Nullable public String url;
            @Nullable public String proxyUrl;

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
