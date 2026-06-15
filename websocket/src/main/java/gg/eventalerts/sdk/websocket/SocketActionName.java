package gg.eventalerts.sdk.websocket;

import gg.eventalerts.sdk.object.EAObject;
import gg.eventalerts.sdk.websocket.message.action.EAPlayerConnectionAction;
import gg.eventalerts.sdk.websocket.message.action.EAUpdateSubscriptionAction;
import org.jetbrains.annotations.NotNull;


public enum SocketActionName implements SocketMessageName {
    UPDATE_SUBSCRIPTION(EAUpdateSubscriptionAction.class),
    PLAYER_CONNECTION(EAPlayerConnectionAction.class);

    @NotNull public final Class<? extends EAObject> objectType;

    SocketActionName(@NotNull Class<? extends EAObject> objectType) {
        this.objectType = objectType;
    }

    @Override @NotNull
    public Class<? extends EAObject> getObjectType() {
        return objectType;
    }
}
