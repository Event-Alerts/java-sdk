package gg.eventalerts.sdk.http.object.response;

import com.google.gson.annotations.SerializedName;
import gg.eventalerts.sdk.object.EAObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Date;


public class EUOnlineUpdateResponse extends EAObject {
    @NotNull public static final String KEY_EXPIRES_AT = "expiresAt";

    @SerializedName(KEY_EXPIRES_AT) @Nullable public Date expiresAt;

    public EUOnlineUpdateResponse() {}

    public EUOnlineUpdateResponse(@Nullable Date expiresAt) {
        this.expiresAt = expiresAt;
    }

    @NotNull
    public static EUOnlineUpdateResponse getExample() {
        return new EUOnlineUpdateResponse(new Date());
    }
}
