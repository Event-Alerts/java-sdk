package gg.eventalerts.core.json;

import gg.eventalerts.sdk.json.GSONProvider;
import gg.eventalerts.sdk.object.EAEvent;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Type;
import java.util.Collections;
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
        assertNull(GSONProvider.GSON.fromJson("\"NOT_A_REAL_ENUM\"", EAEvent.Type.class));
    }

    @Test
    void invalidSetMembersAreDroppedInsteadOfBreakingParsing() {
        final Type type = GSONProvider.typeOf(Set.class, EAEvent.PingRole.class);

        assertEquals(
                Collections.singleton(EAEvent.PingRole.PARTNER),
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
