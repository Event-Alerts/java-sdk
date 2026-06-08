package gg.eventalerts.sdk.http.response;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class FailedResponse<O> extends APIResponse<O> implements MessageResponse {
    @Nullable public Exception exception;
    /**
     * The current response at the time of failure
     */
    @Nullable public APIResponse<O> response;

    public FailedResponse() {}

    public FailedResponse(@NotNull Exception exception, @Nullable APIResponse<O> response) {
        this.exception = exception;
        this.response = response;
    }

    @Override @Nullable
    public String getMessage() {
        return exception != null ? exception.getMessage() : null;
    }
}
