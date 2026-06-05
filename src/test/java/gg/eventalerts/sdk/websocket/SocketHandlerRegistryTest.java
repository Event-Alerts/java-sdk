package gg.eventalerts.sdk.websocket;

import gg.eventalerts.sdk.websocket.handler.BoosterPassGivenHandler;
import gg.eventalerts.sdk.websocket.handler.CrossBanHandler;
import gg.eventalerts.sdk.websocket.handler.EventCancelledHandler;
import gg.eventalerts.sdk.websocket.handler.EventChatHandler;
import gg.eventalerts.sdk.websocket.handler.EventPostedHandler;
import gg.eventalerts.sdk.websocket.handler.FamousEventPostedHandler;
import gg.eventalerts.sdk.websocket.handler.LinkHandler;
import gg.eventalerts.sdk.websocket.handler.ServerEditedHandler;
import gg.eventalerts.sdk.websocket.handler.ServerEnabledHandler;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;


class SocketHandlerRegistryTest {
    @Test
    void eventNamesExposeTheExpectedHandlerClasses() {
        final Map<SocketEventName, Class<?>> expected = Map.ofEntries(
                Map.entry(SocketEventName.BOOSTER_PASS_GIVEN, BoosterPassGivenHandler.class),
                Map.entry(SocketEventName.CROSS_BAN, CrossBanHandler.class),
                Map.entry(SocketEventName.EVENT_CANCELLED, EventCancelledHandler.class),
                Map.entry(SocketEventName.EVENT_CHAT, EventChatHandler.class),
                Map.entry(SocketEventName.EVENT_POSTED, EventPostedHandler.class),
                Map.entry(SocketEventName.FAMOUS_EVENT_POSTED, FamousEventPostedHandler.class),
                Map.entry(SocketEventName.LINK, LinkHandler.class));
        expected.forEach((name, handlerClass) -> assertEquals(handlerClass, name.getHandlerClass()));
        assertEquals(ServerEditedHandler.class, SocketEventName.SERVER_EDITED.getHandlerClass());
        assertEquals(ServerEnabledHandler.class, SocketEventName.SERVER_ENABLED.getHandlerClass());
    }
}
