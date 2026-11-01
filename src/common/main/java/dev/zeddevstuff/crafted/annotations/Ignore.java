package dev.zeddevstuff.crafted.annotations;

import java.lang.annotation.Retention;

/**
 * Prevents a public prop from being included in the generated schema. <br>
 * This is only because I don't know what would be the best way to register properties outside the instance of the class element. <br>
 * Alternative is to make the prop private with a public getter.
 */
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
public @interface Ignore
{
}
