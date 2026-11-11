package dev.zeddevstuff.crafted.platform;

import net.minecraft.resources.ResourceLocation;

public class MinecraftTextureManager implements ITextureManager
{
	@Override
	public ITexture web(String url)
	{
		return new MinecraftTexture(url);
	}

	@Override
	public ITexture get(String path)
	{
		return new MinecraftTexture(ResourceLocation.parse(path));
	}

	@Override
	public ITexture get(String namespace, String path)
	{
		return new MinecraftTexture(ResourceLocation.fromNamespaceAndPath(namespace, path));
	}
}
