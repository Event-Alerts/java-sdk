package gg.eventalerts.sdk.websocket.object.action;

import gg.eventalerts.sdk.object.EAObject;
import gg.eventalerts.sdk.websocket.handler.event.SocketEventName;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;


public class UpdateSubscriptionAction extends EAObject {
    @Nullable public Set<SocketEventName> subscribe;
    @Nullable public Set<SocketEventName> unsubscribe;

    public UpdateSubscriptionAction() {}

    public UpdateSubscriptionAction(@Nullable Set<SocketEventName> subscribe, @Nullable Set<SocketEventName> unsubscribe) {
        this.subscribe = subscribe;
        this.unsubscribe = unsubscribe;
    }

    @NotNull
    public Set<SocketEventName> getSubscribe() {
        return subscribe == null ? Set.of() : subscribe;
    }

    @NotNull
    public Set<SocketEventName> getUnsubscribe() {
        return unsubscribe == null ? Set.of() : unsubscribe;
    }

    @NotNull
    public static UpdateSubscriptionAction getExample() {
        return new UpdateSubscriptionAction(
                Set.of(SocketEventName.EVENT_POSTED, SocketEventName.LINK),
                Set.of(SocketEventName.EVENT_CANCELLED));
    }
}
