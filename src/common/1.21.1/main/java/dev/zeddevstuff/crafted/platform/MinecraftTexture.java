package dev.zeddevstuff.crafted.platform;

import net.minecraft.resources.ResourceLocation;

public class MinecraftTexture implements ITexture
{
	public final ResourceLocation resourceLocation;
	private int width;
	@Override
	public int getWidth() { return 0; }
	private int height;
	@Override
	public int getHeight() { return 0; }

	public MinecraftTexture(ResourceLocation resourceLocation)
	{
		//this.resourceLocation = resourceLocation;
		throw new UnsupportedOperationException("Not implemented yet");
	}

	public MinecraftTexture(ResourceLocation resourceLocation, int width, int height)
	{
		this.resourceLocation = resourceLocation;
		this.width = width;
		this.height = height;
	}
}
