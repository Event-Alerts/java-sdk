package gg.eventalerts.sdk.websocket;

import com.google.gson.JsonObject;
import gg.eventalerts.sdk.EventAlertsSDK;
import gg.eventalerts.sdk.json.GSONProvider;
import gg.eventalerts.sdk.object.EAObject;
import gg.eventalerts.sdk.websocket.message.action.SocketAction;
import gg.eventalerts.sdk.websocket.message.action.EAUpdateSubscriptionAction;
import gg.eventalerts.sdk.websocket.handler.SocketHandler;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.framing.CloseFrame;
import org.java_websocket.handshake.ServerHandshake;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;


public class EAWebSocket extends WebSocketClient {
    @NotNull public static final Logger LOGGER = LoggerFactory.getLogger(EAWebSocket.class);
    @NotNull private static final ScheduledExecutorService RETRY_SCHEDULER = Executors.newSingleThreadScheduledExecutor(runnable -> {
        final Thread thread = new Thread(runnable, "EAWebSocket-Retry");
        thread.setDaemon(true);
        return thread;
    });

    // Handlers
    @NotNull private final Map<SocketEventName, Set<SocketHandler<?>>> handlers = new HashMap<>();

    // Options
    public boolean retry;
    @NotNull public Duration retryDelay;

    // Variables
    @Nullable private ScheduledFuture<?> retryTask;

    // Stats
    @Nullable public Date connectedAt;
    public long messagesSent;
    public long messagesReceived;
    @Nullable public Date lastMessageSentAt;
    @Nullable public Date lastMessageReceivedAt;

    private EAWebSocket(@NotNull String url, @NotNull Map<String, String> headers, @NotNull Set<SocketHandler<?>> handlers, boolean retry, @NotNull Duration retryDelay) {
        super(URI.create(url), headers);

        // Register handlers
        for (final SocketHandler<?> handler : handlers) {
            this.handlers.computeIfAbsent(handler.getName(), k -> new HashSet<>()).add(handler);
        }

        // Options
        this.retry = retry;
        this.retryDelay = retryDelay;
    }

    public void subscribe(@NotNull SocketEventName name) {
        send(SocketActionName.UPDATE_SUBSCRIPTION, new EAUpdateSubscriptionAction(Collections.singleton(name), null));
    }

    public void unsubscribe(@NotNull SocketEventName name) {
        send(SocketActionName.UPDATE_SUBSCRIPTION, new EAUpdateSubscriptionAction(null, Collections.singleton(name)));
    }

    public void updateSubscriptions() {
        // Get subscriptions
        final Set<SocketEventName> subscribe = new HashSet<>();
        final Set<SocketEventName> unsubscribe = new HashSet<>();
        for (final Map.Entry<SocketEventName, Set<SocketHandler<?>>> entry : handlers.entrySet()) {
            if (entry.getValue().stream().anyMatch(SocketHandler::shouldSubscribe)) {
                subscribe.add(entry.getKey());
            } else {
                unsubscribe.add(entry.getKey());
            }
        }

        // Send UPDATE_SUBSCRIPTION
        send(SocketActionName.UPDATE_SUBSCRIPTION, new EAUpdateSubscriptionAction(subscribe, unsubscribe));
    }

    public void retryConnection(@NotNull String reason) {
        if (retryTask != null || !retry) return;

        // Close connection
        close(1001, "Retrying connection");

        // Schedule retry
        LOGGER.info("We will try to reconnect to the websocket in {} due to: {}", formatRetryDelay(retryDelay), reason);
        retryTask = RETRY_SCHEDULER.schedule(() -> {
            LOGGER.info("Retrying websocket connection with reason: {}", reason);
            retryTask = null;
            reconnect();
        }, retryDelay.toMillis(), TimeUnit.MILLISECONDS);
    }

    @NotNull
    public static String formatRetryDelay(@NotNull Duration retryDelay) {
        final long totalSeconds = retryDelay.getSeconds();
        final long hours = totalSeconds / 3600;
        final long minutes = (totalSeconds % 3600) / 60;
        final long seconds = totalSeconds % 60;
        return hours + "h " + minutes + "m " + seconds + "s";
    }

    public <T extends EAObject> boolean shouldSend(@NotNull SocketActionName name, @NotNull SocketAction<T> action) {
        return true;
    }

    public <T extends EAObject> void beforeSend(@NotNull SocketActionName name, @NotNull SocketAction<T> action) {}

    public <T extends EAObject> void afterSend(@NotNull SocketActionName name, @NotNull SocketAction<T> action) {}

