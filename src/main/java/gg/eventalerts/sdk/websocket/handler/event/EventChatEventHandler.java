package gg.eventalerts.sdk.websocket.handler.event;

import gg.eventalerts.sdk.object.EventThreadMessage;
import org.jetbrains.annotations.NotNull;


public abstract class EventChatEventHandler extends SocketEventHandler<EventThreadMessage> {
    @Override @NotNull
    public SocketEventName getName() {
        return SocketEventName.EVENT_CHAT;
    }

    @Override @NotNull
    public Class<EventThreadMessage> getDataType() {
        return EventThreadMessage.class;
    }
}
