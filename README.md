# Event Alerts Java SDK

Java models, HTTP client, and websocket helpers for the Event Alerts API.

## Modules

- `gg.eventalerts.sdk:core` for shared models, JSON helpers, and common utilities
- `gg.eventalerts.sdk:http` for the HTTP client and endpoint wrappers
- `gg.eventalerts.sdk:websocket` for the websocket client, handlers, and message envelopes

## Highlights

- `GSONProvider.GSON` includes adapters for `Date`, `UUID`, `ObjectId`, enums, primitive wrappers, and sets
- API models live under `gg.eventalerts.sdk.object`
- `EAHTTP` wraps the API's HTTP endpoints
- `EAWebSocket` handles websocket connection setup, event dispatch, and action sending
- Typed websocket envelopes are available through `SocketEvent<T>` and `SocketAction<T>`

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
    implementation("gg.eventalerts.sdk:core:0.0.1")
    implementation("gg.eventalerts.sdk:http:0.0.1")
    implementation("gg.eventalerts.sdk:websocket:0.0.1")
}
```

### `Main.java`

```java
import gg.eventalerts.sdk.http.EAHTTP;
import gg.eventalerts.sdk.json.GSONProvider;
import gg.eventalerts.sdk.object.EAEvent;
import gg.eventalerts.sdk.websocket.EAWebSocket;
import gg.eventalerts.sdk.websocket.SocketActionName;
import gg.eventalerts.sdk.websocket.handler.EventPostedHandler;
import gg.eventalerts.sdk.websocket.message.action.EAPlayerConnectionAction;
import gg.eventalerts.sdk.websocket.message.event.SocketEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Date;
import java.util.UUID;

public class Main {
    public static void main(String[] args) {
        // JSON round-trip example
        final EAEvent original = EAEvent.getExample();
        final String json = GSONProvider.GSON.toJson(original);
        final EAEvent parsed = GSONProvider.GSON.fromJson(json, EAEvent.class);
        System.out.println("Round-trip title: " + parsed.title);

        // HTTP example
        final EAHTTP http = new EAHTTP.Builder("EventAlertsExample/1.0").build();
        System.out.println(http.events.retrieveMany());

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
                .buildThenConnect();

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

Contains `EAHTTP` and the typed endpoint wrappers:

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
