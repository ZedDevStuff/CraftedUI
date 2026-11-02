package dev.zeddevstuff.crafted.input.events;

import dev.zeddevstuff.crafted.ui.CraftedElement;

public class SubmitEvent extends UIEvent
{
	public final UIEvent parent;
	public SubmitEvent(CraftedElement target, UIEvent parent)
	{
		super(target);
		this.parent = parent;
	}
}
