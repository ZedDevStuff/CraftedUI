package dev.zeddevstuff.crafted.platform;

public interface ITextureManager
{
	ITexture get(String path, ITexture.Kind kind, int width, int height);
	ITexture get(String namespace, String path, ITexture.Kind kind, int width, int height);
	ITexture get(String path, ITexture.Kind kind);
}
