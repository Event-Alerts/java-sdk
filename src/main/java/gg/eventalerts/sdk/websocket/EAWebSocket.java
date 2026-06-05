package gg.eventalerts.sdk.websocket;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import gg.eventalerts.sdk.json.GSONProvider;
import gg.eventalerts.sdk.object.EAObject;
import gg.eventalerts.sdk.websocket.handler.SocketHandler;
import gg.eventalerts.sdk.websocket.handler.SocketHandlerName;
import gg.eventalerts.sdk.websocket.object.action.SocketAction;
import gg.eventalerts.sdk.websocket.object.action.UpdateSubscriptionAction;
import gg.eventalerts.sdk.websocket.handler.action.SocketActionHandler;
import gg.eventalerts.sdk.websocket.handler.action.SocketActionName;
import gg.eventalerts.sdk.websocket.handler.event.SocketEventHandler;
import gg.eventalerts.sdk.websocket.handler.event.SocketEventName;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.framing.CloseFrame;
import org.java_websocket.handshake.ServerHandshake;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.srnyx.javautilities.MiscUtility;
import xyz.srnyx.javautilities.manipulation.DurationFormatter;
import xyz.srnyx.javautilities.manipulation.Mapper;

import java.net.URI;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;


public class EAWebSocket extends WebSocketClient {
    @NotNull public static final Logger LOGGER = LoggerFactory.getLogger(EAWebSocket.class);

    // Handlers
    @NotNull private final Map<SocketEventName, SocketEventHandler<?>> events = new HashMap<>();
    @NotNull private final Map<SocketActionName, SocketActionHandler> actions = new HashMap<>();

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

    private EAWebSocket(@NotNull URI uri, @NotNull String userAgent, @NotNull Map<SocketHandlerName<?>, SocketHandler<?>> handlers, boolean retry, @NotNull Duration retryDelay, @Nullable String bearerToken, @Nullable String playerKey, @Nullable String serverKey, @NotNull Map<String, String> headers) {
        super(uri, createHeaders(headers, userAgent, bearerToken, playerKey, serverKey));

        // Register handlers
        for (final Map.Entry<SocketHandlerName<?>, SocketHandler<?>> entry : handlers.entrySet()) {
            final SocketHandlerName<?> name = entry.getKey();
            final SocketHandler<?> handler = entry.getValue();
            if (handler instanceof SocketEventHandler<?> eventHandler) {
                this.events.put((SocketEventName) name, eventHandler);
            } else if (handler instanceof SocketActionHandler actionHandler) {
                this.actions.put((SocketActionName) name, actionHandler);
            }
        }

        // Options
        this.retry = retry;
        this.retryDelay = retryDelay;
    }

    @NotNull
    private static Map<String, String> createHeaders(@NotNull Map<String, String> headers, @NotNull String userAgent, @Nullable String bearerToken, @Nullable String playerKey, @Nullable String serverKey) {
        final Map<String, String> modifiedHeaders = new HashMap<>(headers);
        modifiedHeaders.put("User-Agent", userAgent);
        if (bearerToken != null) modifiedHeaders.put("Authorization", "Bearer " + bearerToken);
        if (playerKey != null) modifiedHeaders.put("X-Player-Key", playerKey);
        if (serverKey != null) modifiedHeaders.put("X-Server-Key", serverKey);
        return modifiedHeaders;
    }

    public void subscribe(@NotNull SocketEventName name) {
        send(SocketActionName.UPDATE_SUBSCRIPTION, new UpdateSubscriptionAction(Set.of(name), null));
    }

    public void unsubscribe(@NotNull SocketEventName name) {
        send(SocketActionName.UPDATE_SUBSCRIPTION, new UpdateSubscriptionAction(null, Set.of(name)));
    }

