package dev.zeddevstuff.crafted.platform;

public interface ITexture
{
	Kind getKind();
	int getWidth();
	int getHeight();
	enum Kind
	{
		SIMPLE,
		SPRITE,
	}
	public enum TileMode
	{
		REPEAT,
		CLAMP
	}
}
