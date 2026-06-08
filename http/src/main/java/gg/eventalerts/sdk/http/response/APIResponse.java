package gg.eventalerts.sdk.http.response;

import gg.eventalerts.sdk.object.EAObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;


public class APIResponse<O> extends EAObject {
    public boolean is(@NotNull Class<?> clazz) {
        return clazz.isInstance(this);
    }

    @NotNull
    public <R> R as(@NotNull Class<R> clazz) {
        return clazz.cast(this);
    }

    @NotNull
    public <R> Optional<R> asOptional(@NotNull Class<R> clazz) {
        return is(clazz) ? Optional.of(as(clazz)) : Optional.empty();
    }

    @NotNull
    public <R> R asElse(@NotNull Class<R> clazz, @NotNull R defaultValue) {
        return is(clazz) ? as(clazz) : defaultValue;
    }

    @Nullable
    public <R> R asElseNull(@NotNull Class<R> clazz) {
        return is(clazz) ? as(clazz) : null;
    }

    public boolean isMessage() {
        return is(MessageResponse.class);
    }

    public boolean isFailedOrError() {
        return isFailed() || isError();
    }

    public boolean isFailed() {
        return is(FailedResponse.class);
    }

    public boolean isError() {
        return this instanceof ErrorResponse;
    }

    public boolean isPaginated() {
        return is(PaginatedResponse.class);
    }

    public boolean isSingle() {
        return is(SingleResponse.class);
    }

    @NotNull
    public MessageResponse asMessage() {
        return as(MessageResponse.class);
    }

    /**
     * Alias for {@link #asMessage()}
     */
    @NotNull
    public MessageResponse asFailedOrError() {
        return asMessage();
    }

    @NotNull
    public FailedResponse<O> asFailed() {
        return as(FailedResponse.class);
    }

    @NotNull
    public ErrorResponse<O> asError() {
        return as(ErrorResponse.class);
    }

    @NotNull
    public PaginatedResponse<O> asPaginated() {
        return as(PaginatedResponse.class);
    }

    @NotNull
    public SingleResponse<O> asSingle() {
        return as(SingleResponse.class);
    }

    @NotNull
    public Optional<MessageResponse> asMessageOptional() {
        return isMessage() ? Optional.of(asMessage()) : Optional.empty();
    }

    @NotNull
    public Optional<MessageResponse> asFailedOrErrorOptional() {
        return asMessageOptional();
    }

    @NotNull
    public Optional<FailedResponse<O>> asFailedOptional() {
        return isFailed() ? Optional.of(asFailed()) : Optional.empty();
    }

    @NotNull
    public Optional<ErrorResponse<O>> asErrorOptional() {
        return isError() ? Optional.of(asError()) : Optional.empty();
    }

    @NotNull
    public Optional<PaginatedResponse<O>> asPaginatedOptional() {
        return isPaginated() ? Optional.of(asPaginated()) : Optional.empty();
    }

    @NotNull
    public Optional<SingleResponse<O>> asSingleOptional() {
        return isSingle() ? Optional.of(asSingle()) : Optional.empty();
    }

    @NotNull
    public MessageResponse asMessageElse(@NotNull MessageResponse defaultValue) {
        return asElse(MessageResponse.class, defaultValue);
    }

    @NotNull
    public FailedResponse<O> asFailedElse(@NotNull FailedResponse<O> defaultValue) {
        return asElse(FailedResponse.class, defaultValue);
    }

    @NotNull
    public ErrorResponse<O> asErrorElse(@NotNull ErrorResponse<O> defaultValue) {
        return asElse(ErrorResponse.class, defaultValue);
    }

    @NotNull
    public PaginatedResponse<O> asPaginatedElse(@NotNull PaginatedResponse<O> defaultValue) {
        return asElse(PaginatedResponse.class, defaultValue);
    }

    @NotNull
    public SingleResponse<O> asSingleElse(@NotNull SingleResponse<O> defaultValue) {
        return asElse(SingleResponse.class, defaultValue);
    }

    @Nullable
    public MessageResponse asMessageElseNull() {
        return asElseNull(MessageResponse.class);
    }

    @Nullable
    public MessageResponse asFailedOrErrorElseNull() {
        return asMessageElseNull();
    }

    @Nullable
    public FailedResponse<O> asFailedElseNull() {
        return asElseNull(FailedResponse.class);
    }

    @Nullable
    public ErrorResponse<O> asErrorElseNull() {
        return asElseNull(ErrorResponse.class);
    }

    @Nullable
    public PaginatedResponse<O> asPaginatedElseNull() {
        return asElseNull(PaginatedResponse.class);
    }

    @Nullable
    public SingleResponse<O> asSingleElseNull() {
        return asElseNull(SingleResponse.class);
    }
}
