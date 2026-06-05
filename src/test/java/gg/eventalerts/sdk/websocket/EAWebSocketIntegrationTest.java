package gg.eventalerts.sdk.websocket;

import gg.eventalerts.sdk.object.Event;
import gg.eventalerts.sdk.object.EventThreadMessage;
import gg.eventalerts.sdk.websocket.handler.EventChatHandler;
import gg.eventalerts.sdk.websocket.handler.EventPostedHandler;
import gg.eventalerts.sdk.websocket.handler.SocketHandler;
import gg.eventalerts.sdk.websocket.message.action.PlayerConnectionAction;
import gg.eventalerts.sdk.websocket.message.action.UpdateSubscriptionAction;
import gg.eventalerts.sdk.websocket.message.event.SocketEvent;
import gg.eventalerts.sdk.websocket.support.WebSocketFixtureServer;
import org.java_websocket.handshake.ClientHandshake;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.net.InetSocketAddress;
import java.net.URI;
import java.util.Date;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;


class EAWebSocketIntegrationTest {
    private static final int PORT = 2020;
    private static final URI SOCKET_URI = URI.create("ws://localhost:" + PORT + "/api/v1/socket");

    private static WebSocketFixtureServer server;

    @BeforeAll
    static void startServer() throws Exception {
        server = new WebSocketFixtureServer(new InetSocketAddress("localhost", PORT));
        server.start();
        assertTrue(server.started().await(5, TimeUnit.SECONDS), "Websocket fixture did not start");
    }

    @BeforeEach
    void resetServer() {
        server.reset();
    }

    @AfterAll
    static void stopServer() throws Exception {
        if (server != null) server.stop(1_000);
    }

    @Test
    @Timeout(10)
    void connectsAndDispatchesTypedSocketEvent() throws Exception {
        final CapturingHandler handler = new CapturingHandler();
        final EAWebSocket socket = newSocket(handler);

        assertTrue(socket.connectBlocking(5, TimeUnit.SECONDS));
        assertTrue(server.awaitMessageCount(1, 5, TimeUnit.SECONDS));
        assertTrue(handler.received.await(5, TimeUnit.SECONDS));

        final UpdateSubscriptionAction subscription = server.parseAction(0).data;
        assertNotNull(subscription);
        assertEquals(Set.of(SocketEventName.EVENT_POSTED), subscription.getSubscribe());
        assertEquals(Set.of(), subscription.getUnsubscribe());

        final SocketEvent<Event> event = handler.captured.get();
        assertNotNull(event);
        assertEquals(SocketEventName.EVENT_POSTED, event.event);
        assertEquals(Integer.valueOf(3), event.sequence);
        assertNotNull(event.timestamp);
        assertNotNull(event.data);
        assertEquals(server.sentEvent().id, event.data.id);
        assertEquals(server.sentEvent().title, event.data.title);

        socket.closeBlocking();
    }

    @Test
    @Timeout(10)
    void connectsWithHeadersAndFiltersSubscriptions() throws Exception {
        final CapturingHandler enabledHandler = new CapturingHandler();
        final NeverSubscribesHandler disabledHandler = new NeverSubscribesHandler();
        final EAWebSocket socket = new EAWebSocket.Builder(SOCKET_URI, "gg.eventalerts.sdk-test/1.0")
                .retry(false)
                .bearerToken("bearer-123")
                .playerKey("player-456")
                .serverKey("server-789")
                .handler(enabledHandler, disabledHandler)
                .build();

        assertTrue(socket.connectBlocking(5, TimeUnit.SECONDS));
        assertTrue(server.awaitMessageCount(1, 5, TimeUnit.SECONDS));

        final ClientHandshake handshake = server.handshake();
        assertNotNull(handshake);
        assertEquals("/api/v1/socket", handshake.getResourceDescriptor());
        assertEquals("gg.eventalerts.sdk-test/1.0", handshake.getFieldValue("User-Agent"));
        assertEquals("Bearer bearer-123", handshake.getFieldValue("Authorization"));
        assertEquals("player-456", handshake.getFieldValue("X-Player-Key"));
        assertEquals("server-789", handshake.getFieldValue("X-Server-Key"));
        assertNull(server.failure());

        final UpdateSubscriptionAction subscription = server.parseAction(0).data;
        assertNotNull(subscription);
        assertEquals(Set.of(SocketEventName.EVENT_POSTED), subscription.getSubscribe());
        assertEquals(Set.of(SocketEventName.EVENT_CHAT), subscription.getUnsubscribe());

        socket.closeBlocking();
    }

