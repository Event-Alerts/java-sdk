package gg.eventalerts.sdk.websocket.message.action;

import com.google.gson.annotations.SerializedName;
import gg.eventalerts.sdk.object.EAObject;
import gg.eventalerts.sdk.websocket.SocketEventName;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;


public class EAUpdateSubscriptionAction extends EAObject {
    @NotNull public static final String KEY_SUBSCRIBE = "subscribe";
    @NotNull public static final String KEY_UNSUBSCRIBE = "unsubscribe";

    @SerializedName(KEY_SUBSCRIBE) @Nullable public Set<SocketEventName> subscribe;
    @SerializedName(KEY_UNSUBSCRIBE) @Nullable public Set<SocketEventName> unsubscribe;

    public EAUpdateSubscriptionAction() {}

    public EAUpdateSubscriptionAction(@Nullable Set<SocketEventName> subscribe, @Nullable Set<SocketEventName> unsubscribe) {
        this.subscribe = subscribe;
        this.unsubscribe = unsubscribe;
    }

    @NotNull
    public Set<SocketEventName> getSubscribe() {
        return subscribe == null ? Collections.emptySet() : subscribe;
    }

    @NotNull
    public Set<SocketEventName> getUnsubscribe() {
        return unsubscribe == null ? Collections.emptySet() : unsubscribe;
    }

    @NotNull
    public static EAUpdateSubscriptionAction getExample() {
        return new EAUpdateSubscriptionAction(
                new HashSet<>(Arrays.asList(SocketEventName.EVENT_POSTED, SocketEventName.LINK)),
                Collections.singleton(SocketEventName.EVENT_CANCELLED));
    }
}
