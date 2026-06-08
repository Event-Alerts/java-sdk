package gg.eventalerts.sdk.http.endpoint;

import com.google.gson.JsonObject;
import gg.eventalerts.sdk.http.EAHTTP;
import gg.eventalerts.sdk.http.response.APIResponse;
import gg.eventalerts.sdk.http.response.ErrorResponse;
import gg.eventalerts.sdk.http.response.FailedResponse;
import gg.eventalerts.sdk.http.response.PaginatedResponse;
import gg.eventalerts.sdk.http.response.SingleResponse;
import gg.eventalerts.sdk.json.GSONProvider;
import gg.eventalerts.sdk.object.EAObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;


public abstract class EAEndpoint<O extends EAObject> {
    @NotNull private final EAHTTP http;

    public EAEndpoint(@NotNull EAHTTP http) {
        this.http = http;
    }

    @NotNull
    public abstract String getPath();

    @NotNull
    public abstract Class<O> getObjectClass();

    @NotNull
    public APIResponse<O> retrieve(@Nullable Map<String, Object> queryParams, @Nullable String... pathSegments) {
        // Build query
        final StringBuilder queryString = new StringBuilder();
        if (queryParams != null) {
            queryString.append("?");
            for (final Map.Entry<String, Object> entry : queryParams.entrySet()) {
                // Get encoded key/value
                String key = entry.getKey();
                String value = String.valueOf(entry.getValue());
                try {
                    key = URLEncoder.encode(key, "UTF-8");
                    value = URLEncoder.encode(value, "UTF-8");
                } catch (final UnsupportedEncodingException ignored) {}

                // Append
                queryString.append(key).append("=").append(value).append("&");
            }
            queryString.deleteCharAt(queryString.length() - 1);
        }

        // Build path
        final StringBuilder path = new StringBuilder();
        if (pathSegments != null) for (final String segment : pathSegments) path.append("/").append(segment);

        // Make request
        APIResponse<O> result = null;
        HttpURLConnection connection = null;
        final String endpointPath = getPath();
        try {
            // Setup connection
            connection = (HttpURLConnection) URI.create(http.url + endpointPath + path + queryString).toURL().openConnection();
            connection.setRequestMethod("GET");
            for (final Map.Entry<String, String> header : http.headers.entrySet()) connection.setRequestProperty(header.getKey(), header.getValue());

            // Get InputStream
            InputStream inputStream;
            try {
                inputStream = connection.getInputStream();
            } catch (final Exception e) {
                inputStream = connection.getErrorStream();

                // Big error
                if (inputStream == null) {
                    result = new ErrorResponse<>(connection.getResponseCode(), connection.getResponseMessage());
                    connection.disconnect();
                    return result;
                }
            }

            // Get as JSON
            final InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            final JsonObject json = GSONProvider.GSON.fromJson(reader, JsonObject.class);

            // Error
            if (json.has("message")) {
                result = GSONProvider.GSON.fromJson(json, GSONProvider.typeOf(ErrorResponse.class, getObjectClass()));

                // Return
                connection.disconnect();
                return result;
            }

            // Paginated
            if (json.has(endpointPath)) {
                result = GSONProvider.GSON.fromJson(json, GSONProvider.typeOf(PaginatedResponse.class, getObjectClass()));

                // Inject objects into response.data
                ((PaginatedResponse<O>) result).data = GSONProvider.GSON.fromJson(json.get(endpointPath), GSONProvider.typeOf(List.class, getObjectClass()));

                // Return
                connection.disconnect();
                return result;
            }

            // Single
            result = GSONProvider.GSON.fromJson(json, GSONProvider.typeOf(SingleResponse.class, getObjectClass()));

            // Get object field name
            final String objectFieldName = endpointPath.substring(0, endpointPath.length() - 1);

            // Inject object into response.data
            ((SingleResponse<O>) result).data = GSONProvider.GSON.fromJson(json.get(objectFieldName), getObjectClass());

            // Return
            connection.disconnect();
            return result;
        } catch (final Exception e) {
            e.printStackTrace();

            // Return
            if (connection != null) connection.disconnect();
            return new FailedResponse<>(e, result);
        }
    }

    @NotNull
    public APIResponse<O> retrieveMany(@Nullable Map<String, Object> query) {
        return retrieve(query);
    }

    @NotNull
    public APIResponse<O> retrieveMany() {
        return retrieveMany(null);
    }

    @NotNull
    public APIResponse<O> retrieveOne(@NotNull String... pathSegments) {
        return retrieve(null, pathSegments);
    }
}
