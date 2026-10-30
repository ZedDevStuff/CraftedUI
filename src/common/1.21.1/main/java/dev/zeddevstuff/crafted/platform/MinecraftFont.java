package dev.zeddevstuff.crafted.platform;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Style;

public class MinecraftFont implements IFont
{
	private final String name;
	public final Font font;

	public MinecraftFont(String name, Font font)
	{
		this.name = name;
		this.font = font;
	}

	@Override
	public String getName() { return name; }

	@Override
	public float[] measureText(String text, float fontScale, float wrapWidth)
	{
		if(wrapWidth <= 0)
		{
			return new float[] { (Math.max(0, font.width(text) - 1)) * fontScale, font.lineHeight * fontScale };
		}
		else
		{
			var lines = font.getSplitter().splitLines(text, (int)wrapWidth, Style.EMPTY);
			var maxWidth = 0;
			for(var line : lines)
				maxWidth = Math.max(maxWidth, Math.max(0, font.width(line) - 1));
			return new float[] { maxWidth * fontScale, font.wordWrapHeight(text, (int)wrapWidth) };//lines.size() * font.lineHeight * fontScale };
		}
	}
}
