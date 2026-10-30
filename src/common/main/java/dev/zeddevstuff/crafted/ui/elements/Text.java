package dev.zeddevstuff.crafted.ui.elements;

import dev.zeddevstuff.crafted.Crafted;
import dev.zeddevstuff.crafted.Prop;
import dev.zeddevstuff.crafted.ProxyProp;
import dev.zeddevstuff.crafted.annotations.DefineElement;
import dev.zeddevstuff.crafted.parsing.Parsers;
import dev.zeddevstuff.crafted.platform.ICanvas;
import dev.zeddevstuff.crafted.platform.IFont;
import dev.zeddevstuff.crafted.platform.IText;
import dev.zeddevstuff.crafted.rendering.IRenders;
import dev.zeddevstuff.crafted.types.Color;
import dev.zeddevstuff.crafted.types.TextSettings;
import dev.zeddevstuff.crafted.ui.CraftedContext;
import dev.zeddevstuff.crafted.ui.CraftedElement;
import org.appliedenergistics.yoga.YogaNodeType;
import org.appliedenergistics.yoga.style.StyleSizeLength;

import java.util.Map;

@DefineElement("Text")
public class Text extends CraftedElement
{
	static void register(Crafted crafted)
	{
		crafted.elements.registerElement(Text.class, "Text", Text::new);
		crafted.renderers.registerRenderer(Text.class, "default", new TextRenderer());
	}

	public final Prop<IText> text = registerProp(IText.class, "text", IText.literal("Text"), Parsers::parseText);

	public final Prop<TextSettings> settings = new Prop<>(TextSettings.class, "settings", TextSettings.DEFAULT);
	public final ProxyProp<IFont> font;
	public final ProxyProp<Float> fontScale = registerProp(Float.class, "fontScale", settings.get()::size, (value) -> settings.set(settings.get().withSize(value)), Parsers::parseFloat);
	public final ProxyProp<Float> wrapWidth = registerProp(Float.class, "wrapWidth", settings.get()::wrapWidth, (value) -> settings.set(settings.get().withWrapWidth(value)), Parsers::parseFloat);
	public final ProxyProp<Boolean> shadow = registerProp(Boolean.class, "shadow", settings.get()::shadow, (value) -> settings.set(settings.get().withShadow(value)), Parsers::parseBoolean);
	public final ProxyProp<Color> shadowColor = registerProp(Color.class, "shadowColor", settings.get()::shadowColor, (value) -> settings.set(settings.get().withShadowColor(value)), Parsers::parseColor);

	public final Prop<Color> color = registerProp("color", Color.White, Parsers::parseColor);

	public Text(CraftedContext ctx, Map<String, String> attributes)
	{
		super(ctx, attributes);
		settings.set(ctx.crafted.defaultTextSettings);
		font = registerProp(IFont.class, "font", settings.get()::font, (value) -> settings.set(settings.get().withFont(value)), ctx.crafted::getFontOrDefault);
		node.setNodeType(YogaNodeType.TEXT);
		text.addListener((prop, oldValue, newValue) -> {
			var cFont = font.get();
			if(cFont == null)
				return;
			var size = cFont.measureText(newValue, settings.get().size(), settings.get().wrapWidth());
			this.width.set(StyleSizeLength.points(size[0] - 0));
			this.height.set(StyleSizeLength.points(Math.max(size[1] - 0, 0)));
		});
		settings.addListener((prop, oldValue, newValue) -> {
			var cFont = font.get();
			if(cFont == null)
				return;
			var size = cFont.measureText(text.get(), newValue.size(), newValue.wrapWidth());
			this.width.set(StyleSizeLength.points(size[0]));
			this.height.set(StyleSizeLength.points(size[1]));
		});
	}

	@Override
	protected void refreshLayout()
	{
		var cFont = font.get();
		if(cFont == null)
			return;
		var size = cFont.measureText(text.get(), settings.get().size(), settings.get().wrapWidth());
		this.width.set(StyleSizeLength.points(size[0]));
		this.height.set(StyleSizeLength.points(size[1]));
	}

	public static class TextRenderer implements IRenders<Text>
	{
		@Override
		public void render(ICanvas canvas, Text element)
		{
			var fontScale = element.settings.get().size();
			canvas.beginTransform();
			canvas.scale(fontScale, fontScale);
			canvas.drawText(element.text.get(), (int)(element.getLayout().x / fontScale), (int)(element.getLayout().y / fontScale),
				element.settings.get(), element.color.get());
			canvas.endTransform();
		}
	}
}
