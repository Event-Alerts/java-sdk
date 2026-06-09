package gg.eventalerts.sdk.websocket.handler;

import com.google.gson.JsonObject;
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
    public abstract Class<O> getObjectClass();

    public boolean shouldSubscribe() {
        return true;
    }

    public abstract void onMessage(@NotNull SocketEvent<O> object);

    public final void onMessage(@NotNull JsonObject json) {
        final Type type = GSONProvider.typeOf(SocketEvent.class, getObjectClass());
        final SocketEvent<O> event = GSONProvider.GSON.fromJson(json, type);
        if (event != null) onMessage(event);
    }
}
