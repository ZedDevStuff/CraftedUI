package dev.zeddevstuff.crafted.platform;

import dev.zeddevstuff.crafted.parsing.ParsedNode;
import dev.zeddevstuff.crafted.utils.Lazy;

public interface IDocumentLoader
{
	Lazy<ParsedNode> getDocument(String name);
	default Lazy<ParsedNode> getDocument(String namespace, String path)
	{
		return getDocument(namespace + ":" + path);
	}
}
