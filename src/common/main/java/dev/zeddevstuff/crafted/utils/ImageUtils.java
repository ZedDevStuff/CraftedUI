package dev.zeddevstuff.crafted.utils;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.concurrent.CompletableFuture;

public class ImageUtils
{
	public static CompletableFuture<InputStream> downloadImageAsPng(String url)
	{
		return CompletableFuture.supplyAsync(() -> {
			try
			{
				var image = ImageIO.read(URI.create(url).toURL());
				var outputStream = new java.io.ByteArrayOutputStream();
				ImageIO.write(image, "png", outputStream);
				var bytes = outputStream.toByteArray();
				outputStream.close();
				return new java.io.ByteArrayInputStream(bytes);
			}
			catch (MalformedURLException e)
			{
				e.printStackTrace();
				return null;
			}
			catch (IOException e)
			{
				e.printStackTrace();
				return null;
			}
		});
	}
}
