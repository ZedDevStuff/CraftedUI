package dev.zeddevstuff.crafted.ui.elements;

import dev.zeddevstuff.crafted.Crafted;

public class All
{
	public static void registerInto(Crafted crafted)
	{
		CraftedRoot.register(crafted);
		Box.register(crafted);
	}
}
