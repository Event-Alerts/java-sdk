package gg.eventalerts.sdk.websocket.handler;

import gg.eventalerts.sdk.object.EAPartnerServer;
import gg.eventalerts.sdk.websocket.SocketEventName;
import org.jetbrains.annotations.NotNull;


public abstract class ServerEnabledHandler extends SocketEventHandler<EAPartnerServer> {
    @Override @NotNull
    public SocketEventName getName() {
        return SocketEventName.SERVER_ENABLED;
    }
}
