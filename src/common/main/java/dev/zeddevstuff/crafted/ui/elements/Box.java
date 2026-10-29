package dev.zeddevstuff.crafted.ui.elements;

import dev.zeddevstuff.crafted.Crafted;
import dev.zeddevstuff.crafted.Prop;
import dev.zeddevstuff.crafted.annotations.DefineElement;
import dev.zeddevstuff.crafted.annotations.Description;
import dev.zeddevstuff.crafted.parsing.Parsers;
import dev.zeddevstuff.crafted.platform.ICanvas;
import dev.zeddevstuff.crafted.rendering.IRenders;
import dev.zeddevstuff.crafted.types.Color;
import dev.zeddevstuff.crafted.ui.CraftedContext;
import dev.zeddevstuff.crafted.ui.CraftedElement;
import org.appliedenergistics.yoga.style.YogaStyle;

import java.util.Map;

@DefineElement(value = "Box")
@Description("A basic box element, used for backgrounds and other simple UI elements.")
public class Box extends CraftedElement
{
	public static void register(Crafted crafted)
	{
		crafted.elements.registerElement(Box.class, "Box", Box::new);
		crafted.renderers.registerRenderer(Box.class, "default", new BoxRenderer());
	}

	@Description("Background color of the box")
	public final Prop<Color> color = registerProp("color", Color.Transparent, Parsers::parseColor);

	public Box(CraftedContext ctx, Map<String, String> attributes)
	{
		super(ctx, attributes);
	}

	public static class BoxRenderer implements IRenders<Box>
	{
		@Override
		public void render(ICanvas canvas, Box element)
		{
			var layout = element.getLayout();
			canvas.drawRect(layout.x, layout.y, layout.width, layout.height, element.color.get());
		}
	}
}
