package gg.eventalerts.sdk.websocket.message.event;

import com.google.gson.annotations.SerializedName;
import gg.eventalerts.sdk.object.EAObject;
import gg.eventalerts.sdk.websocket.SocketEventName;
import gg.eventalerts.sdk.websocket.message.SocketMessage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class SocketEvent<T extends EAObject> extends SocketMessage<T> {
    @NotNull public static final String KEY_EVENT = "event";
    @NotNull public static final String KEY_SEQUENCE = "sequence";

    @SerializedName(KEY_EVENT) @Nullable public SocketEventName event;
    @SerializedName(KEY_SEQUENCE) @Nullable public Long sequence;

    public SocketEvent() {}

    public SocketEvent(@Nullable SocketEventName event, @Nullable Long sequence, @Nullable T data) {
        super(data);
        this.event = event;
        this.sequence = sequence;
    }
}
