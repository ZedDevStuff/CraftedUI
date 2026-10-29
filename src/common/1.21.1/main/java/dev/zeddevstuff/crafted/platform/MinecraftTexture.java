package dev.zeddevstuff.crafted.platform;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;

public class MinecraftTexture implements ITexture
{
	public final ResourceLocation resourceLocation;
	private final Kind kind;
	public Kind getKind() { return kind; }
	private final int width;
	public int getWidth() { return width; }
	private final int height;
	public int getHeight() { return height; }

	public MinecraftTexture(ResourceLocation resourceLocation, Kind kind)
	{
		throw new UnsupportedOperationException("I don't remember how to fetch the actual size of the textures :(. Probably involves mixins or access wideners");
	}

	public MinecraftTexture(ResourceLocation resourceLocation, Kind kind, int width, int height)
	{
		this.resourceLocation = resourceLocation;
		this.kind = kind;
		this.width = width;
		this.height = height;
	}
}
