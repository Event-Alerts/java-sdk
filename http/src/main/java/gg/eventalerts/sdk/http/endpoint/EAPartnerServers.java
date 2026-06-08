package gg.eventalerts.sdk.http.endpoint;

import gg.eventalerts.sdk.http.EAHTTP;
import gg.eventalerts.sdk.http.response.APIResponse;
import gg.eventalerts.sdk.object.EAPartnerServer;
import org.bson.types.ObjectId;
import org.jetbrains.annotations.NotNull;


public class EAPartnerServers extends EAEndpoint<EAPartnerServer> {
    public EAPartnerServers(@NotNull EAHTTP http) {
        super(http);
    }

    @Override @NotNull
    public String getPath() {
        return "servers";
    }

    @Override @NotNull
    public Class<EAPartnerServer> getObjectClass() {
        return EAPartnerServer.class;
    }

    @NotNull
    public APIResponse<EAPartnerServer> retrieveOneById(@NotNull ObjectId id) {
        return retrieveOne("id", id.toHexString());
    }
}
