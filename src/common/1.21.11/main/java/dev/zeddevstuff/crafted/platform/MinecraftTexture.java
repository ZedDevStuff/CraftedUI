package dev.zeddevstuff.crafted.platform;

import com.mojang.blaze3d.platform.NativeImage;
import dev.zeddevstuff.crafted.Crafted;
import dev.zeddevstuff.crafted.utils.ImageUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.resources.Identifier;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MinecraftTexture implements ITexture, AutoCloseable
{
	private static final Executor executor = Executors.newCachedThreadPool();
	private AbstractTexture texture;
	private Identifier identifier;
	public Identifier getIdentifier() { return identifier; }
	private Kind kind;
	public Kind getKind() { return kind; }
	private int width = -1;
	public int getWidth() { return width; }
	private int height = -1;
	public int getHeight() { return height; }

	public MinecraftTexture(Identifier identifier)
	{
		this.identifier = identifier;
		fetchData(true);
	}
	public MinecraftTexture(String url)
	{
		this.identifier = null;
		//CompletableFuture.runAsync(() -> downloadFromUrl(url));
		executor.execute(() -> downloadFromUrl(url));
	}

	void fetchData(boolean addExtension)
	{
		if(!Crafted.defaultInstance().dispatcher.isSameThread())
		{
			Crafted.defaultInstance().dispatcher.dispatch(() -> fetchData(addExtension));
			return;
		}
		if(identifier != null)
		{
			var actualLocation = identifier.toString().replace(":widget/", ":textures/gui/sprites/widget/");
			if (actualLocation.contains(":textures/gui/sprites/widget"))
				kind = Kind.SPRITE;
			else
				kind = Kind.SIMPLE;
			if (addExtension && !actualLocation.endsWith(".png"))
				actualLocation += ".png";
			var texture = Minecraft.getInstance().getTextureManager().getTexture(Identifier.parse(actualLocation));
			try
			{
				if (texture instanceof DynamicTexture dynamicTexture)
				{
					var nativeImage = dynamicTexture.getPixels();
					this.width = nativeImage.getWidth();
					this.height = nativeImage.getHeight();
				}
				else if (texture instanceof SimpleTexture simpleTexture)
				{
					var gpuTexture = simpleTexture.getTexture();
					this.width = gpuTexture.getWidth(0);
					this.height = gpuTexture.getHeight(0);
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

	CompletableFuture<Void> downloadFromUrl(String url)
	{
		return ImageUtils.downloadImageAsPng(url)
			.thenAccept(bytes -> {
				if (bytes == null)
				{
					identifier = MissingTextureAtlasSprite.getLocation();
					fetchData(false);
					return;
				}
				Crafted.defaultInstance().dispatcher.dispatch(() -> {
					try
					{
						var nativeImage = NativeImage.read(bytes);
						String textureName = "web_texture_" + url.hashCode();
						identifier = Identifier.fromNamespaceAndPath("crafted_ui", textureName);
						Minecraft.getInstance().getTextureManager().register(identifier, new DynamicTexture(() -> textureName,nativeImage));
						kind = Kind.SIMPLE;
						width = nativeImage.getWidth();
						height = nativeImage.getHeight();
					}
					catch (Exception e)
					{
						identifier = MissingTextureAtlasSprite.getLocation();
						fetchData(false);
						e.printStackTrace();
					}
				});
			})
			.exceptionally(e -> {
				identifier = MissingTextureAtlasSprite.getLocation();
				fetchData(false);
				e.printStackTrace();
				return null;
			});
//		try(BufferedInputStream in = new BufferedInputStream(URI.create(url).toURL().openStream()))
//		{
//			var bytes = in.readAllBytes();
//			Crafted.defaultInstance().dispatcher.dispatch(() -> {
//				try
//				{
//					var finalBytes = ImageUtils.toPng(bytes);
//					var nativeImage = NativeImage.read(finalBytes);
//					String textureName = "web_texture_" + url.hashCode();
//					identifier = Minecraft.getInstance().getTextureManager().register(textureName, new DynamicTexture(nativeImage));
//					kind = ITexture.Kind.SIMPLE;
//					width = nativeImage.getWidth();
//					height = nativeImage.getHeight();
//				}
//				catch (Exception e)
//				{
//					identifier = MissingTextureAtlasSprite.getLocation();
//					fetchData(false);
//					e.printStackTrace();
//				}
//			});
//		}
//		catch (Exception e)
//		{
//			identifier = MissingTextureAtlasSprite.getLocation();
//			fetchData(false);
//			e.printStackTrace();
//		}
	}

	@Override
	public void close() throws Exception
	{
		if(identifier != null)
		{
			var textureManager = Minecraft.getInstance().getTextureManager();
			textureManager.release(identifier);
		}
	}
}
