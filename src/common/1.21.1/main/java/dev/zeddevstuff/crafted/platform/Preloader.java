package dev.zeddevstuff.crafted.platform;

import dev.zeddevstuff.crafted.Constants;
import dev.zeddevstuff.crafted.parsing.CraftedXmlParser;
import dev.zeddevstuff.crafted.parsing.ParsedNode;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

public class Preloader implements IPreloader
{
    private final Logger logger = LogUtils.getLogger();
	private final HashMap<String, ParsedNode> documents = new HashMap<>();
	private final Path rootPath;

	public Preloader(Path rootPath)
	{
		this.rootPath = rootPath;
	}

	@Override
	public void load()
	{
		try(var stream = Files.walk(rootPath))
		{
			var files = stream
				.filter(p -> p.toString().endsWith(".xml"))
				.toList();
			for (var file : files)
			{
                try
                {
                    var relativePath = rootPath.relativize(file).toString().replace("\\", "/");
                    var xmlContent = Files.readString(file);
                    
                    var document = CraftedXmlParser.parse(xmlContent);
                    if(Constants.DEBUG)
                        logger.debug("Preloaded document: " + relativePath);
                    documents.put(relativePath.replace(".xml", ""), document);
                }
                catch (Exception e)
                {
                    logger.error("Failed to preload document: " + file, e);
                }
            }
		}
		catch (Exception e)
		{
			logger.error("Failed to preload documents from path: " + rootPath, e);
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
