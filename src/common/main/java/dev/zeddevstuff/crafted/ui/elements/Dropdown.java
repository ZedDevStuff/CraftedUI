package dev.zeddevstuff.crafted.ui.elements;

import dev.zeddevstuff.crafted.Prop;
import dev.zeddevstuff.crafted.annotations.DefineElement;
import dev.zeddevstuff.crafted.rendering.IRenders;
import dev.zeddevstuff.crafted.ui.CraftedContext;
import dev.zeddevstuff.crafted.ui.CraftedElement;
import dev.zeddevstuff.crafted.utils.TypedList;

import java.util.Map;

@DefineElement("Dropdown")
public class Dropdown extends CraftedElement
{
	public final Prop<TypedList<?>> options = registerProp(TypedList.class, "options", null, this::parseList);
	public final Prop<IStringProvider> labelProvider = registerProp(IStringProvider.class, "labelProvider", Object::toString, this::parseFunction);

	public Dropdown(CraftedContext ctx, Map<String, String> attributes)
	{
		super(ctx, attributes);
	}

	private TypedList<?> parseList(String s, TypedList<?> list)
	{
		var obj = ctx.getVar(TypedList.class, s);
		if(obj != null)
			return obj.value;
		return null;
	}

	private IStringProvider parseFunction(String s, IStringProvider function)
	{
		var obj = ctx.getVar(IStringProvider.class, s);
		if(obj != null)
			return obj.value;
		return Object::toString;
	}

	public static void register(dev.zeddevstuff.crafted.Crafted crafted)
	{
		crafted.elements.registerElement(Dropdown.class, "Dropdown", Dropdown::new);
		crafted.renderers.registerRenderer(Dropdown.class, "default", new DefaultDropdownRenderer());
	}

	public static class DefaultDropdownRenderer implements IRenders<Dropdown>
	{
		@Override
		public void render(dev.zeddevstuff.crafted.platform.ICanvas canvas, Dropdown element)
		{
			var layout = element.getLayout();
		}
	}

	public interface IStringProvider
	{
		String getString(Object obj);
	}
}
