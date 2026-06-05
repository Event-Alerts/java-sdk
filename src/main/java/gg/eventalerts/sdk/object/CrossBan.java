package gg.eventalerts.sdk.object;

import gg.eventalerts.sdk.ExampleUtility;
import org.bson.types.ObjectId;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Date;
import java.util.UUID;


public class CrossBan extends EAObject {
    @Nullable public ObjectId id;
    @Nullable public Long discordId;
    @Nullable public UUID minecraftUuid;
    @Nullable public String reason;
    @Nullable public Date expiration;
    @Nullable public Date created;

    public CrossBan() {}

    public CrossBan(@Nullable ObjectId id, @Nullable Long discordId, @Nullable UUID minecraftUuid, @Nullable String reason, @Nullable Date expiration, @Nullable Date created) {
        this.id = id;
        this.discordId = discordId;
        this.minecraftUuid = minecraftUuid;
        this.reason = reason;
        this.expiration = expiration;
        this.created = created;
    }

    public CrossBan(@NotNull CrossBan crossBan) {
        this(crossBan.id, crossBan.discordId, crossBan.minecraftUuid, crossBan.reason, crossBan.expiration, crossBan.created);
    }

    @Override
    public boolean equals(@Nullable Object object) {
        if (this == object) return true;
        if (!(object instanceof final CrossBan crossBan)) return false;
        return id != null && id.equals(crossBan.id);
    }

    @Override
    public int hashCode() {
        if (id == null) return super.hashCode();
        return id.hashCode();
    }

    @NotNull
    public static CrossBan getExample() {
        return new CrossBan(
                new ObjectId(),
                ExampleUtility.User.SRNYX_ID,
                ExampleUtility.Minecraft.SRNYX_UUID,
                "Example reason",
                new Date(),
                new Date());
    }
}
