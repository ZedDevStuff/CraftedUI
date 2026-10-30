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
			{
				if(settings.shadow())
					graphics.drawWordWrap(font.font, Component.literal(text),
						x + settings.shadowDirection().x, y + settings.shadowDirection().y,
						(int) settings.wrapWidth(), settings.shadowColor().toArgbInt());
				graphics.drawWordWrap(font.font, Component.literal(text), x, y, (int) settings.wrapWidth(), color.toArgbInt());
			}
			else
			{
				if(settings.shadow())
					graphics.drawString(font.font, Component.literal(text),
						x + settings.shadowDirection().x, y + settings.shadowDirection().y,
						settings.shadowColor().toArgbInt(), false);
				graphics.drawString(font.font, text, x, y, color.toArgbInt(), false);
			}
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
			{
				if(settings.shadow())
					graphics.drawWordWrap(font.font, Component.literal(text.get()),
						x + settings.shadowDirection().x, y + settings.shadowDirection().y,
						(int) settings.wrapWidth(), settings.shadowColor().toArgbInt());
				graphics.drawWordWrap(font.font, Component.literal(text.get()), x, y, (int) settings.wrapWidth(), color.toArgbInt());
			}
			else
			{
				if(settings.shadow())
					graphics.drawString(font.font, Component.literal(text.get()),
						x + settings.shadowDirection().x, y + settings.shadowDirection().y,
						settings.shadowColor().toArgbInt(), false);
				graphics.drawString(font.font, Component.literal(text.get()), x, y, color.toArgbInt(), false);
			}
		}
		else
			throw new IllegalArgumentException("Unsupported font type: " + settings.font().getClass().getName());
	}

	@Override
	public void drawTexture(ITexture texture, Rect dest, Color tint)
	{
		if(texture instanceof MinecraftTexture mcTex)
		{
			// void blit(ResourceLocation atlasLocation, int x, int y, int uOffset, int vOffset, int uWidth, int vHeight)
			// void blit(ResourceLocation atlasLocation, int x, int y, int blitOffset, float uOffset, float vOffset, int uWidth, int vHeight, int textureWidth, int textureHeight)
			// void blit(ResourceLocation atlasLocation, int x, int y, int width, int height, float uOffset, float vOffset, int uWidth, int vHeight, int textureWidth, int textureHeight)
			// void blit(ResourceLocation atlasLocation, int x, int y, float uOffset, float vOffset, int width, int height, int textureWidth, int textureHeight)
			// void blit(ResourceLocation atlasLocation, int x1, int x2, int y1, int y2, int blitOffset, int uWidth, int vHeight, float uOffset, float vOffset, int textureWidth, int textureHeight)
			// Sprites
			// void blitSprite(ResourceLocation sprite, int x, int y, int width, int height)
			// void blitSprite(ResourceLocation sprite, int x, int y, int blitOffset, int width, int height)
			// void blitSprite(ResourceLocation sprite, int textureWidth, int textureHeight, int uPosition, int vPosition, int x, int y, int uWidth, int vHeight)
			// void blitSprite(ResourceLocation sprite, int textureWidth, int textureHeight, int uPosition, int vPosition, int x, int y, int blitOffset, int uWidth, int vHeight)
			if(mcTex.getKind() == ITexture.Kind.SIMPLE)
				graphics.blit(mcTex.resourceLocation, (int) dest.minX(), (int) dest.minY(), 0, 0, (int) dest.width(), (int) dest.height(), texture.getWidth(), texture.getHeight());
			else if(mcTex.getKind() == ITexture.Kind.SPRITE)
				graphics.blitSprite(mcTex.resourceLocation, (int) dest.minX(), (int) dest.minY(), (int) dest.width(), (int) dest.height());
		}
	}

	@Override
	public void drawTexture(ITexture texture, Rect src, Rect dest, Color tint)
	{
		if(texture instanceof MinecraftTexture mcTex)
		{
			// void blit(ResourceLocation atlasLocation, int x, int y, int uOffset, int vOffset, int uWidth, int vHeight)
			// void blit(ResourceLocation atlasLocation, int x, int y, int blitOffset, float uOffset, float vOffset, int uWidth, int vHeight, int textureWidth, int textureHeight)
			// void blit(ResourceLocation atlasLocation, int x, int y, int width, int height, float uOffset, float vOffset, int uWidth, int vHeight, int textureWidth, int textureHeight)
			// void blit(ResourceLocation atlasLocation, int x, int y, float uOffset, float vOffset, int width, int height, int textureWidth, int textureHeight)
			// void blit(ResourceLocation atlasLocation, int x1, int x2, int y1, int y2, int blitOffset, int uWidth, int vHeight, float uOffset, float vOffset, int textureWidth, int textureHeight)
			// Sprites
			// void blitSprite(ResourceLocation sprite, int x, int y, int width, int height)
			// void blitSprite(ResourceLocation sprite, int x, int y, int blitOffset, int width, int height)
			// void blitSprite(ResourceLocation sprite, int textureWidth, int textureHeight, int uPosition, int vPosition, int x, int y, int uWidth, int vHeight)
			// void blitSprite(ResourceLocation sprite, int textureWidth, int textureHeight, int uPosition, int vPosition, int x, int y, int blitOffset, int uWidth, int vHeight)
			if(mcTex.getKind() == ITexture.Kind.SIMPLE)
				graphics.blit(mcTex.resourceLocation, (int) dest.minX(), (int) dest.minY(), 0, (int) src.minX(), (int) src.minY(), (int) src.width(), (int) src.height(), texture.getWidth(), texture.getHeight());
			else if(mcTex.getKind() == ITexture.Kind.SPRITE)
				graphics.blitSprite(mcTex.resourceLocation, (int) src.width(), (int) src.height(), (int) src.minX(), (int) src.minY(), (int) dest.minX(), (int) dest.minY(), (int) dest.width(), (int) dest.height());
		}
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
