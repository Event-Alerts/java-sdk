package gg.eventalerts.sdk.websocket.handler;

import gg.eventalerts.sdk.object.PartnerServer;
import gg.eventalerts.sdk.websocket.SocketEventName;
import org.jetbrains.annotations.NotNull;


public abstract class ServerEditedHandler extends SocketHandler<PartnerServer> {
    @Override @NotNull
    public SocketEventName getName() {
        return SocketEventName.SERVER_EDITED;
    }

    @Override @NotNull
    public Class<PartnerServer> getObjectType() {
        return PartnerServer.class;
    }
}
