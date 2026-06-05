package gg.eventalerts.sdk.websocket.handler;

import gg.eventalerts.sdk.object.CrossBan;
import gg.eventalerts.sdk.websocket.SocketEventName;
import org.jetbrains.annotations.NotNull;


public abstract class CrossBanHandler extends SocketHandler<CrossBan> {
    @Override @NotNull
    public SocketEventName getName() {
        return SocketEventName.CROSS_BAN;
    }

    @Override @NotNull
    public Class<CrossBan> getObjectType() {
        return CrossBan.class;
    }
}
