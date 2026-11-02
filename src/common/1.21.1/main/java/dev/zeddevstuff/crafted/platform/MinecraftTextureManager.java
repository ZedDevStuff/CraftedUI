package dev.zeddevstuff.crafted.platform;

import net.minecraft.resources.ResourceLocation;

public class MinecraftTextureManager implements ITextureManager
{
	@Override
	public ITexture get(String path, ITexture.Kind kind, int width, int height)
	{
		return new MinecraftTexture(ResourceLocation.parse(path), kind, width, height);
	}

	@Override
	public ITexture get(String namespace, String path, ITexture.Kind kind, int width, int height)
	{
		return new MinecraftTexture(ResourceLocation.fromNamespaceAndPath(namespace, path), kind, width, height);
	}

	@Override
	public ITexture get(String path, ITexture.Kind kind)
	{
		return new MinecraftTexture(ResourceLocation.parse(path), kind);
	}
}
