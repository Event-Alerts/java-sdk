package gg.eventalerts.sdk.websocket.handler.action;

import gg.eventalerts.sdk.websocket.object.action.PlayerConnectionAction;
import org.jetbrains.annotations.NotNull;


public class PlayerConnectionActionHandler extends SocketActionHandler {
    @Override @NotNull
    public SocketActionName getName() {
        return SocketActionName.PLAYER_CONNECTION;
    }

    @Override @NotNull
    public Class<PlayerConnectionAction> getDataType() {
        return PlayerConnectionAction.class;
    }
}
