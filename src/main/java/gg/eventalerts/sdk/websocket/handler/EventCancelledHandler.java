package gg.eventalerts.sdk.websocket.handler;

import gg.eventalerts.sdk.object.Event;
import gg.eventalerts.sdk.websocket.SocketEventName;
import org.jetbrains.annotations.NotNull;


public abstract class EventCancelledHandler extends SocketHandler<Event> {
    @Override @NotNull
    public SocketEventName getName() {
        return SocketEventName.EVENT_CANCELLED;
    }

    @Override @NotNull
    public Class<Event> getObjectType() {
        return Event.class;
    }
}
