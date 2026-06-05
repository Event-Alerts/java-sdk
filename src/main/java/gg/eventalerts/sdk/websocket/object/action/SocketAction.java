package gg.eventalerts.sdk.websocket.object.action;

import gg.eventalerts.sdk.object.EAObject;
import gg.eventalerts.sdk.websocket.handler.action.SocketActionName;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class SocketAction<T extends EAObject> extends EAObject {
    @Nullable public SocketActionName action;
    @Nullable public T data;

    public SocketAction() {}

    public SocketAction(@NotNull SocketActionName action, @NotNull T data) {
        this.action = action;
        this.data = data;
    }
}
