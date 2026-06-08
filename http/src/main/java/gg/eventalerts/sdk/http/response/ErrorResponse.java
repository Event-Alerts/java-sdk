package gg.eventalerts.sdk.http.response;

import org.jetbrains.annotations.Nullable;


public class ErrorResponse<O> extends CodeResponse<O> implements MessageResponse {
    @Nullable public String message;

    public ErrorResponse() {}

    public ErrorResponse(int code, @Nullable String message) {
        super(code);
        this.message = message;
    }

    @Override @Nullable
    public String getMessage() {
        return message;
    }
}
