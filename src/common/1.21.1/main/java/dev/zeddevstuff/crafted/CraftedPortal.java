package dev.zeddevstuff.crafted;

import dev.zeddevstuff.crafted.input.ImportantKeys;
import dev.zeddevstuff.crafted.input.KeyModifiers;
import dev.zeddevstuff.crafted.input.MouseButton;
import dev.zeddevstuff.crafted.input.events.KeyEvent;
import dev.zeddevstuff.crafted.input.events.MouseEvent;
import dev.zeddevstuff.crafted.parsing.CraftedXmlParser;
import dev.zeddevstuff.crafted.parsing.DocumentBuilder;
import dev.zeddevstuff.crafted.parsing.ParsedNode;
import dev.zeddevstuff.crafted.platform.CanvasImpl;
import dev.zeddevstuff.crafted.platform.TextConverter;
import dev.zeddevstuff.crafted.rendering.CraftedRenderer;
import dev.zeddevstuff.crafted.types.Rect;
import dev.zeddevstuff.crafted.ui.CraftedContext;
import dev.zeddevstuff.crafted.ui.elements.CraftedRoot;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import org.appliedenergistics.yoga.style.StyleSizeLength;

import java.util.function.Function;


/**
 * A widget that renders a Crafted UI tree.
 */
public class CraftedPortal extends AbstractWidget
{
	protected final CanvasImpl canvas;
	protected final CraftedContext context;
	protected final CraftedRoot rootElement;
	protected final CraftedRenderer renderer;
	public CraftedPortal(Rect rect, CraftedRoot rootElement)
	{
		super((int)rect.minX(), (int)rect.minY(), (int)rect.width(), (int)rect.height(), TextConverter.toMinecraftText(rootElement.title.get()));
		this.canvas = new CanvasImpl(Minecraft.getInstance());
		this.context = rootElement.ctx;
		this.rootElement = rootElement;
		this.renderer = new CraftedRenderer(rootElement);
		context.inputHandler.setRootElement(rootElement);
		rootElement.width.set(StyleSizeLength.points(rect.width()));
		rootElement.height.set(StyleSizeLength.points(rect.height()));
		rootElement.refreshLayoutTree();
	}
	public CraftedPortal(Rect rect, CraftedContext ctx, Function<CraftedContext, CraftedRoot> rootElementFactory)
	{
		this(rect, rootElementFactory.apply(ctx));
	}
	public CraftedPortal(Rect rect, CraftedContext ctx, ParsedNode rootElementNode)
	{
		this(rect, ctx, (context) -> (CraftedRoot) DocumentBuilder.buildTree(ctx, rootElementNode));
	}
	public CraftedPortal(Rect rect, CraftedContext ctx, String xml)
	{
		this(rect, ctx, CraftedXmlParser.parse(xml));
	}

	@Override
	protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick)
	{
		canvas.setGraphics(guiGraphics);
		rootElement.layoutTree(width, height);
		canvas.beginTransform();
		canvas.translate(getX(), getY());
		renderer.render(canvas);
		canvas.endTransform();
	}

	@Override
	public void mouseMoved(double mouseX, double mouseY)
	{
		super.mouseMoved(mouseX, mouseY);
		if(rootElement.contains((float) mouseX - getX(), (float) mouseY - getY()))
			context.inputHandler.handleMouseMoved((float) mouseX - getX(), (float) mouseY - getY());
		else
			context.inputHandler.handleMouseMoved(Float.MAX_VALUE, Float.MAX_VALUE);
	}
	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY)
	{
		if(rootElement.contains((float) mouseX - getX(), (float) mouseY - getY()))
			context.inputHandler.handleMouseScrolled((float)mouseX, (float)mouseY, (float)scrollX, (float)scrollY);
		return super.mouseScrolled((float) mouseX - getX(), (float) mouseY - getY(), scrollX, scrollY);
	}
	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button)
	{
		if(rootElement.contains((float) mouseX - getX(), (float) mouseY - getY()))
			context.inputHandler.handleMousePressed((float) mouseX - getX(), (float) mouseY - getY(), MouseButton.fromCode(button));
		else
			context.inputHandler.handleMousePressed(Float.MAX_VALUE, Float.MAX_VALUE, MouseButton.fromCode(button));
		return super.mouseClicked((float) mouseX - getX(), (float) mouseY - getY(), button);
	}
	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button)
	{
		if(rootElement.contains((float) mouseX - getX(), (float) mouseY - getY()))
			context.inputHandler.handleMouseReleased((float) mouseX - getX(), (float) mouseY - getY(), MouseButton.fromCode(button));
		else
			context.inputHandler.handleMouseReleased(Float.MAX_VALUE, Float.MAX_VALUE, MouseButton.fromCode(button));
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
		if(keyCode == ImportantKeys.ESCAPE)
		{
			if(context.inputHandler.getFocusedElement() != null)
				context.inputHandler.setFocus(null, KeyEvent.press(context.inputHandler.getFocusedElement(), keyCode, scanCode, (char) 0, KeyModifiers.fromFlags(modifiers)));
		}
		context.inputHandler.handleKeyPressed(keyCode, scanCode, KeyModifiers.fromFlags(modifiers));
		return super.keyPressed(keyCode, scanCode, modifiers);
	}


	@Override
	public boolean keyReleased(int keyCode, int scanCode, int modifiers)
	{
		context.inputHandler.handleKeyReleased(keyCode, scanCode, KeyModifiers.fromFlags(modifiers));
		return super.keyReleased(keyCode, scanCode, modifiers);
	}

	@Override
	protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput)
	{
	}
}
