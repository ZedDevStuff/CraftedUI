package dev.zeddevstuff.crafted.utils;

public class MathUtils
{
	/**
	 * Clamps a value between a minimum and maximum value. This only exists because Math.clamp is not available in older versions of Java
	 * @param value
	 * @param min
	 * @param max
	 * @return
	 */
	public static float clamp(float value, float min, float max)
	{
		return Math.max(min, Math.min(max, value));
	}
}
