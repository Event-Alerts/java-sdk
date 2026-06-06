package gg.eventalerts.sdk.websocket.handler;

import gg.eventalerts.sdk.object.EAEvent;
import gg.eventalerts.sdk.websocket.SocketEventName;
import org.jetbrains.annotations.NotNull;


public abstract class EventCancelledHandler extends SocketHandler<EAEvent> {
    @Override @NotNull
    public SocketEventName getName() {
        return SocketEventName.EVENT_CANCELLED;
    }

    @Override @NotNull
    public Class<EAEvent> getObjectClass() {
        return EAEvent.class;
    }
}
