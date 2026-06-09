package gg.eventalerts.http.action;

import gg.eventalerts.sdk.http.action.EAAction;
import gg.eventalerts.sdk.http.exception.EAHttpTimeoutException;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;


class EAActionTest {
    @Test
    void submitCompletesAsynchronously() throws Exception {
        final EAAction<Integer> action = new EAAction<>("base", () -> 5);

        assertEquals(Integer.valueOf(5), action.submit().get(5, TimeUnit.SECONDS));
    }

    @Test
    void mapTransformsResult() throws Exception {
        final EAAction<Integer> action = new EAAction<>("base", () -> 5);

        assertEquals("value-5", action.map(value -> "value-" + value).submit().get(5, TimeUnit.SECONDS));
    }

    @Test
    void flatMapChainsActions() throws Exception {
        final EAAction<Integer> action = new EAAction<>("base", () -> 5);

        assertEquals(Integer.valueOf(10), action.flatMap(value -> new EAAction<>("derived", () -> value * 2)).submit().get(5, TimeUnit.SECONDS));
    }

    @Test
    void queueWorksWithMappedAction() throws InterruptedException {
        final EAAction<Integer> action = new EAAction<>("base", () -> 5);
        final CountDownLatch latch = new CountDownLatch(1);
        final AtomicReference<String> result = new AtomicReference<>();

        action.map(value -> "value-" + value).queue(mapped -> {
            result.set(mapped);
            latch.countDown();
        }, throwable -> {
            throw new AssertionError("unexpected failure", throwable);
        });

        assertTrue(latch.await(5, TimeUnit.SECONDS));
        assertEquals("value-5", result.get());
    }

    @Test
    void ifPresentInvokesSuccessForPresentValue() throws InterruptedException {
        final EAAction<Integer> action = new EAAction<>("base", () -> 5);
        final CountDownLatch latch = new CountDownLatch(1);
        final AtomicReference<Integer> result = new AtomicReference<>();

        action.ifPresent(value -> {
            result.set(value);
            latch.countDown();
        }, throwable -> {
            throw new AssertionError("unexpected failure", throwable);
        });

        assertTrue(latch.await(5, TimeUnit.SECONDS));
        assertEquals(Integer.valueOf(5), result.get());
    }

    @Test
    void ifPresentSkipsNullValue() throws InterruptedException {
        final EAAction<Integer> action = new EAAction<>("base", () -> null);
        final CountDownLatch latch = new CountDownLatch(1);
        final AtomicBoolean called = new AtomicBoolean(false);

        action.ifPresent(value -> {
            called.set(true);
            latch.countDown();
        }, throwable -> {
            throw new AssertionError("unexpected failure", throwable);
        });

        assertFalse(latch.await(250, TimeUnit.MILLISECONDS));
        assertFalse(called.get());
    }

    @Test
    void ifPresentInvokesFailureOnError() throws InterruptedException {
        final EAAction<Integer> action = new EAAction<>("base", () -> {
            throw new IllegalStateException("boom");
        });
        final CountDownLatch latch = new CountDownLatch(1);
        final AtomicReference<Throwable> failure = new AtomicReference<>();

        action.ifPresent(value -> {
            throw new AssertionError("unexpected success");
        }, throwable -> {
            failure.set(throwable);
            latch.countDown();
        });

        assertTrue(latch.await(5, TimeUnit.SECONDS));
        assertInstanceOf(IllegalStateException.class, failure.get());
        assertEquals("boom", failure.get().getMessage());
    }

    @Test
    void onSuccessConsumerObservesSuccessWithoutChangingValue() throws Exception {
        final AtomicBoolean called = new AtomicBoolean(false);
        final EAAction<Integer> action = new EAAction<>("base", () -> 5)
                .onSuccess(value -> {
                    called.set(true);
                    assertEquals(Integer.valueOf(5), value);
                });

        assertEquals(Integer.valueOf(5), action.submit().get(5, TimeUnit.SECONDS));
        assertTrue(called.get());
    }

    @Test
    void onSuccessRunnableObservesSuccessWithoutChangingValue() throws Exception {
        final AtomicBoolean called = new AtomicBoolean(false);
        final EAAction<Integer> action = new EAAction<>("base", () -> 5)
                .onSuccess(() -> called.set(true));

        assertEquals(Integer.valueOf(5), action.submit().get(5, TimeUnit.SECONDS));
        assertTrue(called.get());
    }

    @Test
    void onErrorConsumerObservesFailureWithoutChangingIt() {
        final AtomicReference<Throwable> captured = new AtomicReference<>();
        final EAAction<Integer> action = new EAAction<Integer>("base", () -> {
            throw new IllegalStateException("boom");
        }).onError(captured::set);

        final IllegalStateException exception = assertThrows(IllegalStateException.class, action::complete);
        assertEquals("boom", exception.getMessage());
        assertInstanceOf(IllegalStateException.class, captured.get());
        assertEquals("boom", captured.get().getMessage());
    }

