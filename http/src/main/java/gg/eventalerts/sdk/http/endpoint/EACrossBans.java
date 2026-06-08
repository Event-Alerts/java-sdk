package gg.eventalerts.sdk.http.endpoint;

import gg.eventalerts.sdk.http.EAHTTP;
import gg.eventalerts.sdk.http.response.APIResponse;
import gg.eventalerts.sdk.object.EACrossBan;
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
    public Class<EACrossBan> getObjectClass() {
        return EACrossBan.class;
    }

    @NotNull
    public APIResponse<EACrossBan> retrieveOneByDiscordId(long discordId) {
        return retrieveOne("discord_id", String.valueOf(discordId));
    }

    @NotNull
    public APIResponse<EACrossBan> retrieveOneByMinecraftUuid(@NotNull UUID minecraftUuid) {
        return retrieveOne("minecraft_uuid", minecraftUuid.toString());
    }
}