    public void updateSubscriptions() {
        // Get subscriptions
        final Set<SocketEventName> subscribe = new HashSet<>();
        final Set<SocketEventName> unsubscribe = new HashSet<>();
        for (final Map.Entry<SocketEventName, SocketEventHandler<?>> entry : events.entrySet()) {
            if (entry.getValue().shouldSubscribe()) {
                subscribe.add(entry.getKey());
            } else {
                unsubscribe.add(entry.getKey());
            }
        }

        // Send UPDATE_SUBSCRIPTION
        send(SocketActionName.UPDATE_SUBSCRIPTION, new UpdateSubscriptionAction(subscribe, unsubscribe));
    }

    public void retryConnection(@NotNull String reason) {
        if (retryTask != null || !retry) return;

        // Close connection
        close(1001, "Retrying connection");

        // Schedule retry
        LOGGER.info("We will try to reconnect to the websocket in {} due to: {}", formatRetryDelay(retryDelay), reason);
        retryTask = MiscUtility.IO_SCHEDULER.schedule(() -> {
            LOGGER.info("Retrying websocket connection with reason: {}", reason);
            retryTask = null;
            reconnect();
        }, retryDelay.toMillis(), TimeUnit.MILLISECONDS);
    }

    @NotNull
    public static String formatRetryDelay(@NotNull Duration retryDelay) {
        return DurationFormatter.formatDuration(retryDelay.toMillis(), "h'h' m'm' s's'");
    }

    public <T extends EAObject> void send(@NotNull SocketActionName name, @NotNull T object) {
        // Get handler
        final SocketActionHandler handler = actions.get(name);
        if (handler == null) {
            LOGGER.warn("Tried to send an invalid action: {}", name.name());
            return;
        }

        // Update stats
        messagesSent++;
        lastMessageSentAt = new Date();

        // Send
        send(GSONProvider.GSON.toJson(new SocketAction<>(name, object)));
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

        // Get handler
        final SocketEventHandler<?> event = Mapper.convertJsonElement(json.get("event"), JsonPrimitive.class)
                .flatMap(primitive -> Mapper.convertJsonPrimitive(primitive, String.class))
                .flatMap(string -> Mapper.toEnum(string, SocketEventName.class))
                .map(events::get)
                .orElse(null);
        if (event == null) {
            LOGGER.warn("Received JSON with invalid event: {}", message);
            return;
        }

        // Update stats
        messagesReceived++;
        lastMessageReceivedAt = new Date();

        // Handle
        event.onMessage(json);
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
        @NotNull private final URI uri;
        @NotNull private final String userAgent;

        @NotNull private final Map<SocketHandlerName<?>, SocketHandler<?>> handlers = new HashMap<>();
        private boolean retry = true;
        @NotNull private Duration retryDelay = Duration.ofMinutes(5);
        @Nullable private String bearerToken;
        @Nullable private String playerKey;
        @Nullable private String serverKey;
        @NotNull private final Map<String, String> headers = new HashMap<>();

        public Builder(@NotNull URI uri, @NotNull String userAgent) {
            this.uri = uri;
            this.userAgent = userAgent;
        }

        @NotNull
        public Builder addHandlers(@NotNull Collection<SocketHandler<?>> newHandlers) {
            for (final SocketHandler<?> handler : newHandlers) {
                // Check if handler for this name already exists
                if (handlers.containsKey(handler.getName())) throw new IllegalArgumentException("Duplicate handler name: " + handler.getName());

                // Add handler
                handlers.put(handler.getName(), handler);
            }
            return this;
        }

        @NotNull
        public Builder addHandlers(@NotNull SocketHandler<?>... handlers) {
            return addHandlers(List.of(handlers));
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
            return new EAWebSocket(uri, userAgent, handlers, retry, retryDelay, bearerToken, playerKey, serverKey, headers);
        }

        @NotNull
        public EAWebSocket buildThenConnect() {
            final EAWebSocket socket = build();
            socket.connect();
            return socket;
        }
    }
}
