package gg.eventalerts.sdk.http.endpoint;

import gg.eventalerts.sdk.http.EAHTTP;
import gg.eventalerts.sdk.http.action.EAAction;
import gg.eventalerts.sdk.http.response.PaginatedResponse;
import gg.eventalerts.sdk.object.EAEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;


public class EAServerApplications extends EAEndpoint {
    public EAServerApplications(@NotNull EAHTTP http) {
        super(http, "server_applications");
    }

    @NotNull
    public EAAction<PaginatedResponse<EAEvent>> retrievePage(@Nullable Integer page, @Nullable Integer limit, @Nullable Map<String, Object> queryParams) {
        return super.retrievePage(EAEvent.class, "server_applications", page, limit, queryParams);
    }

    @NotNull
    public EAAction<List<EAEvent>> retrieveMany(int count, @Nullable Integer startPage, @Nullable Map<String, Object> queryParams) {
        return super.retrieveMany(EAEvent.class, "server_applications", count, startPage, queryParams);
    }

    @NotNull
    public EAAction<List<EAEvent>> retrieveAll(@Nullable Map<String, Object> queryParams) {
        return super.retrieveAll(EAEvent.class, "server_applications", queryParams);
    }

    @NotNull
    public EAAction<EAEvent> retrieveOne(@NotNull String... pathSegments) {
        return super.retrieveOne(EAEvent.class, "server_application", pathSegments);
    }
}
