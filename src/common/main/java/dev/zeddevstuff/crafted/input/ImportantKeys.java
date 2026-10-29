package dev.zeddevstuff.crafted.input;

public class ImportantKeys
{
	static
	{
		var isSdl = false;
		try
		{
			Class.forName("org.lwjgl.glfw.GLFW");
		}
		catch(ClassNotFoundException e)
		{
			isSdl = true;
		}
		ESCAPE = isSdl ? 27 : 256;
		ENTER = isSdl ? 13 : 257;
		UP = isSdl ? 273 : 265;
		DOWN = isSdl ? 274 : 264;
		LEFT = isSdl ? 276 : 263;
		RIGHT = isSdl ? 275 : 262;
		SPACE = isSdl ? 32 : 32;
		BACKSPACE = isSdl ? 8 : 259;
		DELETE = isSdl ? 127 : 261;
		INSERT = isSdl ? 277 : 260;
		TAB = isSdl ? 9 : 258;
		SHIFT = isSdl ? 340 : 340;
		CONTROL = isSdl ? 341 : 341;
		ALT = isSdl ? 342 : 342;
		A = isSdl ? 97 : 65;
		C = isSdl ? 99 : 67;
		V = isSdl ? 118 : 86;
		X = isSdl ? 120 : 88;
	}

	public static int ESCAPE;
	public static int ENTER;

	public static int UP;
	public static int DOWN;
	public static int LEFT;
	public static int RIGHT;

	public static int SPACE;
	public static int BACKSPACE;
	public static int DELETE;
	public static int INSERT;

	public static int TAB;
	public static int SHIFT;
	public static int CONTROL;
	public static int ALT;

	public static int A;
	public static int C;
	public static int V;
	public static int X;
}
