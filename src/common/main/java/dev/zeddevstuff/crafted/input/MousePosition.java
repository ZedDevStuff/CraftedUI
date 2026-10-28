package dev.zeddevstuff.crafted.input;

public record MousePosition(float x, float y)
{
	public static final MousePosition ZERO = new MousePosition(0, 0);
}