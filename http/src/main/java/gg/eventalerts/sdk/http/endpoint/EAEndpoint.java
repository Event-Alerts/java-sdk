package gg.eventalerts.sdk.http.endpoint;

import com.google.gson.JsonObject;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import gg.eventalerts.sdk.http.EAHTTP;
import gg.eventalerts.sdk.http.response.PaginatedResponse;
import gg.eventalerts.sdk.http.action.EAAction;
import gg.eventalerts.sdk.http.exception.EAHttpRequestException;
import gg.eventalerts.sdk.http.exception.EAHttpResponseException;
import gg.eventalerts.sdk.json.GSONProvider;
import gg.eventalerts.sdk.object.EAObject;
import gg.eventalerts.sdk.object.http.EAItemData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.logging.Logger;


public abstract class EAEndpoint {
    @NotNull public static final Logger LOGGER = Logger.getLogger(EAEndpoint.class.getName());

    @NotNull private final EAHTTP http;
    @NotNull private final String path;

    public EAEndpoint(@NotNull EAHTTP http, @NotNull String @NotNull ... pathSegments) {
        this.http = http;
        this.path = buildPath(pathSegments);
        LOGGER.fine("Created EAEndpoint for path '" + path + "'");
    }

    public EAEndpoint(@NotNull EAEndpoint parent, @NotNull String @NotNull ... pathSegments) {
        this(parent.http, parent.path + buildPath(pathSegments));
    }

    /**
     * Retrieves a specific page with a specific limit
     *
     * @param   page        1-indexed page number
     * @param   limit       maximum items per page
     * @param   queryParams optional query parameters (page/limit are ignored)
     *
     * @return  action yielding a {@link PaginatedResponse}
     */
    @NotNull
    public <O extends EAObject> EAAction<PaginatedResponse<O>> retrievePage(@NotNull Class<O> objectType, @NotNull String fieldName, @Nullable Integer page, @Nullable Integer limit, @Nullable Map<String, Object> queryParams) {
        return new EAAction<>("GET " + path, () -> executePage(objectType, fieldName, queryParams, page, limit));
    }

    /**
     * Auto-paginates to collect exactly {@code count} items (or fewer if the API has less)
     *
     * @param   count       total items to collect
     * @param   startPage   optional page to start from (1-indexed)
     * @param   queryParams optional query parameters (page/limit are ignored)
     *
     * @return  action yielding the accumulated list
     */
    @NotNull
    public <O extends EAObject> EAAction<List<O>> retrieveMany(@NotNull Class<O> objectType, @NotNull String fieldName, int count, @Nullable Integer startPage, @Nullable Map<String, Object> queryParams) {
        return new EAAction<>("GET " + path, () -> {
            final List<O> result = new ArrayList<>();
            int page = (startPage != null && startPage > 0) ? startPage : 1;
            int remaining = count;
            while (remaining > 0) {
                final int limit = Math.min(remaining, 50);
                final PaginatedResponse<O> response = executePage(objectType, fieldName, queryParams, page, limit);
                result.addAll(response.items);
                remaining -= response.count;
                if (response.count < limit || result.size() >= response.total) break;
                page++;
            }
            return result;
        });
    }

    /**
     * Auto-paginates to collect all items (or fewer if the API has less)
     * <br>
     * <br><b>Only use this if you are sure the endpoint will not return a large number of items, as it will make multiple requests until all items are retrieved.</b>
     *
     * @param   fieldName   the field name of the list in the response JSON
     * @param   queryParams optional query parameters (page/limit are ignored)
     *
     * @return  action yielding the accumulated list
     *
     * @param   <O> the type of object to retrieve
     */
    @NotNull
    public <O extends EAObject> EAAction<List<O>> retrieveAll(@NotNull Class<O> objectType, @NotNull String fieldName, @Nullable Map<String, Object> queryParams) {
        return new EAAction<>("GET " + path, () -> {
            final List<O> result = new ArrayList<>();
            int page = 1;
            while (true) {
                final PaginatedResponse<O> response = executePage(objectType, fieldName, queryParams, page, null);
                result.addAll(response.items);
                if (response.count < response.limit || result.size() >= response.total) break;
                page++;
            }
            return result;
        });
    }

    /**
     * Retrieves a single object with a path
     *
     * @param   pathSegments    the path segments to append to the endpoint path
     *
     * @return  action yielding the retrieved object
     */
    @NotNull
    public <O extends EAObject> EAAction<O> retrieveOne(@NotNull Class<O> objectType, @NotNull String fieldName, @NotNull String @NotNull ... pathSegments) {
        return new EAAction<>("GET " + path, () -> executeOne(objectType, fieldName, pathSegments)).map(data -> data.item);
    }

