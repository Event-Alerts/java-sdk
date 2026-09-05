package gg.eventalerts.sdk.http.endpoint;

import gg.eventalerts.sdk.http.EAHTTP;
import gg.eventalerts.sdk.http.action.EAAction;
import gg.eventalerts.sdk.http.response.PaginatedResponse;
import gg.eventalerts.sdk.object.EACrossBan;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.UUID;


public class EACrossBans extends EAEndpoint {
    public EACrossBans(@NotNull EAHTTP http) {
        super(http, "cross_bans");
    }

    @NotNull
    public EAAction<PaginatedResponse<EACrossBan>> retrievePage(@Nullable Integer page, @Nullable Integer limit, @Nullable Map<String, Object> queryParams) {
        return super.retrievePage(EACrossBan.class, "cross_bans", page, limit, queryParams);
    }

    @NotNull
    public EAAction<List<EACrossBan>> retrieveMany(int count, @Nullable Integer startPage, @Nullable Map<String, Object> queryParams) {
        return super.retrieveMany(EACrossBan.class, "cross_bans", count, startPage, queryParams);
    }

    @NotNull
    public EAAction<List<EACrossBan>> retrieveAll(@Nullable Map<String, Object> queryParams) {
        return super.retrieveAll(EACrossBan.class, "cross_bans", queryParams);
    }

    @NotNull
    public EAAction<EACrossBan> retrieveOne(@NotNull String... pathSegments) {
        return super.retrieveOne(EACrossBan.class, "cross_ban", pathSegments);
    }

    @NotNull
    public EAAction<EACrossBan> retrieveOneByDiscordId(long discordId) {
        return retrieveOne("discord_id", String.valueOf(discordId));
    }

    @NotNull
    public EAAction<EACrossBan> retrieveOneByMinecraftUuid(@NotNull UUID minecraftUuid) {
        return retrieveOne("minecraft_uuid", minecraftUuid.toString());
    }
}
