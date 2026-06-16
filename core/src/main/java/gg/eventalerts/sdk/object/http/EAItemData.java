package gg.eventalerts.sdk.object.http;

import com.google.gson.annotations.SerializedName;
import gg.eventalerts.sdk.json.KeyGetter;
import gg.eventalerts.sdk.json.adapters.http.EAItemDataAdapter;
import gg.eventalerts.sdk.object.EAObject;
import org.jetbrains.annotations.NotNull;


public class EAItemData<O extends EAObject> extends EAObject {
    /**
     * Only used for {@link EAItemDataAdapter}
     */
    @NotNull public static final String KEY_ITEM = "item";

    /**
     * The name of the field containing {@link #item}
     */
    @NotNull public transient final String itemFieldName;
    @KeyGetter("itemFieldName") @SerializedName(KEY_ITEM) @NotNull public final O item;

    public EAItemData(@NotNull String itemFieldName, @NotNull O item) {
        this.itemFieldName = itemFieldName;
        this.item = item;
    }
}
