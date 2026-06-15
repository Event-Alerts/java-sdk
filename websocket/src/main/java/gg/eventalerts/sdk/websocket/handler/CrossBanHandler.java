package gg.eventalerts.sdk.websocket.handler;

import gg.eventalerts.sdk.websocket.SocketEventName;
import gg.eventalerts.sdk.websocket.message.event.EACrossBanEvent;
import org.jetbrains.annotations.NotNull;


public abstract class CrossBanHandler extends SocketEventHandler<EACrossBanEvent> {
    @Override @NotNull
    public SocketEventName getName() {
        return SocketEventName.CROSS_BAN;
    }
}
