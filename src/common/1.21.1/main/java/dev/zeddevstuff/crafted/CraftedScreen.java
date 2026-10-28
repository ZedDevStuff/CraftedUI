package dev.zeddevstuff.crafted;

import dev.zeddevstuff.crafted.platform.CanvasImpl;
import dev.zeddevstuff.crafted.platform.TextConverter;
import dev.zeddevstuff.crafted.ui.CraftedContext;
import dev.zeddevstuff.crafted.ui.elements.CraftedRoot;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import org.appliedenergistics.yoga.style.StyleLength;
import org.appliedenergistics.yoga.style.StyleSizeLength;

import java.util.function.Function;

public class CraftedScreen extends Screen
{
	protected final CanvasImpl canvas = new CanvasImpl();
	protected final CraftedRoot rootElement;
	public CraftedScreen(CraftedRoot rootElement)
	{
		super(TextConverter.toMinecraftText(rootElement.title.get()));
		this.rootElement = rootElement;
	}
	public CraftedScreen(CraftedContext ctx, Function<CraftedContext, CraftedRoot> rootElementFactory)
	{
		this(rootElementFactory.apply(ctx));
	}

	@Override
	public void render(GuiGraphics guiGraphics, int i, int i1, float v)
	{
		super.render(guiGraphics, i, i1, v);
		canvas.setGraphics(guiGraphics);
		rootElement.layoutTree(width, height);
		rootElement.renderTree(canvas);
	}

	@Override
	public void resize(Minecraft minecraft, int width, int height)
	{
		rootElement.width.set(StyleSizeLength.points(width));
		rootElement.height.set(StyleSizeLength.points(height));
		rootElement.layoutTree(width, height);
	}
}
