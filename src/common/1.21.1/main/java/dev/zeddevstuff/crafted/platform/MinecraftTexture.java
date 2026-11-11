package dev.zeddevstuff.crafted.platform;

import com.mojang.blaze3d.platform.NativeImage;
import dev.zeddevstuff.crafted.Crafted;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.resources.ResourceLocation;

import java.io.BufferedInputStream;
import java.net.URI;
import java.util.concurrent.CompletableFuture;

public class MinecraftTexture implements ITexture
{
	private AbstractTexture texture;
	private ResourceLocation resourceLocation;
	public ResourceLocation getResourceLocation() { return resourceLocation; }
	private Kind kind;
	public Kind getKind() { return kind; }
	private int width = -1;
	public int getWidth() { return width; }
	private int height = -1;
	public int getHeight() { return height; }

	public MinecraftTexture(ResourceLocation resourceLocation)
	{
		this.resourceLocation = resourceLocation;
		fetchData(true);
	}
	public MinecraftTexture(String url)
	{
		this.resourceLocation = null;
		CompletableFuture.runAsync(() -> downloadFromUrl(url));
	}

	void fetchData(boolean addExtension)
	{
		if(resourceLocation != null)
		{
			var actualLocation = resourceLocation.toString().replace(":widget/", ":textures/gui/sprites/widget/");
			if (actualLocation.contains(":textures/gui/sprites/widget"))
				kind = ITexture.Kind.SPRITE;
			else
				kind = ITexture.Kind.SIMPLE;
			if (addExtension && !actualLocation.endsWith(".png"))
				actualLocation += ".png";
			var resourceLocation = ResourceLocation.parse(actualLocation);
			var texture = Minecraft.getInstance().getTextureManager().getTexture(resourceLocation);
			try
			{
				if (texture instanceof DynamicTexture dynamicTexture)
				{
					var nativeImage = dynamicTexture.getPixels();
					if (nativeImage != null)
					{
						this.width = nativeImage.getWidth();
						this.height = nativeImage.getHeight();
						return;
					}
					this.width = 0;
					this.height = 0;
				} else if (texture instanceof SimpleTexture simpleTexture)
				{
					var manager = Minecraft.getInstance().getResourceManager();
					var textureImage = simpleTexture.getTextureImage(manager);
					var nativeImage = textureImage.getImage();
					this.width = nativeImage.getWidth();
					this.height = nativeImage.getHeight();
				} else
				{
					this.width = 0;
					this.height = 0;
				}
			} catch (Exception e)
			{
				throw new RuntimeException(e);
			}
		}
	}

	void downloadFromUrl(String url)
	{
		try(BufferedInputStream in = new BufferedInputStream(URI.create(url).toURL().openStream()))
		{
			var bytes = in.readAllBytes();
			Crafted.defaultInstance().dispatcher.dispatch(() -> {
				try
				{
					var nativeImage = NativeImage.read(bytes);
					String textureName = "web_texture_" + url.hashCode();
					resourceLocation = Minecraft.getInstance().getTextureManager().register(textureName, new DynamicTexture(nativeImage));
					kind = ITexture.Kind.SIMPLE;
					width = nativeImage.getWidth();
					height = nativeImage.getHeight();
				}
				catch (Exception e)
				{
					resourceLocation = MissingTextureAtlasSprite.getLocation();
					fetchData(false);
					e.printStackTrace();
				}
			});
		}
		catch (Exception e)
		{
			resourceLocation = MissingTextureAtlasSprite.getLocation();
			fetchData(false);
			e.printStackTrace();
		}
	}
}
