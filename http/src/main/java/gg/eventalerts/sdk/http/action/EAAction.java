package gg.eventalerts.sdk.http.action;

import gg.eventalerts.sdk.http.exception.EAHttpRequestException;
import gg.eventalerts.sdk.http.exception.EAHttpTimeoutException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
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
    @Nullable private final Function<? super Throwable, ? extends T> recovery;

    public EAAction(@NotNull String description, @NotNull Callable<T> task) {
        this(description, ignored -> submitCallable(task), null);
    }

    private EAAction(@NotNull String description, @NotNull Function<Void, CompletableFuture<T>> submitter, @Nullable Function<? super Throwable, ? extends T> recovery) {
        this.description = description;
        this.submitter = submitter;
        this.recovery = recovery;
    }

    @NotNull
    public CompletableFuture<T> submit() {
        try {
            final CompletableFuture<T> future = submitter.apply(null);
            return recovery == null ? future : applyRecovery(future);
        } catch (final RuntimeException runtimeException) {
            final CompletableFuture<T> future = new CompletableFuture<>();
            future.completeExceptionally(runtimeException);
            return recovery == null ? future : applyRecovery(future);
        }
    }

    public void queue() {
        queue(null, null);
    }

    public void queue(@Nullable Consumer<? super T> success) {
        queue(success, null);
    }

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

    @NotNull
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

    @NotNull
    public <R> EAAction<R> map(@NotNull Function<? super T, ? extends R> mapper) {
        return new EAAction<>(description + " -> map", ignored -> submit().thenApply(mapper), null);
    }

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
        }), null);
    }

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
        }, null);
    }

    @NotNull
    public EAAction<T> onSuccess(@NotNull Runnable callback) {
        return onSuccess(value -> callback.run());
    }

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
        }, null);
    }

    @NotNull
    public EAAction<T> onError(@NotNull Runnable callback) {
        return onError(throwable -> callback.run());
    }

    @NotNull
    public EAAction<T> onErrorMap(@NotNull Function<? super Throwable, ? extends T> mapper) {
        return onErrorMap(throwable -> true, mapper);
    }

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
        }, null);
    }

    @NotNull
    public EAAction<T> onErrorReturn(@NotNull T value) {
        return onErrorMap(throwable -> value);
    }

    @NotNull
    public EAAction<T> onErrorReturnEmptyList() {
        return onErrorMap(throwable -> (T) Collections.emptyList());
    }

    @NotNull
    public EAAction<T> onErrorReturnNull() {
        return onErrorMap(throwable -> null);
    }

    @NotNull
    public EAAction<T> onErrorFlatMap(@NotNull Function<? super Throwable, ? extends EAAction<? extends T>> mapper) {
        return onErrorFlatMap(throwable -> true, mapper);
    }

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
        }, null);
    }

    @NotNull
    public EAAction<T> delay(long delay, @NotNull TimeUnit unit) {
        return new EAAction<>(description + " -> delay", ignored -> delayed(delay, unit).thenCompose(unused -> submit()), null);
    }

    @NotNull
    public EAAction<T> delay(@NotNull Duration duration) {
        return delay(duration.toMillis(), TimeUnit.MILLISECONDS);
    }

    @NotNull
    public EAAction<T> timeout(long timeout, @NotNull TimeUnit unit) {
        final long timeoutMillis = unit.toMillis(timeout);
        return new EAAction<>(description + " -> timeout", ignored -> withTimeout(submit(), timeoutMillis), null);
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
    private CompletableFuture<T> applyRecovery(@NotNull CompletableFuture<T> source) {
        final CompletableFuture<T> result = new CompletableFuture<>();
        source.whenComplete((value, throwable) -> {
            if (throwable == null) {
                result.complete(value);
                return;
            }

            final Throwable cause = unwrap(throwable);
            try {
                if (cause instanceof Error || recovery == null) {
                    result.completeExceptionally(cause);
                    return;
                }
                result.complete(recovery.apply(cause));
            } catch (final Throwable error) {
                result.completeExceptionally(appendCause(error, cause));
            }
        });
        return result;
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
        if (failure != null) {
            try {
                failure.accept(throwable);
            } catch (final Throwable callbackError) {
                if (callbackError instanceof Error) throw (Error) callbackError;
                throw (RuntimeException) callbackError;
            }
            return;
        }

        LOGGER.log(Level.SEVERE, "Unhandled failure while executing " + description, throwable);
        if (throwable instanceof Error) throw (Error) throwable;
    }
}
