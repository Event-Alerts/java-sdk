package gg.eventalerts.sdk.json.adapters.http;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import gg.eventalerts.sdk.json.GSONProvider;
import gg.eventalerts.sdk.object.EAObject;
import gg.eventalerts.sdk.object.http.EAPageData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;


public class EAPageDataAdapter extends TypeAdapter<EAPageData<? extends EAObject>> {
    @Override
    public void write(@NotNull JsonWriter out, @Nullable EAPageData<? extends EAObject> value) throws IOException {
        if (value == null) {
            out.nullValue();
            return;
        }

        out.beginObject();
        out.name("page").value(value.page);
        out.name("limit").value(value.limit);
        out.name("count").value(value.count);
        out.name("total").value(value.total);
        if (value.all != null) out.name("all").value(value.all);
        out.name(value.itemsFieldName);
        GSONProvider.GSON.toJson(value.items, value.items.getClass(), out);
        out.endObject();
    }

    @Override @Nullable
    public EAPageData<? extends EAObject> read(@NotNull JsonReader in) {
        throw new UnsupportedOperationException("Deserialization of EAPageData is not supported");
    }
}
