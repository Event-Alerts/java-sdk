package gg.eventalerts.sdk;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;


public class EventAlertsSDK {
    @NotNull public static final String HEADER_PLAYER_KEY = "X-Player-Key";
    @NotNull public static final String HEADER_SERVER_KEY = "X-Server-Key";
    @NotNull public static final String HEADER_EVENT_UTILS_KEY = "X-EventUtils-Key";

    @NotNull public static final String KEY_PREFIX_PLAYER = "EA.Player.";
    @NotNull public static final String KEY_PREFIX_SERVER = "EA.PartnerServer.";
    @NotNull public static final String KEY_PREFIX_EVENT_UTILS = "EA.EventUtils.";

    @NotNull
    public static Map<String, String> createHeaders(@NotNull Map<String, String> headers, @NotNull String userAgent, @Nullable String bearerToken, @Nullable String playerKey, @Nullable String serverKey, @Nullable String eventUtilsKey) {
        final Map<String, String> allHeaders = new HashMap<>(headers);
        allHeaders.put("User-Agent", userAgent);
        if (bearerToken != null) allHeaders.put("Authorization", "Bearer " + bearerToken);
        if (playerKey != null) allHeaders.put(HEADER_PLAYER_KEY, playerKey);
        if (serverKey != null) allHeaders.put(HEADER_SERVER_KEY, serverKey);
        if (eventUtilsKey != null) allHeaders.put(HEADER_EVENT_UTILS_KEY, eventUtilsKey);
        return allHeaders;
    }

    public static boolean isKeyValid(@NotNull String key, @NotNull String prefix) {
        return key.startsWith(prefix);
    }

    public static void validateKey(@NotNull String key, @NotNull String prefix) {
        if (!isKeyValid(key, prefix)) throw new IllegalArgumentException("Invalid key: " + key);
    }
}
