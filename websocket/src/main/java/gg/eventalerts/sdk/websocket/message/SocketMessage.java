package gg.eventalerts.sdk.websocket.message;

import gg.eventalerts.sdk.object.EAObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Date;


public class SocketMessage<T extends EAObject> extends EAObject {
    @Nullable public Date timestamp;
    @Nullable public T data;

    public SocketMessage() {}

    public SocketMessage(@NotNull T data) {
        this.timestamp = new Date();
        this.data = data;
    }
}
