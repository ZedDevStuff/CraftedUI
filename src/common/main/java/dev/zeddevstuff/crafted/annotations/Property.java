package dev.zeddevstuff.crafted.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a field as a property of a {@link dev.zeddevstuff.crafted.ui.CraftedElement}. <br>
 * This will generate a schema entry for the property.
 */
@Target({java.lang.annotation.ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Property
{
	String value() default "";
}
