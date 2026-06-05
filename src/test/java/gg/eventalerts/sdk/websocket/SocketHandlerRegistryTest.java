package gg.eventalerts.sdk.websocket;

import gg.eventalerts.sdk.websocket.handler.action.SocketActionName;
import gg.eventalerts.sdk.websocket.handler.action.UpdateSubscriptionActionHandler;
import gg.eventalerts.sdk.websocket.handler.event.BoosterPassGivenEventHandler;
import gg.eventalerts.sdk.websocket.handler.event.CrossBanEventHandler;
import gg.eventalerts.sdk.websocket.handler.event.EventCancelledEventHandler;
import gg.eventalerts.sdk.websocket.handler.event.EventChatEventHandler;
import gg.eventalerts.sdk.websocket.handler.event.EventPostedEventHandler;
import gg.eventalerts.sdk.websocket.handler.event.FamousEventPostedEventHandler;
import gg.eventalerts.sdk.websocket.handler.event.LinkEventHandler;
import gg.eventalerts.sdk.websocket.handler.event.ServerEditedEventHandler;
import gg.eventalerts.sdk.websocket.handler.event.ServerEnabledEventHandler;
import gg.eventalerts.sdk.websocket.handler.event.SocketEventName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;


class SocketHandlerRegistryTest {
    @Test
    void eventNamesExposeTheExpectedHandlerClasses() {
        final Map<SocketEventName, Class<?>> expected = Map.ofEntries(
                Map.entry(SocketEventName.BOOSTER_PASS_GIVEN, BoosterPassGivenEventHandler.class),
                Map.entry(SocketEventName.CROSS_BAN, CrossBanEventHandler.class),
                Map.entry(SocketEventName.EVENT_CANCELLED, EventCancelledEventHandler.class),
                Map.entry(SocketEventName.EVENT_CHAT, EventChatEventHandler.class),
                Map.entry(SocketEventName.EVENT_POSTED, EventPostedEventHandler.class),
                Map.entry(SocketEventName.FAMOUS_EVENT_POSTED, FamousEventPostedEventHandler.class),
                Map.entry(SocketEventName.LINK, LinkEventHandler.class));
        expected.forEach((name, handlerClass) -> assertEquals(handlerClass, name.getHandlerClass()));
        assertEquals(ServerEditedEventHandler.class, SocketEventName.SERVER_EDITED.getHandlerClass());
        assertEquals(ServerEnabledEventHandler.class, SocketEventName.SERVER_ENABLED.getHandlerClass());
    }

    @Test
    void actionNamesExposeTheExpectedHandlerClasses() {
        assertEquals(UpdateSubscriptionActionHandler.class, SocketActionName.UPDATE_SUBSCRIPTION.getHandlerClass());
    }
}
