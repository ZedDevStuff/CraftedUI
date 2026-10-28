package dev.zeddevstuff.crafted.platform;

/**
 * A
 */
public interface IFont
{
	String getName();
	float[] measureText(String text, float fontSize, float wrapWidth);
	default float[] measureText(IText text, float fontSize, float wrapWidth)
	{
		return measureText(text.get(), fontSize, wrapWidth);
	}
}
