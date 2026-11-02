package dev.zeddevstuff.crafted.platform;

import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.Minecraft;

public class Clipboard implements IClipboard
{
	private final KeyboardHandler keyboardHandler = Minecraft.getInstance().keyboardHandler;

	@Override
	public String get()
	{
		return keyboardHandler.getClipboard();
	}

	@Override
	public void set(String text)
	{
		keyboardHandler.setClipboard(text);
	}
}
