package gg.eventalerts.sdk.object;

import com.google.gson.annotations.SerializedName;
import gg.eventalerts.sdk.ExampleUtility;
import org.bson.types.ObjectId;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Date;
import java.util.UUID;


public class EACrossBan extends EAObject {
    @NotNull public static final String KEY_ID = "id";
    @NotNull public static final String KEY_DISCORD_ID = "discordId";
    @NotNull public static final String KEY_MINECRAFT_UUID = "minecraftUuid";
    @NotNull public static final String KEY_REASON = "reason";
    @NotNull public static final String KEY_EXPIRATION = "expiration";
    @NotNull public static final String KEY_CREATED = "created";

    @SerializedName(KEY_ID) @Nullable public ObjectId id;
    @SerializedName(KEY_DISCORD_ID) @Nullable public Long discordId;
    @SerializedName(KEY_MINECRAFT_UUID) @Nullable public UUID minecraftUuid;
    @SerializedName(KEY_REASON) @Nullable public String reason;
    @SerializedName(KEY_EXPIRATION) @Nullable public Date expiration;
    @SerializedName(KEY_CREATED) @Nullable public Date created;

    public EACrossBan() {}

    public EACrossBan(@Nullable ObjectId id, @Nullable Long discordId, @Nullable UUID minecraftUuid, @Nullable String reason, @Nullable Date expiration, @Nullable Date created) {
        this.id = id;
        this.discordId = discordId;
        this.minecraftUuid = minecraftUuid;
        this.reason = reason;
        this.expiration = expiration;
        this.created = created;
    }

    public EACrossBan(@NotNull EACrossBan crossBan) {
        this(crossBan.id, crossBan.discordId, crossBan.minecraftUuid, crossBan.reason, crossBan.expiration, crossBan.created);
    }

    @Override
    public boolean equals(@Nullable Object object) {
        if (this == object) return true;
        if (!(object instanceof EACrossBan)) return false;
        return id != null && id.equals(((EACrossBan) object).id);
    }

    @Override
    public int hashCode() {
        if (id == null) return super.hashCode();
        return id.hashCode();
    }

    @NotNull
    public static EACrossBan getExample() {
        return new EACrossBan(
                new ObjectId(),
                ExampleUtility.User.SRNYX_ID,
                ExampleUtility.Minecraft.SRNYX_UUID,
                "Example reason",
                new Date(),
                new Date());
    }
}
