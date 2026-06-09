# Event Alerts Java SDK [![Release](https://repo.srnyx.com/api/badge/latest/releases/gg/eventalerts/sdk/core?color=b8860b&name=Release)](https://repo.srnyx.com/#/releases/gg/eventalerts/sdk) [![Snapshot](https://repo.srnyx.com/api/badge/latest/snapshots/gg/eventalerts/sdk/core?color=b8860b&name=Snapshot)](https://repo.srnyx.com/#/releases/gg/eventalerts/sdk)

Java models, HTTP client, and websocket helpers for the Event Alerts API.

## Modules

- `gg.eventalerts.sdk:core` for shared models, JSON helpers, and common utilities
- `gg.eventalerts.sdk:http` for the HTTP client and endpoint wrappers
- `gg.eventalerts.sdk:websocket` for the websocket client, handlers, and message envelopes

## Highlights

- `GSONProvider.GSON` includes adapters for `Date`, `UUID`, `ObjectId`, enums, primitive wrappers, and sets
- API models live under `gg.eventalerts.sdk.object`
- `EAHTTP` wraps the API's HTTP endpoints and returns action objects with `complete()` and `queue()`
- HTTP actions support JDA-style composition with `map(...)`, `flatMap(...)`, `recoverWithEmptyList()`, and `recoverWithNull()`
- `EAWebSocket` handles websocket connection setup, event dispatch, and action sending
- Typed websocket envelopes are available through `SocketEvent<T>` and `SocketAction<T>`

## Dependency Patterns

Use the smallest module that matches your use case:

### Shared models and JSON only

```kotlin
dependencies {
    implementation("gg.eventalerts.sdk:core:1.0.0")
}
```

### HTTP only

```kotlin
dependencies {
    implementation("gg.eventalerts.sdk:http:1.0.0")
}
```

### Websocket only

```kotlin
dependencies {
    implementation("gg.eventalerts.sdk:websocket:1.0.0")
}
```

### HTTP and websocket together

```kotlin
dependencies {
    implementation("gg.eventalerts.sdk:http:1.0.0")
    implementation("gg.eventalerts.sdk:websocket:1.0.0")
}
```

`core` is brought in transitively by `http` and `websocket`, so most consumers do not need to declare it directly.

## Requirements

- Java 8

## Example Project

A small example project layout you can copy into your own repository:

### `build.gradle.kts`

```kotlin
repositories {
    mavenCentral()
    maven("https://repo.srnyx.com/releases/")
    maven("https://repo.srnyx.com/snapshots/")
}

dependencies {
    implementation("gg.eventalerts.sdk:core:1.0.0")
    implementation("gg.eventalerts.sdk:http:1.0.0")
    implementation("gg.eventalerts.sdk:websocket:1.0.0")
}
```

### `Main.java`

```java
public class Main {
    static void main(String[] args) {
        // JSON round-trip example
        final EAEvent original = EAEvent.getExample();
        final String json = GSONProvider.GSON.toJson(original);
        final EAEvent parsed = GSONProvider.GSON.fromJson(json, EAEvent.class);
        System.out.println("Round-trip title: " + parsed.title);

        // HTTP example
        final EAHTTP http = new EAHTTP.Builder("EventAlertsExample/1.0").build();
        http.events.retrieveOneById(new ObjectId("507f1f77bcf86cd799439011")).queue(
                event -> System.out.println("Loaded event: " + event.title),
                error -> System.err.println("Failed to load event: " + error.getMessage()));

        // Recover-with HTTP example
        http.events.retrieveMany()
                .recoverWithEmptyList() // If an error occurs, events will just be an empty list
                .map(events -> events.size())
                .queue(size -> System.out.println("Loaded events: " + size));

        // Websocket example
        final EAWebSocket socket = new EAWebSocket.Builder("EventAlertsExample/1.0")
                .retry(false)
                .handler(new EventPostedHandler() {
                    @Override
                    public void onMessage(@NotNull SocketEvent<EAEvent> object) {
                        System.out.println("Received: " + object.event);
                        System.out.println("Sequence: " + object.sequence);
                        System.out.println("Timestamp: " + object.timestamp);
                        System.out.println("Title: " + object.data.title);
                    }
                })
                .build()
                .connectBlocking();
        socket.send(SocketActionName.PLAYER_CONNECTION, new EAPlayerConnectionAction(
                UUID.fromString("00000000-0000-0000-0000-000000000000"),
                "srnyx",
                new Date(),
                EAPlayerConnectionAction.Type.JOIN));
    }
}
```

## Package Guide

### JSON: `gg.eventalerts.sdk.json`

Provides the shared `GSONProvider.GSON` instance used throughout the SDK. It is configured with adapters for the common API types used by the library.

### Models: `gg.eventalerts.sdk.object`

Contains the common API models used by the Event Alerts API:

- `EACrossBan`
- `EAEvent`
- `EAEventThreadMessage`
- `EAFamousEvent`
- `EAPartnerServer`
- `EAPlayer`
- `EAServerApplication`

### HTTP: `gg.eventalerts.sdk.http`

Contains `EAHTTP`, the shared `EAAction` type, and the typed endpoint wrappers:

- `EACrossBans`
- `EAEvents`
- `EAPartnerServers`
- `EAPlayers`
- `EAServerApplications`

### Websocket: `gg.eventalerts.sdk.websocket`

Contains the websocket client, event/action enums, and typed handlers.

`EAWebSocket` automatically updates subscriptions when the socket opens, routes incoming events to registered handlers, and can send typed actions back to the API.

#### Handlers

Event handlers extend `SocketHandler<T>` and receive a typed `SocketEvent<T>`.

Built-in handler base classes:

- `BoosterPassGivenHandler`
- `CrossBanHandler`
- `EventCancelledHandler`
- `EventChatHandler`
- `EventPostedHandler`
- `FamousEventPostedHandler`
- `LinkHandler`
- `ServerEditedHandler`
- `ServerEnabledHandler`

#### Actions

Action names are sent directly through `EAWebSocket.send(...)` using `SocketActionName` values.

Built-in action names:

- `PLAYER_CONNECTION`
- `UPDATE_SUBSCRIPTION`

### `SocketEvent<T>`

Each event envelope contains:

- `event`
- `sequence`
- `timestamp`
- `data`

### `SocketAction<T>`

Each action envelope contains:

- `action`
- `data`

## Typical Flow

- Serialize or deserialize API models through `GSONProvider.GSON`
- Use `EAHTTP` when you want REST endpoints
- Use `EAWebSocket` when you want streaming event handlers
- Subclass a built-in websocket handler when you need custom behavior
- Add event handlers to `EAWebSocket.Builder`
- Connect with `build()`, `connect()`, or `buildThenConnect()`
- Use `subscribe(...)` and `unsubscribe(...)` to update subscriptions manually
- Use `updateSubscriptions()` to automatically update based on `SocketHandler.shouldSubscribe()`
- Use `send(...)` when you need to push actions
