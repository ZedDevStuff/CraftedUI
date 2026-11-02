package dev.zeddevstuff.crafted.types;

public record Float2(float x, float y)
{
	public float width() { return x; }
	public float height() { return y; }
}
