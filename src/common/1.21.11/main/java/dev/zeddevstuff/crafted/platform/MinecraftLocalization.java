package dev.zeddevstuff.crafted.platform;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;

public class MinecraftLocalization implements ILocalization
{

	@Override
	public String get(String key)
	{
		return I18n.get(key);
	}

	@Override
	public String getFormatted(String key, Object... args)
	{
		return I18n.get(key, args);
	}

	@Override
	public String currentLocale()
	{
		return Minecraft.getInstance().getLanguageManager().getSelected();
	}
}
