package dev.zeddevstuff.crafted.input.events;

import dev.zeddevstuff.crafted.input.KeyModifiers;

public class KeyboardEvent
{
	public final Type type;

	public final int keyCode;
	public final int scanCode;
	public final char character;
	public final KeyModifiers modifiers;

	private KeyboardEvent(Type type, int keyCode, int scanCode, char character, KeyModifiers modifiers)
	{
		this.type = type;
		this.keyCode = keyCode;
		this.scanCode = scanCode;
		this.character = character;
		this.modifiers = modifiers;
	}

	public static KeyboardEvent press(int keyCode, int scanCode, char character, KeyModifiers modifiers)
	{
		return new KeyboardEvent(Type.PRESS, keyCode, scanCode, character, modifiers);
	}
	public static KeyboardEvent release(int keyCode, int scanCode, char character, KeyModifiers modifiers)
	{
		return new KeyboardEvent(Type.RELEASE, keyCode, scanCode, character, modifiers);
	}
	public static KeyboardEvent type(int keyCode, int scanCode, char character, KeyModifiers modifiers)
	{
		return new KeyboardEvent(Type.TYPE, keyCode, scanCode, character, modifiers);
	}

	public enum Type
	{
		PRESS,
		RELEASE,
		TYPE
	}
}
