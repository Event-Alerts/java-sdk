package gg.eventalerts.sdk.object;

import gg.eventalerts.sdk.json.GSONProvider;
import org.jetbrains.annotations.NotNull;


public abstract class EAObject {
    @NotNull
    public String toJson() {
        return GSONProvider.GSON.toJson(this);
    }

    @Override @NotNull
    public String toString() {
        return toJson();
    }
}
