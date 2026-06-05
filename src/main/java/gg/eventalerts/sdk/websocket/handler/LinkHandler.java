package gg.eventalerts.sdk.websocket.handler;

import gg.eventalerts.sdk.object.Player;
import gg.eventalerts.sdk.websocket.SocketEventName;
import org.jetbrains.annotations.NotNull;


public abstract class LinkHandler extends SocketHandler<Player> {
    @Override @NotNull
    public SocketEventName getName() {
        return SocketEventName.LINK;
    }

    @Override @NotNull
    public Class<Player> getObjectType() {
        return Player.class;
    }
}
