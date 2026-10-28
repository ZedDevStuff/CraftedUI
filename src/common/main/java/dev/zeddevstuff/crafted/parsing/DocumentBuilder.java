package dev.zeddevstuff.crafted.parsing;

import dev.zeddevstuff.crafted.Crafted;
import dev.zeddevstuff.crafted.ui.CraftedContext;
import dev.zeddevstuff.crafted.ui.CraftedElement;

public class DocumentBuilder
{
	public static CraftedElement buildTree(Crafted crafted, CraftedContext ctx, ParsedNode rootNode)
	{
		return buildElementRecursive(crafted, ctx, rootNode);
	}
	private static CraftedElement buildElement(Crafted crafted, CraftedContext ctx, ParsedNode node)
	{
		return crafted.elements.createElement(ctx, node.tag(), node.attributes());
	}
	private static CraftedElement buildElementRecursive(Crafted crafted, CraftedContext ctx, ParsedNode node)
	{
		var element = buildElement(crafted, ctx, node);
		for (var childNode : node.children())
		{
			var childElement = buildElementRecursive(crafted, ctx, childNode);
			element.addChild(childElement);
		}
		return element;
	}
}
