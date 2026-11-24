package dev.zeddevstuff.crafted;

import dev.zeddevstuff.crafted.platform.*;
import dev.zeddevstuff.crafted.ui.elements.All;
import net.minecraft.client.Minecraft;

class CraftedEntrypoint
{
	static void init()
	{
		Crafted.setDefaultInstance(new Crafted(
			MinecraftLogger::new,
			new MinecraftLocalization(),
			new MinecraftFont("minecraft", Minecraft.getInstance().font),
			new MinecraftDispatcher(),
			new MinecraftTextureManager(),
			new MinecraftClipboard()
		));
		Crafted.defaultInstance().logger.info("CraftedUI initialized");
		All.registerInto(Crafted.defaultInstance());
	}
}
