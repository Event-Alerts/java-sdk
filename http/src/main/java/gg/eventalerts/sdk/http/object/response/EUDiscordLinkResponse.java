package gg.eventalerts.sdk.http.object.response;

import com.google.gson.annotations.SerializedName;
import gg.eventalerts.sdk.object.EAObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class EUDiscordLinkResponse extends EAObject {
    @NotNull public static final String KEY_URL = "url";

    @SerializedName(KEY_URL) @Nullable public String url;

    public EUDiscordLinkResponse() {}

    public EUDiscordLinkResponse(@NotNull String url) {
        this.url = url;
    }

    @NotNull
    public static EUDiscordLinkResponse getExample() {
        return new EUDiscordLinkResponse("https://discord.com/oauth2/authorize?client_id=1142603508827299883&response_type=code&scope=identify&state=EXAMPLE");
    }
}
