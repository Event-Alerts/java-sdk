package gg.eventalerts.sdk.object.http;

import com.google.gson.annotations.SerializedName;
import gg.eventalerts.sdk.json.KeyGetter;
import gg.eventalerts.sdk.json.adapters.http.EAPageDataAdapter;
import gg.eventalerts.sdk.object.EAObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;


public class EAPageData<O extends EAObject> extends EAObject {
    /**
     * Only used for {@link EAPageDataAdapter}
     */
    @NotNull public static final String KEY_ITEMS = "items";
    @NotNull public static final String KEY_PAGE = "page";
    @NotNull public static final String KEY_LIMIT = "limit";
    @NotNull public static final String KEY_COUNT = "count";
    @NotNull public static final String KEY_TOTAL = "total";
    @NotNull public static final String KEY_ALL = "all";

    /**
     * The name of the field containing {@link #items}
     */
    @NotNull public transient final String itemsFieldName;
    @KeyGetter("itemsFieldName") @SerializedName(KEY_ITEMS) @NotNull public final List<O> items;
    /**
     * 1-based
     */
    @SerializedName(KEY_PAGE) public final int page;
    /**
     * The number of items per page
     */
    @SerializedName(KEY_LIMIT) public final int limit;
    /**
     * Number of items on this page
     * <br>This is always equal to {@link #items items.size()}
     */
    @SerializedName(KEY_COUNT) public final int count;
    /**
     * Total number of items across all pages with filters
     */
    @SerializedName(KEY_TOTAL) public final int total;
    /**
     * Total number of items across all pages without filters
     */
    @SerializedName(KEY_ALL) @Nullable public final Integer all;

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
