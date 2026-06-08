package gg.eventalerts.sdk.http.response;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;


public class PaginatedResponse<O> extends CodeResponse<O> {
    @Nullable public Integer page;
    @Nullable public Integer limit;
    @Nullable public Integer count;
    @Nullable public Integer total;
    @Nullable public Integer all;
    @Nullable public List<O> data;

    @NotNull
    public Optional<List<O>> getDataOptional() {
        return Optional.ofNullable(data);
    }

    @NotNull
    public List<O> getDataElseEmpty() {
        return getDataOptional().orElse(Collections.emptyList());
    }
}
