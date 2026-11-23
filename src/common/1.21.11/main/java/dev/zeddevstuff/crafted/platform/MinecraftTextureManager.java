package dev.zeddevstuff.crafted.platform;

import net.minecraft.resources.Identifier;

import java.util.Map;

public class MinecraftTextureManager implements ITextureManager
{
	private static final Map<String, MinecraftTexture> textureCache = new java.util.HashMap<>();
	@Override
	public ITexture web(String url)
	{
		return textureCache.computeIfAbsent(url, MinecraftTexture::new);
	}

	@Override
	public ITexture get(String path)
	{
		return textureCache.computeIfAbsent(path, p -> new MinecraftTexture(Identifier.parse(p)));
	}

	@Override
	public ITexture get(String namespace, String path)
	{
		return textureCache.computeIfAbsent(namespace + ":" + path, p -> new MinecraftTexture(Identifier.fromNamespaceAndPath(namespace, path)));
	}
}
