package gg.eventalerts.sdk.websocket.handler;

import gg.eventalerts.sdk.object.FamousEvent;
import gg.eventalerts.sdk.websocket.SocketEventName;
import org.jetbrains.annotations.NotNull;


public abstract class FamousEventPostedHandler extends SocketHandler<FamousEvent> {
    @Override @NotNull
    public SocketEventName getName() {
        return SocketEventName.FAMOUS_EVENT_POSTED;
    }

    @Override @NotNull
    public Class<FamousEvent> getObjectType() {
        return FamousEvent.class;
    }
}
