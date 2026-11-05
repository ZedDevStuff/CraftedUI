package dev.zeddevstuff.crafted.ui.elements;

import dev.zeddevstuff.crafted.Crafted;
import dev.zeddevstuff.crafted.Prop;
import dev.zeddevstuff.crafted.annotations.DefineElement;
import dev.zeddevstuff.crafted.annotations.Description;
import dev.zeddevstuff.crafted.platform.ICanvas;
import dev.zeddevstuff.crafted.rendering.IRendersType;
import dev.zeddevstuff.crafted.types.Color;
import dev.zeddevstuff.crafted.ui.CraftedContext;
import dev.zeddevstuff.crafted.ui.CraftedElement;
import org.appliedenergistics.yoga.YogaOverflow;

import java.util.Map;

@DefineElement(value = "Box")
@Description("A basic box element, used for backgrounds and other simple UI elements")
public class Box extends CraftedElement
{
	public static void register(Crafted crafted)
	{
		crafted.elements.registerElement(Box.class, "Box", Box::new);
		crafted.renderers.registerRenderer(Box.class, "default", new Renderer());
	}

	@Description("Background color of the box")
	public final Prop<Color> color = registerProp("color", Color.Transparent, this::parseColor);

	public Box(CraftedContext ctx, Map<String, String> attributes)
	{
		super(ctx, attributes);
	}

	public static class Renderer implements IRendersType<Box>
	{
		@Override
		public Class<?> getElementType() { return Box.class; }

		@Override
		public void beginRender(ICanvas canvas, Box element)
		{
			if(element.overflow.get() != YogaOverflow.VISIBLE)
				IRendersType.beginClip(canvas, element, 0, 0);
		}

		@Override
		public void render(ICanvas canvas, Box element)
		{
			var layout = element.getLayout();
			canvas.drawRect(layout.x, layout.y, layout.width, layout.height, element.color.get());
		}

		@Override
		public void afterRender(ICanvas canvas, Box element)
		{
			if(element.overflow.get() != YogaOverflow.VISIBLE)
				IRendersType.endClip(canvas, element, 0, 0);
		}
	}
}
