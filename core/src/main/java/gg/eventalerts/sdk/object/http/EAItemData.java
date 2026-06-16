package gg.eventalerts.sdk.object.http;

import gg.eventalerts.sdk.json.KeyGetter;
import gg.eventalerts.sdk.object.EAObject;
import org.jetbrains.annotations.NotNull;


public class EAItemData<O extends EAObject> extends EAObject {
    /**
     * The name of the field containing {@link #item}
     */
    @NotNull public transient final String itemFieldName;
    @KeyGetter("itemFieldName") @NotNull public final O item;

    public EAItemData(@NotNull String itemFieldName, @NotNull O item) {
        this.itemFieldName = itemFieldName;
        this.item = item;
    }
}
