package gg.eventalerts.sdk.websocket;

import gg.eventalerts.sdk.object.EAObject;
import org.jetbrains.annotations.NotNull;


public interface SocketMessageName {
    @NotNull
    Class<? extends EAObject> getObjectType();
}
