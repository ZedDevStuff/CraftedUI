package dev.zeddevstuff.crafted;

import dev.zeddevstuff.crafted.parsing.ParsedNode;
import dev.zeddevstuff.crafted.platform.*;
import dev.zeddevstuff.crafted.ui.elements.All;
import net.minecraft.client.Minecraft;

class CraftedEntrypoint
{
	static void init()
	{
		Crafted.setDefaultInstance(new Crafted(
			Slf4jLogger::new,
			new MinecraftLocalization(),
			new MinecraftFont("minecraft", Minecraft.getInstance().font),
			new MainThreadDispatcher(),
			new MinecraftTextureManager(),
			new Clipboard()
		));
		Crafted.defaultInstance().logger.info("CraftedUI initialized");
		All.registerInto(Crafted.defaultInstance());
	}
}
