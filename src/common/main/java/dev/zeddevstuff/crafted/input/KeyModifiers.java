package dev.zeddevstuff.crafted.input;

public record KeyModifiers(boolean shift, boolean control, boolean alt)
{
	public static KeyModifiers fromFlags(int flags)
	{
		return new KeyModifiers(
			(flags & 0x1) != 0,
			(flags & 0x2) != 0,
			(flags & 0x4) != 0
		);
	}
}
