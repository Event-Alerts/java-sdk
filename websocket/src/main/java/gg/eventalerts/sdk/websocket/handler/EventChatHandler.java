package gg.eventalerts.sdk.websocket.handler;

import gg.eventalerts.sdk.object.EAEventThreadMessage;
import gg.eventalerts.sdk.websocket.SocketEventName;
import org.jetbrains.annotations.NotNull;


public abstract class EventChatHandler extends SocketEventHandler<EAEventThreadMessage> {
    @Override @NotNull
    public SocketEventName getName() {
        return SocketEventName.EVENT_CHAT;
    }
}
