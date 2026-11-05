package dev.zeddevstuff.crafted.ui.elements;

import dev.zeddevstuff.crafted.Crafted;
import dev.zeddevstuff.crafted.annotations.DefineElement;
import dev.zeddevstuff.crafted.annotations.Description;
import dev.zeddevstuff.crafted.input.ImportantKeys;
import dev.zeddevstuff.crafted.input.MouseButton;
import dev.zeddevstuff.crafted.input.events.KeyEvent;
import dev.zeddevstuff.crafted.input.events.MouseEvent;
import dev.zeddevstuff.crafted.parsing.Parsers;
import dev.zeddevstuff.crafted.platform.ICanvas;
import dev.zeddevstuff.crafted.platform.IText;
import dev.zeddevstuff.crafted.platform.ITexture;
import dev.zeddevstuff.crafted.platform.ITextureManager;
import dev.zeddevstuff.crafted.rendering.IRendersType;
import dev.zeddevstuff.crafted.types.Color;
import dev.zeddevstuff.crafted.types.Rect;
import dev.zeddevstuff.crafted.ui.CraftedContext;
import dev.zeddevstuff.crafted.ui.CraftedElement;
import dev.zeddevstuff.crafted.utils.ColorUtils;
import org.appliedenergistics.yoga.YogaAlign;
import org.appliedenergistics.yoga.YogaDimension;
import org.appliedenergistics.yoga.YogaJustify;
import org.appliedenergistics.yoga.style.StyleLength;
import org.appliedenergistics.yoga.style.StyleSizeLength;
import org.appliedenergistics.yoga.style.YogaStyle;

import java.util.Map;

@DefineElement("Button")
@Description("A simple button element")
public class Button extends CraftedElement
{
	public Button(CraftedContext ctx, Map<String, String> attributes)
	{
		super(ctx, attributes);
		if(attributes.containsKey("text"))
		{
			var text = new Text(ctx, Map.of());
			text.text.set(Parsers.parseText(attributes.get("text"), IText.literal("???")));
			text.settings.set(text.settings.get().withShadow(true));
			addChild(text);
		}
	}

	@Override
	public boolean receivesInputEvents() { return true; }
	@Override
	public boolean isFocusable() { return true; }

	@Override
	protected YogaStyle initLayoutNode(YogaStyle style, Map<String, String> attributes)
	{
		style.setDimension(YogaDimension.WIDTH, StyleSizeLength.points(100));
		style.setDimension(YogaDimension.HEIGHT, StyleSizeLength.points(20));
		style.setAlignItems(YogaAlign.CENTER);
		style.setJustifyContent(YogaJustify.CENTER);
		return style;
	}

	@Override
	public void onMousePress(MouseEvent event)
	{
		if(event.button == MouseButton.LEFT)
			applyLayoutChanges();
	}

	@Override
	public void onMouseRelease(MouseEvent event)
	{
		if(event.button == MouseButton.LEFT)
			revertLayoutChanges();
	}

	@Override
	public void onKeyPress(KeyEvent event)
	{
		if(event.keyCode == ImportantKeys.SPACE || event.keyCode == ImportantKeys.ENTER)
			applyLayoutChanges();
	}

	@Override
	public void onKeyRelease(KeyEvent event)
	{
		if(event.keyCode == ImportantKeys.SPACE || event.keyCode == ImportantKeys.ENTER)
			revertLayoutChanges();
	}
	private boolean appliedLayoutChanges = false;
	private void applyLayoutChanges()
	{
		if(renderer.get() instanceof CraftedRenderer && !appliedLayoutChanges)
		{
			marginTop.set(StyleLength.points(3));
			paddingBottom.set(StyleLength.points(3));
			height.set(StyleSizeLength.points(height.get().value().getValue() - 3));
			appliedLayoutChanges = true;
		}
	}
	private void revertLayoutChanges()
	{
		if(renderer.get() instanceof CraftedRenderer && appliedLayoutChanges)
		{
			marginTop.set(StyleLength.points(0));
			paddingBottom.set(StyleLength.points(0));
			height.set(StyleSizeLength.points(height.get().value().getValue() + 3));
			appliedLayoutChanges = false;
		}
	}

	public static void register(Crafted crafted)
	{
		crafted.elements.registerElement(Button.class, "Button", Button::new);
		crafted.renderers.registerRenderer(Button.class, "default", new DefaultRenderer(crafted.textures));
		crafted.renderers.registerRenderer(Button.class, "crafted", new CraftedRenderer());
	}

