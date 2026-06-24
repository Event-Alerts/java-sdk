package gg.eventalerts.sdk.websocket.message;

import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import gg.eventalerts.sdk.object.EAObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.Date;


public class SocketMessage<T extends EAObject> extends EAObject {
    @NotNull public static final String KEY_TIMESTAMP = "timestamp";
    @NotNull public static final String KEY_DATA = "data";

    @SerializedName(KEY_TIMESTAMP) @Nullable public Date timestamp;
    @SerializedName(KEY_DATA) @Nullable public T data;

    public SocketMessage() {}

    public SocketMessage(@Nullable T data) {
        this.timestamp = new Date();
        this.data = data;
    }

    @Override @NotNull
    public Type getType() {
        return data != null ? TypeToken.getParameterized(this.getClass(), data.getClass()).getType() : super.getType();
    }
}
