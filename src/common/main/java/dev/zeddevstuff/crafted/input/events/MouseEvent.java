package dev.zeddevstuff.crafted.input.events;

import dev.zeddevstuff.crafted.input.MouseButton;
import dev.zeddevstuff.crafted.ui.CraftedElement;

public class MouseEvent extends UIEvent
{
	public final Type type;
	public final float x;
	public final float y;
	public final MouseButton button;
	public final float scrollX;
	public final float scrollY;

	private MouseEvent(Type type, CraftedElement target, float x, float y, MouseButton button, float scrollX, float scrollY)
	{
		super(target);
		this.type = type;
		this.x = x;
		this.y = y;
		this.button = button;
		this.scrollX = scrollX;
		this.scrollY = scrollY;
	}

	public static MouseEvent move(CraftedElement target, float x, float y)
	{
		return new MouseEvent(Type.MOVE, target, x, y, null, 0, 0);
	}
	public static MouseEvent scroll(CraftedElement target, float x, float y, float scrollX, float scrollY)
	{
		return new MouseEvent(Type.SCROLL, target, x, y, null, scrollX, scrollY);
	}
	public static MouseEvent press(CraftedElement target, float x, float y, MouseButton button)
	{
		return new MouseEvent(Type.PRESS, target, x, y, button, 0, 0);
	}
	public static MouseEvent release(CraftedElement target, float x, float y, MouseButton button)
	{
		return new MouseEvent(Type.RELEASE, target, x, y, button, 0, 0);
	}
	public static MouseEvent enter(CraftedElement target, float x, float y)
	{
		return new MouseEvent(Type.ENTER, target, x, y, null, 0, 0);
	}
	public static MouseEvent exit(CraftedElement target, float x, float y)
	{
		return new MouseEvent(Type.EXIT, target, x, y, null, 0, 0);
	}

	public enum Type
	{
		MOVE,
		SCROLL,
		PRESS,
		RELEASE,
		ENTER,
		EXIT
	}
}
