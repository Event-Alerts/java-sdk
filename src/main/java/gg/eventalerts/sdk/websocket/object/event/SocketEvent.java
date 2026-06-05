package gg.eventalerts.sdk.websocket.object.event;

import gg.eventalerts.sdk.object.EAObject;
import gg.eventalerts.sdk.websocket.handler.event.SocketEventName;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Date;


public class SocketEvent<T extends EAObject> extends EAObject {
    @Nullable public SocketEventName event;
    @Nullable public Integer messagesSent;
    @Nullable public Date timestamp;
    @Nullable public T data;

    public SocketEvent() {}

    public SocketEvent(@NotNull SocketEventName event, int messagesSent, @NotNull Date timestamp, @NotNull T data) {
        this.event = event;
        this.messagesSent = messagesSent;
        this.timestamp = timestamp;
        this.data = data;
    }
}
