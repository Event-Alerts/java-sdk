package gg.eventalerts.sdk.object;

import gg.eventalerts.sdk.ExampleUtility;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;


public class EAServerApplication extends EAObject {
    @Nullable public Long id;
    @Nullable public Long applicant;
    @Nullable public Long channel;
    @Nullable public Date created;
    @Nullable public Long message;
    @Nullable public ApprovedBy approvedBy;
    @Nullable public Data data;

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
        @Nullable public Set<Long> representatives;
        @Nullable public String name;
        @Nullable public String description;
        @Nullable public String invite;
        @Nullable public Set<EAPartnerServer.Tag> tags;
        @Nullable public Integer color;
        @Nullable public String thumbnail;

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
