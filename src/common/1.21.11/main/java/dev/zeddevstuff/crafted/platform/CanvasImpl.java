package dev.zeddevstuff.crafted.platform;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.math.Axis;
import dev.zeddevstuff.crafted.types.Color;
import dev.zeddevstuff.crafted.types.Rect;
import dev.zeddevstuff.crafted.types.TextSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;

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
						(int) settings.wrapWidth(), settings.shadowColor().toArgbInt(), false);
				graphics.drawWordWrap(font.font, Component.literal(text), x, y, (int) settings.wrapWidth(), color.toArgbInt(), false);
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
						(int) settings.wrapWidth(), settings.shadowColor().toArgbInt(), false);
				graphics.drawWordWrap(font.font, Component.literal(text.get()), x, y, (int) settings.wrapWidth(), color.toArgbInt(), false);
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
			if(mcTex.getKind() == ITexture.Kind.SIMPLE && mcTex.getIdentifier() != null)
				graphics.blit(RenderPipelines.GUI_TEXTURED, mcTex.getIdentifier(),
					(int) dest.minX(), (int) dest.minY(),
					0, 0,
					(int) dest.width(), (int) dest.height(),
					(int) dest.width(), (int) dest.height());
			else if(mcTex.getKind() == ITexture.Kind.SPRITE && mcTex.getIdentifier() != null)
				graphics.blitSprite(RenderPipelines.GUI_TEXTURED, mcTex.getIdentifier(),
					(int) dest.minX(), (int) dest.minY(),
					(int) dest.width(), (int) dest.height());
		}
	}

	@Override
	public void drawTexture(ITexture texture, Rect src, Rect dest)
	{
		if(texture instanceof MinecraftTexture mcTex)
		{
			if(mcTex.getKind() == ITexture.Kind.SIMPLE && mcTex.getIdentifier() != null)
				// It sorta works? I really hate programming without documentation
				graphics.blit(RenderPipelines.GUI_TEXTURED, mcTex.getIdentifier(),
					(int) dest.minX(), (int) dest.minY(),
					(int)src.uOffset(texture.getWidth()), (int)src.vOffset(texture.getHeight()),
					(int) dest.width(), (int) dest.height(),
					(int) (dest.width() * src.uScale(16)),
					(int) (dest.height() * src.vScale(16)));
			else if(mcTex.getKind() == ITexture.Kind.SPRITE && mcTex.getIdentifier() != null)
				graphics.blitSprite(RenderPipelines.GUI_TEXTURED, mcTex.getIdentifier(),
					(int) src.width(), (int) src.height(),
					(int) src.minX(), (int) src.minY(),
					(int) dest.minX(), (int) dest.minY(),
					(int) dest.width(), (int) dest.height());
		}
	}

	@Override
	public void beginTransform()
	{
		graphics.pose().pushMatrix();
	}
	@Override
	public void translate(float x, float y)
	{
		graphics.pose().translate(x, y);
	}
	@Override
	public void rotate(float angle)
	{
		graphics.pose().rotate((float) Math.toRadians(angle));
	}
	@Override
	public void scale(float x, float y)
	{
		graphics.pose().scale(x, y);
	}
	@Override
	public void endTransform()
	{
		graphics.pose().popMatrix();
	}

	@Override
	public void beginClip(int x, int y, int width, int height)
	{
		graphics.scissorStack.push(new ScreenRectangle(x, y, width, height));
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
		//RenderSystem.enableBlend();
	}

	@Override
	public void disableBlend()
	{
		//RenderSystem.disableBlend();
	}

	@Override
	public float deltaTime()
	{
		return minecraft.getDeltaTracker().getRealtimeDeltaTicks() / 20f;
	}
}
