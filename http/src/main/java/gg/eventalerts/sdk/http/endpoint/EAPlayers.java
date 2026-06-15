package gg.eventalerts.sdk.http.endpoint;

import gg.eventalerts.sdk.http.EAHTTP;
import gg.eventalerts.sdk.http.action.EAAction;
import gg.eventalerts.sdk.object.EAPlayer;
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
    public EAAction<EAPlayer> retrieveOneByDiscordId(long discordId) {
        return retrieveOne("discord", "id", String.valueOf(discordId));
    }

    @NotNull
    public EAAction<EAPlayer> retrieveOneByMinecraftUuid(@NotNull UUID minecraftUuid) {
        return retrieveOne("minecraft", "uuid", minecraftUuid.toString());
    }
}
