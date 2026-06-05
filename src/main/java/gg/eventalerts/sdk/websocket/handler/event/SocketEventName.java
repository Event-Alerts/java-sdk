package gg.eventalerts.sdk.websocket.handler.event;

import gg.eventalerts.sdk.websocket.handler.SocketHandlerName;
import org.jetbrains.annotations.NotNull;


public enum SocketEventName implements SocketHandlerName<SocketEventHandler<?>> {
    BOOSTER_PASS_GIVEN(BoosterPassGivenEventHandler.class),
    CROSS_BAN(CrossBanEventHandler.class),
    EVENT_CANCELLED(EventCancelledEventHandler.class),
    EVENT_CHAT(EventChatEventHandler.class),
    EVENT_POSTED(EventPostedEventHandler.class),
    FAMOUS_EVENT_POSTED(FamousEventPostedEventHandler.class),
    LINK(LinkEventHandler.class),
    SERVER_EDITED(ServerEditedEventHandler.class),
    SERVER_ENABLED(ServerEnabledEventHandler.class);

    @NotNull public final Class<? extends SocketEventHandler<?>> eventClass;

    SocketEventName(@NotNull Class<? extends SocketEventHandler<?>> eventClass) {
        this.eventClass = eventClass;
    }

    @Override @NotNull
    public Class<SocketEventHandler<?>> getHandlerClass() {
        return (Class<SocketEventHandler<?>>) eventClass;
    }
}
