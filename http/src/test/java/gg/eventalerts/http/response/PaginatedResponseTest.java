package gg.eventalerts.http.response;

import gg.eventalerts.sdk.http.action.EAAction;
import gg.eventalerts.sdk.http.response.PaginatedResponse;
import gg.eventalerts.sdk.object.EAObject;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;


class PaginatedResponseTest {
    @Test
    void emptyHasNoItems() {
        assertTrue(PaginatedResponse.empty().items.isEmpty());
    }

    @Test
    void emptyHasNoNextPage() {
        final PaginatedResponse<EAObject> empty = PaginatedResponse.empty();
        assertFalse(empty.hasNextPage());
        assertNull(empty.retrieveNextPage());
    }

    @Test
    void emptyRetrieveMoreReturnsEmptyList() throws Exception {
        final List<EAObject> result = PaginatedResponse.empty().retrieveMore(10).submit().get(5, TimeUnit.SECONDS);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void retrieveMoreAccumulatesAcrossPages() throws Exception {
        final EAObject item = new EAObject();

        // page3: 2 items, no next page (3*2=6 >= 7)
        final PaginatedResponse<EAObject> page3 = new PaginatedResponse<>(
            "", List.of(item, item), 3, 2, 2, 7, 7,
            (page, limit) -> { throw new IllegalStateException("no more pages"); }
        );
        // page2: 2 items, has next (2*2=4 < 7)
        final PaginatedResponse<EAObject> page2 = new PaginatedResponse<>(
            "", List.of(item, item), 2, 2, 2, 7, 7,
            (page, limit) -> new EAAction<>("fetch page3", () -> page3)
        );
        // page1: 2 items, has next (1*2=2 < 7)
        final PaginatedResponse<EAObject> page1 = new PaginatedResponse<>(
            "", List.of(item, item), 1, 2, 2, 7, 7,
            (page, limit) -> new EAAction<>("fetch page2", () -> page2)
        );

        // Request 3 more items — should take 2 from page2 and 1 from page3
        final List<EAObject> result = page1.retrieveMore(3).submit().get(5, TimeUnit.SECONDS);
        assertEquals(3, result.size());
    }

    @Test
    void retrieveMoreStopsAtEndOfData() throws Exception {
        final EAObject item = new EAObject();

        final PaginatedResponse<EAObject> page2 = new PaginatedResponse<>(
            "", List.of(item, item), 2, 5, 2, 7, 7,
            (page, limit) -> { throw new IllegalStateException("no more pages"); }
        );

        final PaginatedResponse<EAObject> page1 = new PaginatedResponse<>(
            "", List.of(item, item, item, item, item), 1, 5, 5, 7, 7,
            (page, limit) -> new EAAction<>("fetch page2", () -> page2)
        );

        final List<EAObject> result = page1.retrieveMore(100).submit().get(5, TimeUnit.SECONDS);
        assertEquals(2, result.size());
    }

    @Test
    void retrieveMoreDoesNotFetchUnnecessaryPages() throws Exception {
        final EAObject item = new EAObject();
        final AtomicInteger fetchCount = new AtomicInteger(0);

        final PaginatedResponse<EAObject> page2 = new PaginatedResponse<>(
            "", List.of(item, item, item), 2, 3, 3, 6, 6,
            (page, limit) -> {
                fetchCount.incrementAndGet();
                throw new IllegalStateException("no more pages");
            }
        );

        final PaginatedResponse<EAObject> page1 = new PaginatedResponse<>(
            "", List.of(item, item, item), 1, 3, 3, 6, 6,
            (page, limit) -> {
                fetchCount.incrementAndGet();
                return new EAAction<>("fetch page2", () -> page2);
            }
        );

        page1.retrieveMore(2).submit().get(5, TimeUnit.SECONDS);
        assertEquals(1, fetchCount.get());
    }

    @Test
    void onErrorReturnEmptyPageIntegration() throws Exception {
        final EAAction<PaginatedResponse<EAObject>> action =
            new EAAction<PaginatedResponse<EAObject>>("fail", () -> { throw new RuntimeException("boom"); })
                .onErrorReturnEmptyPage();

        final PaginatedResponse<EAObject> result = action.submit().get(5, TimeUnit.SECONDS);
        assertNotNull(result);
        assertTrue(result.items.isEmpty());
        assertFalse(result.hasNextPage());
    }
}
