package gg.eventalerts.sdk.http.response;

import gg.eventalerts.sdk.object.EAObject;
import org.jetbrains.annotations.Nullable;


public class SingleResponse<O extends EAObject> extends APIResponse {
    @Nullable public O data;
}
