package dev.zeddevstuff.crafted.ui.elements;

import dev.zeddevstuff.crafted.Crafted;
import dev.zeddevstuff.crafted.annotations.DefineElement;
import dev.zeddevstuff.crafted.parsing.Parsers;
import dev.zeddevstuff.crafted.platform.ICanvas;
import dev.zeddevstuff.crafted.platform.IText;
import dev.zeddevstuff.crafted.platform.ITexture;
import dev.zeddevstuff.crafted.platform.ITextureFactory;
import dev.zeddevstuff.crafted.rendering.IRenders;
import dev.zeddevstuff.crafted.types.Color;
import dev.zeddevstuff.crafted.types.Rect;
import dev.zeddevstuff.crafted.ui.CraftedContext;
import dev.zeddevstuff.crafted.ui.CraftedElement;
import org.appliedenergistics.yoga.YogaAlign;
import org.appliedenergistics.yoga.YogaDimension;
import org.appliedenergistics.yoga.YogaJustify;
import org.appliedenergistics.yoga.style.StyleSizeLength;
import org.appliedenergistics.yoga.style.YogaStyle;

import java.util.Map;

@DefineElement("Button")
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
	protected YogaStyle initLayoutNode(YogaStyle style, Map<String, String> attributes)
	{
		style.setDimension(YogaDimension.WIDTH, StyleSizeLength.points(100));
		style.setDimension(YogaDimension.HEIGHT, StyleSizeLength.points(20));
		style.setAlignItems(YogaAlign.CENTER);
		style.setJustifyContent(YogaJustify.CENTER);
		return style;
	}

	public static void register(Crafted crafted)
	{
		crafted.elements.registerElement(Button.class, "Button", Button::new);
		crafted.renderers.registerRenderer(Button.class, "default", new DefaultButtonRenderer(crafted.textureFactory));
	}

	public static class DefaultButtonRenderer implements IRenders<Button>
	{
		private final ITexture buttonTexture, buttonDisabledTexture, buttonHighlightedTexture;

		public DefaultButtonRenderer(ITextureFactory textureFactory)
		{
			buttonTexture = textureFactory.getTexture("minecraft", "widget/button", ITexture.Kind.SPRITE, 200, 20);
			buttonDisabledTexture = textureFactory.getTexture("minecraft", "widget/button_disabled", ITexture.Kind.SPRITE, 200, 20);
			buttonHighlightedTexture = textureFactory.getTexture("minecraft", "widget/button_highlighted", ITexture.Kind.SPRITE, 200, 20);
		}
		@Override
		public void render(ICanvas canvas, Button element)
		{
			var layout = element.getLayout();
			canvas.drawTexture(buttonTexture, Rect.posAndSize(layout.x, layout.y, layout.width, layout.height), Color.White);
		}
	}
}
