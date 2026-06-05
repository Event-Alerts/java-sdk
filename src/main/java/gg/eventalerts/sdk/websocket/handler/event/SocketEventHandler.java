package gg.eventalerts.sdk.websocket.handler.event;

import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import gg.eventalerts.sdk.json.GSONProvider;
import gg.eventalerts.sdk.object.EAObject;
import gg.eventalerts.sdk.websocket.handler.SocketHandler;
import gg.eventalerts.sdk.websocket.object.event.SocketEvent;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;


public abstract class SocketEventHandler<T extends EAObject> implements SocketHandler<SocketEventName> {
    @Override @NotNull
    public abstract SocketEventName getName();

    public boolean shouldSubscribe() {
        return true;
    }

    public abstract void onMessage(@NotNull SocketEvent<T> object);

    public final void onMessage(@NotNull JsonObject json) {
        final Type type = TypeToken.getParameterized(SocketEvent.class, getDataType()).getType();
        final SocketEvent<T> event = GSONProvider.GSON.fromJson(json, type);
        if (event != null) onMessage(event);
    }
}
