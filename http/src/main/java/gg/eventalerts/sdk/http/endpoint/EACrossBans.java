package gg.eventalerts.sdk.http.endpoint;

import gg.eventalerts.sdk.http.EAHTTP;
import gg.eventalerts.sdk.http.action.EAAction;
import gg.eventalerts.sdk.object.EACrossBan;
import gg.eventalerts.sdk.object.http.EAItemData;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;


public class EACrossBans extends EAEndpoint<EACrossBan> {
    public EACrossBans(@NotNull EAHTTP http) {
        super(http);
    }

    @Override @NotNull
    public String getPath() {
        return "cross_bans";
    }

    @Override @NotNull
    public Class<EACrossBan> getObjectType() {
        return EACrossBan.class;
    }

    @NotNull
    public EAAction<EAItemData<EACrossBan>> retrieveOneDataByDiscordId(long discordId) {
        return retrieveOneData("discord_id", String.valueOf(discordId));
    }

    @NotNull
    public EAAction<EACrossBan> retrieveOneByDiscordId(long discordId) {
        return retrieveOneDataByDiscordId(discordId).map(data -> data.item);
    }

    @NotNull
    public EAAction<EAItemData<EACrossBan>> retrieveOneDataByMinecraftUuid(@NotNull UUID minecraftUuid) {
        return retrieveOneData("minecraft_uuid", minecraftUuid.toString());
    }

    @NotNull
    public EAAction<EACrossBan> retrieveOneByMinecraftUuid(@NotNull UUID minecraftUuid) {
        return retrieveOneDataByMinecraftUuid(minecraftUuid).map(data -> data.item);
    }
}
