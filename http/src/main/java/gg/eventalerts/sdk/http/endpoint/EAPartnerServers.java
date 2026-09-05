package gg.eventalerts.sdk.http.endpoint;

import gg.eventalerts.sdk.http.EAHTTP;
import gg.eventalerts.sdk.http.action.EAAction;
import gg.eventalerts.sdk.http.response.PaginatedResponse;
import gg.eventalerts.sdk.object.EAPartnerServer;
import org.bson.types.ObjectId;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;


public class EAPartnerServers extends EAEndpoint {
    public EAPartnerServers(@NotNull EAHTTP http) {
        super(http, "servers");
    }

    @NotNull
    public EAAction<PaginatedResponse<EAPartnerServer>> retrievePage(@Nullable Integer page, @Nullable Integer limit, @Nullable Map<String, Object> queryParams) {
        return super.retrievePage(EAPartnerServer.class, "servers", page, limit, queryParams);
    }

    @NotNull
    public EAAction<List<EAPartnerServer>> retrieveMany(int count, @Nullable Integer startPage, @Nullable Map<String, Object> queryParams) {
        return super.retrieveMany(EAPartnerServer.class, "servers", count, startPage, queryParams);
    }

    @NotNull
    public EAAction<List<EAPartnerServer>> retrieveAll(@Nullable Map<String, Object> queryParams) {
        return super.retrieveAll(EAPartnerServer.class, "servers", queryParams);
    }

    @NotNull
    public EAAction<EAPartnerServer> retrieveOne(@NotNull String... pathSegments) {
        return super.retrieveOne(EAPartnerServer.class, "server", pathSegments);
    }

    @NotNull
    public EAAction<EAPartnerServer> retrieveOneById(@NotNull ObjectId id) {
        return retrieveOne("id", id.toHexString());
    }
}
