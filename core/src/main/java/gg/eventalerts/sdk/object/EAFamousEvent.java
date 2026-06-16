package gg.eventalerts.sdk.object;

import com.google.gson.annotations.SerializedName;
import gg.eventalerts.sdk.ExampleUtility;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class EAFamousEvent extends EAObject {
    @NotNull public static final String KEY_TYPE = "type";
    @NotNull public static final String KEY_CHANNEL = "channel";
    @NotNull public static final String KEY_MESSAGE = "message";
    @NotNull public static final String KEY_USER = "user";

    @SerializedName(KEY_TYPE) @Nullable public Type type;
    @SerializedName(KEY_CHANNEL) @Nullable public Long channel;
    @SerializedName(KEY_MESSAGE) @Nullable public String message;
    @SerializedName(KEY_USER) @Nullable public Long user;

    public EAFamousEvent() {}

    public EAFamousEvent(@NotNull Type type, long channel, @NotNull String message, long user) {
        this.type = type;
        this.channel = channel;
        this.message = message;
        this.user = user;
    }

    @NotNull
    public static EAFamousEvent getExample() {
        return new EAFamousEvent(
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
