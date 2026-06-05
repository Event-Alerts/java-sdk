package gg.eventalerts.sdk.websocket.handler.event;

import gg.eventalerts.sdk.object.PartnerServer;
import org.jetbrains.annotations.NotNull;


public abstract class ServerEditedEventHandler extends SocketEventHandler<PartnerServer> {
    @Override @NotNull
    public SocketEventName getName() {
        return SocketEventName.SERVER_EDITED;
    }

    @Override @NotNull
    public Class<PartnerServer> getDataType() {
        return PartnerServer.class;
    }
}
