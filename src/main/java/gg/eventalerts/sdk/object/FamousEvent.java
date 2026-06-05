package gg.eventalerts.sdk.object;

import gg.eventalerts.sdk.ExampleUtility;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class FamousEvent extends EAObject {
    @Nullable public Type type;
    @Nullable public Long channel;
    @Nullable public String message;
    @Nullable public Long user;

    public FamousEvent() {}

    public FamousEvent(@NotNull Type type, long channel, @NotNull String message, long user) {
        this.type = type;
        this.channel = channel;
        this.message = message;
        this.user = user;
    }

    @NotNull
    public static FamousEvent getExample() {
        return new FamousEvent(
                Type.FAMOUS,
                ExampleUtility.Random.discordId(),
                "This is a famous event",
                ExampleUtility.User.SRNYX_ID);
    }

    public enum Type {
        SKEPPY,
        FAMOUS,
        POTENTIAL_FAMOUS,
        SIGHTING
    }
}
