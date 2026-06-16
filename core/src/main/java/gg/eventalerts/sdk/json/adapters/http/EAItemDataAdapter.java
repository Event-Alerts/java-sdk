package gg.eventalerts.sdk.json.adapters.http;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import gg.eventalerts.sdk.json.GSONProvider;
import gg.eventalerts.sdk.object.EAObject;
import gg.eventalerts.sdk.object.http.EAItemData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;


public class EAItemDataAdapter extends TypeAdapter<EAItemData<? extends EAObject>> {
    @Override
    public void write(@NotNull JsonWriter out, @Nullable EAItemData<? extends EAObject> value) throws IOException {
        if (value == null) {
            out.nullValue();
            return;
        }

        out.beginObject();
        out.name(value.itemFieldName);
        GSONProvider.GSON.toJson(value.item, value.item.getClass(), out);
        out.endObject();
    }

    @Override @Nullable
    public EAItemData<? extends EAObject> read(@NotNull JsonReader in) {
        throw new UnsupportedOperationException("Deserialization of EAItemData is not supported");
    }
}
