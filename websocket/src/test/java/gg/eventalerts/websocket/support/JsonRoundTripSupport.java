package gg.eventalerts.websocket.support;

import gg.eventalerts.sdk.json.GSONProvider;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.Objects;


public final class JsonRoundTripSupport {
    private JsonRoundTripSupport() {}

    @NotNull
    public static <T> T roundTrip(T value, @NotNull Type type) {
        return Objects.requireNonNull(
                GSONProvider.GSON.fromJson(GSONProvider.GSON.toJson(value, type), type),
                "Round-trip JSON produced null for " + type);
    }

    @NotNull
    public static Type typeOf(@NotNull Class<?> rawType, @NotNull Class<?>... typeArguments) {
        return GSONProvider.typeOf(rawType, typeArguments);
    }
}
