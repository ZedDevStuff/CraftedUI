package dev.zeddevstuff.crafted.input;

public enum MouseButton
{
	LEFT(0),
	RIGHT(1),
	MIDDLE(2),
	BUTTON_4(3),
	BUTTON_5(4),
	BUTTON_6(5),
	BUTTON_7(6),
	BUTTON_8(7);

	public final int code;
	MouseButton(int code)
	{
		this.code = code;
	}
}
