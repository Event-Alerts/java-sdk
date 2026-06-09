package gg.eventalerts.sdk.http.exception;

import org.jetbrains.annotations.Nullable;


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
}
