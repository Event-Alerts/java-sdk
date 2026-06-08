package gg.eventalerts.sdk.http.response;

import org.jetbrains.annotations.Nullable;


public class CodeResponse<O> extends APIResponse<O> {
    @Nullable public Integer code;

    public CodeResponse() {}

    public CodeResponse(int code) {
        this.code = code;
    }
}
