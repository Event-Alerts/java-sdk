package gg.eventalerts.sdk.websocket.handler.event;

import gg.eventalerts.sdk.object.Event;
import org.jetbrains.annotations.NotNull;


public abstract class EventCancelledEventHandler extends SocketEventHandler<Event> {
    @Override @NotNull
    public SocketEventName getName() {
        return SocketEventName.EVENT_CANCELLED;
    }

    @Override @NotNull
    public Class<Event> getDataType() {
        return Event.class;
    }
}
