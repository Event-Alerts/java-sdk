package gg.eventalerts.sdk.websocket.handler;

import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import gg.eventalerts.sdk.json.GSONProvider;
import gg.eventalerts.sdk.object.EAObject;
import gg.eventalerts.sdk.websocket.SocketEventName;
import gg.eventalerts.sdk.websocket.message.event.SocketEvent;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;


public abstract class SocketHandler<O extends EAObject> {
    @NotNull
    public abstract SocketEventName getName();

    @NotNull
    public abstract Class<O> getObjectType();

    public boolean shouldSubscribe() {
        return true;
    }

    public abstract void onMessage(@NotNull SocketEvent<O> object);

    public final void onMessage(@NotNull JsonObject json) {
        final Type type = TypeToken.getParameterized(SocketEvent.class, getObjectType()).getType();
        final SocketEvent<O> event = GSONProvider.GSON.fromJson(json, type);
        if (event != null) onMessage(event);
    }
}
