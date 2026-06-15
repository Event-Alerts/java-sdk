package gg.eventalerts.sdk.websocket;

import gg.eventalerts.sdk.object.*;
import gg.eventalerts.sdk.websocket.message.event.EACrossBanEvent;
import gg.eventalerts.sdk.websocket.message.event.EALinkEvent;
import org.jetbrains.annotations.NotNull;


public enum SocketEventName implements SocketMessageName {
    BOOSTER_PASS_GIVEN(EAPlayer.class),
    CROSS_BAN(EACrossBanEvent.class),
    EVENT_CANCELLED(EAEvent.class),
    EVENT_CHAT(EAEventThreadMessage.class),
    EVENT_POSTED(EAEvent.class),
    FAMOUS_EVENT_POSTED(EAFamousEvent.class),
    LINK(EALinkEvent.class),
    SERVER_EDITED(EAPartnerServer.class),
    SERVER_ENABLED(EAPartnerServer.class);

    @NotNull public final Class<? extends EAObject> objectType;

    SocketEventName(@NotNull Class<? extends EAObject> objectType) {
        this.objectType = objectType;
    }

    @Override @NotNull
    public Class<? extends EAObject> getObjectType() {
        return objectType;
    }
}
