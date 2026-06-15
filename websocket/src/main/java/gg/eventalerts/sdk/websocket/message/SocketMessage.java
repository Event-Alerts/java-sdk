package gg.eventalerts.sdk.websocket.message;

import gg.eventalerts.sdk.json.GSONProvider;
import gg.eventalerts.sdk.object.EAObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.Date;


public class SocketMessage<T extends EAObject> extends EAObject {
    @Nullable public Date timestamp;
    @Nullable public T data;

    public SocketMessage() {}

    public SocketMessage(@Nullable T data) {
        this.timestamp = new Date();
        this.data = data;
    }

    @Override @NotNull
    public Type getType() {
        return data != null ? GSONProvider.typeOf(this.getClass(), data.getClass()) : super.getType();
    }
}
