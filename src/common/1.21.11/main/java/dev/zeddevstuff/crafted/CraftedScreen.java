package dev.zeddevstuff.crafted;

import dev.zeddevstuff.crafted.input.ImportantKeys;
import dev.zeddevstuff.crafted.input.KeyModifiers;
import dev.zeddevstuff.crafted.input.MouseButton;
import dev.zeddevstuff.crafted.input.events.KeyEvent;
import dev.zeddevstuff.crafted.parsing.XmlParser;
import dev.zeddevstuff.crafted.parsing.UIDocumentBuilder;
import dev.zeddevstuff.crafted.parsing.ParsedNode;
import dev.zeddevstuff.crafted.parsing.Parsers;
import dev.zeddevstuff.crafted.platform.MinecraftCanvas;
import dev.zeddevstuff.crafted.platform.IText;
import dev.zeddevstuff.crafted.platform.TextConverter;
import dev.zeddevstuff.crafted.rendering.CraftedRenderer;
import dev.zeddevstuff.crafted.ui.UIContext;
import dev.zeddevstuff.crafted.ui.CraftedElement;
import dev.zeddevstuff.crafted.ui.elements.CraftedRoot;
import dev.zeddevstuff.crafted.utils.HotReloadable;
import dev.zeddevstuff.crafted.utils.Lazy;
import dev.zeddevstuff.crafted.utils.Typed;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.MouseButtonEvent;
import org.appliedenergistics.yoga.style.StyleSizeLength;

import javax.annotation.Nonnull;
import java.util.function.Predicate;

public class CraftedScreen extends Screen
{
	protected final MinecraftCanvas canvas;
	protected final UIContext context;
	private HotReloadable<ParsedNode> hotReloadable;
	private final ParsedNode rootElementNode;
	private CraftedRoot rootElement;
	private CraftedRenderer renderer;

	public CraftedScreen(CraftedRoot rootElement)
	{
		super(TextConverter.toMinecraftText(rootElement.title.get()));
		this.canvas = new MinecraftCanvas(Minecraft.getInstance());
		this.context = rootElement.ctx;
		this.rootElementNode = null;
		this.rootElement = rootElement;
		this.renderer = new CraftedRenderer(rootElement);
		context.inputHandler.setRootElement(rootElement);
		rootElement.refreshLayoutTree();
	}
	public CraftedScreen(UIContext ctx, ParsedNode rootElementNode)
	{
		super(TextConverter.toMinecraftText(Parsers.parseText(rootElementNode.attributes().getOrDefault("title", null), IText.literal("Crafted Screen"))));
		this.canvas = new MinecraftCanvas(Minecraft.getInstance());
		this.context = ctx;
		this.rootElementNode = rootElementNode;
	}
	public CraftedScreen(UIContext ctx, String xml)
	{
		this(ctx, XmlParser.parse(xml));
	}
	public CraftedScreen(UIContext ctx, HotReloadable<ParsedNode> hotReloadable)
	{
		this(ctx, hotReloadable.getValue());
		this.hotReloadable = hotReloadable;
		hotReloadable.addListener((newValue) -> {
			try
			{
				var newRoot = UIDocumentBuilder.buildTree(ctx, newValue);
				this.rootElement = newRoot.as(CraftedRoot.class);
				this.renderer = new CraftedRenderer(rootElement);
				context.inputHandler.setRootElement(rootElement);
				rootElement.refreshLayoutTree();
			}
			catch(Exception e)
			{
				ctx.crafted.logger.error("Failed to hot reload '" + hotReloadable.file + "'", e);
			}
		});
	}

	@Override
	protected void init()
	{
		initContext(context);
		var rootElement = (CraftedRoot) UIDocumentBuilder.buildTree(context, rootElementNode);
		this.rootElement = rootElement;
		this.renderer = new CraftedRenderer(rootElement);
		context.inputHandler.setRootElement(rootElement);
		treeBuilt(rootElement);
		rootElement.refreshLayoutTree();
	}

