package dev.zeddevstuff.crafted.types;

import dev.zeddevstuff.crafted.platform.IFont;

public record TextSettings(IFont font, float size, float wrapWidth)
{
	public TextSettings withFont(IFont font)
	{
		return new TextSettings(font, size, wrapWidth);
	}
	public TextSettings withSize(float size)
	{
		return new TextSettings(font, size, wrapWidth);
	}
	public TextSettings withWrapWidth(float wrapWidth)
	{
		return new TextSettings(font, size, wrapWidth);
	}
	public TextSettings withNoWrap()
	{
		return new TextSettings(font, size, -1);
	}

	public boolean shouldWrap()
	{
		return wrapWidth > 0;
	}
}
