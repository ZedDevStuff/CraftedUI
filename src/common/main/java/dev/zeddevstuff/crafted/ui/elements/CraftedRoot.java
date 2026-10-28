package dev.zeddevstuff.crafted.ui.elements;

import dev.zeddevstuff.crafted.Crafted;
import dev.zeddevstuff.crafted.Prop;
import dev.zeddevstuff.crafted.annotations.DefineElement;
import dev.zeddevstuff.crafted.annotations.Description;
import dev.zeddevstuff.crafted.parsing.Parsers;
import dev.zeddevstuff.crafted.platform.IText;
import dev.zeddevstuff.crafted.ui.CraftedContext;
import dev.zeddevstuff.crafted.ui.CraftedElement;
import org.appliedenergistics.yoga.style.YogaStyle;

import java.util.Map;

@DefineElement("Crafted")
@Description("Root element of a Crafted document. All other elements must be children of this element.")
public class CraftedRoot extends CraftedElement
{
	public static void register(Crafted crafted)
	{
		crafted.elements.registerElement(CraftedRoot.class, "Crafted", CraftedRoot::new);
	}
	@Description("Title of the document, used for window title and other purposes")
	public final Prop<IText> title = registerProp(IText.class, "title", IText.literal("Crafted Document"), Parsers::parseText);
	@Description("The renderer to use for elements in the tree")
	public final Prop<String> renderer = registerProp("renderer", "default", Parsers::parseString);

	public CraftedRoot(CraftedContext ctx, Map<String, String> attributes)
	{
		super(ctx, attributes);
		//tryParseInto(title, attributes.get("title"), Parsers::tryParseText);
		renderer.addListener((prop, oldValue, newValue) -> {
			ctx.currentRenderer = newValue;
			setRendererRecursive(newValue);
		});
	}

	@Override
	protected YogaStyle initLayoutNode(YogaStyle style, Map<String, String> attributes)
	{
		return style;
	}
}
