package gg.eventalerts.sdk.websocket.message.action;

import com.google.gson.annotations.SerializedName;
import gg.eventalerts.sdk.ExampleUtility;
import gg.eventalerts.sdk.object.EAObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Date;
import java.util.UUID;


public class EAPlayerConnectionAction extends EAObject {
    @NotNull public static final String KEY_UUID = "uuid";
    @NotNull public static final String KEY_USERNAME = "username";
    @NotNull public static final String KEY_TIMESTAMP = "timestamp";
    @NotNull public static final String KEY_TYPE = "type";

    @SerializedName(KEY_UUID) @Nullable public UUID uuid;
    @SerializedName(KEY_USERNAME) @Nullable public String username;
    @SerializedName(KEY_TIMESTAMP) @Nullable public Date timestamp;
    @SerializedName(KEY_TYPE) @Nullable public Type type;

    public EAPlayerConnectionAction() {}

    public EAPlayerConnectionAction(@NotNull UUID uuid, @NotNull String username, @NotNull Date timestamp, @NotNull Type type) {
        this.uuid = uuid;
        this.username = username;
        this.timestamp = timestamp;
        this.type = type;
    }

    @NotNull
    public static EAPlayerConnectionAction getExample() {
        return new EAPlayerConnectionAction(
                ExampleUtility.Minecraft.SRNYX_UUID,
                "srnyx",
                new Date(),
                EAPlayerConnectionAction.Type.JOIN);
    }

    public enum Type {
        JOIN,
        QUIT
    }
}
