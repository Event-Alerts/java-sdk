package gg.eventalerts.sdk;

import org.jetbrains.annotations.NotNull;

import java.util.Base64;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;


public class ExampleUtility {
    public static class Guild {
        public static final long EVENT_ALERTS_ID = 970411885293895801L;
    }

    public static class Role {
        public static final long PARTNER_EVENTS_ID = 970434201990070424L;
        public static final long MONEY_EVENTS_ID = 970434305203511359L;
    }

    public static class User {
        public static final long SRNYX_ID = 242385234992037888L;
        public static final long OIIINK_ID = 365630764244664320L;
        public static final long RAME_ID = 381890968971902976L;
        public static final long REECE_ID = 533985117589471233L;

        public static final String SRNYX_USERNAME = "srnyx";
    }

    public static class Minecraft {
        public static final UUID SRNYX_UUID = UUID.fromString("e907083e-5db6-41fc-9e32-5c4d99a08712");

        public static final String SRNYX_USERNAME = "srnyx";
    }

    public static class Random {
        private static final long DISCORD_EPOCH = 1420070400000L;

        public static long discordId() {
            final long timestamp = System.currentTimeMillis() - DISCORD_EPOCH;
            final long workerId = ThreadLocalRandom.current().nextLong(32);
            final long processId = ThreadLocalRandom.current().nextLong(32);
            final long increment = ThreadLocalRandom.current().nextLong(4096);
            return (timestamp << 22)
                    | (workerId << 17)
                    | (processId << 12)
                    | increment;
        }

        @NotNull
        public static String base64(int bytes) {
            final byte[] data = new byte[bytes];
            ThreadLocalRandom.current().nextBytes(data);
            return Base64.getUrlEncoder().withoutPadding().encodeToString(data);
        }
    }

    private ExampleUtility() {}
}
