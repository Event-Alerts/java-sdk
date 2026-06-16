package gg.eventalerts.sdk.http.endpoint;

import gg.eventalerts.sdk.http.EAHTTP;
import gg.eventalerts.sdk.http.action.EAAction;
import gg.eventalerts.sdk.object.EAEvent;
import gg.eventalerts.sdk.object.http.EAItemData;
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
    public Class<EAEvent> getObjectType() {
        return EAEvent.class;
    }

    @NotNull
    public EAAction<EAItemData<EAEvent>> retrieveOneDataById(@NotNull ObjectId id) {
        return retrieveOneData("id", id.toHexString());
    }

    @NotNull
    public EAAction<EAEvent> retrieveOneById(@NotNull ObjectId id) {
        return retrieveOneDataById(id).map(data -> data.item);
    }
}
