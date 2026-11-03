package dev.zeddevstuff.crafted.ui.elements;

import dev.zeddevstuff.crafted.Crafted;
import dev.zeddevstuff.crafted.Prop;
import dev.zeddevstuff.crafted.annotations.DefineElement;
import dev.zeddevstuff.crafted.annotations.Description;
import dev.zeddevstuff.crafted.parsing.Parsers;
import dev.zeddevstuff.crafted.platform.ICanvas;
import dev.zeddevstuff.crafted.rendering.IRenders;
import dev.zeddevstuff.crafted.ui.CraftedContext;
import org.appliedenergistics.yoga.YogaEdge;
import org.appliedenergistics.yoga.style.YogaStyle;

import java.util.Map;

@DefineElement("ScrollView")
public class ScrollView extends Box
{
	public Prop<Integer> scrollX = registerProp("scrollX", 0, this::parseInteger);
	public Prop<Integer> scrollY = registerProp("scrollY", 0, this::parseInteger);

	public Prop<Boolean> horizontalScrollbar = registerProp("horizontalScrollbar", true, Parsers::parseBoolean);
	public Prop<Boolean> verticalScrollbar = registerProp("verticalScrollbar", true, Parsers::parseBoolean);

	public Prop<Integer> scrollbarSize = registerProp("scrollbarSize", 4, this::parseInteger);

	public ScrollView(CraftedContext ctx, Map<String, String> attributes)
	{
		super(ctx, attributes);
		horizontalScrollbar.addListener((prop, oldValue, newValue) -> {
			if(!newValue)
			{
				node.setPadding(YogaEdge.BOTTOM, 0);
				scrollX.set(0);
			}
			else
			{
				node.setPadding(YogaEdge.BOTTOM, scrollbarSize.get());
			}
		});
		verticalScrollbar.addListener((prop, oldValue, newValue) -> {
			if(!newValue)
			{
				node.setPadding(YogaEdge.RIGHT, 0);
				scrollY.set(0);
			}
			else
			{
				node.setPadding(YogaEdge.RIGHT, scrollbarSize.get());
			}
		});
	}

	private Integer parseInteger(String value, Integer defaultValue)
	{
		try
		{
			return Math.max(0, Integer.parseInt(value));
		}
		catch(NumberFormatException e)
		{
			return defaultValue;
		}
	}

	public static void register(Crafted crafted)
	{
		crafted.elements.registerElement(ScrollView.class, "ScrollView", ScrollView::new);
	}

	public static class ScrollViewRenderer implements IRenders<ScrollView>
	{
		@Override
		public void render(ICanvas canvas, ScrollView element)
		{
			var layout = element.getLayout();
			IRenders.beginClip(canvas, element, element.scrollX.get(), element.scrollY.get());
			canvas.drawRect(layout.x, layout.y, layout.width, layout.height, element.color.get());
		}

		@Override
		public void afterRender(ICanvas canvas, ScrollView element)
		{
			IRenders.endClip(canvas, element, element.scrollX.get(), element.scrollY.get());
		}
	}
}
