package gg.eventalerts.sdk.websocket.handler.event;

import gg.eventalerts.sdk.object.Player;
import org.jetbrains.annotations.NotNull;


public abstract class LinkEventHandler extends SocketEventHandler<Player> {
    @Override @NotNull
    public SocketEventName getName() {
        return SocketEventName.LINK;
    }

    @Override @NotNull
    public Class<Player> getDataType() {
        return Player.class;
    }
}
