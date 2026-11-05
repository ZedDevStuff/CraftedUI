package dev.zeddevstuff.crafted.input.events;

import dev.zeddevstuff.crafted.ui.CraftedElement;

public class FocusEvent extends UIEvent
{
	public final Type type;
	public final CraftedElement newFocus;
	public final CraftedElement previousFocus;
	public final UIEvent source;

	private FocusEvent(CraftedElement target, Type type, CraftedElement other, UIEvent source)
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
		this.source = source;
	}

	public static FocusEvent gained(CraftedElement target, CraftedElement previousFocus, UIEvent source)
	{
		return new FocusEvent(target, Type.GAINED, previousFocus, source);
	}
	public static FocusEvent lost(CraftedElement target, CraftedElement previousFocus, UIEvent source)
	{
		return new FocusEvent(target, Type.LOST, previousFocus, source);
	}

	public enum Type
	{
		GAINED,
		LOST
	}
}
