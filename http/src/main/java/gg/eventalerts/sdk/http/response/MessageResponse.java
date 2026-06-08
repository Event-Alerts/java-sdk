package gg.eventalerts.sdk.http.response;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;


public interface MessageResponse {
    @Nullable
    String getMessage();

    @NotNull
    default Optional<String> getMessageOptional() {
        return Optional.ofNullable(getMessage());
    }
}
