package gg.eventalerts.sdk.websocket;

import gg.eventalerts.sdk.websocket.handler.*;
import org.jetbrains.annotations.NotNull;


public enum SocketEventName {
    BOOSTER_PASS_GIVEN(BoosterPassGivenHandler.class),
    CROSS_BAN(CrossBanHandler.class),
    EVENT_CANCELLED(EventCancelledHandler.class),
    EVENT_CHAT(EventChatHandler.class),
    EVENT_POSTED(EventPostedHandler.class),
    FAMOUS_EVENT_POSTED(FamousEventPostedHandler.class),
    LINK(LinkHandler.class),
    SERVER_EDITED(ServerEditedHandler.class),
    SERVER_ENABLED(ServerEnabledHandler.class);

    @NotNull public final Class<? extends SocketHandler<?>> eventClass;

    SocketEventName(@NotNull Class<? extends SocketHandler<?>> eventClass) {
        this.eventClass = eventClass;
    }

    @NotNull
    public Class<SocketHandler<?>> getHandlerClass() {
        return (Class<SocketHandler<?>>) eventClass;
    }
}
