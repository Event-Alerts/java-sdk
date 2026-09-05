package gg.eventalerts.sdk.http.endpoint;

import gg.eventalerts.sdk.http.EAHTTP;
import gg.eventalerts.sdk.http.action.EAAction;
import gg.eventalerts.sdk.http.response.PaginatedResponse;
import gg.eventalerts.sdk.object.EAEvent;
import org.bson.types.ObjectId;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;


public class EAEvents extends EAEndpoint {
    public EAEvents(@NotNull EAHTTP http) {
        super(http, "events");
    }

    @NotNull
    public EAAction<PaginatedResponse<EAEvent>> retrievePage(@Nullable Integer page, @Nullable Integer limit, @Nullable Map<String, Object> queryParams) {
        return super.retrievePage(EAEvent.class, "events", page, limit, queryParams);
    }

    @NotNull
    public EAAction<List<EAEvent>> retrieveMany(int count, @Nullable Integer startPage, @Nullable Map<String, Object> queryParams) {
        return super.retrieveMany(EAEvent.class, "events", count, startPage, queryParams);
    }

    @NotNull
    public EAAction<List<EAEvent>> retrieveAll(@Nullable Map<String, Object> queryParams) {
        return super.retrieveAll(EAEvent.class, "events", queryParams);
    }

    @NotNull
    public EAAction<EAEvent> retrieveOne(@NotNull String... pathSegments) {
        return super.retrieveOne(EAEvent.class, "event", pathSegments);
    }

    @NotNull
    public EAAction<EAEvent> postOne(@NotNull EAEvent body, @NotNull String... pathSegments) {
        return super.postOne(EAEvent.class, "event", body, pathSegments);
    }

    @NotNull
    public EAAction<EAEvent> retrieveOneById(@NotNull ObjectId id) {
        return retrieveOne("id", id.toHexString());
    }
}
