package gg.eventalerts.sdk.websocket.message.event;

import gg.eventalerts.sdk.object.EAObject;
import gg.eventalerts.sdk.websocket.SocketEventName;
import gg.eventalerts.sdk.websocket.message.SocketMessage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class SocketEvent<T extends EAObject> extends SocketMessage<T> {
    @Nullable public SocketEventName event;
    @Nullable public Integer sequence;

    public SocketEvent() {}

    public SocketEvent(@NotNull SocketEventName event, int sequence, @NotNull T data) {
        super(data);
        this.event = event;
        this.sequence = sequence;
    }
}
