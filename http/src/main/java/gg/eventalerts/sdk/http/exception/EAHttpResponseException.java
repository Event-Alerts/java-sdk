package gg.eventalerts.sdk.http.exception;

import com.google.gson.JsonObject;
import gg.eventalerts.sdk.json.GSONProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;


public class EAHttpResponseException extends EAHttpException {
    private final int statusCode;
    @Nullable private final String responseBody;

    public EAHttpResponseException(int statusCode, @Nullable String message, @Nullable String responseBody) {
        super("HTTP " + statusCode + (message == null || message.isEmpty() ? "" : ": " + message));
        this.statusCode = statusCode;
        this.responseBody = responseBody;
    }

    public int getStatusCode() {
        return statusCode;
    }

    @Nullable
    public String getResponseBody() {
        return responseBody;
    }

    @NotNull
    public Optional<String> getResponseBodyOptional() {
        return Optional.ofNullable(responseBody);
    }

    @Nullable
    public JsonObject getResponseBodyJson() {
        if (responseBody != null) try {
            return GSONProvider.GSON.fromJson(responseBody, JsonObject.class);
        } catch (final Exception ignored) {}
        return null;
    }

    @NotNull
    public Optional<JsonObject> getResponseBodyJsonOptional() {
        return Optional.ofNullable(getResponseBodyJson());
    }
}
