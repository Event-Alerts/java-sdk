package gg.eventalerts.sdk.object;

import com.google.gson.JsonObject;
import gg.eventalerts.sdk.json.GSONProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;


/**
 * All classes should have a public static method called {@code getExample()} that returns an instance of itself
 */
public class EAObject {
    @NotNull
    public Type getType() {
        return this.getClass();
    }

    @NotNull
    public JsonObject toJsonObject() {
        return GSONProvider.GSON.toJsonTree(this, getType()).getAsJsonObject();
    }

    @Override @NotNull
    public String toString() {
        return toJsonObject().toString();
    }

    @Nullable
    public static <O extends EAObject> O getExample(@NotNull Class<O> clazz) {
        try {
            return (O) clazz.getDeclaredMethod("getExample").invoke(null, (Object[]) null);
        } catch (final Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
