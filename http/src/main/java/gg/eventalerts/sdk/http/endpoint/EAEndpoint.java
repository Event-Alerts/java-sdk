package gg.eventalerts.sdk.http.endpoint;

import com.google.gson.JsonObject;
import com.google.gson.JsonElement;
import gg.eventalerts.sdk.http.EAHTTP;
import gg.eventalerts.sdk.http.action.EAAction;
import gg.eventalerts.sdk.http.exception.EAHttpRequestException;
import gg.eventalerts.sdk.http.exception.EAHttpResponseException;
import gg.eventalerts.sdk.json.GSONProvider;
import gg.eventalerts.sdk.object.EAObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;


public abstract class EAEndpoint<O extends EAObject> {
    @NotNull private static final Logger LOGGER = Logger.getLogger(EAEndpoint.class.getName());

    @NotNull private final EAHTTP http;

    public EAEndpoint(@NotNull EAHTTP http) {
        this.http = http;
    }

    @NotNull
    public abstract String getPath();

    @NotNull
    public abstract Class<O> getObjectClass();

    @NotNull
    public EAAction<List<O>> retrieveMany(@Nullable Map<String, Object> queryParams) {
        return new EAAction<>("GET " + getPath(), () -> executeMany(queryParams));
    }

    @NotNull
    public EAAction<List<O>> retrieveMany() {
        return retrieveMany(null);
    }

    @NotNull
    public EAAction<O> retrieveOne(@NotNull String... pathSegments) {
        return new EAAction<>("GET " + getPath(), () -> executeOne(pathSegments));
    }

    @NotNull
    protected ConnectionDetails openConnection(@NotNull String endpointPath, @NotNull String objectField, @Nullable Map<String, Object> queryParams, @Nullable String... pathSegments) throws IOException {
        // Build query parameters
        final StringBuilder queryString = new StringBuilder();
        if (queryParams != null && !queryParams.isEmpty()) {
            queryString.append("?");
            boolean first = true;
            for (final Map.Entry<String, Object> entry : queryParams.entrySet()) {
                if (!first) queryString.append("&");
                first = false;
                queryString.append(encode(entry.getKey())).append("=").append(encode(String.valueOf(entry.getValue())));
            }
        }

        // Build path
        final StringBuilder path = new StringBuilder();
        if (pathSegments != null) for (final String segment : pathSegments) path.append("/").append(segment);

        // Open connection
        final HttpURLConnection connection = (HttpURLConnection) URI.create(http.url + endpointPath + path + queryString).toURL().openConnection();
        connection.setRequestMethod("GET");
        for (final Map.Entry<String, String> header : http.headers.entrySet()) connection.setRequestProperty(header.getKey(), header.getValue());

        // Get response
        final int statusCode = connection.getResponseCode();
        final String body = readBody(statusCode >= 400 ? connection.getErrorStream() : connection.getInputStream());
        final JsonObject json = parseJson(body);
        if (json == null) {
            if (statusCode >= 400) throw new EAHttpResponseException(statusCode, connection.getResponseMessage(), body);
            throw new EAHttpRequestException("GET " + endpointPath, new IllegalStateException("Failed to parse JSON response"));
        }

        // Error
        if (statusCode >= 400 || isErrorPayload(json)) throw toResponseException(statusCode, json, body);

        // Get raw object
        final JsonElement raw = json.has(objectField) ? json.get(objectField) : null;
        if (raw == null) throw new EAHttpResponseException(statusCode, "Missing field '" + objectField + "'", body);

        // Return details
        return new ConnectionDetails(connection, statusCode, body, raw);
    }

    protected static class ConnectionDetails {
        @NotNull private final HttpURLConnection connection;
        private final int statusCode;
        @NotNull private final String body;
        @NotNull private final JsonElement raw;

        private ConnectionDetails(@NotNull HttpURLConnection connection, int statusCode, @NotNull String body, @NotNull JsonElement raw) {
            this.connection = connection;
            this.statusCode = statusCode;
            this.body = body;
            this.raw = raw;
        }
    }

    @NotNull
    private List<O> executeMany(@Nullable Map<String, Object> queryParams) {
        final String endpointPath = getPath();
        HttpURLConnection connection = null;
        try {
            // Open connection and get details
            final ConnectionDetails details = openConnection(endpointPath, endpointPath, queryParams);
            connection = details.connection;

            // Parse objects
            final List<O> objects = GSONProvider.GSON.fromJson(details.raw, GSONProvider.typeOf(List.class, getObjectClass()));
            return objects != null ? objects : Collections.emptyList();
        } catch (final RuntimeException e) {
            throw e;
        } catch (final Exception e) {
            throw new EAHttpRequestException("GET " + endpointPath, e);
        } finally {
            if (connection != null) connection.disconnect();
        }
    }

    @NotNull
    private O executeOne(@Nullable String... pathSegments) {
        final String endpointPath = getPath();
        final String objectField = endpointPath.endsWith("s") ? endpointPath.substring(0, endpointPath.length() - 1) : endpointPath;
        HttpURLConnection connection = null;
        try {
            // Open connection and get details
            final ConnectionDetails details = openConnection(endpointPath, objectField, null, pathSegments);
            connection = details.connection;

            // Parse object
            final O object = GSONProvider.GSON.fromJson(details.raw, getObjectClass());
            if (object == null) throw new EAHttpResponseException(details.statusCode, "Failed to parse object field '" + objectField + "'", details.body);
            return object;
        } catch (final RuntimeException e) {
            throw e;
        } catch (final Exception e) {
            throw new EAHttpRequestException("GET " + endpointPath, e);
        } finally {
            if (connection != null) connection.disconnect();
        }
    }

    @Nullable
    private JsonObject parseJson(@NotNull String body) {
        try {
            return GSONProvider.GSON.fromJson(body, JsonObject.class);
        } catch (final Exception e) {
            return null;
        }
    }

    @NotNull
    private String readBody(@Nullable InputStream inputStream) throws IOException {
        if (inputStream == null) return "";
        final StringBuilder body = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) body.append(line);
        }
        return body.toString();
    }

    private boolean isErrorPayload(@Nullable JsonObject json) {
        return json != null && json.has("message");
    }

    @NotNull
    private EAHttpResponseException toResponseException(int statusCode, @Nullable JsonObject json, @NotNull String body) {
        final int responseCode = extractResponseCode(json, statusCode);
        final String message;
        if (json != null && json.has("message")) {
            try {
                message = json.get("message").getAsString();
            } catch (final Exception ignored) {
                return new EAHttpResponseException(responseCode, "Unexpected error payload", body);
            }
        } else {
            message = null;
        }
        return new EAHttpResponseException(responseCode, message, body);
    }

    private int extractResponseCode(@Nullable JsonObject json, int fallbackStatusCode) {
        if (json == null || !json.has("code")) return fallbackStatusCode;
        try {
            return json.get("code").getAsInt();
        } catch (final Exception ignored) {
            return fallbackStatusCode;
        }
    }

    @NotNull
    private String encode(@NotNull String value) {
        try {
            return URLEncoder.encode(value, "UTF-8");
        } catch (final Exception e) {
            return value;
        }
    }
}
