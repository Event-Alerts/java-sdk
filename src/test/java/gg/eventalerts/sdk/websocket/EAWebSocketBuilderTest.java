package gg.eventalerts.sdk.websocket;

import gg.eventalerts.sdk.websocket.handler.action.UpdateSubscriptionActionHandler;
import org.junit.jupiter.api.Test;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertThrows;


class EAWebSocketBuilderTest {
    @Test
    void duplicateHandlerNamesAreRejected() {
        assertThrows(IllegalArgumentException.class, () -> new EAWebSocket.Builder(
                URI.create("ws://localhost:1/api/v1/socket"),
                "gg.eventalerts.sdk-test/1.0")
                .addHandlers(new UpdateSubscriptionActionHandler(), new UpdateSubscriptionActionHandler()));
    }
}
