package gg.eventalerts.sdk.http.exception;

public abstract class EAHttpException extends RuntimeException {
    protected EAHttpException(String message) {
        super(message);
    }

    protected EAHttpException(String message, Throwable cause) {
        super(message, cause);
    }
}
