package dev.zeddevstuff.crafted.ui.elements;

import dev.zeddevstuff.crafted.Prop;
import dev.zeddevstuff.crafted.parsing.ParsedNode;
import dev.zeddevstuff.crafted.parsing.Parsers;
import dev.zeddevstuff.crafted.ui.UIContext;
import dev.zeddevstuff.crafted.ui.CraftedElement;
import dev.zeddevstuff.crafted.ui.MetaElement;

import java.util.Map;

public class Collapsible extends CraftedElement implements MetaElement
{
	public final Prop<Boolean> expanded = registerProp("expanded", false, Parsers::parseBoolean);

	public Collapsible(UIContext ctx, Map<String, String> attributes)
	{
		super(ctx, attributes);
	}

	@Override
	public void setTemplateNodes(ParsedNode[] templateNodes)
	{

	}
}
