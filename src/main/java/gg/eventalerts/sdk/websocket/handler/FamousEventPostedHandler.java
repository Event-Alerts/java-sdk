package gg.eventalerts.sdk.websocket.handler;

import gg.eventalerts.sdk.object.EAFamousEvent;
import gg.eventalerts.sdk.websocket.SocketEventName;
import org.jetbrains.annotations.NotNull;


public abstract class FamousEventPostedHandler extends SocketHandler<EAFamousEvent> {
    @Override @NotNull
    public SocketEventName getName() {
        return SocketEventName.FAMOUS_EVENT_POSTED;
    }

    @Override @NotNull
    public Class<EAFamousEvent> getObjectClass() {
        return EAFamousEvent.class;
    }
}
