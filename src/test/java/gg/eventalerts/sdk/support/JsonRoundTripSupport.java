package gg.eventalerts.sdk.support;

import com.google.gson.reflect.TypeToken;
import gg.eventalerts.sdk.json.GSONProvider;
import org.jspecify.annotations.NonNull;

import java.lang.reflect.Type;
import java.util.Objects;


public final class JsonRoundTripSupport {
    private JsonRoundTripSupport() {}

    @NonNull
    public static <T> T roundTrip(T value, @NonNull Type type) {
        return Objects.requireNonNull(
                GSONProvider.GSON.fromJson(GSONProvider.GSON.toJson(value, type), type),
                "Round-trip JSON produced null for " + type);
    }

    @NonNull
    public static Type typeOf(@NonNull Class<?> rawType, @NonNull Class<?>... typeArguments) {
        return TypeToken.getParameterized(rawType, typeArguments).getType();
    }
}
