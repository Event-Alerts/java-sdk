package gg.eventalerts.sdk.http.endpoint;

import gg.eventalerts.sdk.http.EAHTTP;
import gg.eventalerts.sdk.http.action.EAAction;
import gg.eventalerts.sdk.object.EAEvent;
import org.bson.types.ObjectId;
import org.jetbrains.annotations.NotNull;


public class EAEvents extends EAEndpoint<EAEvent> {
    public EAEvents(@NotNull EAHTTP http) {
        super(http);
    }

    @Override @NotNull
    public String getPath() {
        return "events";
    }

    @Override @NotNull
    public Class<EAEvent> getObjectClass() {
        return EAEvent.class;
    }

    @NotNull
    public EAAction<EAEvent> retrieveOneById(@NotNull ObjectId id) {
        return retrieveOne("id", id.toHexString());
    }
}
