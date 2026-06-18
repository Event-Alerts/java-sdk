package gg.eventalerts.sdk.json.adapters;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import gg.eventalerts.sdk.json.GSONProvider;
import gg.eventalerts.sdk.object.EAObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;


public class EAObjectAdapter implements TypeAdapterFactory {
    @Override @Nullable
    public <T> TypeAdapter<T> create(@NotNull Gson gson, @NotNull TypeToken<T> type) {
        if (!EAObject.class.isAssignableFrom(type.getRawType())) return null;

        final TypeAdapter<T> delegate = gson.getDelegateAdapter(this, type);
        return new TypeAdapter<T>() {
            @Override
            public void write(@NotNull JsonWriter out, @Nullable T value) throws IOException {
                delegate.write(out, value);
            }

            @Override @Nullable
            public T read(@NotNull JsonReader in) {
                if (GSONProvider.peek(in) == null) return null;
                try {
                    return delegate.read(in);
                } catch (final Exception e) {
                    try {
                        in.skipValue();
                    } catch (final Exception ignored) {}
                    return null;
                }
            }
        };
    }
}
