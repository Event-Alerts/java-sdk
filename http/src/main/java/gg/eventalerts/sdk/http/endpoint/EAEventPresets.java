package gg.eventalerts.sdk.http.endpoint;

import gg.eventalerts.sdk.http.EAHTTP;
import gg.eventalerts.sdk.http.action.EAAction;
import gg.eventalerts.sdk.http.response.PaginatedResponse;
import gg.eventalerts.sdk.object.EAEventPreset;
import org.bson.types.ObjectId;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;


public class EAEventPresets extends EAEndpoint {
    public EAEventPresets(@NotNull EAHTTP http) {
        super(http, "event_presets");
    }

    @NotNull
    public EAAction<PaginatedResponse<EAEventPreset>> retrievePage(@Nullable Integer page, @Nullable Integer limit, @Nullable Map<String, Object> queryParams) {
        return super.retrievePage(EAEventPreset.class, "data", page, limit, queryParams);
    }

    @NotNull
    public EAAction<List<EAEventPreset>> retrieveMany(int count, @Nullable Integer startPage, @Nullable Map<String, Object> queryParams) {
        return super.retrieveMany(EAEventPreset.class, "data", count, startPage, queryParams);
    }

    @NotNull
    public EAAction<List<EAEventPreset>> retrieveAll(@Nullable Map<String, Object> queryParams) {
        return super.retrieveAll(EAEventPreset.class, "data", queryParams);
    }

    @NotNull
    public EAAction<EAEventPreset> retrieveOne(@NotNull String... pathSegments) {
        return super.retrieveOne(EAEventPreset.class, "data", pathSegments);
    }

    @NotNull
    public EAAction<EAEventPreset> retrieveOneById(@NotNull ObjectId id) {
        return retrieveOne("id", id.toHexString());
    }
}
