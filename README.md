# Event Alerts Java SDK

Java models, custom Gson adapters, and websocket helpers for the Event Alerts API.

## Highlights

- `GSONProvider.GSON` includes adapters for `Date`, `UUID`, `ObjectId`, enums, primitive wrappers, and sets
- API models live under `gg.eventalerts.sdk.object`
- `EAWebSocket` handles websocket connection setup, event dispatch, and action sending
- Typed socket envelopes are available through `SocketEvent<T>` and `SocketAction<T>`

## Requirements

- Java 17

## Example Project

A small example project layout you can copy into your own repository:

```text
example/
  build.gradle.kts
  src/main/java/gg/eventalerts/example/Main.java
```

### `build.gradle.kts`

```kotlin
repositories {
    maven("https://repo.srnyx.com/releases/") // Releases
    maven("https://repo.srnyx.com/snapshots/") // Snapshots
}

dependencies {
    implementation("gg.eventalerts:java-sdk:1.0.0")
}
```

### `Main.java`

```java
void main() {
    // JSON round-trip example
    final Event original = Event.getExample();
    final String json = GSONProvider.GSON.toJson(original);
    final Event parsed = GSONProvider.GSON.fromJson(json, Event.class);
    System.out.println("Round-trip title: " + parsed.title);

    // WebSocket example
    final EAWebSocket socket = new EAWebSocket.Builder(
                    URI.create("ws://localhost:9090/api/v1/socket"),
                    "EventAlertsExample/1.0") // User-Agent
            .retry(false) // Disable automatic reconnects
            .addHandlers(
                    new EventPostedEventHandler() {
                        @Override
                        public void onMessage(@NotNull SocketEvent<Event> object) {
                            System.out.println("Received: " + object.event);
                            System.out.println("Sequence: " + object.sequence);
                            System.out.println("Timestamp: " + object.timestamp);
                            System.out.println("Title: " + object.data.title);
                        }
                    },
                    new PlayerConnectionActionHandler())
            .build();
    socket.connectBlocking();
    socket.send(SocketActionName.PLAYER_CONNECTION, PlayerConnectionAction.getExample());
}
```

## Package Guide

### `gg.eventalerts.sdk.json`

Provides the shared `GSONProvider.GSON` instance used throughout the SDK. It is configured with safe adapters so invalid values return `null` instead of throwing.

### `gg.eventalerts.sdk.object`

Contains the common API models used by the Event Alerts API:

- `CrossBan`
- `Event`
- `EventThreadMessage`
- `FamousEvent`
- `PartnerServer`
- `Player`
- `ServerApplication`

### `gg.eventalerts.sdk.websocket`

Contains the websocket client and the typed socket envelope classes.

`EAWebSocket` automatically updates subscriptions when the socket opens, routes incoming events to registered handlers, and can send typed actions back to the API.

### Handlers

Handlers are split into event handlers and action handlers.

- Event handlers extend `SocketEventHandler<T>` and receive a typed `SocketEvent<T>`
- Action handlers extend `SocketActionHandler` and declare which outgoing action they support

Built-in event names:

- `BOOSTER_PASS_GIVEN`
- `CROSS_BAN`
- `EVENT_CANCELLED`
- `EVENT_CHAT`
- `EVENT_POSTED`
- `FAMOUS_EVENT_POSTED`
- `LINK`
- `SERVER_EDITED`
- `SERVER_ENABLED`

Built-in action names:

- `PLAYER_CONNECTION`
- `UPDATE_SUBSCRIPTION`

### `SocketEvent<T>`

Each event envelope contains:

- `event`
- `sequence`
- `timestamp`
- `data`

Example:

```json
{
  "event": "EVENT_POSTED",
  "sequence": 2583,
  "timestamp": "1735689600000",
  "data": {
    "title": "Example Event",
    "type": "PARTNER"
  }
}
```

### `SocketAction<T>`

Each action envelope contains:

- `action`
- `data`

Example:

```json
{
  "action": "UPDATE_SUBSCRIPTION",
  "data": {
    "subscribe": ["BOOSTER_PASS_GIVEN", "EVENT_POSTED"],
    "unsubscribe": ["EVENT_CHAT"]
  }
}
```

## Typical Flow

- Serialize or deserialize API models through `GSONProvider.GSON`
- Subclass a built-in event or action handler when you need custom websocket behavior
- Add handlers to `EAWebSocket.Builder`
- Connect with `build()` + `connectBlocking()` or `buildThenConnect()`
- Use `send(...)`, `subscribe(...)`, or `unsubscribe(...)` when you need to push actions
