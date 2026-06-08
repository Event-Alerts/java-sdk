package gg.eventalerts.sdk.http.response;

import gg.eventalerts.sdk.object.EAObject;
import org.jetbrains.annotations.Nullable;


public class APIResponse extends EAObject {
    @Nullable public Integer code;

    public APIResponse() {}

    public APIResponse(int code) {
        this.code = code;
    }
}
