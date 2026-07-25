package gg.eventalerts.sdk.http.object.response;

import com.google.gson.annotations.SerializedName;
import gg.eventalerts.sdk.object.EAObject;
import gg.eventalerts.sdk.object.EAPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Date;
import java.util.Set;


public class EUOnlineUpdateResponse extends EAObject {
    @NotNull public static final String KEY_HEARTBEAT_EXPIRES_AT = "heartbeatExpiresAt";
    @NotNull public static final String KEY_ONLINE_PLAYERS = "onlinePlayers";

    @SerializedName(KEY_HEARTBEAT_EXPIRES_AT) @Nullable public Date heartbeatExpiresAt;
    @SerializedName(KEY_ONLINE_PLAYERS) @Nullable public Set<EAPlayer> onlinePlayers;

    public EUOnlineUpdateResponse() {}

    public EUOnlineUpdateResponse(@Nullable Date heartbeatExpiresAt, @Nullable Set<EAPlayer> onlinePlayers) {
        this.heartbeatExpiresAt = heartbeatExpiresAt;
        this.onlinePlayers = onlinePlayers;
    }

    @NotNull
    public static EUOnlineUpdateResponse getExample() {
        return new EUOnlineUpdateResponse(
                new Date(),
                Collections.singleton(EAPlayer.getExample()));
    }
}
