package dev.zeddevstuff.crafted.input.events;

import dev.zeddevstuff.crafted.input.MouseButton;
import dev.zeddevstuff.crafted.ui.CraftedElement;

public class PointerEvent extends UIEvent
{
	public final Type type;
	public final float x;
	public final float y;
	public final MouseButton button;
	public final float scrollX;
	public final float scrollY;

	private PointerEvent(Type type, CraftedElement target, float x, float y, MouseButton button, float scrollX, float scrollY)
	{
		super(target);
		this.type = type;
		this.x = x;
		this.y = y;
		this.button = button;
		this.scrollX = scrollX;
		this.scrollY = scrollY;
	}

	public static PointerEvent move(CraftedElement target, float x, float y)
	{
		return new PointerEvent(Type.MOVE, target, x, y, null, 0, 0);
	}
	public static PointerEvent scroll(CraftedElement target, float x, float y, float scrollX, float scrollY)
	{
		return new PointerEvent(Type.SCROLL, target, x, y, null, scrollX, scrollY);
	}
	public static PointerEvent down(CraftedElement target, float x, float y, MouseButton button)
	{
		return new PointerEvent(Type.DOWN, target, x, y, button, 0, 0);
	}
	public static PointerEvent up(CraftedElement target, float x, float y, MouseButton button)
	{
		return new PointerEvent(Type.UP, target, x, y, button, 0, 0);
	}
	public static PointerEvent click(CraftedElement target, float x, float y, MouseButton button)
	{
		return new PointerEvent(Type.CLICK, target, x, y, button, 0, 0);
	}
	public static PointerEvent doubleClick(CraftedElement target, float x, float y, MouseButton button)
	{
		return new PointerEvent(Type.DOUBLE_CLICK, target, x, y, button, 0, 0);
	}
	public static PointerEvent enter(CraftedElement target, float x, float y)
	{
		return new PointerEvent(Type.ENTER, target, x, y, null, 0, 0);
	}
	public static PointerEvent exit(CraftedElement target, float x, float y)
	{
		return new PointerEvent(Type.EXIT, target, x, y, null, 0, 0);
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
