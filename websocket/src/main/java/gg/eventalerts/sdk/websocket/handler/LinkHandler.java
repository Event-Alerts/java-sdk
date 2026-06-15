package gg.eventalerts.sdk.websocket.handler;

import gg.eventalerts.sdk.websocket.SocketEventName;
import gg.eventalerts.sdk.websocket.message.event.EALinkEvent;
import org.jetbrains.annotations.NotNull;


public abstract class LinkHandler extends SocketEventHandler<EALinkEvent> {
    @Override @NotNull
    public SocketEventName getName() {
        return SocketEventName.LINK;
    }
}
