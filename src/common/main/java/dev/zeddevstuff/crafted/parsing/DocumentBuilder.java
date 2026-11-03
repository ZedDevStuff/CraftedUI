package dev.zeddevstuff.crafted.parsing;

import dev.zeddevstuff.crafted.Crafted;
import dev.zeddevstuff.crafted.ui.CraftedContext;
import dev.zeddevstuff.crafted.ui.CraftedElement;

public class DocumentBuilder
{
	public static CraftedElement buildTree(CraftedContext ctx, ParsedNode rootNode)
	{
		return buildElementRecursive(ctx, rootNode);
	}
	private static CraftedElement buildElement(CraftedContext ctx, ParsedNode node)
	{
		return ctx.crafted.elements.createElement(ctx, node.tag(), node.attributes());
	}
	private static CraftedElement buildElementRecursive(CraftedContext ctx, ParsedNode node)
	{
		var element = buildElement(ctx, node);
		for (var childNode : node.children())
		{
			var childElement = buildElementRecursive(ctx, childNode);
			element.addChild(childElement);
		}
		return element;
	}
}
