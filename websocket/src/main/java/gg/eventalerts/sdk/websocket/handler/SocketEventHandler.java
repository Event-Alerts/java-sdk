package gg.eventalerts.sdk.websocket.handler;

import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import gg.eventalerts.sdk.json.GSONProvider;
import gg.eventalerts.sdk.object.EAObject;
import gg.eventalerts.sdk.websocket.SocketEventName;
import gg.eventalerts.sdk.websocket.message.event.SocketEvent;
import org.jetbrains.annotations.NotNull;


public abstract class SocketEventHandler<O extends EAObject> {
    @NotNull
    public abstract SocketEventName getName();

    public boolean shouldSubscribe() {
        return true;
    }

    public abstract void onMessage(@NotNull SocketEvent<O> object);

    public final void onMessage(@NotNull JsonObject json) {
        final SocketEvent<O> event = GSONProvider.GSON.fromJson(json, TypeToken.getParameterized(SocketEvent.class, getName().getObjectType()).getType());
        if (event != null) onMessage(event);
    }
}
