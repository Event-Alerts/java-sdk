package gg.eventalerts.sdk.http.endpoint;

import gg.eventalerts.sdk.http.EAHTTP;
import gg.eventalerts.sdk.http.action.EAAction;
import gg.eventalerts.sdk.http.object.body.EUOnlineAuthBody;
import gg.eventalerts.sdk.http.object.body.EUOnlineAuthUpdateBody;
import gg.eventalerts.sdk.http.object.response.EUOnlineAuthResponse;
import gg.eventalerts.sdk.http.object.response.EUOnlineUpdateResponse;
import gg.eventalerts.sdk.http.response.PaginatedResponse;
import gg.eventalerts.sdk.object.EAPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.UUID;


public class EAPlayers extends EAEndpoint {
    @NotNull public final EventUtils eventUtils = new EventUtils(this);

    public EAPlayers(@NotNull EAHTTP http) {
        super(http, "players");
    }

    @NotNull
    public EAAction<PaginatedResponse<EAPlayer>> retrievePage(@Nullable Integer page, @Nullable Integer limit, @Nullable Map<String, Object> queryParams) {
        return super.retrievePage(EAPlayer.class, "players", page, limit, queryParams);
    }

    @NotNull
    public EAAction<List<EAPlayer>> retrieveMany(int count, @Nullable Integer startPage, @Nullable Map<String, Object> queryParams) {
        return super.retrieveMany(EAPlayer.class, "players", count, startPage, queryParams);
    }

    @NotNull
    public EAAction<EAPlayer> retrieveOne(@NotNull String... pathSegments) {
        return super.retrieveOne(EAPlayer.class, "player", pathSegments);
    }

    @NotNull
    public EAAction<EAPlayer> retrieveOneByDiscordId(long discordId) {
        return retrieveOne("discord", "id", String.valueOf(discordId));
    }

    @NotNull
    public EAAction<EAPlayer> retrieveOneByMinecraftUuid(@NotNull UUID minecraftUuid) {
        return retrieveOne("minecraft", "uuid", minecraftUuid.toString());
    }

    public static class EventUtils extends EAEndpoint {
        public EventUtils(@NotNull EAPlayers parent) {
            super(parent, "eventutils");
        }

        @NotNull
        public EAAction<EUOnlineAuthResponse> postOnlineAuth(@NotNull EUOnlineAuthBody body) {
            return super.postOne(EUOnlineAuthResponse.class, "data", body, "online", "auth");
        }

        @NotNull
        public EAAction<EUOnlineUpdateResponse> postOnlineUpdate(@NotNull EUOnlineAuthUpdateBody body) {
            return super.postOne(EUOnlineUpdateResponse.class, "data", body, "online", "update");
        }
    }
}
