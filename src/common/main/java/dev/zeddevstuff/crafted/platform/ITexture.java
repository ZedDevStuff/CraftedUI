package dev.zeddevstuff.crafted.platform;

public interface ITexture
{
	int getWidth();
	int getHeight();

	public enum TileMode
	{
		REPEAT,
		CLAMP
	}
}
