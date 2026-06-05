package gg.eventalerts.sdk.websocket.handler;

import gg.eventalerts.sdk.object.EventThreadMessage;
import gg.eventalerts.sdk.websocket.SocketEventName;
import org.jetbrains.annotations.NotNull;


public abstract class EventChatHandler extends SocketHandler<EventThreadMessage> {
    @Override @NotNull
    public SocketEventName getName() {
        return SocketEventName.EVENT_CHAT;
    }

    @Override @NotNull
    public Class<EventThreadMessage> getObjectType() {
        return EventThreadMessage.class;
    }
}
