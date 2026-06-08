package gg.eventalerts.http;

import gg.eventalerts.sdk.http.EAHTTP;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


class EAHTTPBuilderTest {
    @Test
    void buildAddsTrailingSlashAndStandardHeaders() {
        final EAHTTP http = new EAHTTP.Builder("EventAlertsSDK/1.0")
                .url("https://example.com/api/v1")
                .bearerToken("bearer-token")
                .playerKey("player-key")
                .serverKey("server-key")
                .header("X-Test", "value")
                .build();

        assertEquals("https://example.com/api/v1/", http.url);
        assertEquals("application/json", http.headers.get("Accept"));
        assertEquals("EventAlertsSDK/1.0", http.headers.get("User-Agent"));
        assertEquals("Bearer bearer-token", http.headers.get("Authorization"));
        assertEquals("player-key", http.headers.get("X-Player-Key"));
        assertEquals("server-key", http.headers.get("X-Server-Key"));
        assertEquals("value", http.headers.get("X-Test"));
    }
}
