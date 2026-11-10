package dev.zeddevstuff.crafted.platform;

import dev.zeddevstuff.crafted.parsing.CraftedXmlParser;
import dev.zeddevstuff.crafted.parsing.ParsedNode;
import dev.zeddevstuff.crafted.utils.Lazy;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public class DocumentLoader implements IDocumentLoader
{
	private final HashMap<String, Lazy<ParsedNode>> cache = new HashMap<>();
	@Override
	public Lazy<ParsedNode> getDocument(String name)
	{
		if(cache.containsKey(name))
			return cache.get(name);
		var doc = new Lazy<>(() -> {
			var resource = Minecraft.getInstance().getResourceManager().getResource(ResourceLocation.parse(name)).orElseThrow(() -> new RuntimeException("Could not find document: " + name));
			try(var stream = resource.open())
			{
				String xml = new String(stream.readAllBytes(), StandardCharsets.UTF_8);
				return CraftedXmlParser.parse(xml);
			}
			catch (Exception e)
			{
				throw new RuntimeException("Failed to parse document: " + name, e);
			}
		});
		cache.put(name, doc);
		return doc;
	}
}
