package dev.zeddevstuff.crafted.ui.elements;

import dev.zeddevstuff.crafted.Crafted;
import dev.zeddevstuff.crafted.Prop;
import dev.zeddevstuff.crafted.ProxyProp;
import dev.zeddevstuff.crafted.annotations.DefineElement;
import dev.zeddevstuff.crafted.annotations.Description;
import dev.zeddevstuff.crafted.parsing.Parsers;
import dev.zeddevstuff.crafted.platform.ICanvas;
import dev.zeddevstuff.crafted.platform.IFont;
import dev.zeddevstuff.crafted.platform.IText;
import dev.zeddevstuff.crafted.rendering.IRendersType;
import dev.zeddevstuff.crafted.types.Color;
import dev.zeddevstuff.crafted.types.TextSettings;
import dev.zeddevstuff.crafted.ui.UIContext;
import dev.zeddevstuff.crafted.ui.CraftedElement;
import org.appliedenergistics.yoga.YogaFlexDirection;
import org.appliedenergistics.yoga.YogaNodeType;
import org.appliedenergistics.yoga.style.StyleSizeLength;

import java.util.Map;

@DefineElement("Text")
@Description("Displays text on the screen")
public class Text extends CraftedElement
{
	static void register(Crafted crafted)
	{
		crafted.elements.registerElement(Text.class, "Text", Text::new);
		crafted.renderers.registerRenderer(Text.class, "default", new Renderer());
	}

	public final Prop<IText> text = registerProp(IText.class, "text", IText.literal("Text"), Parsers::parseText);

	public final Prop<TextSettings> settings = new Prop<>(TextSettings.class, "settings", TextSettings.DEFAULT);
	public final ProxyProp<IFont> font;
	public final ProxyProp<Float> fontScale = registerProp(Float.class, "fontScale", settings.get()::size, (value) -> settings.set(settings.get().withSize(value)), Parsers::parseFloat);
	public final ProxyProp<Float> wrapWidth = registerProp(Float.class, "wrapWidth", settings.get()::wrapWidth, (value) -> settings.set(settings.get().withWrapWidth(value)), Parsers::parseFloat);
	public final Prop<Boolean> textWrap = registerProp(Boolean.class, "textWrap", true, Parsers::parseBoolean);
	public final ProxyProp<Boolean> shadow = registerProp(Boolean.class, "shadow", settings.get()::shadow, (value) -> settings.set(settings.get().withShadow(value)), Parsers::parseBoolean);
	public final ProxyProp<Color> shadowColor = registerProp(Color.class, "shadowColor", settings.get()::shadowColor, (value) -> settings.set(settings.get().withShadowColor(value)), this::parseColor);

	public final Prop<Color> color = registerProp("color", Color.White, this::parseColor);

	public Text(UIContext ctx, Map<String, String> attributes)
	{
		super(ctx, attributes);
		settings.set(ctx.crafted.defaultTextSettings);
		font = registerProp(IFont.class, "font", settings.get()::font, (value) -> settings.set(settings.get().withFont(value)), ctx.crafted::getFontOrDefault);
		node.setNodeType(YogaNodeType.TEXT);
		text.addListener((prop, oldValue, newValue) -> {
			var cFont = font.get();
			if(cFont == null)
				return;
			refreshLayout();
		});
		settings.addListener((prop, oldValue, newValue) -> {
			refreshLayout();
		});
		textWrap.addListener((prop, oldValue, newValue) -> {
			refreshLayout();
		});
	}

	@Override
	protected void refreshLayout()
	{
		var cFont = font.get();
		if(cFont == null)
			return;
		var size = cFont.measureText(text.get(), settings.get().size(), settings.get().wrapWidth());
		var layoutWidth = textWrap.get() && settings.get().shouldWrap() ? settings.get().wrapWidth() : size.width();
		this.width.set(StyleSizeLength.points(layoutWidth));
		this.height.set(StyleSizeLength.points(size.height()));
	}

	@Override
	protected boolean updateLayoutAfterLayout()
	{
		if(!textWrap.get())
			return false;
		var cFont = font.get();
		if(cFont == null)
			return false;
		var naturalWidth = cFont.measureText(text.get(), settings.get().size(), -1).width();
		if(naturalWidth <= 0)
			return false;
		var parent = getParent();
		float parentWidth = parent == null ? naturalWidth : parent.getLayout().width;
		if(parent != null && (parent.flexDirection.get() == YogaFlexDirection.ROW
			|| parent.flexDirection.get() == YogaFlexDirection.ROW_REVERSE))
		{
			var siblingWidth = 0f;
			for(var sibling : parent.getChildren())
				if(sibling != this)
					siblingWidth += sibling.getLayout().width;
			var gap = parent.columnGap.get().resolve(parentWidth).unwrapOrDefault(0);
			parentWidth = Math.max(0f, parentWidth - siblingWidth - gap * Math.max(0, parent.getChildren().size() - 1));
		}
		var wrapWidth = parentWidth > 0 ? Math.min(naturalWidth, parentWidth) : naturalWidth;
		if(settings.get().wrapWidth() == wrapWidth)
			return false;
		settings.set(settings.get().withWrapWidth(wrapWidth + 1));
		return true;
	}

	public static class Renderer implements IRendersType<Text>
	{
		@Override
		public Class<?> getElementType() { return Text.class; }
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