    @Test
    void onErrorRunnableObservesFailureWithoutChangingIt() {
        final AtomicBoolean called = new AtomicBoolean(false);
        final EAAction<Integer> action = new EAAction<Integer>("base", () -> {
            throw new IllegalStateException("boom");
        }).onError(() -> called.set(true));

        final IllegalStateException exception = assertThrows(IllegalStateException.class, action::complete);
        assertEquals("boom", exception.getMessage());
        assertTrue(called.get());
    }

    @Test
    void onErrorMapCanUseThrowableForFallback() throws Exception {
        final AtomicReference<Throwable> captured = new AtomicReference<>();
        final EAAction<String> action = new EAAction<String>("base", () -> {
            throw new IllegalStateException("boom");
        }).onErrorMap(error -> {
            captured.set(error);
            return "fallback";
        });

        assertEquals("fallback", action.submit().get(5, TimeUnit.SECONDS));
        assertInstanceOf(IllegalStateException.class, captured.get());
        assertEquals("boom", captured.get().getMessage());
    }

    @Test
    void onErrorMapCanReturnEmptyListFallback() throws Exception {
        final EAAction<java.util.List<Integer>> action = new EAAction<java.util.List<Integer>>("base", () -> {
            throw new IllegalStateException("boom");
        }).onErrorReturnEmptyList();

        assertTrue(action.submit().get(5, TimeUnit.SECONDS).isEmpty());
    }

    @Test
    void onErrorMapCanReturnNullFallback() throws Exception {
        final EAAction<Integer> action = new EAAction<Integer>("base", () -> {
            throw new IllegalStateException("boom");
        }).onErrorReturnNull();

        assertNull(action.submit().get(5, TimeUnit.SECONDS));
    }

    @Test
    void flatMapSkipsDownstreamWhenSourceFails() {
        final AtomicBoolean mapperCalled = new AtomicBoolean(false);
        final EAAction<Integer> action = new EAAction<>("base", () -> {
            throw new IllegalStateException("boom");
        });

        final IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> action.flatMap(value -> {
                    mapperCalled.set(true);
                    return new EAAction<>("derived", () -> value * 2);
                }).complete());

        assertEquals("boom", exception.getMessage());
        assertFalse(mapperCalled.get());
    }

    @Test
    void onErrorMapRecoversFromFailure() throws Exception {
        final EAAction<Integer> action = new EAAction<>("base", () -> {
            throw new IllegalStateException("boom");
        });

        assertEquals(Integer.valueOf(42), action.onErrorMap(error -> 42).submit().get(5, TimeUnit.SECONDS));
    }

    @Test
    void flatOnErrorMapRecoversFromFailure() throws Exception {
        final EAAction<Integer> action = new EAAction<>("base", () -> {
            throw new IllegalStateException("boom");
        });

        assertEquals(Integer.valueOf(99), action.onErrorFlatMap(error -> new EAAction<>("fallback", () -> 99)).submit().get(5, TimeUnit.SECONDS));
    }

    @Test
    void onSuccessCallbackFailurePropagates() {
        final EAAction<Integer> action = new EAAction<Integer>("base", () -> 5)
                .onSuccess(value -> {
                    throw new IllegalStateException("callback");
                });

        final IllegalStateException exception = assertThrows(IllegalStateException.class, action::complete);
        assertEquals("callback", exception.getMessage());
    }

    @Test
    void onErrorCallbackFailurePreservesOriginalCause() {
        final EAAction<Integer> action = new EAAction<Integer>("base", () -> {
            throw new IllegalStateException("boom");
        }).onError(error -> {
            throw new IllegalStateException("callback");
        });

        final IllegalStateException exception = assertThrows(IllegalStateException.class, action::complete);
        assertEquals("callback", exception.getMessage());
        assertNotNull(exception.getCause());
        assertEquals("boom", exception.getCause().getMessage());
    }

    @Test
    void timeoutFailsFast() {
        final EAAction<Integer> action = new EAAction<>("base", () -> {
            Thread.sleep(100);
            return 5;
        });

        final EAHttpTimeoutException throwable = assertThrows(EAHttpTimeoutException.class, () -> action.timeout(10, TimeUnit.MILLISECONDS).complete());
        assertTrue(throwable.getMessage().contains("timed out"));
    }

    @Test
    void delayDefersExecutionForSubmitAfter() throws Exception {
        final long start = System.nanoTime();
        final Integer result = new EAAction<>("base", () -> 7)
                .delay(Duration.ofMillis(75))
                .submit()
                .get(5, TimeUnit.SECONDS);
        final long elapsedMillis = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);

        assertEquals(Integer.valueOf(7), result);
        assertTrue(elapsedMillis >= 50);
    }

    @Test
    void queueAfterDefersCallbackExecution() throws InterruptedException {
        final long start = System.nanoTime();
        final CountDownLatch latch = new CountDownLatch(1);

        new EAAction<>("base", () -> 7)
                .delay(75, TimeUnit.MILLISECONDS)
                .queue(value -> {
                    assertEquals(Integer.valueOf(7), value);
                    latch.countDown();
                });

        assertTrue(latch.await(5, TimeUnit.SECONDS));
        final long elapsedMillis = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);
        assertTrue(elapsedMillis >= 50);
    }
}
