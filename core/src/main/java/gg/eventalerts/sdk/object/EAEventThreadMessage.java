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

    public EAEventThreadMessage(@NotNull EAEvent event, @NotNull Channel channel, @NotNull Author author, @NotNull EAEventThreadMessage.Message message) {
        this.event = event;
        this.channel = channel;
        this.author = author;
        this.message = message;
    }

    public static class Channel extends EAObject{
        @Nullable public Long id;
        @Nullable public String name;

        public Channel() {}

        public Channel(long id, @NotNull String name) {
            this.id = id;
            this.name = name;
        }
    }

    public static class Author extends EAObject {
        @Nullable public Long id;
        @Nullable public String name;
        @Nullable public String effectiveName;
        @Nullable public EAPlayer player;

        public Author() {}

        public Author(long id, @NotNull String name, @NotNull String effectiveName, @Nullable EAPlayer player) {
            this.id = id;
            this.name = name;
            this.effectiveName = effectiveName;
            this.player = player;
        }
    }

    public static class Message extends EAObject {
        @Nullable public Long id;
        @Nullable public Content content;
        @Nullable public List<Attachment> attachments;

        public Message() {}

        public Message(long id, @NotNull Content content, @NotNull List<Attachment> attachments) {
            this.id = id;
            this.content = content;
            this.attachments = attachments;
        }

        public static class Content extends EAObject {
            @Nullable public String raw;
            @Nullable public String display;
            @Nullable public String stripped;

            public Content() {}

            public Content(@NotNull String raw, @NotNull String display, @NotNull String stripped) {
                this.raw = raw;
                this.display = display;
                this.stripped = stripped;
            }
        }

        public static class Attachment extends EAObject {
            @Nullable public Long id;
            @Nullable public String name;
            @Nullable public String url;
            @Nullable public String proxyUrl;

            public Attachment() {}

            public Attachment(long id, @NotNull String name, @NotNull String url, @NotNull String proxyUrl) {
                this.id = id;
                this.name = name;
                this.url = url;
                this.proxyUrl = proxyUrl;
            }
        }
    }
}
