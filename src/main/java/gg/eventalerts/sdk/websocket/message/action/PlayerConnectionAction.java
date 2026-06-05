package gg.eventalerts.sdk.websocket.message.action;

import gg.eventalerts.sdk.ExampleUtility;
import gg.eventalerts.sdk.object.EAObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Date;
import java.util.UUID;


public class PlayerConnectionAction extends EAObject {
    @Nullable public UUID uuid;
    @Nullable public String username;
    @Nullable public Date timestamp;
    @Nullable public Type type;

    public PlayerConnectionAction() {}

    public PlayerConnectionAction(@NotNull UUID uuid, @NotNull String username, @NotNull Date timestamp, @NotNull Type type) {
        this.uuid = uuid;
        this.username = username;
        this.timestamp = timestamp;
        this.type = type;
    }

    @NotNull
    public static PlayerConnectionAction getExample() {
        return new PlayerConnectionAction(
                ExampleUtility.Minecraft.SRNYX_UUID,
                "srnyx",
                new Date(),
                PlayerConnectionAction.Type.JOIN);
    }

    public enum Type {
        JOIN,
        QUIT
    }
}
