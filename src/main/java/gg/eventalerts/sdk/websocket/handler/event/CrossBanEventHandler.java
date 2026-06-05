package gg.eventalerts.sdk.websocket.handler.event;

import gg.eventalerts.sdk.object.CrossBan;
import org.jetbrains.annotations.NotNull;


public abstract class CrossBanEventHandler extends SocketEventHandler<CrossBan> {
    @Override @NotNull
    public SocketEventName getName() {
        return SocketEventName.CROSS_BAN;
    }

    @Override @NotNull
    public Class<CrossBan> getDataType() {
        return CrossBan.class;
    }
}
