package gg.eventalerts.sdk.http.object.response;

import com.google.gson.annotations.SerializedName;
import gg.eventalerts.sdk.object.EAObject;
import gg.eventalerts.sdk.object.EAPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Date;


public class EUOnlineAuthResponse extends EAObject {
    @NotNull public static final String KEY_TOKEN = "token";
    @NotNull public static final String KEY_EXPIRES_AT = "expiresAt";
    @NotNull public static final String KEY_PLAYER = "player";

    @SerializedName(KEY_TOKEN) @Nullable public String token;
    @SerializedName(KEY_EXPIRES_AT) @Nullable public Date expiresAt;
    @SerializedName(KEY_PLAYER) @Nullable public EAPlayer player;

    public EUOnlineAuthResponse() {}

    public EUOnlineAuthResponse(@NotNull String token, @NotNull Date expiresAt, @NotNull EAPlayer player) {
        this.token = token;
        this.expiresAt = expiresAt;
        this.player = player;
    }

    @NotNull
    public static EUOnlineAuthResponse getExample() {
        return new EUOnlineAuthResponse(
                "exampleToken",
                new Date(),
                EAPlayer.getExample());
    }
}
