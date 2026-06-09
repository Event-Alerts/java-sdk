package gg.eventalerts.sdk.http.action;

import gg.eventalerts.sdk.http.exception.EAHttpRequestException;
import gg.eventalerts.sdk.http.exception.EAHttpTimeoutException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.NoSuchElementException;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.Collections;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Represents a deferred HTTP action.
 * <p>
 * Actions are async-first: {@link #queue()} and {@link #submit()} are the primary
 * execution paths, while {@link #complete()} is the blocking convenience method.
 * Actions can be composed with mapping, error handling, and side-effect taps
 * without forcing immediate execution.
 *
 * @param <T> the action result type
 */
public class EAAction<T> {
    @NotNull private static final Logger LOGGER = Logger.getLogger(EAAction.class.getName());
    @NotNull private static final ExecutorService EXECUTOR = Executors.newCachedThreadPool(runnable -> {
        final Thread thread = new Thread(runnable, "EAHTTP-Action");
        thread.setDaemon(true);
        return thread;
    });
    @NotNull private static final ScheduledExecutorService SCHEDULER = Executors.newSingleThreadScheduledExecutor(runnable -> {
        final Thread thread = new Thread(runnable, "EAHTTP-Action-Scheduler");
        thread.setDaemon(true);
        return thread;
    });

    @NotNull private final String description;
    @NotNull private final Function<Void, CompletableFuture<T>> submitter;

    public EAAction(@NotNull String description, @NotNull Callable<T> task) {
        this(description, ignored -> submitCallable(task));
    }

    private EAAction(@NotNull String description, @NotNull Function<Void, CompletableFuture<T>> submitter) {
        this.description = description;
        this.submitter = submitter;
    }

    @NotNull
    public CompletableFuture<T> submit() {
        try {
            return submitter.apply(null);
        } catch (final RuntimeException runtimeException) {
            final CompletableFuture<T> future = new CompletableFuture<>();
            future.completeExceptionally(runtimeException);
            return future;
        }
    }

    /**
     * Queues this action with no success handler and {@link #getDefaultFailure() default failure handling}.
     */
    public void queue() {
        queue(null);
    }

    /**
     * Queues this action and invokes the provided success callback if the action completes successfully.
     *
     * @param success the success callback, or {@code null} to ignore success
     */
    public void queue(@Nullable Consumer<? super T> success) {
        queue(success, getDefaultFailure());
    }

    /**
     * Queues this action and invokes the provided callbacks when the action completes.
     * <p>
     * If the action succeeds, {@code success} receives the result.
     * If the action fails and {@code failure} is present, the failure callback receives the throwable.
     * If the failure callback is absent, the failure is logged and fatal {@link Error}s are rethrown.
     *
     * @param success the success callback, or {@code null} to ignore success
     * @param failure the failure callback, or {@code null} to ignore failure
     */
    public void queue(@Nullable Consumer<? super T> success, @Nullable Consumer<? super Throwable> failure) {
        submit().whenComplete((value, throwable) -> {
            if (throwable == null) {
                try {
                    if (success != null) success.accept(value);
                } catch (final Throwable error) {
                    handleFailure(error, failure);
                }
                return;
            }

            handleFailure(unwrap(throwable), failure);
        });
    }

    /**
     * Queues this action and invokes the provided success callback only if the completed value is present (not null).
     * <p>
     * If the action fails and {@code failure} is present, the failure callback receives the throwable.
     * If the action succeeds with {@code null}, neither callback is invoked.
     *
     * @param success the success callback, or {@code null} to ignore a present value
     * @param failure the failure callback, or {@code null} to use the default failure handling
     */
    public void ifPresent(@Nullable Consumer<? super T> success, @Nullable Consumer<? super Throwable> failure) {
        submit().whenComplete((value, throwable) -> {
            if (throwable != null) {
                handleFailure(unwrap(throwable), failure);
                return;
            }

            if (value == null) return;
            try {
                if (success != null) success.accept(value);
            } catch (final Throwable error) {
                handleFailure(error, failure);
            }
        });
    }

    /**
     * Queues this action and invokes the provided success callback only if the completed value is present (not null).
     *
     * @param success the success callback, or {@code null} to ignore a present value
     */
    public void ifPresent(@Nullable Consumer<? super T> success) {
        ifPresent(success, getDefaultFailure());
    }

    /**
     * Blocks until this action completes and returns the result.
     *
     * @return the completed result, or {@code null} if {@link #onErrorReturnNull()} is used
     * @throws EAHttpRequestException if the action fails with a checked/transport-style failure
     */
    public T complete() {
        try {
            return submit().join();
        } catch (final CompletionException e) {
            final Throwable throwable = e.getCause() == null ? e : e.getCause();
            if (throwable instanceof RuntimeException) throw (RuntimeException) throwable;
            if (throwable instanceof Error) throw (Error) throwable;
            throw new EAHttpRequestException(description, throwable);
        }
    }

    /**
     * Transforms a successful result into another value.
     *
     * @param mapper the success-value mapper
     * @param <R> the mapped result type
     * @return a new mapped action
     */
    @NotNull
    public <R> EAAction<R> map(@NotNull Function<? super T, ? extends R> mapper) {
        return new EAAction<>(description + " -> map", ignored -> submit().thenApply(mapper));
    }

    /**
     * Chains another action that depends on this action's successful result.
     *
     * @param mapper the action factory for the successful result
     * @param <R> the downstream result type
     * @return a new flat-mapped action
     */
    @NotNull
    public <R> EAAction<R> flatMap(@NotNull Function<? super T, ? extends EAAction<? extends R>> mapper) {
        return new EAAction<>(description + " -> flatMap", ignored -> submit().thenCompose(value -> {
            final EAAction<? extends R> action;
            try {
                action = mapper.apply(value);
            } catch (final Throwable error) {
                final CompletableFuture<R> failed = new CompletableFuture<>();
                failed.completeExceptionally(error);
                return failed;
            }
            if (action == null) {
                final CompletableFuture<R> failed = new CompletableFuture<>();
                failed.completeExceptionally(new IllegalStateException("FlatMap operand is null"));
                return failed;
            }
            return (CompletableFuture<R>) action.submit();
        }));
    }

    /**
     * Registers a success tap that observes the completed value without changing it.
     *
     * @param callback the success callback
     * @return a new action with the success tap applied
     */
    @NotNull
    public EAAction<T> onSuccess(@NotNull Consumer<? super T> callback) {
        return new EAAction<>(description + " -> onSuccess", ignored -> {
            final CompletableFuture<T> future = new CompletableFuture<>();
            submit().whenComplete((value, throwable) -> {
                if (throwable != null) {
                    future.completeExceptionally(unwrap(throwable));
                    return;
                }

                try {
                    callback.accept(value);
                    future.complete(value);
                } catch (final Throwable error) {
                    future.completeExceptionally(error);
                }
            });
            return future;
        });
    }

    /**
     * Registers a success tap that runs without needing the completed value.
     *
     * @param callback the success callback
     * @return a new action with the success tap applied
     */
    @NotNull
    public EAAction<T> onSuccess(@NotNull Runnable callback) {
        return onSuccess(value -> callback.run());
    }

    /**
     * Registers an error tap that observes failures without changing the failure result.
     *
     * @param callback the error callback
     * @return a new action with the error tap applied
     */
    @NotNull
    public EAAction<T> onError(@NotNull Consumer<? super Throwable> callback) {
        return new EAAction<>(description + " -> onError", ignored -> {
            final CompletableFuture<T> future = new CompletableFuture<>();
            submit().whenComplete((value, throwable) -> {
                if (throwable == null) {
                    future.complete(value);
                    return;
                }

                final Throwable cause = unwrap(throwable);
                try {
                    callback.accept(cause);
                    future.completeExceptionally(cause);
                } catch (final Throwable error) {
                    future.completeExceptionally(appendCause(error, cause));
                }
            });
            return future;
        });
    }

    /**
     * Registers an error tap that runs without needing the thrown value.
     *
     * @param callback the error callback
     * @return a new action with the error tap applied
     */
    @NotNull
    public EAAction<T> onError(@NotNull Runnable callback) {
        return onError(throwable -> callback.run());
    }

    /**
     * Transforms a failure into a success value.
     *
     * @param mapper the error-to-value mapper
     * @return a new action with error mapping applied
     */
    @NotNull
    public EAAction<T> onErrorMap(@NotNull Function<? super Throwable, ? extends T> mapper) {
        return onErrorMap(throwable -> true, mapper);
    }

    /**
     * Transforms matching failures into a success value.
     *
     * @param filter the failure filter
     * @param mapper the error-to-value mapper
     * @return a new action with filtered error mapping applied
     */
    @NotNull
    public EAAction<T> onErrorMap(@NotNull Predicate<? super Throwable> filter, @NotNull Function<? super Throwable, ? extends T> mapper) {
        return new EAAction<>(description + " -> onErrorMap", ignored -> {
            final CompletableFuture<T> future = new CompletableFuture<>();
            submit().whenComplete((value, throwable) -> {
                if (throwable == null) {
                    future.complete(value);
                    return;
                }

                final Throwable cause = unwrap(throwable);
                try {
                    if (filter.test(cause)) {
                        future.complete(mapper.apply(cause));
                    } else {
                        future.completeExceptionally(cause);
                    }
                } catch (final Throwable error) {
                    future.completeExceptionally(appendCause(error, cause));
                }
            });
            return future;
        });
    }

    /**
     * Replaces any failure with a fixed success value.
     *
     * @param value the fallback value
     * @return a new action that returns the fallback on failure
     */
    @NotNull
    public EAAction<T> onErrorReturn(@NotNull T value) {
        return onErrorMap(throwable -> value);
    }

    /**
     * Replaces any failure with an empty list.
     *
     * @return a new action that returns an empty list on failure
     */
    @NotNull
    public EAAction<T> onErrorReturnEmptyList() {
        return onErrorMap(throwable -> (T) Collections.emptyList());
    }

    /**
     * Replaces any failure with {@code null}.
     *
     * @return a new action that returns {@code null} on failure
     */
    @NotNull
    public EAAction<T> onErrorReturnNull() {
        return onErrorMap(throwable -> null);
    }

    /**
     * Transforms a failure into another action.
     *
     * @param mapper the failure-to-action mapper
     * @return a new action with error flat-mapping applied
     */
    @NotNull
    public EAAction<T> onErrorFlatMap(@NotNull Function<? super Throwable, ? extends EAAction<? extends T>> mapper) {
        return onErrorFlatMap(throwable -> true, mapper);
    }

    /**
     * Transforms matching failures into another action.
     *
     * @param filter the failure filter
     * @param mapper the failure-to-action mapper
     * @return a new action with filtered error flat-mapping applied
     */
    @NotNull
    public EAAction<T> onErrorFlatMap(@NotNull Predicate<? super Throwable> filter, @NotNull Function<? super Throwable, ? extends EAAction<? extends T>> mapper) {
        return new EAAction<>(description + " -> onErrorFlatMap", ignored -> {
            final CompletableFuture<T> future = new CompletableFuture<>();
            submit().whenComplete((value, throwable) -> {
                if (throwable == null) {
                    future.complete(value);
                    return;
                }

                final Throwable cause = unwrap(throwable);
                try {
                    if (!filter.test(cause)) {
                        future.completeExceptionally(cause);
                        return;
                    }

                    final EAAction<? extends T> action = mapper.apply(cause);
                    if (action == null) {
                        future.completeExceptionally(new IllegalStateException("FlatMapError operand is null", cause));
                        return;
                    }
                    action.submit().whenComplete((mapped, mappedThrowable) -> {
                        if (mappedThrowable == null) {
                            future.complete(mapped);
                        } else {
                            future.completeExceptionally(appendCause(unwrap(mappedThrowable), cause));
                        }
                    });
                } catch (final Throwable error) {
                    future.completeExceptionally(appendCause(error, cause));
                }
            });
            return future;
        });
    }

    /**
     * Delays execution by the given amount of time.
     *
     * @param delay the delay amount
     * @param unit the delay unit
     * @return a delayed action
     */
    @NotNull
    public EAAction<T> delay(long delay, @NotNull TimeUnit unit) {
        return new EAAction<>(description + " -> delay", ignored -> delayed(delay, unit).thenCompose(unused -> submit()));
    }

    /**
     * Delays execution by the given duration.
     *
     * @param duration the delay duration
     * @return a delayed action
     */
    @NotNull
    public EAAction<T> delay(@NotNull Duration duration) {
        return delay(duration.toMillis(), TimeUnit.MILLISECONDS);
    }

    /**
     * Fails the action if it does not complete within the given timeout.
     *
     * @param timeout the timeout amount
     * @param unit the timeout unit
     * @return a timeout-wrapped action
     */
    @NotNull
    public EAAction<T> timeout(long timeout, @NotNull TimeUnit unit) {
        final long timeoutMillis = unit.toMillis(timeout);
        return new EAAction<>(description + " -> timeout", ignored -> withTimeout(submit(), timeoutMillis));
    }

    @NotNull
    private static <T> CompletableFuture<T> submitCallable(@NotNull Callable<T> task) {
        final CompletableFuture<T> future = new CompletableFuture<>();
        EXECUTOR.execute(() -> {
            try {
                future.complete(task.call());
            } catch (final Throwable throwable) {
                future.completeExceptionally(throwable);
            }
        });
        return future;
    }

    @NotNull
    private static <T> CompletableFuture<T> withTimeout(@NotNull CompletableFuture<T> source, long timeoutMillis) {
        if (timeoutMillis <= 0) return source;

        final CompletableFuture<T> result = new CompletableFuture<>();
        final ScheduledFuture<?> timeoutTask = SCHEDULER.schedule(() -> {
            result.completeExceptionally(new EAHttpTimeoutException("Action timed out after " + timeoutMillis + "ms"));
        }, timeoutMillis, TimeUnit.MILLISECONDS);

        source.whenComplete((value, throwable) -> {
            if (throwable == null) {
                result.complete(value);
            } else {
                result.completeExceptionally(unwrap(throwable));
            }
            timeoutTask.cancel(true);
        });
        return result;
    }

    @NotNull
    private static CompletableFuture<Void> delayed(long delay, @NotNull TimeUnit unit) {
        final CompletableFuture<Void> future = new CompletableFuture<>();
        SCHEDULER.schedule(() -> future.complete(null), delay, unit);
        return future;
    }

    @NotNull
    private static Throwable unwrap(@NotNull Throwable throwable) {
        if (throwable instanceof CompletionException) {
            final Throwable cause = throwable.getCause();
            if (cause != null) return cause;
        }
        return throwable;
    }

    @NotNull
    private static Throwable appendCause(@NotNull Throwable throwable, @NotNull Throwable cause) {
        if (throwable == cause) return throwable;
        if (throwable.getCause() == null) {
            try {
                throwable.initCause(cause);
            } catch (final IllegalStateException ignored) {
                throwable.addSuppressed(cause);
            }
        } else {
            throwable.addSuppressed(cause);
        }
        return throwable;
    }

    private void handleFailure(@NotNull Throwable throwable, @Nullable Consumer<? super Throwable> failure) {
        if (failure == null) return;
        try {
            failure.accept(throwable);
        } catch (final Throwable callbackError) {
            if (callbackError instanceof Error) throw (Error) callbackError;
            throw (RuntimeException) callbackError;
        }
    }

    @NotNull
    public Consumer<? super Throwable> getDefaultFailure() {
        return t -> {
            LOGGER.log(Level.SEVERE, "Unhandled failure while executing " + description, t);
            if (t instanceof Error) throw (Error) t;
        };
    }
}