	/**
	 * Override this method to initialize the {@link UIContext} before the tree is built. <br>
	 * This is useful for registering callbacks and other context-related things.
	 */
	public void initContext(UIContext ctx) {}
	/**
	 * Override this method to do something after the tree is built. <br>
	 * This is mostly used for finding and storing references to elements for later use.
	 */
	public void treeBuilt(CraftedRoot rootElement) {}
	//Timer timer = new Timer();
	@Override
	public void render(@Nonnull GuiGraphics guiGraphics, int i, int i1, float v)
	{
//      timer.start();
		super.render(guiGraphics, i, i1, v);
		canvas.setGraphics(guiGraphics);
		rootElement.updateTooltip(canvas.deltaTime());
		rootElement.layoutTree(width, height);
		rootElement.positionTooltip(width, height);
		rootElement.layoutTree(width, height);
		renderer.render(canvas);
//		timer.stop();
//		guiGraphics.drawString(minecraft.font, "render took " + timer.getElapsedTimeMillis() + " ms", 10, 10, 0xFFFFFFFF);
	}

	public Lazy<CraftedElement> $el(String id)
	{
		return new Lazy<>(() -> rootElement.getElementById(id));
	}
	public <T extends CraftedElement> Lazy<T> $el(String id, Class<T> type)
	{
		return new Lazy<>(() -> {
			var el = rootElement.getElementById(id);
			if(el != null)
				return el.as(type);
			return null;
		});
	}
	public Lazy<CraftedElement> $el(Predicate<CraftedElement> predicate)
	{
		return new Lazy<>(() -> rootElement.findDescendant(predicate));
	}
	public <T extends CraftedElement> Lazy<T> $el(Predicate<CraftedElement> predicate, Class<T> type)
	{
		return new Lazy<>(() -> {
			var el = rootElement.findDescendant(predicate);
			if(el != null)
				return el.as(type);
			return null;
		});
	}

	public <T> T $var(String name, T value)
	{
		context.setVar(name, value);
		return value;
	}
	public <T> T $var(Class<T> type, String name, T value)
	{
		context.setVar(name, type, value);
		return value;
	}
	public <T> T $var(String name, Typed<T> value)
	{
		context.setVar(name, value);
		return value.value;
	}

	public <T> T $handler(Class<T> type, String name, T callback)
	{
		context.registerHandler(type, name, callback);
		return callback;
	}

	// I'm never finding a use for this
	@Override
	protected void rebuildWidgets() {}

	@Override
	public void resize(int width, int height)
	{
		super.resize(width, height);
		rootElement.width.set(StyleSizeLength.points(width));
		rootElement.height.set(StyleSizeLength.points(height));
		rootElement.refreshLayoutTree();
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
	public boolean mouseClicked(@Nonnull MouseButtonEvent event, boolean doubleClick)
	{
		context.inputHandler.handleMousePressed((float)event.x(), (float)event.y(), MouseButton.fromCode(event.button()));
		return super.mouseClicked(event, false);
	}
	@Override
	public boolean mouseReleased(@Nonnull MouseButtonEvent event)
	{
		context.inputHandler.handleMouseReleased((float)event.x(), (float)event.y(), MouseButton.fromCode(event.button()));
		return super.mouseReleased(event);
	}

	@Override
	public boolean charTyped(@Nonnull CharacterEvent event)
	{
		context.inputHandler.handleCharTyped((char)event.codepoint(), KeyModifiers.fromFlags(event.modifiers()));
		return super.charTyped(event);
	}
	@Override
	public boolean keyPressed(@Nonnull net.minecraft.client.input.KeyEvent event)
	{
		if(event.key() == ImportantKeys.ESCAPE)
		{
			if(context.inputHandler.getFocusedElement() != null)
				context.inputHandler.setFocus(null, KeyEvent.press(context.inputHandler.getFocusedElement(), event.key(), event.scancode(), (char) 0, KeyModifiers.fromFlags(event.modifiers())));
			else
				onClose();
		}
		context.inputHandler.handleKeyPressed(event.key(), event.scancode(), KeyModifiers.fromFlags(event.modifiers()));
		return super.keyPressed(event);
	}


	@Override
	public boolean keyReleased(@Nonnull net.minecraft.client.input.KeyEvent event)
	{
		context.inputHandler.handleKeyReleased(event.key(), event.scancode(), KeyModifiers.fromFlags(event.modifiers()));
		return super.keyReleased(event);
	}

	@Override
	public boolean shouldCloseOnEsc() { return false; }

	// endregion Input Handling
	@Override
	public void onClose()
	{
		hotReloadable.close();
		super.onClose();
	}
}
