package dev.zeddevstuff.crafted.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Will be used by the schema generator. Can be applied to the element class or its fields.
 */
@Target({java.lang.annotation.ElementType.TYPE, java.lang.annotation.ElementType.FIELD})
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
public @interface Description
{
	String value();
}
