package gg.eventalerts.sdk.object;

import gg.eventalerts.sdk.json.GSONProvider;
import org.jetbrains.annotations.NotNull;


public abstract class EAObject {
    @Override @NotNull
    public String toString() {
        return GSONProvider.GSON.toJson(this);
    }
}
