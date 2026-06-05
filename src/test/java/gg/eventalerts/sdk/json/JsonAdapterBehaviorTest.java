package gg.eventalerts.sdk.json;

import gg.eventalerts.sdk.object.Event;
import gg.eventalerts.sdk.support.JsonRoundTripSupport;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;


class JsonAdapterBehaviorTest {
    @Test
    void invalidPrimitiveInputsReturnNull() {
        assertNull(GSONProvider.GSON.fromJson("\"not-a-date\"", Date.class));
        assertNull(GSONProvider.GSON.fromJson("\"not-a-uuid\"", UUID.class));
        assertNull(GSONProvider.GSON.fromJson("\"not-an-object-id\"", ObjectId.class));
        assertNull(GSONProvider.GSON.fromJson("\"NOT_A_REAL_ENUM\"", Event.Type.class));
    }

    @Test
    void invalidSetMembersAreDroppedInsteadOfBreakingParsing() {
        final Type type = JsonRoundTripSupport.typeOf(Set.class, Event.PingRole.class);

        assertEquals(
                Set.of(Event.PingRole.PARTNER),
                GSONProvider.GSON.fromJson("[\"PARTNER\",\"NOT_A_REAL_VALUE\"]", type));
    }

    @Test
    void stringAndNumberAdaptersHandleBadTypesAsNull() {
        assertNull(GSONProvider.GSON.fromJson("{}", String.class));
        assertNull(GSONProvider.GSON.fromJson("{}", Long.class));
        assertNull(GSONProvider.GSON.fromJson("{}", Integer.class));
        assertNull(GSONProvider.GSON.fromJson("{}", Double.class));
        assertNull(GSONProvider.GSON.fromJson("{}", Boolean.class));
    }
}
