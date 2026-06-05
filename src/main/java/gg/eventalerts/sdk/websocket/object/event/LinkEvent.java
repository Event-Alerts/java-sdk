package gg.eventalerts.sdk.websocket.object.event;

import gg.eventalerts.sdk.object.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class LinkEvent extends Player {
    @Nullable public LinkStatus linkStatus;

    public LinkEvent() {}

    public LinkEvent(@NotNull Player player, @Nullable LinkStatus linkStatus) {
        super(player);
        this.linkStatus = linkStatus;
    }

    @NotNull
    public static LinkEvent getExample() {
        return new LinkEvent(
                Player.getExample(),
                LinkStatus.ADDED);
    }

    public enum LinkStatus {
        ADDED,
        REMOVED
    }
}
