package dev.zeddevstuff.crafted;

import com.mojang.logging.LogUtils;
import dev.zeddevstuff.crafted.input.ImportantKeys;
import dev.zeddevstuff.crafted.input.events.IMouseEventHandler;
import dev.zeddevstuff.crafted.input.events.MouseEvent;
import dev.zeddevstuff.crafted.parsing.DocumentBuilder;
import dev.zeddevstuff.crafted.parsing.CraftedXmlParser;
import dev.zeddevstuff.crafted.platform.MainThreadDispatcher;
import dev.zeddevstuff.crafted.platform.MinecraftFont;
import dev.zeddevstuff.crafted.platform.MinecraftLocalization;
import dev.zeddevstuff.crafted.platform.MinecraftTextureFactory;
import dev.zeddevstuff.crafted.ui.CraftedContext;
import dev.zeddevstuff.crafted.ui.elements.All;
import dev.zeddevstuff.crafted.ui.elements.CraftedRoot;
import dev.zeddevstuff.crafted.utils.SchemaGenerator;
import net.minecraft.client.Minecraft;

public class Entry
{
	public static CraftedRoot rootElement;
	static void init()
	{
		Crafted.setDefaultInstance(new Crafted(
			new MinecraftLocalization(),
			new MinecraftFont("minecraft", Minecraft.getInstance().font),
			new MainThreadDispatcher(),
			new MinecraftTextureFactory()
		));
		LogUtils.getLogger().info("CraftedUI initialized");
		All.registerInto(Crafted.defaultInstance());
		if(Constants.DEV_MODE)
		{
			var schema = SchemaGenerator.generateSchema(Crafted.defaultInstance());

			System.out.println(schema);
		}
		var parsed = CraftedXmlParser.parse("""
			<Crafted title="Gallery" renderer="default">
				<Box id="titlebar" width="100%" height="20" color="rgb(200, 200, 200)" />
				<Box id="content" width="100%" height="stretch" color="rgb(255, 255, 255)" />
				<Text fontScale="1">ijJ</Text>
				<Button>Click me</Button>
				<Box>
					<Box>
						<Box>
							<Box>
							<Box>
							<Box>
							<Box>
							<Box>
							<Box>
							<Box>
							<Box>
							<Box>
							<Box>
							<Box>
							<Box>
							<Box>
							<Box>
							<Box>
							<Box>
							<Box>
							<Box>
							<Box>
							<Box>
							<Box>
							<Box>
							<Box>
							<Box>
							<Box>
							<Box>
							<Box>
							<Button onMousePress="onDeepClick">Insanely deeply nested button</Button>
							</Box>
							</Box>
							</Box>
							</Box>
							</Box>
							</Box>
							</Box>
							</Box>
							</Box>
							</Box>
							</Box>
							</Box>
							</Box>
							</Box>
							</Box>
							</Box>
							</Box>
							</Box>
							</Box>
							</Box>
							</Box>
							</Box>
							</Box>
							</Box>
							</Box>
							</Box>
							</Box>
						</Box>
					</Box>
				</Box>
			</Crafted>
			""");
		var ctx = new CraftedContext(Crafted.defaultInstance());
		ctx.registerCallback(IMouseEventHandler.class, "onDeepClick", event -> {
			System.out.println("Deep click event received by context on element " + event.getTarget());
		});
		rootElement = (CraftedRoot)DocumentBuilder.buildTree(Crafted.defaultInstance(), ctx, parsed);

	}
}
