package dev.zeddevstuff.crafted.input.events;

import dev.zeddevstuff.crafted.input.KeyModifiers;
import dev.zeddevstuff.crafted.ui.CraftedElement;

public class KeyEvent extends UIEvent
{
	public final Type type;

	public final int keyCode;
	public final int scanCode;
	public final char character;
	public final KeyModifiers modifiers;

	private KeyEvent(CraftedElement target, Type type, int keyCode, int scanCode, char character, KeyModifiers modifiers)
	{
		super(target);
		this.type = type;
		this.keyCode = keyCode;
		this.scanCode = scanCode;
		this.character = character;
		this.modifiers = modifiers;
	}

	public static KeyEvent press(CraftedElement target, int keyCode, int scanCode, char character, KeyModifiers modifiers)
	{
		return new KeyEvent(target, Type.PRESS, keyCode, scanCode, character, modifiers);
	}
	public static KeyEvent release(CraftedElement target, int keyCode, int scanCode, char character, KeyModifiers modifiers)
	{
		return new KeyEvent(target, Type.RELEASE, keyCode, scanCode, character, modifiers);
	}
	public static KeyEvent type(CraftedElement target, int keyCode, int scanCode, char character, KeyModifiers modifiers)
	{
		return new KeyEvent(target, Type.TYPE, keyCode, scanCode, character, modifiers);
	}

	public enum Type
	{
		PRESS,
		RELEASE,
		TYPE
	}
}
