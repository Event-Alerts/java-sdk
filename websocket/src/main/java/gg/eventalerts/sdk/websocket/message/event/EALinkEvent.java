package gg.eventalerts.sdk.websocket.message.event;

import com.google.gson.annotations.SerializedName;
import gg.eventalerts.sdk.object.EAPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class EALinkEvent extends EAPlayer {
    @NotNull public static final String KEY_LINK_STATUS = "linkStatus";

    @SerializedName(KEY_LINK_STATUS) @Nullable public LinkStatus linkStatus;

    public EALinkEvent() {}

    public EALinkEvent(@NotNull EAPlayer player, @Nullable LinkStatus linkStatus) {
        super(player);
        this.linkStatus = linkStatus;
    }

    @NotNull
    public static EALinkEvent getExample() {
        return new EALinkEvent(
                EAPlayer.getExample(),
                LinkStatus.ADDED);
    }

    public enum LinkStatus {
        ADDED,
        REMOVED
    }
}
