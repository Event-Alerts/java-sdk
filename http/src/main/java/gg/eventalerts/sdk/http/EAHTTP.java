package gg.eventalerts.sdk.http;

import gg.eventalerts.sdk.EventAlertsSDK;
import gg.eventalerts.sdk.http.endpoint.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;


public class EAHTTP {
    @NotNull public String url;
    @NotNull public String userAgent;
    @NotNull public Map<String, String> headers;

    @NotNull public final EACrossBans crossBans = new EACrossBans(this);
    @NotNull public final EAEvents events = new EAEvents(this);
    @NotNull public final EAPartnerServers partnerServers = new EAPartnerServers(this);
    @NotNull public final EAPlayers players = new EAPlayers(this);
    @NotNull public final EAServerApplications serverApplications = new EAServerApplications(this);
    @NotNull public final EAEventPresets eventPresets = new EAEventPresets(this);

    private EAHTTP(@NotNull String url, @NotNull String userAgent, @NotNull Map<String, String> headers) {
        this.url = url;
        this.userAgent = userAgent;
        this.headers = headers;
    }

    @NotNull
    public EAHTTP setBearerToken(@Nullable String bearerToken) {
        if (bearerToken != null) {
            headers.put("Authorization", "Bearer " + bearerToken);
        } else {
            headers.remove("Authorization");
        }
        return this;
    }

    @NotNull
    public EAHTTP setPlayerKey(@Nullable String playerKey) {
        if (playerKey != null) {
            EventAlertsSDK.validateKey(playerKey, EventAlertsSDK.KEY_PREFIX_PLAYER);
            headers.put(EventAlertsSDK.HEADER_PLAYER_KEY, playerKey);
        } else {
            headers.remove(EventAlertsSDK.HEADER_PLAYER_KEY);
        }
        return this;
    }

    @NotNull
    public EAHTTP setServerKey(@Nullable String serverKey) {
        if (serverKey != null) {
            EventAlertsSDK.validateKey(serverKey, EventAlertsSDK.KEY_PREFIX_SERVER);
            headers.put(EventAlertsSDK.HEADER_SERVER_KEY, serverKey);
        } else {
            headers.remove(EventAlertsSDK.HEADER_SERVER_KEY);
        }
        return this;
    }

    @NotNull
    public EAHTTP setEventUtilsKey(@Nullable String eventUtilsKey) {
        if (eventUtilsKey != null) {
            EventAlertsSDK.validateKey(eventUtilsKey, EventAlertsSDK.KEY_PREFIX_EVENT_UTILS);
            headers.put(EventAlertsSDK.HEADER_EVENT_UTILS_KEY, eventUtilsKey);
        } else {
            headers.remove(EventAlertsSDK.HEADER_EVENT_UTILS_KEY);
        }
        return this;
    }

    public static class Builder {
        // Required
        @NotNull private final String userAgent;

        // Optional
        @NotNull private String url = "https://eventalerts.gg/api/v1";
        @Nullable private String bearerToken;
        @Nullable private String playerKey;
        @Nullable private String serverKey;
        @Nullable private String eventUtilsKey;
        @NotNull private final Map<String, String> headers = new HashMap<>();

        public Builder(@NotNull String userAgent) {
            this.userAgent = userAgent;
            headers.put("Accept", "application/json");
        }

        @NotNull
        public Builder url(@NotNull String url) {
            this.url = url;
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
        public Builder eventUtilsKey(@Nullable String eventUtilsKey) {
            this.eventUtilsKey = eventUtilsKey;
            return this;
        }

        @NotNull
        public Builder header(@NotNull String key, @NotNull String value) {
            headers.put(key, value);
            return this;
        }

        @NotNull
        public EAHTTP build() {
            // URL: remove trailing slash
            if (url.endsWith("/")) url = url.substring(0, url.length() - 1);

            // KEYS: Validate
            if (playerKey != null) EventAlertsSDK.validateKey(playerKey, EventAlertsSDK.KEY_PREFIX_PLAYER);
            if (serverKey != null) EventAlertsSDK.validateKey(serverKey, EventAlertsSDK.KEY_PREFIX_SERVER);
            if (eventUtilsKey != null) EventAlertsSDK.validateKey(eventUtilsKey, EventAlertsSDK.KEY_PREFIX_EVENT_UTILS);

            // Build
            return new EAHTTP(url, userAgent, EventAlertsSDK.createHeaders(headers, userAgent, bearerToken, playerKey, serverKey, eventUtilsKey));
        }
    }
}
