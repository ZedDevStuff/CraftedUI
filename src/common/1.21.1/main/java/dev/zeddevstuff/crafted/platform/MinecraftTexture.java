package dev.zeddevstuff.crafted.platform;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.resources.ResourceLocation;

public class MinecraftTexture implements ITexture
{
	public final ResourceLocation resourceLocation;
	private final Kind kind;
	public Kind getKind() { return kind; }
	private int width = -1;
	public int getWidth()
	{
		if(width == -1)
			fetchSize();
		return width;
	}
	private int height = -1;
	public int getHeight()
	{
		if(height == -1)
			fetchSize();
		return height;
	}

	public MinecraftTexture(ResourceLocation resourceLocation, Kind kind)
	{
		this.resourceLocation = resourceLocation;
		this.kind = kind;
	}

	public MinecraftTexture(ResourceLocation resourceLocation, Kind kind, int width, int height)
	{
		this(resourceLocation, kind);
		this.width = width;
		this.height = height;
	}

	void fetchSize()
	{
		var texture = Minecraft.getInstance().getTextureManager().getTexture(resourceLocation);
		try
		{
			if (texture instanceof DynamicTexture dynamicTexture)
			{
				var nativeImage = dynamicTexture.getPixels();
				if(nativeImage != null)
				{
					this.width = nativeImage.getWidth();
					this.height = nativeImage.getHeight();
					return;
				}
				this.width = 0;
				this.height = 0;
			}
			else if (texture instanceof SimpleTexture simpleTexture)
			{
				var manager = Minecraft.getInstance().getResourceManager();
				var textureImage = simpleTexture.getTextureImage(manager);
				var nativeImage = textureImage.getImage();
				this.width = nativeImage.getWidth();
				this.height = nativeImage.getHeight();
			}
			else
			{
				this.width = 0;
				this.height = 0;
			}
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}
}
