package gg.eventalerts.sdk.object;

import gg.eventalerts.sdk.ExampleUtility;
import org.bson.types.ObjectId;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Date;
import java.util.Set;


public class ServerApplication extends EAObject {
    @Nullable public ObjectId id;
    @Nullable public Long applicant;
    @Nullable public Long channel;
    @Nullable public Date created;
    @Nullable public Long message;
    @Nullable public ApprovedBy approvedBy;
    @Nullable public Data data;

    public ServerApplication() {}

    public ServerApplication(@Nullable ObjectId id, @Nullable Long applicant, @Nullable Long channel, @Nullable Date created, @Nullable Long message, @Nullable ApprovedBy approvedBy, @Nullable Data data) {
        this.id = id;
        this.applicant = applicant;
        this.channel = channel;
        this.created = created;
        this.message = message;
        this.approvedBy = approvedBy;
        this.data = data;
    }

    @NotNull
    public static ServerApplication getExample() {
        return new ServerApplication(
                new ObjectId(),
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
        @Nullable public Set<PartnerServer.Tag> tags;
        @Nullable public Integer color;
        @Nullable public String thumbnail;

        public Data() {}

        public Data(@Nullable Set<Long> representatives, @Nullable String name, @Nullable String description, @Nullable String invite, @Nullable Set<PartnerServer.Tag> tags, @Nullable Integer color, @Nullable String thumbnail) {
            this.representatives = representatives;
            this.name = name;
            this.description = description;
            this.invite = invite;
            this.tags = tags;
            this.color = color;
            this.thumbnail = thumbnail;
        }

        @NotNull
        public static Data getExample() {
            return new Data(
                    Set.of(ExampleUtility.User.SRNYX_ID, ExampleUtility.User.OIIINK_ID),
                    "Example Server",
                    "This is an example server application.",
                    "skeppy",
                    Set.of(PartnerServer.Tag.FUN, PartnerServer.Tag.PVP),
                    0xFF0000,
                    "https://us-east-1.tixte.net/uploads/img.venox.network/bee.png");
        }
    }
}
