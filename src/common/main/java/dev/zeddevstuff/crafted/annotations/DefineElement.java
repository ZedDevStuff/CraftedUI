package dev.zeddevstuff.crafted.annotations;

import dev.zeddevstuff.crafted.ui.CraftedElement;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Marks a class as a {@link CraftedElement} needing a schema entry
 */
@Target({java.lang.annotation.ElementType.TYPE})
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
public @interface DefineElement
{
	String value() default "";
	ContentType contentType() default ContentType.ANY;
	public enum ContentType
	{
		NONE,
		ANY,
		TEXT
	}
}
