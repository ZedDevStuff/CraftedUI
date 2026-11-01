package dev.zeddevstuff.crafted.ui.elements;

import dev.zeddevstuff.crafted.Crafted;
import dev.zeddevstuff.crafted.Prop;
import dev.zeddevstuff.crafted.annotations.DefineElement;
import dev.zeddevstuff.crafted.annotations.Description;
import dev.zeddevstuff.crafted.parsing.Parsers;
import dev.zeddevstuff.crafted.platform.ICanvas;
import dev.zeddevstuff.crafted.platform.ITexture;
import dev.zeddevstuff.crafted.rendering.IRenders;
import dev.zeddevstuff.crafted.types.Color;
import dev.zeddevstuff.crafted.types.Rect;
import dev.zeddevstuff.crafted.ui.CraftedContext;
import dev.zeddevstuff.crafted.ui.CraftedElement;

import java.util.Map;

@DefineElement("Image")
@Description("Displays a texture or sprite on the screen")
public class Image extends CraftedElement
{
	public final Prop<ITexture> src = registerProp(ITexture.class, "src", null, this::parseTexture);
	public final Prop<Color> tint = registerProp("tint", Color.White, Parsers::parseColor);

	public Image(CraftedContext ctx, Map<String, String> attributes)
	{
		super(ctx, attributes);
	}

	private ITexture parseTexture(String s, ITexture iTexture)
	{
		var split = s.split(":", 2);
		if(split.length == 1)
			return ctx.crafted.textures.get(s, ITexture.Kind.SIMPLE);
		else
		{
			if(split[0].equals("sprite"))
				return ctx.crafted.textures.get(split[1], ITexture.Kind.SPRITE);
			else
				return ctx.crafted.textures.get(split[1], ITexture.Kind.SIMPLE);
		}
	}

	public static void register(Crafted crafted)
	{
		crafted.elements.registerElement(Image.class, "Image", Image::new);
		crafted.renderers.registerRenderer(Image.class, "default", new ImageRenderer());
	}

	public static class ImageRenderer implements IRenders<Image>
	{
		@Override
		public void render(ICanvas canvas, Image element)
		{
			if(element.src.get() == null)
				return;
			var layout = element.getLayout();
			canvas.drawTexture(element.src.get(), Rect.posAndSize(layout.x, layout.y, layout.width, layout.height), Color.White);
		}
	}
}
