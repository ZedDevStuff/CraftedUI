package dev.zeddevstuff.crafted.platform;

public interface ITextureManager
{
	ITexture get(String path);
	ITexture get(String namespace, String path);
}
