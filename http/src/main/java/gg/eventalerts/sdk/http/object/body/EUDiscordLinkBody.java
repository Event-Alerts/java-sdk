package gg.eventalerts.sdk.http.object.body;

import com.google.gson.annotations.SerializedName;
import gg.eventalerts.sdk.object.EAObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class EUDiscordLinkBody extends EAObject {
    @NotNull public static final String KEY_NONCE = "nonce";
    @NotNull public static final String KEY_PORT  = "port";

    @SerializedName(KEY_NONCE) @Nullable public String nonce;
    @SerializedName(KEY_PORT) @Nullable public Integer port;

    public EUDiscordLinkBody() {}

    public EUDiscordLinkBody(@NotNull String nonce, int port) {
        this.nonce = nonce;
        this.port = port;
    }

    @NotNull
    public static EUDiscordLinkBody getExample() {
        return new EUDiscordLinkBody("dGhpcy1pcy1hLTMyLWJ5dGUtbm9uY2UtZXhhbXBsZQ", 57356);
    }
}
