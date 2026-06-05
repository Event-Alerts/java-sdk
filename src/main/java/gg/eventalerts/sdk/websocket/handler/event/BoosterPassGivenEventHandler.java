package gg.eventalerts.sdk.websocket.handler.event;

import gg.eventalerts.sdk.object.Player;
import org.jetbrains.annotations.NotNull;


public abstract class BoosterPassGivenEventHandler extends SocketEventHandler<Player> {
    @Override @NotNull
    public SocketEventName getName() {
        return SocketEventName.BOOSTER_PASS_GIVEN;
    }

    @Override @NotNull
    public Class<Player> getDataType() {
        return Player.class;
    }
}
