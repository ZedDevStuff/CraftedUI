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
	public static MouseEvent down(CraftedElement target, float x, float y, MouseButton button)
	{
		return new MouseEvent(Type.DOWN, target, x, y, button, 0, 0);
	}
	public static MouseEvent up(CraftedElement target, float x, float y, MouseButton button)
	{
		return new MouseEvent(Type.UP, target, x, y, button, 0, 0);
	}
	public static MouseEvent click(CraftedElement target, float x, float y, MouseButton button)
	{
		return new MouseEvent(Type.CLICK, target, x, y, button, 0, 0);
	}
	public static MouseEvent doubleClick(CraftedElement target, float x, float y, MouseButton button)
	{
		return new MouseEvent(Type.DOUBLE_CLICK, target, x, y, button, 0, 0);
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
		DOWN,
		UP,
		CLICK,
		DOUBLE_CLICK,
		ENTER,
		EXIT
	}
}
