package dev.zeddevstuff.crafted.platform;

import dev.zeddevstuff.crafted.types.Color;
import dev.zeddevstuff.crafted.types.Rect;
import dev.zeddevstuff.crafted.types.TextSettings;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.joml.*;

public class CanvasImpl implements ICanvas
{
	private GuiGraphics graphics;
	public void setGraphics(GuiGraphics graphics)
	{
		this.graphics = graphics;
	}
	@Override
	public void drawRect(int x, int y, int width, int height, Color color)
	{
		graphics.fill(x, y, x + width, y + height, color.toArgbInt());
	}

	@Override
	public void drawText(String text, int x, int y, TextSettings settings, Color color)
	{
		if(settings.font() == null)
			return;
		if(settings.font() instanceof MinecraftFont font)
		{
			if(settings.shouldWrap())
				graphics.drawWordWrap(font.font, Component.literal(text), x, y, (int) settings.wrapWidth(), color.toArgbInt());
			else
				graphics.drawString(font.font, text, x, y, color.toArgbInt(), false);
		}
		else
			throw new IllegalArgumentException("Unsupported font type: " + settings.font().getClass().getName());
	}

	@Override
	public void drawText(IText text, int x, int y, TextSettings settings, Color color)
	{
		if(settings.font() == null)
			return;
		if(settings.font() instanceof MinecraftFont font)
		{
			if(settings.shouldWrap())
				graphics.drawWordWrap(font.font, Component.literal(text.get()), x, y, (int) settings.wrapWidth(), color.toArgbInt());
			else
				graphics.drawString(font.font, Component.literal(text.get()), x, y, color.toArgbInt(), false);
		}
		else
			throw new IllegalArgumentException("Unsupported font type: " + settings.font().getClass().getName());
	}

	@Override
	public void drawTexture(ITexture texture, Rect dest, Color tint)
	{
		graphics.blit(((MinecraftTexture) texture).resourceLocation, (int) dest.minX(), (int) dest.minY(), 0,  0, 0, (int) dest.width(), (int) dest.height(), texture.getWidth(), texture.getHeight());
	}

	@Override
	public void drawTexture(ITexture texture, Rect src, Rect dest, Color tint)
	{
		graphics.blit(((MinecraftTexture) texture).resourceLocation, (int) dest.minX(), (int) dest.minY(), 0, (float) src.minX(), (float) src.minY(), (int) src.width(), (int) src.height(), texture.getWidth(), texture.getHeight());
	}

	@Override
	public void beginTransform()
	{
		graphics.pose().pushPose();
	}
	@Override
	public void translate(float x, float y)
	{
		graphics.pose().translate(x, y, 0);
	}
	@Override
	public void rotate(float angle)
	{
		graphics.pose().mulPose(new Matrix4f().rotate(new AxisAngle4f(angle, 0, 0, 1)));
	}
	@Override
	public void scale(float x, float y)
	{
		graphics.pose().scale(x, y, 1);
	}
	@Override
	public void endTransform()
	{
		graphics.pose().popPose();
	}

	@Override
	public void beginClip(int x, int y, int width, int height)
	{
		graphics.enableScissor(x, y, x + width, y + height);
	}

	@Override
	public boolean clipContains(int x, int y)
	{
		return graphics.containsPointInScissor(x, y);
	}

	@Override
	public void endClip()
	{
		graphics.disableScissor();
	}
}
