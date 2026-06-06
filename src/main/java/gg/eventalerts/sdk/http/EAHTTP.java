package gg.eventalerts.sdk.http;

import gg.eventalerts.sdk.http.endpoint.EACrossBans;
import gg.eventalerts.sdk.http.endpoint.EAEvents;
import gg.eventalerts.sdk.http.endpoint.EAPartnerServers;
import gg.eventalerts.sdk.http.endpoint.EAPlayers;
import gg.eventalerts.sdk.http.endpoint.EAServerApplications;
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

    private EAHTTP(@NotNull String url, @NotNull String userAgent, @NotNull Map<String, String> headers) {
        this.url = url;
        this.userAgent = userAgent;
        this.headers = headers;
    }

    public static class Builder {
        // Required
        @NotNull private final String userAgent;

        // Optional
        @NotNull private String url = "https://eventalerts.gg/api/v1/";
        @Nullable private String bearerToken;
        @Nullable private String playerKey;
        @Nullable private String serverKey;
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
        public Builder header(@NotNull String key, @NotNull String value) {
            headers.put(key, value);
            return this;
        }

        @NotNull
        public EAHTTP build() {
            // URL
            if (!url.endsWith("/")) url += "/";

            // Headers
            final Map<String, String> allHeaders = new HashMap<>(headers);
            allHeaders.put("User-Agent", userAgent);
            if (bearerToken != null) allHeaders.put("Authorization", "Bearer " + bearerToken);
            if (playerKey != null) allHeaders.put("X-Player-Key", playerKey);
            if (serverKey != null) allHeaders.put("X-Server-Key", serverKey);

            return new EAHTTP(url, userAgent, allHeaders);
        }
    }
}
