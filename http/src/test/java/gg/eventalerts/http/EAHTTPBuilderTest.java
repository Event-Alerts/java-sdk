package gg.eventalerts.http;

import gg.eventalerts.sdk.EventAlertsSDK;
import gg.eventalerts.sdk.http.EAHTTP;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


class EAHTTPBuilderTest {
    @Test
    void buildAddsTrailingSlashAndStandardHeaders() {
        final EAHTTP http = new EAHTTP.Builder("EventAlertsSDK/1.0")
                .url("https://example.com/api/v1")
                .bearerToken("bearer-token")
                .playerKey(EventAlertsSDK.KEY_PREFIX_PLAYER + "1.abc")
                .serverKey(EventAlertsSDK.KEY_PREFIX_SERVER + "1.def")
                .eventUtilsKey(EventAlertsSDK.KEY_PREFIX_EVENT_UTILS + "1.ghi")
                .header("X-Test", "value")
                .build();

        assertEquals("https://example.com/api/v1", http.url);
        assertEquals("application/json", http.headers.get("Accept"));
        assertEquals("EventAlertsSDK/1.0", http.headers.get("User-Agent"));
        assertEquals("Bearer bearer-token", http.headers.get("Authorization"));
        assertEquals(EventAlertsSDK.KEY_PREFIX_PLAYER + "1.abc", http.headers.get(EventAlertsSDK.HEADER_PLAYER_KEY));
        assertEquals(EventAlertsSDK.KEY_PREFIX_SERVER + "1.def", http.headers.get(EventAlertsSDK.HEADER_SERVER_KEY));
        assertEquals(EventAlertsSDK.KEY_PREFIX_EVENT_UTILS + "1.ghi", http.headers.get(EventAlertsSDK.HEADER_EVENT_UTILS_KEY));
        assertEquals("value", http.headers.get("X-Test"));
    }
}
