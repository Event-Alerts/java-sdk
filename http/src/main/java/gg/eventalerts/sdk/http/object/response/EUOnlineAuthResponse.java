package gg.eventalerts.sdk.http.object.response;

import com.google.gson.annotations.SerializedName;
import gg.eventalerts.sdk.object.EAPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Date;
import java.util.Set;


public class EUOnlineAuthResponse extends EUOnlineUpdateResponse {
    @NotNull public static final String KEY_TOKEN = "token";
    @NotNull public static final String KEY_TOKEN_EXPIRES_AT = "tokenExpiresAt";
    @NotNull public static final String KEY_PLAYER = "player";

    @SerializedName(KEY_TOKEN) @Nullable public String token;
    @SerializedName(KEY_TOKEN_EXPIRES_AT) @Nullable public Date tokenExpiresAt;
    @SerializedName(KEY_PLAYER) @Nullable public EAPlayer player;

    public EUOnlineAuthResponse() {}

    public EUOnlineAuthResponse(@NotNull Date heartbeatExpiresAt, @NotNull Set<EAPlayer> onlinePlayers, @NotNull String token, @NotNull Date tokenExpiresAt, @NotNull EAPlayer player) {
        super(heartbeatExpiresAt, onlinePlayers);
        this.token = token;
        this.tokenExpiresAt = tokenExpiresAt;
        this.player = player;
    }

    @NotNull
    public static EUOnlineAuthResponse getExample() {
        final EUOnlineUpdateResponse updateResponse = EUOnlineUpdateResponse.getExample();
        if (updateResponse.heartbeatExpiresAt == null || updateResponse.onlinePlayers == null) {
            throw new IllegalStateException("Example EUOnlineUpdateResponse has null fields");
        }
        return new EUOnlineAuthResponse(
                updateResponse.heartbeatExpiresAt,
                updateResponse.onlinePlayers,
                "exampleToken",
                new Date(),
                EAPlayer.getExample());
    }
}