	public static class DefaultRenderer implements IRendersType<Button>
	{
		@Override
		public Class<?> getElementType() { return Button.class; }
		private final ITexture buttonTexture, buttonDisabledTexture, buttonHighlightedTexture;

		public DefaultRenderer(ITextureManager textures)
		{
			buttonTexture = textures.get("minecraft", "widget/button");
			buttonDisabledTexture = textures.get("minecraft", "widget/button_disabled");
			buttonHighlightedTexture = textures.get("minecraft", "widget/button_highlighted");
		}
		@Override
		public void render(ICanvas canvas, Button element)
		{
			var layout = element.getLayout();
			if(element.state.isDisabled())
				canvas.drawTexture(buttonDisabledTexture, Rect.posAndSize(layout.x, layout.y, layout.width, layout.height));
			else if(element.state.isHovered() || element.state.isFocused())
				canvas.drawTexture(buttonHighlightedTexture, Rect.posAndSize(layout.x, layout.y, layout.width, layout.height));
			else
				canvas.drawTexture(buttonTexture, Rect.posAndSize(layout.x, layout.y, layout.width, layout.height));
		}
	}

	public static class CraftedRenderer implements IRendersType<Button>
	{
		@Override
		public Class<?> getElementType() { return Button.class; }
		@Override
		public void render(ICanvas canvas, Button element)
		{
			var theme = element.ctx.theme;
			Color borderColor, backgroundColor, elevationColor;
			switch (element.style.get())
			{
				case "secondary" -> {
					borderColor = element.state.isDisabled()
						? theme.disabled()
						: element.state.isHoveredOrFocused()
							? ColorUtils.bestContrast(theme.onSecondary(), Color.White, Color.Black)
							: theme.onSecondary();
					backgroundColor = theme.secondary();
					elevationColor = ColorUtils.adjustLightness(theme.secondary(), -0.15f);
				}
				case "error" -> {
					borderColor = element.state.isDisabled()
						? theme.disabled()
						: element.state.isHoveredOrFocused()
							? ColorUtils.bestContrast(theme.onError(), Color.White, Color.Black)
							: theme.onError();
					backgroundColor = theme.error();
					elevationColor = ColorUtils.adjustLightness(theme.error(), -0.15f);
				}
				case "warning" -> {
					borderColor = element.state.isDisabled()
						? theme.disabled()
						: element.state.isHoveredOrFocused()
							? ColorUtils.bestContrast(theme.onWarning(), Color.White, Color.Black)
							: theme.onWarning();
					backgroundColor = theme.warning();
					elevationColor = ColorUtils.adjustLightness(theme.warning(), -0.15f);
				}
				case "success" -> {
					borderColor = element.state.isDisabled()
						? theme.disabled()
						: element.state.isHoveredOrFocused()
							? ColorUtils.bestContrast(theme.onSuccess(), Color.White, Color.Black)
							: theme.onSuccess();
					backgroundColor = theme.success();
					elevationColor = ColorUtils.adjustLightness(theme.success(), -0.15f);
				}
				case "info" -> {
					borderColor = element.state.isDisabled()
						? theme.disabled()
						: element.state.isHoveredOrFocused()
							? ColorUtils.bestContrast(theme.onInfo(), Color.White, Color.Black)
							: theme.onInfo();
					backgroundColor = theme.info();
					elevationColor = ColorUtils.adjustLightness(theme.info(), -0.15f);
				}
				default -> {
					borderColor = element.state.isDisabled()
						? theme.disabled()
						: element.state.isHoveredOrFocused()
							? ColorUtils.bestContrast(theme.onPrimary(), Color.White, Color.Black)
							: theme.onPrimary();
					backgroundColor = theme.primary();
					elevationColor = ColorUtils.adjustLightness(theme.primary(), -0.15f);
				}
			}
			var layout = element.getLayout();
			int y = layout.y;
			int height = layout.height;
			int elevationY = y + height - 4;
			int elevationHeight = 3;
			canvas.drawRect(layout.x, y, layout.width, height, borderColor);
			canvas.drawRect(layout.x + 1, y + 1, layout.width - 2, height - 2, backgroundColor);
			if(!element.state.isActive())
				canvas.drawRect(layout.x + 1, elevationY, layout.width - 2, elevationHeight, elevationColor);
		}
	}
}
