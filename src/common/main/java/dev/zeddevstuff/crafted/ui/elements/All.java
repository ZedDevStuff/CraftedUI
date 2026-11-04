package dev.zeddevstuff.crafted.ui.elements;

import dev.zeddevstuff.crafted.Crafted;

public class All
{
	public static void registerInto(Crafted crafted)
	{
		CraftedRoot.register(crafted);
		Box.register(crafted);
		Text.register(crafted);
		Button.register(crafted);
		Checkbox.register(crafted);
		Input.register(crafted);
		Image.register(crafted);
		ScrollView.register(crafted);
		Dropdown.register(crafted);
	}
}
