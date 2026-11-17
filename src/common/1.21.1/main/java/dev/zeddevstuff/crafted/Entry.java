package dev.zeddevstuff.crafted;

import dev.zeddevstuff.crafted.parsing.ParsedNode;
import dev.zeddevstuff.crafted.platform.*;
import dev.zeddevstuff.crafted.ui.elements.All;
import net.minecraft.client.Minecraft;

public class Entry
{
	public static ParsedNode gallery;

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
		gallery = Crafted.defaultInstance().getLoader(Crafted.class).getDocument("/assets/crafted_ui/ui/Gallery.xml").get();
	}
}
