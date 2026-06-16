package gg.eventalerts.sdk.json.adapters;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;


public class CollectionAdapter implements TypeAdapterFactory {
    @Override @Nullable
    public <T> TypeAdapter<T> create(@NotNull Gson gson, @NotNull TypeToken<T> type) {
        final Class<? super T> rawType = type.getRawType();
        if (!Collection.class.isAssignableFrom(rawType)) return null;
        final Type javaType = type.getType();
        if (!(javaType instanceof ParameterizedType)) return null;

        // Get element type
        final Type elementType = ((ParameterizedType) javaType).getActualTypeArguments()[0];
        final TypeAdapter<Object> elementAdapter = (TypeAdapter<Object>) gson.getAdapter(TypeToken.get(elementType));

        // Create adapter
        return new TypeAdapter<T>() {
            @Override
            public void write(@NotNull JsonWriter out, @Nullable T value) throws IOException {
                // Null or empty
                if (value == null || ((Collection<?>) value).isEmpty()) {
                    out.nullValue();
                    return;
                }

                // Write elements
                out.beginArray();
                for (final Object element : (Collection<?>) value) elementAdapter.write(out, element);
                out.endArray();
            }

            @Override @Nullable
            public T read(@NotNull JsonReader in) throws IOException {
                // Null
                if (in.peek() == JsonToken.NULL) {
                    in.nextNull();
                    return null;
                }

                // Create collection
                Collection<Object> collection;
                if (Set.class.isAssignableFrom(rawType)) {
                    // Set
                    collection = new LinkedHashSet<>();
                } else if (List.class.isAssignableFrom(rawType)) {
                    // List
                    collection = new ArrayList<>();
                } else if (rawType.isInterface()) {
                    // Interface
                    collection = new ArrayList<>();
                } else try {
                    // Unknown, try to create empty collection
                    collection = (Collection<Object>) rawType.getDeclaredConstructor().newInstance();
                } catch (final Exception e) {
                    // Unknown creation failed, fallback to ArrayList
                    collection = new ArrayList<>();
                }

                // Add elements
                in.beginArray();
                while (in.hasNext()) {
                    final Object element = elementAdapter.read(in);
                    if (element != null) collection.add(element);
                }
                in.endArray();

                return (T) collection;
            }
        };
    }
}
