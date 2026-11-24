package dev.zeddevstuff.crafted.parsing;

import dev.zeddevstuff.crafted.ui.UIContext;
import dev.zeddevstuff.crafted.ui.CraftedElement;
import dev.zeddevstuff.crafted.ui.MetaElement;

public class UIDocumentBuilder
{
	public static CraftedElement buildTree(UIContext ctx, ParsedNode rootNode)
	{
		return buildElementRecursive(ctx, rootNode);
	}
	private static CraftedElement buildElement(UIContext ctx, ParsedNode node)
	{
		return ctx.crafted.elements.createElement(ctx, node.tag(), node.attributes());
	}
	private static CraftedElement buildElementRecursive(UIContext ctx, ParsedNode node)
	{
		var element = buildElement(ctx, node);
		if(element instanceof MetaElement metaElement)
		{
			metaElement.setTemplateNodes(node.children());
			return element;
		}
		for (var childNode : node.children())
		{
			var childElement = buildElementRecursive(ctx, childNode);
			element.addChild(childElement);
		}
		return element;
	}
}