    /**
     * Posts to the endpoint with a path and returns the response object
     *
     * @param   pathSegments    the path segments to append to the endpoint path
     *
     * @return  action yielding the response object
     */
    @NotNull
    public <O extends EAObject> EAAction<O> postOne(@NotNull Class<O> objectType, @NotNull String fieldName, @NotNull String body, @NotNull String @NotNull ... pathSegments) {
        final String url = buildUrl(null, pathSegments);
        return new EAAction<>("POST " + url, () -> {
            HttpURLConnection connection = null;
            try {
                // Open connection and get details
                final ConnectionDetails details = openConnectionPOST(url, fieldName, body);
                connection = details.connection;

                // Parse object
                O item = null;
                if (details.raw != null) item = GSONProvider.GSON.fromJson(details.raw, objectType);

                // Build and return EAItemData
                return new EAItemData<>(fieldName, item);
            } catch (final RuntimeException e) {
                throw e;
            } catch (final Exception e) {
                throw new EAHttpRequestException("POST " + url, e);
            } finally {
                if (connection != null) connection.disconnect();
            }
        }).map(data -> data.item);
    }

    @NotNull
    public <O extends EAObject> EAAction<O> postOne(@NotNull Class<O> objectType, @NotNull String fieldName, @NotNull EAObject body, @NotNull String @NotNull ... pathSegments) {
        return postOne(objectType, fieldName, GSONProvider.GSON.toJson(body), pathSegments);
    }

    @NotNull
    public String buildUrl(@Nullable Map<String, Object> queryParams, @NotNull String @Nullable ... pathSegments) {
        // Build query parameters
        final StringBuilder queryString = new StringBuilder();
        if (queryParams != null && !queryParams.isEmpty()) {
            queryString.append("?");
            boolean first = true;
            for (final Map.Entry<String, Object> entry : queryParams.entrySet()) {
                if (!first) queryString.append("&");
                first = false;

                final StringBuilder builder = new StringBuilder(encode(entry.getKey()));
                final Object value = entry.getValue();
                if (value != null) {
                    builder.append("=");

                    if (value instanceof Collection<?>) {
                        // Collection
                        final Collection<?> collection = (Collection<?>) value;
                        if (!collection.isEmpty()) {
                            for (final Object item : collection) builder.append(encode(String.valueOf(item))).append(",");
                            builder.deleteCharAt(builder.length() - 1);
                        }
                    } else if (value.getClass().isArray()) {
                        // Array
                        final int length = Array.getLength(value);
                        if (length > 0) {
                            for (int i = 0; i < length; i++) builder.append(encode(String.valueOf(Array.get(value, i)))).append(",");
                            builder.deleteCharAt(builder.length() - 1);
                        }
                    } else {
                        builder.append(encode(String.valueOf(value)));
                    }
                }
                queryString.append(builder);
            }
        }

        // Build and return full URL
        return http.url + path + buildPath(pathSegments) + queryString;
    }

    @NotNull
    protected ConnectionDetails openConnection(@NotNull HttpURLConnection connection, @NotNull String objectField) throws IOException {
        // Get response
        final int statusCode = connection.getResponseCode();
        final String body = readBody(statusCode >= 400 ? connection.getErrorStream() : connection.getInputStream());
        final JsonObject json = GSONProvider.GSON.fromJson(body, JsonObject.class);
        if (json == null) {
            if (statusCode >= 400) throw new EAHttpResponseException(statusCode, connection.getResponseMessage(), body);
            throw new EAHttpRequestException("GET " + connection.getURL(), new IllegalStateException("Failed to parse JSON response"));
        }

        // Error
        if (statusCode >= 400 || json.has("message")) {
            // Get response code
            int responseCode = statusCode;
            if (json.has("code")) try {
                responseCode = json.get("code").getAsInt();
            } catch (final Exception ignored) {}

            // Get message
            String message = null;
            if (json.has("message")) try {
                message = json.get("message").getAsString();
            } catch (final Exception ignored) {
                throw new EAHttpResponseException(responseCode, "Unexpected error payload", body);
            }

            // Throw exception
            throw new EAHttpResponseException(responseCode, message, body);
        }

        // Get raw object
        final JsonElement raw = json.has(objectField) ? json.get(objectField) : null;

        // Parse pagination fields
        final int page = getIntField(json, "page");
        final int limit = getIntField(json, "limit");
        final int count = getIntField(json, "count");
        final int total = getIntField(json, "total");
        final int all = getIntField(json, "all");

        // Return details
        return new ConnectionDetails(connection, raw, page, limit, count, total, all);
    }

