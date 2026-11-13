package dev.zeddevstuff.crafted;

import com.mojang.logging.LogUtils;
import dev.zeddevstuff.crafted.parsing.ParsedNode;
import dev.zeddevstuff.crafted.platform.*;
import dev.zeddevstuff.crafted.ui.elements.All;
import dev.zeddevstuff.crafted.utils.SchemaGenerator;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;

import java.nio.file.Path;

public class Entry
{
	public static ParsedNode gallery;

	static void init()
	{
		Crafted.setDefaultInstance(new Crafted(
			new MinecraftLocalization(),
			new MinecraftFont("minecraft", Minecraft.getInstance().font),
			new MainThreadDispatcher(),
			new MinecraftTextureManager(),
			new Clipboard()
		));
		LogUtils.getLogger().info("CraftedUI initialized");
		All.registerInto(Crafted.defaultInstance());
		if(Constants.DEV_MODE)
		{
			var schema = SchemaGenerator.generateSchema(Crafted.defaultInstance());
			System.out.println(schema);
		}
		gallery = Crafted.defaultInstance().getLoader(Crafted.class).getDocument("/assets/crafted_ui/ui/Gallery.xml").get();
	}
}
