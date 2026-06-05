package gg.eventalerts.sdk.websocket.handler;

import gg.eventalerts.sdk.object.Event;
import gg.eventalerts.sdk.websocket.SocketEventName;
import org.jetbrains.annotations.NotNull;


public abstract class EventPostedHandler extends SocketHandler<Event> {
    @Override @NotNull
    public SocketEventName getName() {
        return SocketEventName.EVENT_POSTED;
    }

    @Override @NotNull
    public Class<Event> getObjectType() {
        return Event.class;
    }
}
