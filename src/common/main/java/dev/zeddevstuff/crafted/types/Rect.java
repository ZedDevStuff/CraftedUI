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

	public Rect(float minX, float minY, float maxX, float maxY)
	{
		this.minX = Math.min(minX, maxX);
		this.minY = Math.min(minY, maxY);
		this.maxX = Math.max(minX, maxX);
		this.maxY = Math.max(minY, maxY);
	}

	public static Rect posAndSize(float x, float y, float width, float height)
	{
		return new Rect(x, y, x + width, y + height);
	}

	public float uOffset(float width)
	{
		var u = width / minX;
		if(Float.isInfinite(u) || Float.isNaN(u))
			return 0f;
		return u;
	}
	public float vOffset(float height)
	{
		var v = height / minY;
		if(Float.isInfinite(v) || Float.isNaN(v))
			return 0f;
		return v;
	}
	public float uScale(float width)
	{
		var u = width / this.width();
		if(Float.isInfinite(u) || Float.isNaN(u))
			return 0f;
		return u;
	}
	public float vScale(float height)
	{
		var v = height / this.height();
		if (Float.isInfinite(v) || Float.isNaN(v))
			return 0f;
		return v;
	}
}
