package dev.zeddevstuff.crafted.types;

public enum ShadowDirection
{
	TOP_LEFT(-1, -1),
	TOP_RIGHT(1, -1),
	BOTTOM_LEFT(-1, 1),
	BOTTOM_RIGHT(1, 1);

	public final int x;
	public final int y;
	ShadowDirection(int x, int y)
	{
		this.x = x;
		this.y = y;
	}
}