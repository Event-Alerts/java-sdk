package gg.eventalerts.sdk.json;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface KeyGetter {
    /**
     * <b>Example:</b>
     * <pre>{@code
     * @NotNull public final String itemFieldName;
     * @KeyGetter("itemFieldName") @NotNull public final O item;
     * }</pre>
     *
     * The VALUE of {@code itemFieldName} is the JSON key name for the {@code item} field
     *
     * @return  the name of the OTHER field that contains the key name for THIS field
     */
    String value();
}
