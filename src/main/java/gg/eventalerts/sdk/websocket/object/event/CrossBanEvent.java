package gg.eventalerts.sdk.websocket.object.event;

import gg.eventalerts.sdk.object.CrossBan;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class CrossBanEvent extends CrossBan {
    @Nullable public Status status;

    public CrossBanEvent(@NotNull CrossBan crossBan, @Nullable Status status) {
        super(crossBan);
        this.status = status;
    }

    @NotNull
    public static CrossBanEvent getExample() {
        return new CrossBanEvent(
                CrossBan.getExample(),
                Status.ADDED);
    }

    public enum Status {
        ADDED,
        REMOVED,
        EDITED
    }
}
