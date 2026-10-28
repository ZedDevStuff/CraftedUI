package dev.zeddevstuff.crafted.platform;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class TextConverter
{
	public static Component toMinecraftText(IText text)
	{
		if(text instanceof IText.Literal literal)
		{
			return Component.literal(literal.get());
		}
		else if(text instanceof IText.Localized translatable)
		{
			return Component.translatable(translatable.get());
		}
		else
		{
			throw new IllegalArgumentException("Unknown IText type: " + text.getClass().getName());
		}
	}
}
