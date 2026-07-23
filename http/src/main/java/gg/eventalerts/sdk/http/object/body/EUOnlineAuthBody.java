package gg.eventalerts.sdk.http.object.body;

import com.google.gson.annotations.SerializedName;
import gg.eventalerts.sdk.ExampleUtility;
import gg.eventalerts.sdk.object.EAObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;


public class EUOnlineAuthBody extends EAObject {
    @NotNull public static final String KEY_ACCESS_TOKEN = "accessToken";
    @NotNull public static final String KEY_UUID = "uuid";
    @NotNull public static final String KEY_USERNAME = "username";
    @NotNull public static final String KEY_MOD_VERSION = "modVersion";
    @NotNull public static final String KEY_MINECRAFT_VERSION = "minecraftVersion";

    @SerializedName(KEY_ACCESS_TOKEN) @Nullable public String accessToken;
    @SerializedName(KEY_UUID) @Nullable public UUID uuid;
    @SerializedName(KEY_USERNAME) @Nullable public String username;
    @SerializedName(KEY_MOD_VERSION) @Nullable public String modVersion;
    @SerializedName(KEY_MINECRAFT_VERSION) @Nullable public String minecraftVersion;

    public EUOnlineAuthBody() {}

    public EUOnlineAuthBody(@NotNull String accessToken, @NotNull UUID uuid, @NotNull String username, @NotNull String modVersion, @NotNull String minecraftVersion) {
        this.uuid = uuid;
        this.accessToken = accessToken;
        this.username = username;
        this.modVersion = modVersion;
        this.minecraftVersion = minecraftVersion;
    }

    @NotNull
    public static EUOnlineAuthBody getExample() {
        return new EUOnlineAuthBody(
                "exampleAccessToken",
                ExampleUtility.Minecraft.SRNYX_UUID,
                ExampleUtility.Minecraft.SRNYX_USERNAME,
                "3.0.0",
                "1.21.11");
    }
}