    @NotNull
    protected ConnectionDetails openConnectionGET(@NotNull String url, @NotNull String objectField) throws IOException {
        LOGGER.fine("Opening GET connection to " + url + " for object field '" + objectField + "'");
        final HttpURLConnection connection = (HttpURLConnection) URI.create(url).toURL().openConnection();
        connection.setRequestMethod("GET");
        for (final Map.Entry<String, String> header : http.headers.entrySet()) connection.setRequestProperty(header.getKey(), header.getValue());
        return openConnection(connection, objectField);
    }

    @NotNull
    protected ConnectionDetails openConnectionPOST(@NotNull String url, @NotNull String objectField, @NotNull String body) throws IOException {
        final HttpURLConnection connection = (HttpURLConnection) URI.create(url).toURL().openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        for (final Map.Entry<String, String> header : http.headers.entrySet()) connection.setRequestProperty(header.getKey(), header.getValue());
        connection.getOutputStream().write(body.getBytes(StandardCharsets.UTF_8));
        return openConnection(connection, objectField);
    }

    protected static class ConnectionDetails {
        @NotNull private final HttpURLConnection connection;
        @Nullable private final JsonElement raw;
        private final int page;
        private final int limit;
        private final int count;
        private final int total;
        private final int all;

        private ConnectionDetails(@NotNull HttpURLConnection connection, @Nullable JsonElement raw, int page, int limit, int count, int total, int all) {
            this.connection = connection;
            this.raw = raw;
            this.page = page;
            this.limit = limit;
            this.count = count;
            this.total = total;
            this.all = all;
        }
    }

    @NotNull
    private <O extends EAObject> PaginatedResponse<O> executePage(@NotNull Class<O> objectType, @NotNull String fieldName, @Nullable Map<String, Object> queryParams, @Nullable Integer page, @Nullable Integer limit) {
        final Map<String, Object> params = new HashMap<>();
        if (queryParams != null) params.putAll(queryParams);
        if (page != null) params.put("page", page);
        if (limit != null) params.put("limit", limit);
        final String url = buildUrl(params);

        HttpURLConnection connection = null;
        try {
            // Open connection and get details
            final ConnectionDetails details = openConnectionGET(url, fieldName);
            connection = details.connection;

            // Parse objects
            List<O> items = Collections.emptyList();
            if (details.raw != null) {
                final List<O> fromJson = GSONProvider.GSON.fromJson(details.raw, TypeToken.getParameterized(List.class, objectType).getType());
                if (fromJson != null) items = fromJson;
            }

            // Build and return PaginatedResponse
            return new PaginatedResponse<>(fieldName, items, details.page, details.limit, details.count, details.total, details.all,
                (fetcherPage, fetcherLimit) -> retrievePage(objectType, fieldName, fetcherPage, fetcherLimit, queryParams));
        } catch (final RuntimeException e) {
            throw e;
        } catch (final Exception e) {
            throw new EAHttpRequestException("GET " + url, e);
        } finally {
            if (connection != null) connection.disconnect();
        }
    }

    @NotNull
    private <O extends EAObject> EAItemData<O> executeOne(@NotNull Class<O> objectType, @NotNull String fieldName, @NotNull String @Nullable ... pathSegments) {
        final String url = buildUrl(null, pathSegments);

        HttpURLConnection connection = null;
        try {
            // Open connection and get details
            final ConnectionDetails details = openConnectionGET(url, fieldName);
            connection = details.connection;

            // Parse object
            O item = null;
            if (details.raw != null) item = GSONProvider.GSON.fromJson(details.raw, objectType);

            // Build and return EAItemData
            return new EAItemData<>(fieldName, item);
        } catch (final RuntimeException e) {
            throw e;
        } catch (final Exception e) {
            throw new EAHttpRequestException("GET " + url, e);
        } finally {
            if (connection != null) connection.disconnect();
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

    private int getIntField(@NotNull JsonObject json, @NotNull String field) {
        if (json.has(field)) try {
            return json.get(field).getAsInt();
        } catch (final Exception ignored) {}
        return 0;
    }

    @NotNull
    private String encode(@NotNull String value) {
        try {
            return URLEncoder.encode(value, "UTF-8");
        } catch (final Exception e) {
            return value;
        }
    }

    @NotNull
    private static String buildPath(@NotNull String @Nullable ... pathSegments) {
        final StringBuilder path = new StringBuilder();
        if (pathSegments != null) for (final String segment : pathSegments) {
            if (!segment.startsWith("/")) path.append("/");
            path.append(segment);
        }
        return path.toString();
    }
}
