package gg.eventalerts.sdk.websocket.handler.event;

import gg.eventalerts.sdk.object.Event;
import org.jetbrains.annotations.NotNull;


public abstract class EventPostedEventHandler extends SocketEventHandler<Event> {
    @Override @NotNull
    public SocketEventName getName() {
        return SocketEventName.EVENT_POSTED;
    }

    @Override @NotNull
    public Class<Event> getDataType() {
        return Event.class;
    }
}
