package dev.zeddevstuff.crafted.platform;

import net.minecraft.resources.ResourceLocation;

public class MinecraftTextureFactory implements ITextureFactory
{
	@Override
	public ITexture getTexture(String path, ITexture.Kind kind, int width, int height)
	{
		return new MinecraftTexture(ResourceLocation.parse(path), kind, width, height);
	}

	@Override
	public ITexture getTexture(String namespace, String path, ITexture.Kind kind, int width, int height)
	{
		return new MinecraftTexture(ResourceLocation.fromNamespaceAndPath(namespace, path), kind, width, height);
	}
}
