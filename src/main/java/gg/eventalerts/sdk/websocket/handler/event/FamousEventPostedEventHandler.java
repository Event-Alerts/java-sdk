package gg.eventalerts.sdk.websocket.handler.event;

import gg.eventalerts.sdk.object.FamousEvent;
import org.jetbrains.annotations.NotNull;


public abstract class FamousEventPostedEventHandler extends SocketEventHandler<FamousEvent> {
    @Override @NotNull
    public SocketEventName getName() {
        return SocketEventName.FAMOUS_EVENT_POSTED;
    }

    @Override @NotNull
    public Class<FamousEvent> getDataType() {
        return FamousEvent.class;
    }
}
