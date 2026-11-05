package dev.zeddevstuff.crafted.ui.elements;

import dev.zeddevstuff.crafted.Crafted;
import dev.zeddevstuff.crafted.Prop;
import dev.zeddevstuff.crafted.annotations.DefineElement;
import dev.zeddevstuff.crafted.annotations.Description;
import dev.zeddevstuff.crafted.platform.ICanvas;
import dev.zeddevstuff.crafted.platform.ITexture;
import dev.zeddevstuff.crafted.rendering.IRenders;
import dev.zeddevstuff.crafted.types.Rect;
import dev.zeddevstuff.crafted.ui.CraftedContext;
import dev.zeddevstuff.crafted.ui.CraftedElement;

import java.util.Map;

@DefineElement("Image")
@Description("Displays a texture or sprite on the screen")
public class Image extends CraftedElement
{
	public final Prop<ITexture> src = registerProp(ITexture.class, "src", null, this::parseTexture);

	public Image(CraftedContext ctx, Map<String, String> attributes)
	{
		super(ctx, attributes);
	}

	private ITexture parseTexture(String s, ITexture iTexture)
	{
		return ctx.crafted.textures.get(s);
	}

	public static void register(Crafted crafted)
	{
		crafted.elements.registerElement(Image.class, "Image", Image::new);
		crafted.renderers.registerRenderer(Image.class, "default", new Renderer());
	}

	public static class Renderer implements IRenders<Image>
	{
		@Override
		public void render(ICanvas canvas, Image element)
		{
			if(element.src.get() == null)
				return;
			var layout = element.getLayout();
			canvas.drawTexture(element.src.get(), Rect.posAndSize(layout.x, layout.y, layout.width, layout.height));
		}
	}
}
