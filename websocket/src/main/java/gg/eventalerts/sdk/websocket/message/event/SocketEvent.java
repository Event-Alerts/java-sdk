package gg.eventalerts.sdk.websocket.message.event;

import gg.eventalerts.sdk.object.EAObject;
import gg.eventalerts.sdk.websocket.SocketEventName;
import gg.eventalerts.sdk.websocket.message.SocketMessage;
import org.jetbrains.annotations.Nullable;


public class SocketEvent<T extends EAObject> extends SocketMessage<T> {
    @Nullable public SocketEventName event;
    @Nullable public Long sequence;

    public SocketEvent() {}

    public SocketEvent(@Nullable SocketEventName event, @Nullable Long sequence, @Nullable T data) {
        super(data);
        this.event = event;
        this.sequence = sequence;
    }
}
