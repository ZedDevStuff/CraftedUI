package dev.zeddevstuff.crafted.input.events;

import dev.zeddevstuff.crafted.ui.CraftedElement;

public class FocusEvent extends UIEvent
{
	public final Type type;
	public final CraftedElement newFocus;
	public final CraftedElement previousFocus;

	private FocusEvent(CraftedElement target, Type type, CraftedElement other)
	{
		super(target);
		this.type = type;
		if(type == Type.GAINED)
		{
			this.newFocus = target;
			this.previousFocus = other;
		}
		else
		{
			this.newFocus = other;
			this.previousFocus = target;
		}
	}

	public static FocusEvent gained(CraftedElement target, CraftedElement previousFocus)
	{
		return new FocusEvent(target, Type.GAINED, previousFocus);
	}
	public static FocusEvent lost(CraftedElement target, CraftedElement previousFocus)
	{
		return new FocusEvent(target, Type.LOST, previousFocus);
	}

	public enum Type
	{
		GAINED,
		LOST
	}
}
