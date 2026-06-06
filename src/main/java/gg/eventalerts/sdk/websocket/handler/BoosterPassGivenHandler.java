package gg.eventalerts.sdk.websocket.handler;

import gg.eventalerts.sdk.object.EAPlayer;
import gg.eventalerts.sdk.websocket.SocketEventName;
import org.jetbrains.annotations.NotNull;


public abstract class BoosterPassGivenHandler extends SocketHandler<EAPlayer> {
    @Override @NotNull
    public SocketEventName getName() {
        return SocketEventName.BOOSTER_PASS_GIVEN;
    }

    @Override @NotNull
    public Class<EAPlayer> getObjectClass() {
        return EAPlayer.class;
    }
}
