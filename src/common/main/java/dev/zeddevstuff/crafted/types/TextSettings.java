package dev.zeddevstuff.crafted.types;

import dev.zeddevstuff.crafted.platform.IFont;

public record TextSettings(IFont font, float size, float wrapWidth, boolean shadow, Color shadowColor, ShadowDirection shadowDirection)
{
	public TextSettings withFont(IFont font) { return new TextSettings(font, size, wrapWidth, shadow, shadowColor, shadowDirection); }
	public TextSettings withSize(float size) { return new TextSettings(font, size, wrapWidth, shadow, shadowColor, shadowDirection); }
	public TextSettings withWrapWidth(float wrapWidth) { return new TextSettings(font, size, wrapWidth, shadow, shadowColor, shadowDirection); }
	public TextSettings withNoWrap() { return new TextSettings(font, size, -1, shadow, shadowColor, shadowDirection); }
	public TextSettings withShadow(boolean shadow) { return new TextSettings(font, size, wrapWidth, shadow, shadowColor, shadowDirection); }
	public TextSettings withShadowColor(Color shadowColor) { return new TextSettings(font, size, wrapWidth, shadow, shadowColor, shadowDirection); }
	public TextSettings withShadowDirection(ShadowDirection direction) { return new TextSettings(font, size, wrapWidth, shadow, shadowColor, direction); }
	
	public boolean shouldWrap() { return wrapWidth > 0; }

	public static final TextSettings DEFAULT = new TextSettings(null, 1.0f, -1, false, new Color(63,63,63), ShadowDirection.BOTTOM_RIGHT);
}
