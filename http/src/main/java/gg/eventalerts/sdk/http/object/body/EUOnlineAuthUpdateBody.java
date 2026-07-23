package gg.eventalerts.sdk.http.object.body;

import com.google.gson.annotations.SerializedName;
import gg.eventalerts.sdk.object.EAObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class EUOnlineAuthUpdateBody extends EAObject {
    @NotNull public static final String KEY_ONLINE = "online";

    @SerializedName(KEY_ONLINE) @Nullable public Boolean online;

    public EUOnlineAuthUpdateBody() {}

    public EUOnlineAuthUpdateBody(boolean online) {
        this.online = online;
    }

    @NotNull
    public static EUOnlineAuthUpdateBody getExample() {
        return new EUOnlineAuthUpdateBody(true);
    }
}
