package gg.eventalerts.sdk.websocket.message.event;

import gg.eventalerts.sdk.object.EACrossBan;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class EACrossBanEvent extends EACrossBan {
    @Nullable public Status status;

    public EACrossBanEvent() {}

    public EACrossBanEvent(@NotNull EACrossBan crossBan, @Nullable Status status) {
        super(crossBan);
        this.status = status;
    }

    @NotNull
    public static EACrossBanEvent getExample() {
        return new EACrossBanEvent(
                EACrossBan.getExample(),
                Status.ADDED);
    }

    public enum Status {
        ADDED,
        REMOVED,
        EDITED
    }
}
