package dev.zeddevstuff.crafted.types;

public record Rect(float minX, float minY, float maxX, float maxY)
{
	public float width()
	{
		return maxX - minX;
	}
	public float height()
	{
		return maxY - minY;
	}

	public static Rect posAndSize(float x, float y, float width, float height)
	{
		return new Rect(x, y, x + width, y + height);
	}
}
