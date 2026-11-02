package dev.zeddevstuff.crafted.platform;

import dev.zeddevstuff.crafted.types.Float2;
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
	public Float2 measureText(String text, float fontScale, float wrapWidth)
	{
		if(wrapWidth <= 0)
		{
			return new Float2(Math.max(0, font.width(text) - 1) * fontScale, font.lineHeight * fontScale);
		}
		else
		{
			var lines = font.getSplitter().splitLines(text, (int)wrapWidth, Style.EMPTY);
			var maxWidth = 0;
			for(var line : lines)
				maxWidth = Math.max(maxWidth, Math.max(0, font.width(line) - 1));
			return new Float2(maxWidth * fontScale, font.wordWrapHeight(text, (int)wrapWidth));
		}
	}
}
