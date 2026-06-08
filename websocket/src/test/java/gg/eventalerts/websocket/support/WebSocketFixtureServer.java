package gg.eventalerts.websocket.support;

import gg.eventalerts.sdk.json.GSONProvider;
import gg.eventalerts.sdk.object.EAEvent;
import gg.eventalerts.sdk.websocket.SocketEventName;
import gg.eventalerts.sdk.websocket.message.action.EAPlayerConnectionAction;
import gg.eventalerts.sdk.websocket.message.action.EAUpdateSubscriptionAction;
import gg.eventalerts.sdk.websocket.message.action.SocketAction;
import gg.eventalerts.sdk.websocket.message.event.SocketEvent;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.jetbrains.annotations.NotNull;

import java.net.InetSocketAddress;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;


public final class WebSocketFixtureServer extends WebSocketServer {
    private final CountDownLatch started = new CountDownLatch(1);
    private final List<String> messages = new CopyOnWriteArrayList<>();
    private final AtomicReference<ClientHandshake> handshake = new AtomicReference<>();
    private final AtomicReference<Throwable> failure = new AtomicReference<>();
    private final AtomicBoolean eventSent = new AtomicBoolean(false);
    private final AtomicReference<EAEvent> sentEvent = new AtomicReference<>();

    private final Type actionType = JsonRoundTripSupport.typeOf(SocketAction.class, EAUpdateSubscriptionAction.class);
    private final Type playerConnectionActionType = JsonRoundTripSupport.typeOf(SocketAction.class, EAPlayerConnectionAction.class);
    private final Type eventType = JsonRoundTripSupport.typeOf(SocketEvent.class, EAEvent.class);

    public WebSocketFixtureServer(InetSocketAddress address) {
        super(address);
    }

    public void reset() {
        messages.clear();
        handshake.set(null);
        failure.set(null);
        eventSent.set(false);
        sentEvent.set(null);
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        this.handshake.set(handshake);
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {}

    @Override
    public void onError(WebSocket conn, Exception ex) {
        failure.compareAndSet(null, ex);
    }

    @Override
    public void onStart() {
        started.countDown();
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        messages.add(message);

        if (eventSent.compareAndSet(false, true)) {
            final EAEvent event = EAEvent.getExample();
            sentEvent.set(event);

            final SocketEvent<EAEvent> envelope = new SocketEvent<>();
            envelope.event = SocketEventName.EVENT_POSTED;
            envelope.sequence = 3;
            envelope.timestamp = new Date(1_700_000_000_123L);
            envelope.data = event;

            conn.send(GSONProvider.GSON.toJson(envelope, eventType));
        }
    }

    public CountDownLatch started() {
        return started;
    }

    public ClientHandshake handshake() {
        return handshake.get();
    }

    public Throwable failure() {
        return failure.get();
    }

    public boolean awaitMessageCount(int expected, long timeout, @NotNull TimeUnit unit) throws InterruptedException {
        final long deadline = System.nanoTime() + unit.toNanos(timeout);
        while (System.nanoTime() < deadline) {
            if (messages.size() >= expected) return true;
            Thread.sleep(10);
        }
        return messages.size() >= expected;
    }

    public SocketAction<EAUpdateSubscriptionAction> parseAction(int index) {
        return GSONProvider.GSON.fromJson(messages.get(index), actionType);
    }

    public SocketAction<EAPlayerConnectionAction> parsePlayerConnectionAction(int index) {
        return GSONProvider.GSON.fromJson(messages.get(index), playerConnectionActionType);
    }

    public EAEvent sentEvent() {
        return sentEvent.get();
    }
}
