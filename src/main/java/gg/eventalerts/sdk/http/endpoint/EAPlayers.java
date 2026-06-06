package gg.eventalerts.sdk.http.endpoint;

import gg.eventalerts.sdk.http.EAHTTP;
import gg.eventalerts.sdk.http.response.APIResponse;
import gg.eventalerts.sdk.object.EAPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
    public Class<EAPlayer> getObjectClass() {
        return EAPlayer.class;
    }

    @Nullable
    public APIResponse retrieveOneByDiscordId(long discordId) {
        return retrieveOne("discord", "id", String.valueOf(discordId));
    }

    @Nullable
    public APIResponse retrieveOneByMinecraftUuid(@NotNull UUID minecraftUuid) {
        return retrieveOne("minecraft", "uuid", minecraftUuid.toString());
    }
}
