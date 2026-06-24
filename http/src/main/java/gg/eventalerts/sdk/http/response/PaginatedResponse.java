package gg.eventalerts.sdk.http.response;

import gg.eventalerts.sdk.http.action.EAAction;
import gg.eventalerts.sdk.object.EAObject;
import gg.eventalerts.sdk.object.http.EAPageData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;


/**
 * Immutable result of a paginated API request.
 * <br>Holds the current page of items alongside pagination metadata, and provides methods to fetch subsequent pages or accumulate more items.
 *
 * @param   <O> the item type
 */
public class PaginatedResponse<O extends EAObject> extends EAPageData<O> {
    /**
     * (page, limit) -> EAAction to fetch the next page of items
     */
    @NotNull private final BiFunction<Integer, Integer, EAAction<PaginatedResponse<O>>> fetcher;

    public PaginatedResponse(@NotNull String itemsFieldName, @NotNull List<O> items, int page, int limit, int count, int total, int all, @NotNull BiFunction<Integer, Integer, EAAction<PaginatedResponse<O>>> fetcher) {
        super(itemsFieldName, items, page, limit, count, total, all);
        this.fetcher = fetcher;
    }

    /**
     * Creates an action that fetches the next page
     *
     * @return  action yielding the next {@link PaginatedResponse} or {@code null} if there are no more pages
     */
    @Nullable
    public EAAction<PaginatedResponse<O>> retrieveNextPage() {
        if (!hasNextPage()) return null;
        return fetcher.apply(page + 1, limit);
    }

    /**
     * Creates an action that fetches up to {@code count} additional items from subsequent pages.
     * <br>Items on the current page are not included.
     * <br>Use {@link #retrieveNextPage()} instead when you need the full {@link PaginatedResponse} for a single page.
     *
     * @param   count   maximum number of additional items to fetch
     *
     * @return  action yielding the accumulated additional items as a flat list
     */
    @NotNull
    public EAAction<List<O>> retrieveMore(int count) {
        return new EAAction<>("paginate more", () -> {
            final List<O> result = new ArrayList<>();
            PaginatedResponse<O> current = this;
            int remaining = count;
            while (remaining > 0) {
                final EAAction<PaginatedResponse<O>> nextPageAction = current.retrieveNextPage();
                if (nextPageAction == null) break; // No more pages
                current = nextPageAction.complete();
                final int take = Math.min(current.items.size(), remaining);
                result.addAll(current.items.subList(0, take));
                remaining -= take;
            }
            return result;
        });
    }

    /**
     * Creates an empty, non-pageable response with no items.
     * <br>Useful as a safe fallback with {@link gg.eventalerts.sdk.http.action.EAAction#onErrorReturnEmptyPage()}.
     *
     * @param   <O> the item type
     *
     * @return  an empty {@link PaginatedResponse} where {@link #hasNextPage()} is always {@code false}
     */
    @NotNull
    public static <O extends EAObject> PaginatedResponse<O> empty() {
        return new PaginatedResponse<>("", Collections.emptyList(), 1, 0, 0, 0, 0,
                (page, limit) -> { throw new IllegalStateException("Cannot paginate an empty PaginatedResponse"); });
    }
}
