package dev.zeddevstuff.crafted.platform;

import dev.zeddevstuff.crafted.types.Float2;

/**
 * A
 */
public interface IFont
{
	String getName();
	Float2 measureText(String text, float fontSize, float wrapWidth);
	default Float2 measureText(IText text, float fontSize, float wrapWidth)
	{
		return measureText(text.get(), fontSize, wrapWidth);
	}
}
