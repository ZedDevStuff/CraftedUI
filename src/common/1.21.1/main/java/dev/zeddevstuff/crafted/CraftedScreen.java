package dev.zeddevstuff.crafted;

import dev.zeddevstuff.crafted.input.KeyModifiers;
import dev.zeddevstuff.crafted.input.MouseButton;
import dev.zeddevstuff.crafted.platform.CanvasImpl;
import dev.zeddevstuff.crafted.platform.TextConverter;
import dev.zeddevstuff.crafted.ui.CraftedContext;
import dev.zeddevstuff.crafted.ui.elements.CraftedRoot;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import org.appliedenergistics.yoga.style.StyleSizeLength;

import java.util.function.Function;

public class CraftedScreen extends Screen
{
	protected final CanvasImpl canvas = new CanvasImpl();
	protected final CraftedContext context;
	protected final CraftedRoot rootElement;
	public CraftedScreen(CraftedRoot rootElement)
	{
		super(TextConverter.toMinecraftText(rootElement.title.get()));
		this.context = rootElement.ctx;
		this.rootElement = rootElement;
		context.inputHandler.setRootElement(rootElement);
		rootElement.refreshLayoutTree();
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

	// I'm never finding a use for this
	@Override
	protected void rebuildWidgets() {}

	@Override
	public void resize(Minecraft minecraft, int width, int height)
	{
		super.resize(minecraft, width, height);
		rootElement.width.set(StyleSizeLength.points(width));
		rootElement.height.set(StyleSizeLength.points(height));
		rootElement.layoutTree(width, height);
	}

	// region Input Handling

	@Override
	public void mouseMoved(double mouseX, double mouseY)
	{
		super.mouseMoved(mouseX, mouseY);
		context.inputHandler.handleMouseMoved((float)mouseX, (float)mouseY);
	}
	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY)
	{
		context.inputHandler.handleMouseScrolled((float)mouseX, (float)mouseY, (float)scrollX, (float)scrollY);
		return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
	}
	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button)
	{
		context.inputHandler.handleMousePressed((float)mouseX, (float)mouseY, MouseButton.fromCode(button));
		return super.mouseClicked(mouseX, mouseY, button);
	}
	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button)
	{
		context.inputHandler.handleMouseReleased((float)mouseX, (float)mouseY, MouseButton.fromCode(button));
		return super.mouseReleased(mouseX, mouseY, button);
	}

	@Override
	public boolean charTyped(char c, int modifiers)
	{
		context.inputHandler.handleCharTyped(c, KeyModifiers.fromFlags(modifiers));
		return super.charTyped(c, modifiers);
	}
	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers)
	{
		context.inputHandler.handleKeyPressed(keyCode, scanCode, KeyModifiers.fromFlags(modifiers));
		return super.keyPressed(keyCode, scanCode, modifiers);
	}
	@Override
	public boolean keyReleased(int keyCode, int scanCode, int modifiers)
	{
		context.inputHandler.handleKeyReleased(keyCode, scanCode, KeyModifiers.fromFlags(modifiers));
		return super.keyReleased(keyCode, scanCode, modifiers);
	}

	// endregion Input Handling
}
