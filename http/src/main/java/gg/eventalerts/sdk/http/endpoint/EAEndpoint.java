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


public abstract class EAEndpoint<O extends EAObject> {
    @NotNull private final EAHTTP http;

    public EAEndpoint(@NotNull EAHTTP http) {
        this.http = http;
    }

    @NotNull
    public abstract String getPath();

    @NotNull
    public String getPaginatedFieldName() {
        return getPath();
    }

    @NotNull
    public String getSingleFieldName() {
        final String paginatedFieldName = getPaginatedFieldName();
        return paginatedFieldName.endsWith("s") ? paginatedFieldName.substring(0, paginatedFieldName.length() - 1) : paginatedFieldName;
    }

    @NotNull
    public abstract Class<O> getObjectType();

    /**
     * Retrieves the first page of results
     */
    @NotNull
    public EAAction<PaginatedResponse<O>> retrievePage() {
        return retrievePage(null);
    }

    /**
     * Retrieves the first page of results.
     * <br>You can specify page/limit in the query parameters.
     *
     * @param   queryParams optional query parameters
     *
     * @return  action yielding a {@link PaginatedResponse} with items and metadata
     */
    @NotNull
    public EAAction<PaginatedResponse<O>> retrievePage(@Nullable Map<String, Object> queryParams) {
        return retrievePage(null, null, queryParams);
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
    public EAAction<PaginatedResponse<O>> retrievePage(@Nullable Integer page, @Nullable Integer limit, @Nullable Map<String, Object> queryParams) {
        return new EAAction<>("GET " + getPath(), () -> executePage(queryParams, page, limit));
    }

    /**
     * Auto-paginates to collect exactly {@code count} items (or fewer if the API has less)
     *
     * @param   count   total items to collect
     *
     * @return  action yielding the accumulated list
     */
    @NotNull
    public EAAction<List<O>> retrieveMany(int count) {
        return retrieveMany(count, null, null);
    }

    /**
     * Auto-paginates to collect exactly {@code count} items (or fewer if the API has less)
     *
     * @param   count       total items to collect
     * @param   startPage   optional page to start from (1-indexed)
     *
     * @return  action yielding the accumulated list
     */
    @NotNull
    public EAAction<List<O>> retrieveMany(int count, @Nullable Integer startPage) {
        return retrieveMany(count, startPage, null);
    }

    /**
     * Auto-paginates to collect exactly {@code count} items (or fewer if the API has less)
     *
     * @param   count       total items to collect
     * @param   queryParams optional query parameters (page/limit are ignored)
     *
     * @return  action yielding the accumulated list
     */
    @NotNull
    public EAAction<List<O>> retrieveMany(int count, @Nullable Map<String, Object> queryParams) {
        return retrieveMany(count, null, queryParams);
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
    public EAAction<List<O>> retrieveMany(int count, @Nullable Integer startPage, @Nullable Map<String, Object> queryParams) {
        return new EAAction<>("GET " + getPath(), () -> {
            final List<O> result = new ArrayList<>();
            int page = (startPage != null && startPage > 0) ? startPage : 1;
            int remaining = count;
            while (remaining > 0) {
                final int limit = Math.min(remaining, 50);
                final PaginatedResponse<O> response = executePage(queryParams, page, limit);
                result.addAll(response.items);
                remaining -= response.count;
                if (response.count < limit || result.size() >= response.total) break;
                page++;
            }
            return result;
        });
    }

    /**
     * Retrieves a single object with a path and returns it wrapped in a {@link EAItemData}
     *
     * @param   pathSegments    the path segments to append to the endpoint path
     *
     * @return  action yielding the retrieved object wrapped in an {@link EAItemData}
     */
    @NotNull
    public EAAction<EAItemData<O>> retrieveOneData(@NotNull String... pathSegments) {
        return new EAAction<>("GET " + getPath(), () -> executeOne(pathSegments));
    }

    /**
     * Retrieves a single object with a path
     *
     * @param   pathSegments    the path segments to append to the endpoint path
     *
     * @return  action yielding the retrieved object
     */
    @NotNull
    public EAAction<O> retrieveOne(@NotNull String... pathSegments) {
        return retrieveOneData(pathSegments).map(data -> data.item);
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
        final JsonObject json = GSONProvider.GSON.fromJson(body, JsonObject.class);
        if (json == null) {
            if (statusCode >= 400) throw new EAHttpResponseException(statusCode, connection.getResponseMessage(), body);
            throw new EAHttpRequestException("GET " + endpointPath, new IllegalStateException("Failed to parse JSON response"));
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
    private PaginatedResponse<O> executePage(@Nullable Map<String, Object> queryParams, @Nullable Integer page, @Nullable Integer limit) {
        final Map<String, Object> params = new HashMap<>();
        if (queryParams != null) params.putAll(queryParams);
        if (page != null) params.put("page", page);
        if (limit != null) params.put("limit", limit);

        final String endpointPath = getPath();
        final String objectField = getPaginatedFieldName();

        HttpURLConnection connection = null;
        try {
            // Open connection and get details
            final ConnectionDetails details = openConnection(endpointPath, objectField, params);
            connection = details.connection;

            // Parse objects
            List<O> items = Collections.emptyList();
            if (details.raw != null) {
                final List<O> fromJson = GSONProvider.GSON.fromJson(details.raw, TypeToken.getParameterized(List.class, getObjectType()).getType());
                if (fromJson != null) items = fromJson;
            }

            // Build and return PaginatedResponse
            return new PaginatedResponse<>(objectField, items, details.page, details.limit, details.count, details.total, details.all,
                (fetcherPage, fetcherLimit) -> retrievePage(fetcherPage, fetcherLimit, queryParams));
        } catch (final RuntimeException e) {
            throw e;
        } catch (final Exception e) {
            throw new EAHttpRequestException("GET " + endpointPath, e);
        } finally {
            if (connection != null) connection.disconnect();
        }
    }

    @NotNull
    private EAItemData<O> executeOne(@Nullable String... pathSegments) {
        final String endpointPath = getPath();
        final String objectField = getSingleFieldName();

        HttpURLConnection connection = null;
        try {
            // Open connection and get details
            final ConnectionDetails details = openConnection(endpointPath, objectField, null, pathSegments);
            connection = details.connection;

            // Parse object
            O item = null;
            if (details.raw != null) item = GSONProvider.GSON.fromJson(details.raw, getObjectType());

            // Build and return EAItemData
            return new EAItemData<>(objectField, item);
        } catch (final RuntimeException e) {
            throw e;
        } catch (final Exception e) {
            throw new EAHttpRequestException("GET " + endpointPath, e);
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
}
