package dev.zeddevstuff.crafted.platform;

public interface ITextureFactory
{
	ITexture getTexture(String path, ITexture.Kind kind, int width, int height);
	ITexture getTexture(String namespace, String path, ITexture.Kind kind, int width, int height);
	// TODO: Uncomment this whenever i figure out how to get the texture size
	// ITexture getTexture(String path, ITexture.Kind kind);
}
