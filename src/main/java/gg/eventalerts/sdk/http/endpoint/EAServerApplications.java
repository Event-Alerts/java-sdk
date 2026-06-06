package gg.eventalerts.sdk.http.endpoint;

import gg.eventalerts.sdk.http.EAHTTP;
import gg.eventalerts.sdk.object.EAServerApplication;
import org.jetbrains.annotations.NotNull;


public class EAServerApplications extends EAEndpoint<EAServerApplication> {
    public EAServerApplications(@NotNull EAHTTP http) {
        super(http);
    }

    @Override @NotNull
    public String getPath() {
        return "server_applications";
    }

    @Override @NotNull
    public Class<EAServerApplication> getObjectClass() {
        return EAServerApplication.class;
    }
}