    @Test
    @Timeout(10)
    void subscribeAndUnsubscribeSendExplicitActionMessages() throws Exception {
        final EAWebSocket socket = newSocket();

        assertTrue(socket.connectBlocking(5, TimeUnit.SECONDS));
        assertTrue(server.awaitMessageCount(1, 5, TimeUnit.SECONDS));

        socket.subscribe(SocketEventName.LINK);
        socket.unsubscribe(SocketEventName.EVENT_CANCELLED);

        assertTrue(server.awaitMessageCount(3, 5, TimeUnit.SECONDS));

        final UpdateSubscriptionAction initial = server.parseAction(0).data;
        final UpdateSubscriptionAction subscribe = server.parseAction(1).data;
        final UpdateSubscriptionAction unsubscribe = server.parseAction(2).data;

        assertNotNull(initial);
        assertEquals(Set.of(), initial.getSubscribe());
        assertEquals(Set.of(), initial.getUnsubscribe());

        assertNotNull(subscribe);
        assertEquals(Set.of(SocketEventName.LINK), subscribe.getSubscribe());
        assertEquals(Set.of(), subscribe.getUnsubscribe());

        assertNotNull(unsubscribe);
        assertEquals(Set.of(), unsubscribe.getSubscribe());
        assertEquals(Set.of(SocketEventName.EVENT_CANCELLED), unsubscribe.getUnsubscribe());

        socket.closeBlocking();
    }

    @Test
    @Timeout(10)
    void sendsPlayerConnectionActionPayload() throws Exception {
        final EAWebSocket socket = new EAWebSocket.Builder(SOCKET_URI, "gg.eventalerts.sdk-test/1.0")
                .retry(false)
                .build();

        assertTrue(socket.connectBlocking(5, TimeUnit.SECONDS));
        assertTrue(server.awaitMessageCount(1, 5, TimeUnit.SECONDS));

        final PlayerConnectionAction action = new PlayerConnectionAction(
                UUID.fromString("aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee"),
                "tester",
                new Date(1_700_000_000_123L),
                PlayerConnectionAction.Type.JOIN);
        socket.send(SocketActionName.PLAYER_CONNECTION, action);

        assertTrue(server.awaitMessageCount(2, 5, TimeUnit.SECONDS));

        final PlayerConnectionAction sent = server.parsePlayerConnectionAction(1).data;
        assertNotNull(sent);
        assertEquals(action.uuid, sent.uuid);
        assertEquals(action.username, sent.username);
        assertEquals(action.timestamp, sent.timestamp);
        assertEquals(action.type, sent.type);

        socket.closeBlocking();
    }

    private static EAWebSocket newSocket(SocketHandler<?>... handlers) {
        return new EAWebSocket.Builder(SOCKET_URI, "gg.eventalerts.sdk-test/1.0")
                .retry(false)
                .handler(handlers)
                .build();
    }

    private static final class CapturingHandler extends EventPostedHandler {
        private final CountDownLatch received = new CountDownLatch(1);
        private final AtomicReference<SocketEvent<Event>> captured = new AtomicReference<>();

        @Override
        public void onMessage(@NonNull SocketEvent<Event> object) {
            captured.set(object);
            received.countDown();
        }
    }

    private static final class NeverSubscribesHandler extends EventChatHandler {
        @Override
        public boolean shouldSubscribe() {
            return false;
        }

        @Override
        public void onMessage(@NonNull SocketEvent<EventThreadMessage> object) {}
    }
}
