package dev.zeddevstuff.crafted.ui.elements;

import dev.zeddevstuff.crafted.Crafted;
import dev.zeddevstuff.crafted.annotations.DefineElement;
import dev.zeddevstuff.crafted.annotations.Description;
import dev.zeddevstuff.crafted.input.MouseButton;
import dev.zeddevstuff.crafted.input.events.ClickEvent;
import dev.zeddevstuff.crafted.input.events.MouseEvent;
import dev.zeddevstuff.crafted.parsing.Parsers;
import dev.zeddevstuff.crafted.platform.ICanvas;
import dev.zeddevstuff.crafted.platform.IText;
import dev.zeddevstuff.crafted.platform.ITexture;
import dev.zeddevstuff.crafted.platform.ITextureManager;
import dev.zeddevstuff.crafted.rendering.IRenders;
import dev.zeddevstuff.crafted.types.Color;
import dev.zeddevstuff.crafted.types.Rect;
import dev.zeddevstuff.crafted.ui.CraftedContext;
import dev.zeddevstuff.crafted.ui.CraftedElement;
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
		if(event.button == MouseButton.LEFT && renderer.get() instanceof CraftedButtonRenderer)
		{
			marginTop.set(StyleLength.points(3));
			height.set(StyleSizeLength.points(height.get().value().getValue() - 3));
		}
	}

	@Override
	public void onMouseRelease(MouseEvent event)
	{
		if(event.button == MouseButton.LEFT && renderer.get() instanceof CraftedButtonRenderer)
		{
			marginTop.set(StyleLength.points(0));
			height.set(StyleSizeLength.points(height.get().value().getValue() + 3));
		}
	}

	public static void register(Crafted crafted)
	{
		crafted.elements.registerElement(Button.class, "Button", Button::new);
		crafted.renderers.registerRenderer(Button.class, "default", new DefaultButtonRenderer(crafted.textures));
		crafted.renderers.registerRenderer(Button.class, "crafted", new CraftedButtonRenderer());
	}

	public static class DefaultButtonRenderer implements IRenders<Button>
	{
		private final ITexture buttonTexture, buttonDisabledTexture, buttonHighlightedTexture;

		public DefaultButtonRenderer(ITextureManager textures)
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

	public static class CraftedButtonRenderer implements IRenders<Button>
	{
		@Override
		public void render(ICanvas canvas, Button element)
		{
			var layout = element.getLayout();
			// 1. Border
			// 2. Top (background)
			// 3. Elevation (darker background)
			// 4. Text
			Color borderColor = element.state.isDisabled() ? Color.Gray : Color.Black;
			Color backgroundColor = element.state.isDisabled() ? Color.LightGray : Color.Gray;
			int y = layout.y;
			int height = layout.height;
			int elevationY = y + height - 4;
			int elevationHeight = 3;
			canvas.drawRect(layout.x, y, layout.width, height, borderColor);
			canvas.drawRect(layout.x + 1, y + 1, layout.width - 2, height - 2, Color.White);
			if(!element.state.isActive())
				canvas.drawRect(layout.x + 1, elevationY, layout.width - 2, elevationHeight, Color.DarkGray);
		}
	}
}
