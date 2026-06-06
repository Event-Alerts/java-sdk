package gg.eventalerts.sdk.websocket.handler;

import gg.eventalerts.sdk.object.EAPartnerServer;
import gg.eventalerts.sdk.websocket.SocketEventName;
import org.jetbrains.annotations.NotNull;


public abstract class ServerEnabledHandler extends SocketHandler<EAPartnerServer> {
    @Override @NotNull
    public SocketEventName getName() {
        return SocketEventName.SERVER_ENABLED;
    }

    @Override @NotNull
    public Class<EAPartnerServer> getObjectClass() {
        return EAPartnerServer.class;
    }
}
