package gg.eventalerts.sdk.json.adapters.http;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import gg.eventalerts.sdk.object.http.EAItemData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;


public class EAItemDataAdapter implements TypeAdapterFactory {
    @Override @Nullable
    public <T> TypeAdapter<T> create(@NotNull Gson gson, @NotNull TypeToken<T> type) {
        if (!EAItemData.class.isAssignableFrom(type.getRawType())) return null;

        final TypeAdapter<T> delegate = gson.getDelegateAdapter(this, type);
        return new TypeAdapter<T>() {
            @Override
            public void write(@NotNull JsonWriter out, @Nullable T value) throws IOException {
                // Null
                if (value == null) {
                    out.nullValue();
                    return;
                }

                final EAItemData<?> itemData = (EAItemData<?>) value;
                final JsonObject jsonObject = delegate.toJsonTree(value).getAsJsonObject();
                jsonObject.add(itemData.itemFieldName, jsonObject.remove(EAItemData.KEY_ITEM));
                gson.toJson(jsonObject, out);
            }

            @Override
            public T read(@NotNull JsonReader in) {
                throw new UnsupportedOperationException("Deserialization of EAItemData is not supported");
            }
        };
    }
}
