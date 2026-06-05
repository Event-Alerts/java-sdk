package gg.eventalerts.sdk.websocket.handler.action;

import gg.eventalerts.sdk.websocket.handler.SocketHandlerName;
import org.jetbrains.annotations.NotNull;


public enum SocketActionName implements SocketHandlerName<SocketActionHandler> {
    UPDATE_SUBSCRIPTION(UpdateSubscriptionActionHandler.class),
    PLAYER_CONNECTION(PlayerConnectionActionHandler.class);

    @NotNull private final Class<? extends SocketActionHandler> actionClass;

    SocketActionName(@NotNull Class<? extends SocketActionHandler> actionClass) {
        this.actionClass = actionClass;
    }

    @Override @NotNull
    public Class<SocketActionHandler> getHandlerClass() {
        return (Class<SocketActionHandler>) actionClass;
    }
}
