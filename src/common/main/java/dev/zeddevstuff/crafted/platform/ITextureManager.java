package dev.zeddevstuff.crafted.platform;

public interface ITextureManager
{
	ITexture web(String url);
	ITexture get(String path);
	ITexture get(String namespace, String path);
}
