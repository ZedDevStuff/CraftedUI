package dev.zeddevstuff.crafted.platform;

import dev.zeddevstuff.crafted.Crafted;
import dev.zeddevstuff.crafted.parsing.DocumentBuilder;
import dev.zeddevstuff.crafted.parsing.ParsedNode;
import dev.zeddevstuff.crafted.ui.CraftedContext;

/**
 * Represents an object that can load and parse XML documents then cache them for later use. <br>
 * Pass any {@link ParsedNode} to {@link DocumentBuilder#buildTree(Crafted, CraftedContext, ParsedNode)} to build a ready-to-use element tree from the document.
 */
public interface IPreloader
{
	void load();

	ParsedNode getDocument(String path);
}
