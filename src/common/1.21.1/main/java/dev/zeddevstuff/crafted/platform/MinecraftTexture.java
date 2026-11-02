package dev.zeddevstuff.crafted.platform;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.resources.ResourceLocation;

public class MinecraftTexture implements ITexture
{
	public final ResourceLocation resourceLocation;
	private Kind kind;
	public Kind getKind() { return kind; }
	private int width = -1;
	public int getWidth() { return width; }
	private int height = -1;
	public int getHeight() { return height; }

	public MinecraftTexture(ResourceLocation resourceLocation)
	{
		this.resourceLocation = resourceLocation;
		fetchData();
	}

	void fetchData()
	{
		var actualLocation = resourceLocation.toString().replace(":widget/", ":textures/gui/sprites/widget/");
		if(actualLocation.contains(":textures/gui/sprites/widget"))
			kind = ITexture.Kind.SPRITE;
		else
			kind = ITexture.Kind.SIMPLE;
		if(!actualLocation.endsWith(".png"))
			actualLocation += ".png";
		var resourceLocation = ResourceLocation.parse(actualLocation);
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
