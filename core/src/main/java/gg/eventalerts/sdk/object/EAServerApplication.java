package gg.eventalerts.sdk.object;

import com.google.gson.annotations.SerializedName;
import gg.eventalerts.sdk.ExampleUtility;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;


public class EAServerApplication extends EAObject {
    @NotNull public static final String KEY_ID = "id";
    @NotNull public static final String KEY_APPLICANT = "applicant";
    @NotNull public static final String KEY_CHANNEL = "channel";
    @NotNull public static final String KEY_CREATED = "created";
    @NotNull public static final String KEY_MESSAGE = "message";
    @NotNull public static final String KEY_APPROVED_BY = "approvedBy";
    @NotNull public static final String KEY_DATA = "data";

    @SerializedName(KEY_ID) @Nullable public Long id;
    @SerializedName(KEY_APPLICANT) @Nullable public Long applicant;
    @SerializedName(KEY_CHANNEL) @Nullable public Long channel;
    @SerializedName(KEY_CREATED) @Nullable public Date created;
    @SerializedName(KEY_MESSAGE) @Nullable public Long message;
    @SerializedName(KEY_APPROVED_BY) @Nullable public ApprovedBy approvedBy;
    @SerializedName(KEY_DATA) @Nullable public Data data;

    public EAServerApplication() {}

    public EAServerApplication(@Nullable Long id, @Nullable Long applicant, @Nullable Long channel, @Nullable Date created, @Nullable Long message, @Nullable ApprovedBy approvedBy, @Nullable Data data) {
        this.id = id;
        this.applicant = applicant;
        this.channel = channel;
        this.created = created;
        this.message = message;
        this.approvedBy = approvedBy;
        this.data = data;
    }

    public EAServerApplication(@NotNull EAServerApplication serverApplication) {
        this(serverApplication.id, serverApplication.applicant, serverApplication.channel, serverApplication.created, serverApplication.message, serverApplication.approvedBy, serverApplication.data);
    }

    @NotNull
    public static EAServerApplication getExample() {
        return new EAServerApplication(
                ExampleUtility.Random.discordId(),
                ExampleUtility.User.SRNYX_ID,
                ExampleUtility.Random.discordId(),
                new Date(),
                ExampleUtility.Random.discordId(),
                ApprovedBy.MOD,
                Data.getExample());
    }

    public enum ApprovedBy {
        MOD,
        ADMIN
    }

    public static class Data extends EAObject {
        @NotNull public static final String KEY_REPRESENTATIVES = "representatives";
        @NotNull public static final String KEY_NAME = "name";
        @NotNull public static final String KEY_DESCRIPTION = "description";
        @NotNull public static final String KEY_INVITE = "invite";
        @NotNull public static final String KEY_TAGS = "tags";
        @NotNull public static final String KEY_COLOR = "color";
        @NotNull public static final String KEY_THUMBNAIL = "thumbnail";

        @SerializedName(KEY_REPRESENTATIVES) @Nullable public Set<Long> representatives;
        @SerializedName(KEY_NAME) @Nullable public String name;
        @SerializedName(KEY_DESCRIPTION) @Nullable public String description;
        @SerializedName(KEY_INVITE) @Nullable public String invite;
        @SerializedName(KEY_TAGS) @Nullable public Set<EAPartnerServer.Tag> tags;
        @SerializedName(KEY_COLOR) @Nullable public Integer color;
        @SerializedName(KEY_THUMBNAIL) @Nullable public String thumbnail;

        public Data() {}

        public Data(@Nullable Collection<Long> representatives, @Nullable String name, @Nullable String description, @Nullable String invite, @Nullable Collection<EAPartnerServer.Tag> tags, @Nullable Integer color, @Nullable String thumbnail) {
            this.representatives = representatives == null ? null : new HashSet<>(representatives);
            this.name = name;
            this.description = description;
            this.invite = invite;
            this.tags = tags == null ? null : new HashSet<>(tags);
            this.color = color;
            this.thumbnail = thumbnail;
        }

        public Data(@NotNull Data data) {
            this(data.representatives, data.name, data.description, data.invite, data.tags, data.color, data.thumbnail);
        }

        @NotNull
        public static Data getExample() {
            return new Data(
                    Arrays.asList(ExampleUtility.User.SRNYX_ID, ExampleUtility.User.OIIINK_ID),
                    "Example Server",
                    "This is an example server application.",
                    "skeppy",
                    Arrays.asList(EAPartnerServer.Tag.FUN, EAPartnerServer.Tag.PVP),
                    0xFF0000,
                    "https://us-east-1.tixte.net/uploads/img.venox.network/bee.png");
        }
    }
}