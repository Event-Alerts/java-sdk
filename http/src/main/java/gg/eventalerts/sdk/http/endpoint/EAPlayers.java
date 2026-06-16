package gg.eventalerts.sdk.http.endpoint;

import gg.eventalerts.sdk.http.EAHTTP;
import gg.eventalerts.sdk.http.action.EAAction;
import gg.eventalerts.sdk.object.EAPlayer;
import gg.eventalerts.sdk.object.http.EAItemData;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;


public class EAPlayers extends EAEndpoint<EAPlayer> {
    public EAPlayers(@NotNull EAHTTP http) {
        super(http);
    }

    @Override @NotNull
    public String getPath() {
        return "players";
    }

    @Override @NotNull
    public Class<EAPlayer> getObjectType() {
        return EAPlayer.class;
    }

    @NotNull
    public EAAction<EAItemData<EAPlayer>> retrieveOneDataByDiscordId(long discordId) {
        return retrieveOneData("discord", "id", String.valueOf(discordId));
    }

    @NotNull
    public EAAction<EAPlayer> retrieveOneByDiscordId(long discordId) {
        return retrieveOneDataByDiscordId(discordId).map(data -> data.item);
    }

    @NotNull
    public EAAction<EAItemData<EAPlayer>> retrieveOneDataByMinecraftUuid(@NotNull UUID minecraftUuid) {
        return retrieveOneData("minecraft", "uuid", minecraftUuid.toString());
    }

    @NotNull
    public EAAction<EAPlayer> retrieveOneByMinecraftUuid(@NotNull UUID minecraftUuid) {
        return retrieveOneDataByMinecraftUuid(minecraftUuid).map(data -> data.item);
    }
}
