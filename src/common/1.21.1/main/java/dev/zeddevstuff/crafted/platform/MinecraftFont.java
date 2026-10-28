package dev.zeddevstuff.crafted.platform;

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
	public float[] measureText(String text, float fontSize, float wrapWidth)
	{
		if(wrapWidth <= 0)
		{
			font.width(text);
			return new float[] { font.width(text) * fontSize, font.lineHeight * fontSize };
		}
		else
		{
			var lines = font.getSplitter().splitLines(text, (int)wrapWidth, Style.EMPTY);
			var maxWidth = 0;
			for(var line : lines)
				maxWidth = Math.max(maxWidth, font.width(line));
			return new float[] { maxWidth * fontSize, lines.size() * font.lineHeight * fontSize };
		}
	}
}
