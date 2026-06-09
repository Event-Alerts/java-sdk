package gg.eventalerts.sdk.http.exception;

public class EAHttpRequestException extends EAHttpException {
    public EAHttpRequestException(String action, Throwable cause) {
        super("Failed to complete HTTP action: " + action, cause);
    }
}