    public <T extends EAObject> void send(@NotNull SocketActionName name, @NotNull T object) {
        if (!isOpen()) return;
        final SocketAction<T> action = new SocketAction<>(name, object);

        // Should send?
        if (!shouldSend(name, action)) return;

        // Before send
        beforeSend(name, action);

        // Update stats
        messagesSent++;
        lastMessageSentAt = new Date();

        // Send
        send(GSONProvider.GSON.toJson(action, GSONProvider.typeOf(SocketAction.class, name.objectType)));

        // After send
        afterSend(name, action);
    }

    @Override
    public void onOpen(@NotNull ServerHandshake handshake) {
        LOGGER.info("Websocket opened");
        connectedAt = new Date();
        updateSubscriptions();
    }

    @Override
    public void onMessage(@NotNull String message) {
        // Parse as JSON
        final JsonObject json = GSONProvider.GSON.fromJson(message, JsonObject.class);
        if (json == null) {
            LOGGER.warn("Failed to parse JSON: {}", message);
            return;
        }

        // Get handlers
        final SocketEventName eventName;
        try {
            eventName = json.has("event") ? SocketEventName.valueOf(json.get("event").getAsString()) : null;
        } catch (final IllegalArgumentException ignored) {
            LOGGER.warn("Received JSON with invalid event: {}", message);
            return;
        }
        final Set<SocketHandler<?>> handlers = eventName == null ? null : this.handlers.get(eventName);
        if (handlers == null) {
            LOGGER.warn("Received JSON with invalid event: {}", message);
            return;
        }
        if (handlers.isEmpty()) {
            LOGGER.warn("Received JSON with no handlers for event: {}", message);
            return;
        }

        // Update stats
        messagesReceived++;
        lastMessageReceivedAt = new Date();

        // Handle
        for (final SocketHandler<?> handler : handlers) handler.onMessage(json);
    }

    @Override
    public void onClose(int code, @Nullable String reason, boolean remote) {
        // Protect against double-errors when initial connection fails
        if (code == CloseFrame.NEVER_CONNECTED) return;

        // Cancel retryTask
        if (retryTask != null) {
            retryTask.cancel(true);
            retryTask = null;
        }

        // Abnormal closure
        if (code == CloseFrame.ABNORMAL_CLOSE) {
            retryConnection("Experienced abnormal closure");
            return;
        }

        // Log closure
        LOGGER.info("Websocket closed with status code {} and reason: {}", code, reason);
    }

    @Override
    public void onError(@NotNull Exception exception) {
        retryConnection("Experienced an error! Check nearby for more details.");
        exception.printStackTrace();
    }

    public static class Builder {
        // Required
        @NotNull private final String userAgent;

        // Optional
        @NotNull private String url = "wss://eventalerts.gg/api/v1/socket";
        @NotNull private final Set<SocketHandler<?>> handlers = new HashSet<>();
        private boolean retry = true;
        @NotNull private Duration retryDelay = Duration.ofMinutes(5);
        @Nullable private String bearerToken;
        @Nullable private String playerKey;
        @Nullable private String serverKey;
        @NotNull private final Map<String, String> headers = new HashMap<>();

        public Builder(@NotNull String userAgent) {
            this.userAgent = userAgent;
        }

        @NotNull
        public Builder url(@NotNull String url) {
            this.url = url;
            return this;
        }

        @NotNull
        public Builder handler(@NotNull SocketHandler<?>... handlers) {
            this.handlers.addAll(Arrays.asList(handlers));
            return this;
        }

        @NotNull
        public Builder retry(boolean retry) {
            this.retry = retry;
            return this;
        }

        @NotNull
        public Builder retryDelay(@NotNull Duration retryDelay) {
            this.retryDelay = retryDelay;
            return this;
        }

        @NotNull
        public Builder bearerToken(@Nullable String bearerToken) {
            this.bearerToken = bearerToken;
            return this;
        }

        @NotNull
        public Builder playerKey(@Nullable String playerKey) {
            this.playerKey = playerKey;
            return this;
        }

        @NotNull
        public Builder serverKey(@Nullable String serverKey) {
            this.serverKey = serverKey;
            return this;
        }

        @NotNull
        public Builder header(@NotNull String key, @NotNull String value) {
            headers.put(key, value);
            return this;
        }

        @NotNull
        public EAWebSocket build() {
            // URL: remove trailing slash
            if (url.endsWith("/")) url = url.substring(0, url.length() - 1);

            // Build
            return new EAWebSocket(url, EventAlertsSDK.createHeaders(headers, userAgent, bearerToken, playerKey, serverKey), handlers, retry, retryDelay);
        }

        @NotNull
        public EAWebSocket buildThenConnect() {
            final EAWebSocket socket = build();
            socket.connect();
            return socket;
        }
    }
}
