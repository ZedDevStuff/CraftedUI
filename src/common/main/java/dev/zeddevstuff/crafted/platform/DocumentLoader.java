package dev.zeddevstuff.crafted.platform;

import dev.zeddevstuff.crafted.parsing.CraftedXmlParser;
import dev.zeddevstuff.crafted.parsing.ParsedNode;
import dev.zeddevstuff.crafted.utils.Lazy;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public class DocumentLoader
{
	private final Class<?> sourceClass;
	private final HashMap<String, Lazy<ParsedNode>> cache = new HashMap<>();

	public DocumentLoader(Class<?> sourceClass)
	{
		this.sourceClass = sourceClass;
	}

	public Lazy<ParsedNode> getDocument(@Nonnull String path)
	{
		if(cache.containsKey(path))
			return cache.get(path);
		var doc = new Lazy<>(() -> getAndParseDocument(path));
		cache.put(path, doc);
		return doc;
	}

	private ParsedNode getAndParseDocument(String path)
	{
		var resource = sourceClass.getResource(path);
		if(resource == null)
			throw new RuntimeException("Could not find document at '" + path + "'");
		try(var stream = resource.openStream())
		{
			String xml = new String(stream.readAllBytes(), StandardCharsets.UTF_8);
			return CraftedXmlParser.parse(xml);
		}
		catch (IOException e)
		{
			throw new RuntimeException("Failed to open document at '" + path + "'", e);
		}
		catch (Exception e)
		{
			throw new RuntimeException("Failed to parse document at '" + path + "'", e);
		}
	}
}
