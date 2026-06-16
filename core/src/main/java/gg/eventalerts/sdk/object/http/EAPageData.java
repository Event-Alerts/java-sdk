package gg.eventalerts.sdk.object.http;

import gg.eventalerts.sdk.json.KeyGetter;
import gg.eventalerts.sdk.object.EAObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;


public class EAPageData<O extends EAObject> extends EAObject {
    /**
     * The name of the field containing {@link #items}
     */
    @NotNull public transient final String itemsFieldName;
    @KeyGetter("itemsFieldName") @NotNull public final List<O> items;
    /**
     * 1-based
     */
    public final int page;
    /**
     * The number of items per page
     */
    public final int limit;
    /**
     * Number of items on this page
     * <br>This is always equal to {@link #items items.size()}
     */
    public final int count;
    /**
     * Total number of items across all pages with filters
     */
    public final int total;
    /**
     * Total number of items across all pages without filters
     */
    @Nullable public final Integer all;

    public EAPageData(@NotNull String itemsFieldName, @NotNull List<O> items, int page, int limit, int count, int total, @Nullable Integer all) {
        this.itemsFieldName = itemsFieldName;
        this.items = items;
        this.page = page;
        this.limit = limit;
        this.count = count;
        this.total = total;
        this.all = all;
    }

    public EAPageData(@NotNull String itemsFieldName, @NotNull List<O> items, int page, int limit, int total, @Nullable Integer all) {
        this(itemsFieldName, items, page, limit, items.size(), total, all);
    }

    /**
     * @return  {@code true} if there is at least one more page after this one
     */
    public boolean hasNextPage() {
        return (long) page * limit < total;
    }
}
