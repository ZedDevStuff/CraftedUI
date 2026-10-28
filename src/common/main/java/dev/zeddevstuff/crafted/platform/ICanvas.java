package dev.zeddevstuff.crafted.platform;

import dev.zeddevstuff.crafted.types.Color;
import dev.zeddevstuff.crafted.types.Rect;
import dev.zeddevstuff.crafted.types.TextSettings;

/**
 * Abstract rendering interface. This typically is implemented as a GuiGraphics (or whatever the Minecraft version calls it) wrapper
 */
public interface ICanvas
{
	void drawRect(int x, int y, int width, int height, Color color);

	void drawText(String text, int x, int y, TextSettings settings, Color color);
	void drawText(IText text, int x, int y, TextSettings settings, Color color);

	void drawTexture(ITexture texture, Rect dest, Color tint);
	void drawTexture(ITexture texture, Rect src, Rect dest, Color tint);

	/**
	 * Begins a transformation block. All subsequent drawing operations will be transformed by the current transformation matrix.
	 * Call endTransform() to end the transformation block.
	 */
	void beginTransform();
	void translate(float x, float y);
	void rotate(float angle);
	void scale(float x, float y);
	/**
	 * Ends a transformation block.
	 * Must be called after a call to beginTransform().
	 */
	void endTransform();

	void beginClip(int x, int y, int width, int height);
	boolean clipContains(int x, int y);
	void endClip();
}
