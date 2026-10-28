package dev.zeddevstuff.crafted.types;

public record Color(int r, int g, int b, int a)
{
	public Color(int r, int g, int b)
	{
		this(Math.clamp(r, 0, 255), Math.clamp(g, 0, 255), Math.clamp(b, 0, 255), 255);
	}

	public Color(int r, int g, int b, int a)
	{
		this.r = Math.clamp(r, 0, 255);
		this.g = Math.clamp(g, 0, 255);
		this.b = Math.clamp(b, 0, 255);
		this.a = Math.clamp(a, 0, 255);
	}

	public Color withAlpha(int alpha)
	{
		return new Color(r, g, b, alpha);
	}

	public int toArgbInt()
	{
		return (a << 24) | (r << 16) | (g << 8) | b;
	}

	public int toRgbaInt()
	{
		return (r << 24) | (g << 16) | (b << 8) | a;
	}

	public static Color tryParse(String value, Color defaultValue)
	{
		if (value == null)
			return defaultValue;
		try
		{
			if (value.startsWith("#"))
			{
				value = value.substring(1);
				if (value.length() == 6)
				{
					int r = Integer.parseInt(value.substring(0, 2), 16);
					int g = Integer.parseInt(value.substring(2, 4), 16);
					int b = Integer.parseInt(value.substring(4, 6), 16);
					return new Color(r, g, b);
				}
				else if (value.length() == 8)
				{
					int r = Integer.parseInt(value.substring(0, 2), 16);
					int g = Integer.parseInt(value.substring(2, 4), 16);
					int b = Integer.parseInt(value.substring(4, 6), 16);
					int a = Integer.parseInt(value.substring(6, 8), 16);
					return new Color(r, g, b, a);
				}
			}
			else if(value.startsWith("rgb(") && value.endsWith(")"))
			{
				String[] parts = value.substring(4, value.length() - 1).split(",");
				if (parts.length == 3)
				{
					int r = Integer.parseInt(parts[0].trim());
					int g = Integer.parseInt(parts[1].trim());
					int b = Integer.parseInt(parts[2].trim());
					return new Color(r, g, b);
				}
			}
			else if(value.startsWith("rgba(") && value.endsWith(")"))
			{
				String[] parts = value.substring(5, value.length() - 1).split(",");
				if (parts.length == 4)
				{
					int r = Integer.parseInt(parts[0].trim());
					int g = Integer.parseInt(parts[1].trim());
					int b = Integer.parseInt(parts[2].trim());
					int a = Integer.parseInt(parts[3].trim());
					return new Color(r, g, b, a);
				}
			}
			else
			{
				// Try to parse as a named color
				switch (value.toLowerCase())
				{
					case "transparent": return TRANSPARENT;
					case "white": return WHITE;
					case "black": return BLACK;
					case "red": return RED;
					case "green": return GREEN;
					case "blue": return BLUE;
				}
			}
		}
		catch (NumberFormatException e)
		{
			return defaultValue;
		}
		return defaultValue;
	}

	public static Color TRANSPARENT = new Color(0, 0, 0, 0);
	public static Color WHITE = new Color(255, 255, 255);
	public static Color BLACK = new Color(0, 0, 0);

	public static Color RED = new Color(255, 0, 0);
	public static Color GREEN = new Color(0, 255, 0);
	public static Color BLUE = new Color(0, 0, 255);
}
