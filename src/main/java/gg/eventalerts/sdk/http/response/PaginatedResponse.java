package gg.eventalerts.sdk.http.response;

import gg.eventalerts.sdk.object.EAObject;
import org.jetbrains.annotations.Nullable;

import java.util.List;


public class PaginatedResponse<O extends EAObject> extends APIResponse {
    @Nullable public Integer page;
    @Nullable public Integer limit;
    @Nullable public Integer count;
    @Nullable public Integer total;
    @Nullable public Integer all;
    @Nullable public List<O> data;
}
