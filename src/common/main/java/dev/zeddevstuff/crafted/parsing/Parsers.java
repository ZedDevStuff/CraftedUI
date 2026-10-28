package dev.zeddevstuff.crafted.parsing;

import dev.zeddevstuff.crafted.platform.IText;
import dev.zeddevstuff.crafted.types.Color;
import dev.zeddevstuff.crafted.types.StyleLength2;
import dev.zeddevstuff.crafted.types.StyleLength4;
import org.appliedenergistics.yoga.*;
import org.appliedenergistics.yoga.style.StyleLength;
import org.appliedenergistics.yoga.style.StyleSizeLength;

import java.util.Locale;

public class Parsers
{
	public static String parseString(String value, String defaultValue)
	{
		if (value == null)
			return defaultValue;
		return value;
	}
	public static IText parseText(String value, IText defaultValue)
	{
		if (value == null)
			return defaultValue;
		try
		{
			if(value.startsWith("loc:"))
				return IText.localized(value.substring(4));
			else
				return IText.literal(value);
		}
		catch (Exception e)
		{
			return defaultValue;
		}
	}
	public static Integer parseInt(String value, Integer defaultValue)
	{
		if (value == null)
			return defaultValue;
		try
		{
			return Integer.parseInt(value);
		}
		catch (NumberFormatException e)
		{
			return defaultValue;
		}
	}
	public static Float parseFloat(String value, Float defaultValue)
	{
		if (value == null)
			return defaultValue;
		try
		{
			return Float.parseFloat(value);
		}
		catch (NumberFormatException e)
		{
			return defaultValue;
		}
	}
	public static StyleLength parseStyleLength(String value, StyleLength defaultValue)
	{
		if (value == null)
			return defaultValue;
		try
		{
			if (value.endsWith("px"))
				return StyleLength.points(Integer.parseInt(value.substring(0, value.length() - 2)));
			else if (value.endsWith("%"))
				return StyleLength.percent(Integer.parseInt(value.substring(0, value.length() - 1)));
			else if (value.equals("auto"))
				return StyleLength.ofAuto();
			else
				return StyleLength.points(Integer.parseInt(value));
		}
		catch (NumberFormatException e)
		{
			return defaultValue;
		}
	}
	public static StyleLength2 parseStyleLength2(String value, StyleLength2 defaultValue)
	{
		if (value == null)
			return defaultValue;
		try
		{
			String[] parts = value.split("[, ]+");
			if (parts.length == 1)
			{
				StyleLength length = parseStyleLength(parts[0], StyleLength.ofAuto());
				return new StyleLength2(length, length);
			}
			else if (parts.length == 2)
			{
				return new StyleLength2(parseStyleLength(parts[0], StyleLength.ofAuto()), parseStyleLength(parts[1], StyleLength.ofAuto()));
			}
			else
			{
				return defaultValue;
			}
		}
		catch (Exception e)
		{
			return defaultValue;
		}
	}
	public static StyleLength4 parseStyleLength4(String value, StyleLength4 defaultValue)
	{
		if (value == null)
			return defaultValue;
		try
		{
			String[] parts = value.split("[, ]+");
			if (parts.length == 1)
			{
				StyleLength length = parseStyleLength(parts[0], StyleLength.ofAuto());
				return new StyleLength4(length, length, length, length);
			}
			else if (parts.length == 2)
			{
				StyleLength vertical = parseStyleLength(parts[0], StyleLength.ofAuto());
				StyleLength horizontal = parseStyleLength(parts[1], StyleLength.ofAuto());
				return new StyleLength4(vertical, horizontal, vertical, horizontal);
			}
			else if (parts.length == 4)
			{
				return new StyleLength4(
						parseStyleLength(parts[0], StyleLength.ofAuto()),
						parseStyleLength(parts[1], StyleLength.ofAuto()),
						parseStyleLength(parts[2], StyleLength.ofAuto()),
						parseStyleLength(parts[3], StyleLength.ofAuto())
				);
			}
			else
			{
				return defaultValue;
			}
		}
		catch (Exception e)
		{
			return defaultValue;
		}
	}
	public static StyleSizeLength parseStyleSizeLength(String value, StyleSizeLength defaultValue)
	{
		if (value == null)
			return defaultValue;
		try
		{
			if (value.endsWith("px"))
				return StyleSizeLength.points(Integer.parseInt(value.substring(0, value.length() - 2)));
			else if (value.endsWith("%"))
				return StyleSizeLength.percent(Integer.parseInt(value.substring(0, value.length() - 1)));
			else if (value.equals("auto"))
				return StyleSizeLength.ofAuto();
			else if (value.equals("stretch"))
				return StyleSizeLength.ofStretch();
			else if (value.equals("fit-content"))
				return StyleSizeLength.ofFitContent();
			else if (value.equals("max-content"))
				return StyleSizeLength.ofMaxContent();
			else
				return StyleSizeLength.points(Integer.parseInt(value));
		}
		catch (NumberFormatException e)
		{
			return defaultValue;
		}
	}
	public static YogaDirection parseYogaDirection(String value, YogaDirection defaultValue)
	{
		if (value == null)
			return defaultValue;
		try
		{
			return YogaDirection.valueOf(value.toUpperCase(Locale.ROOT));
		}
		catch (IllegalArgumentException e)
		{
			return defaultValue;
		}
	}
	public static YogaFlexDirection parseYogaFlexDirection(String value, YogaFlexDirection defaultValue)
	{
		if (value == null)
			return defaultValue;
		try
		{
			return YogaFlexDirection.valueOf(value.replace("-", "_").toUpperCase(Locale.ROOT));
		}
		catch (IllegalArgumentException e)
		{
			return defaultValue;
		}
	}
	public static YogaJustify parseYogaJustify(String value, YogaJustify defaultValue)
	{
		if (value == null)
			return defaultValue;
		try
		{
			return YogaJustify.valueOf(value.replace("-", "_").toUpperCase(Locale.ROOT));
		}
		catch (IllegalArgumentException e)
		{
			return defaultValue;
		}
	}
	public static YogaAlign parseYogaAlign(String value, YogaAlign defaultValue)
	{
		if (value == null)
			return defaultValue;
		try
		{
			return YogaAlign.valueOf(value.replace("-", "_").toUpperCase(Locale.ROOT));
		}
		catch (IllegalArgumentException e)
		{
			return defaultValue;
		}
	}
	public static YogaPositionType parseYogaPositionType(String value, YogaPositionType defaultValue)
	{
		if (value == null)
			return defaultValue;
		try
		{
			return YogaPositionType.valueOf(value.toUpperCase(Locale.ROOT));
		}
		catch (IllegalArgumentException e)
		{
			return defaultValue;
		}
	}
	public static YogaWrap parseYogaWrap(String value, YogaWrap defaultValue)
	{
		if (value == null)
			return defaultValue;
		try
		{
			return YogaWrap.valueOf(value.replace("-", "_").toUpperCase(Locale.ROOT));
		}
		catch (IllegalArgumentException e)
		{
			return defaultValue;
		}
	}
	public static YogaOverflow parseYogaOverflow(String value, YogaOverflow defaultValue)
	{
		if (value == null)
			return defaultValue;
		try
		{
			return YogaOverflow.valueOf(value.toUpperCase(Locale.ROOT));
		}
		catch (IllegalArgumentException e)
		{
			return defaultValue;
		}
	}
	public static YogaDisplay parseYogaDisplay(String value, YogaDisplay defaultValue)
	{
		if (value == null)
			return defaultValue;
		try
		{
			return YogaDisplay.valueOf(value.toUpperCase(Locale.ROOT));
		}
		catch (IllegalArgumentException e)
		{
			return defaultValue;
		}
	}
	public static YogaBoxSizing parseYogaBoxSizing(String value, YogaBoxSizing defaultValue)
	{
		if (value == null)
			return defaultValue;
		try
		{
			return YogaBoxSizing.valueOf(value.replace("-", "_").toUpperCase(Locale.ROOT));
		}
		catch (IllegalArgumentException e)
		{
			return defaultValue;
		}
	}
	public static Color parseColor(String value, Color defaultValue)
	{
		if (value == null)
			return defaultValue;
		try
		{
			return Color.tryParse(value, defaultValue);
		}
		catch (Exception e)
		{
			return defaultValue;
		}
	}
}
