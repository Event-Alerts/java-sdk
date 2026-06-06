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
import xyz.srnyx.javautilities.MapGenerator;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;


class SocketHandlerRegistryTest {
    @Test
    void eventNamesExposeTheExpectedHandlerClasses() {
        final Map<SocketEventName, Class<?>> expected = MapGenerator.HASH_MAP.mapOf(
                SocketEventName.BOOSTER_PASS_GIVEN, BoosterPassGivenHandler.class,
                SocketEventName.CROSS_BAN, CrossBanHandler.class,
                SocketEventName.EVENT_CANCELLED, EventCancelledHandler.class,
                SocketEventName.EVENT_CHAT, EventChatHandler.class,
                SocketEventName.EVENT_POSTED, EventPostedHandler.class,
                SocketEventName.FAMOUS_EVENT_POSTED, FamousEventPostedHandler.class,
                SocketEventName.LINK, LinkHandler.class);
        expected.forEach((name, handlerClass) -> assertEquals(handlerClass, name.getHandlerClass()));
        assertEquals(ServerEditedHandler.class, SocketEventName.SERVER_EDITED.getHandlerClass());
        assertEquals(ServerEnabledHandler.class, SocketEventName.SERVER_ENABLED.getHandlerClass());
    }
}
