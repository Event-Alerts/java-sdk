package gg.eventalerts.sdk;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;


public class EventAlertsSDK {
    @NotNull
    public static Map<String, String> createHeaders(@NotNull Map<String, String> headers, @NotNull String userAgent, @Nullable String bearerToken, @Nullable String playerKey, @Nullable String serverKey) {
        final Map<String, String> allHeaders = new HashMap<>(headers);
        allHeaders.put("User-Agent", userAgent);
        if (bearerToken != null) allHeaders.put("Authorization", "Bearer " + bearerToken);
        if (playerKey != null) allHeaders.put("X-Player-Key", playerKey);
        if (serverKey != null) allHeaders.put("X-Server-Key", serverKey);
        return allHeaders;
    }
}
