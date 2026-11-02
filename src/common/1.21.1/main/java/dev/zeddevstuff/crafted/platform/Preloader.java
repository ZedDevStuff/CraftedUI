package dev.zeddevstuff.crafted.platform;

import dev.zeddevstuff.crafted.parsing.CraftedXmlParser;
import dev.zeddevstuff.crafted.parsing.DocumentBuilder;
import dev.zeddevstuff.crafted.parsing.ParsedNode;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;

public class Preloader implements IPreloader
{
	private final HashMap<String, ParsedNode> documents = new HashMap<>();
	private final Path rootPath;

	public Preloader(Path rootPath)
	{
		this.rootPath = rootPath;
	}

	@Override
	public void load()
	{
		try
		{
			var files = Files.walk(rootPath)
				.filter(p -> p.toString().endsWith(".xml"))
				.toList();
			for (var file : files)
			{
				var relativePath = rootPath.relativize(file).toString().replace("\\", "/");
				var xmlContent = Files.readString(file);
				var document = CraftedXmlParser.parse(xmlContent);
				System.out.println("[CraftedUI] Preloaded document: " + relativePath);
				documents.put(relativePath.replace(".xml", ""), document);
			}
		}
		catch (Exception e)
		{
			throw new RuntimeException("Failed to preload documents", e);
		}
	}

	@Override
	public ParsedNode getDocument(String path)
	{
		var document = documents.get(path);
		if(document == null)
			throw new RuntimeException("Document not found: " + path);
		return document;
	}
}
