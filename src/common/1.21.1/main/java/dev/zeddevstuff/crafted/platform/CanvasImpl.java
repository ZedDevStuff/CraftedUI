package dev.zeddevstuff.crafted.platform;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.math.Axis;
import dev.zeddevstuff.crafted.types.Color;
import dev.zeddevstuff.crafted.types.Rect;
import dev.zeddevstuff.crafted.types.TextSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.joml.AxisAngle4f;
import org.joml.Matrix4f;

public class CanvasImpl implements ICanvas
{
	private final Minecraft minecraft;
	private GuiGraphics graphics;
	public void setGraphics(GuiGraphics graphics)
	{
		this.graphics = graphics;
	}

	public CanvasImpl(Minecraft minecraft)
	{
		this.minecraft = minecraft;
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
	public void drawTexture(ITexture texture, Rect dest)
	{
		if(texture instanceof MinecraftTexture mcTex)
		{
			if(mcTex.getKind() == ITexture.Kind.SIMPLE && mcTex.getResourceLocation() != null)
				graphics.blit(mcTex.getResourceLocation(),
					(int) dest.minX(), (int) dest.minY(),
					0f, 0f,
					(int) dest.width(), (int) dest.height(),
					(int) dest.width(), (int) dest.height());
			else if(mcTex.getKind() == ITexture.Kind.SPRITE && mcTex.getResourceLocation() != null)
				graphics.blitSprite(mcTex.getResourceLocation(),
					(int) dest.minX(), (int) dest.minY(),
					(int) dest.width(), (int) dest.height());
		}
	}

	@Override
	public void drawTexture(ITexture texture, Rect src, Rect dest)
	{
		if(texture instanceof MinecraftTexture mcTex)
		{
			if(mcTex.getKind() == ITexture.Kind.SIMPLE && mcTex.getResourceLocation() != null)
				// I don't even remember how this one works, I'll probably only touch it if someone complains about it, I'm not using it in my own stuff yet either.
				graphics.blit(mcTex.getResourceLocation(),
					(int) dest.minX(), (int) dest.minY(),
					src.uOffset(texture.getWidth()), src.vOffset(texture.getHeight()),
					(int) dest.width(), (int) dest.height(),
					(int) (dest.width() * src.uScale(16)),
					(int) (dest.height() * src.vScale(16)));
			else if(mcTex.getKind() == ITexture.Kind.SPRITE && mcTex.getResourceLocation() != null)
				graphics.blitSprite(mcTex.getResourceLocation(),
					(int) src.width(), (int) src.height(),
					(int) src.minX(), (int) src.minY(),
					(int) dest.minX(), (int) dest.minY(),
					(int) dest.width(), (int) dest.height());
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
		graphics.pose().mulPose(Axis.ZP.rotationDegrees(angle));
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

	@Override
	public void enableBlend()
	{
		RenderSystem.enableBlend();
	}

	@Override
	public void disableBlend()
	{
		RenderSystem.disableBlend();
	}

	@Override
	public float deltaTime()
	{
		return minecraft.getTimer().getRealtimeDeltaTicks() / 20f;
	}
}
