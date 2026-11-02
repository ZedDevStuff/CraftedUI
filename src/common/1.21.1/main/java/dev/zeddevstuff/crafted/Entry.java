package dev.zeddevstuff.crafted;

import com.mojang.logging.LogUtils;
import dev.zeddevstuff.crafted.input.events.IClickEventHandler;
import dev.zeddevstuff.crafted.input.events.ISubmitEventHandler;
import dev.zeddevstuff.crafted.parsing.CraftedXmlParser;
import dev.zeddevstuff.crafted.parsing.DocumentBuilder;
import dev.zeddevstuff.crafted.platform.*;
import dev.zeddevstuff.crafted.ui.CraftedContext;
import dev.zeddevstuff.crafted.ui.elements.All;
import dev.zeddevstuff.crafted.ui.elements.CraftedRoot;
import dev.zeddevstuff.crafted.ui.elements.Input;
import dev.zeddevstuff.crafted.utils.SchemaGenerator;
import net.minecraft.client.Minecraft;

import java.nio.file.Path;

public class Entry
{
	public static CraftedRoot rootElement;
	private static IPreloader loader;
	static void init(Path resources)
	{
		loader = new Preloader(resources);
		loader.load();
		Crafted.setDefaultInstance(new Crafted(
			new MinecraftLocalization(),
			new MinecraftFont("minecraft", Minecraft.getInstance().font),
			new MainThreadDispatcher(),
			new MinecraftTextureManager()
		));
		LogUtils.getLogger().info("CraftedUI initialized");
		All.registerInto(Crafted.defaultInstance());
		if(Constants.DEV_MODE)
		{
			var schema = SchemaGenerator.generateSchema(Crafted.defaultInstance());

			System.out.println(schema);
		}
		var ctx = new CraftedContext(Crafted.defaultInstance());
		ctx.registerCallback(IClickEventHandler.class, "onDeepClick", event -> {
			System.out.println("Button " + event.getTarget().id.get() + " clicked");
		});
		ctx.registerCallback(ISubmitEventHandler.class, "testSubmit", event -> {
			System.out.println("Submitted value: " + event.getTargetAs(Input.class).value.get());
		});
		rootElement = (CraftedRoot)DocumentBuilder.buildTree(Crafted.defaultInstance(), ctx, loader.getDocument("Gallery"));
	}
}
