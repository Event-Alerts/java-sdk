package gg.eventalerts.sdk.websocket.message.action;

import com.google.gson.annotations.SerializedName;
import gg.eventalerts.sdk.object.EAObject;
import gg.eventalerts.sdk.websocket.SocketActionName;
import gg.eventalerts.sdk.websocket.message.SocketMessage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class SocketAction<T extends EAObject> extends SocketMessage<T> {
    @NotNull public static final String KEY_ACTION = "action";

    @SerializedName(KEY_ACTION) @Nullable public SocketActionName action;

    public SocketAction() {}

    public SocketAction(@NotNull SocketActionName action, @NotNull T data) {
        super(data);
        this.action = action;
    }
}
