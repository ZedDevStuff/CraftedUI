package dev.zeddevstuff.crafted.utils;

import dev.zeddevstuff.crafted.types.Color;

public final class ColorUtils
{
	private static final float TWO_PI = (float)(Math.PI * 2.0);

	private ColorUtils() {}

	public record Hsl(float hue, float saturation, float lightness, float alpha) {}
	public record Oklch(float lightness, float chroma, float hue, float alpha) {}

	public static Hsl toHsl(Color color)
	{
		float red = color.r() / 255.0f;
		float green = color.g() / 255.0f;
		float blue = color.b() / 255.0f;
		float max = Math.max(red, Math.max(green, blue));
		float min = Math.min(red, Math.min(green, blue));
		float delta = max - min;
		float lightness = (max + min) / 2.0f;
		float saturation = 0.0f;
		float hue = 0.0f;

		if (delta != 0.0f)
		{
			saturation = delta / (1.0f - Math.abs(2.0f * lightness - 1.0f));
			if (max == red)
				hue = ((green - blue) / delta) % 6.0f;
			else if (max == green)
				hue = (blue - red) / delta + 2.0f;
			else
				hue = (red - green) / delta + 4.0f;
			hue /= 6.0f;
		}

		return new Hsl(normalizeHue(hue), saturation, lightness, color.a() / 255.0f);
	}

	public static Color fromHsl(Hsl hsl)
	{
		float hue = normalizeHue(hsl.hue());
		float saturation = Math.clamp(hsl.saturation(), 0.0f, 1.0f);
		float lightness = Math.clamp(hsl.lightness(), 0.0f, 1.0f);
		float chroma = (1.0f - Math.abs(2.0f * lightness - 1.0f)) * saturation;
		float x = chroma * (1.0f - Math.abs((hue * 6.0f) % 2.0f - 1.0f));
		float match = lightness - chroma / 2.0f;
		float red;
		float green;
		float blue;

		if (hue < 1.0f / 6.0f) { red = chroma; green = x; blue = 0.0f; }
		else if (hue < 2.0f / 6.0f) { red = x; green = chroma; blue = 0.0f; }
		else if (hue < 3.0f / 6.0f) { red = 0.0f; green = chroma; blue = x; }
		else if (hue < 4.0f / 6.0f) { red = 0.0f; green = x; blue = chroma; }
		else if (hue < 5.0f / 6.0f) { red = x; green = 0.0f; blue = chroma; }
		else { red = chroma; green = 0.0f; blue = x; }

		return new Color(red + match, green + match, blue + match, Math.clamp(hsl.alpha(), 0.0f, 1.0f));
	}

	public static Oklch toOklch(Color color)
	{
		float red = toLinear(color.r() / 255.0f);
		float green = toLinear(color.g() / 255.0f);
		float blue = toLinear(color.b() / 255.0f);

		float l = cubeRoot(0.4122214708f * red + 0.5363325363f * green + 0.0514459929f * blue);
		float m = cubeRoot(0.2119034982f * red + 0.6806995451f * green + 0.1073969566f * blue);
		float s = cubeRoot(0.0883024619f * red + 0.2817188376f * green + 0.6299787005f * blue);
		float lightness = 0.2104542553f * l + 0.7936177850f * m - 0.0040720468f * s;
		float a = 1.9779984951f * l - 2.4285922050f * m + 0.4505937099f * s;
		float b = 0.0259040371f * l + 0.7827717662f * m - 0.8086757660f * s;

		return new Oklch(lightness, (float)Math.hypot(a, b), normalizeHue((float)Math.atan2(b, a) / TWO_PI), color.a() / 255.0f);
	}

	public static Color fromOklch(Oklch oklch)
	{
		float hue = normalizeHue(oklch.hue()) * TWO_PI;
		float a = oklch.chroma() * (float)Math.cos(hue);
		float b = oklch.chroma() * (float)Math.sin(hue);
		float lightness = oklch.lightness();
		float l = lightness + 0.3963377774f * a + 0.2158037573f * b;
		float m = lightness - 0.1055613458f * a - 0.0638541728f * b;
		float s = lightness - 0.0894841775f * a - 1.2914855480f * b;
		l *= l * l;
		m *= m * m;
		s *= s * s;

		float red = 4.0767416621f * l - 3.3077115913f * m + 0.2309699292f * s;
		float green = -1.2684380046f * l + 2.6097574011f * m - 0.3413193965f * s;
		float blue = -0.0041960863f * l - 0.7034186147f * m + 1.7076147010f * s;
		return new Color(toSrgb(red), toSrgb(green), toSrgb(blue), Math.clamp(oklch.alpha(), 0.0f, 1.0f));
	}

	public static Color withLightness(Color color, float lightness)
	{
		var value = toOklch(color);
		return fromOklch(new Oklch(lightness, value.chroma(), value.hue(), value.alpha()));
	}

	public static Color adjustLightness(Color color, float amount)
	{
		var value = toOklch(color);
		return withLightness(color, value.lightness() + amount);
	}

	public static Color withChroma(Color color, float chroma)
	{
		var value = toOklch(color);
		return fromOklch(new Oklch(value.lightness(), Math.max(0.0f, chroma), value.hue(), value.alpha()));
	}

	public static Color rotateHue(Color color, float turns)
	{
		var value = toOklch(color);
		return fromOklch(new Oklch(value.lightness(), value.chroma(), value.hue() + turns, value.alpha()));
	}

	public static Color mix(Color first, Color second, float amount)
	{
		float t = Math.clamp(amount, 0.0f, 1.0f);
		return new Color(
			lerp(first.r(), second.r(), t),
			lerp(first.g(), second.g(), t),
			lerp(first.b(), second.b(), t),
			lerp(first.a(), second.a(), t));
	}

	public static Color composite(Color foreground, Color background)
	{
		float foregroundAlpha = foreground.a() / 255.0f;
		float backgroundAlpha = background.a() / 255.0f;
		float alpha = foregroundAlpha + backgroundAlpha * (1.0f - foregroundAlpha);
		if (alpha == 0.0f)
			return Color.Transparent;
		return new Color(
			(foreground.r() * foregroundAlpha + background.r() * backgroundAlpha * (1.0f - foregroundAlpha)) / alpha,
			(foreground.g() * foregroundAlpha + background.g() * backgroundAlpha * (1.0f - foregroundAlpha)) / alpha,
			(foreground.b() * foregroundAlpha + background.b() * backgroundAlpha * (1.0f - foregroundAlpha)) / alpha,
			alpha);
	}

	public static float relativeLuminance(Color color)
	{
		return 0.2126f * toLinear(color.r() / 255.0f)
			+ 0.7152f * toLinear(color.g() / 255.0f)
			+ 0.0722f * toLinear(color.b() / 255.0f);
	}

	public static float contrastRatio(Color first, Color second)
	{
		float firstLuminance = relativeLuminance(first);
		float secondLuminance = relativeLuminance(second);
		float lighter = Math.max(firstLuminance, secondLuminance);
		float darker = Math.min(firstLuminance, secondLuminance);
		return (lighter + 0.05f) / (darker + 0.05f);
	}

	public static Color bestContrast(Color background, Color first, Color second)
	{
		return contrastRatio(background, first) >= contrastRatio(background, second) ? first : second;
	}

	private static float normalizeHue(float hue)
	{
		hue %= 1.0f;
		return hue < 0.0f ? hue + 1.0f : hue;
	}

	private static float toLinear(float value)
	{
		return value <= 0.04045f ? value / 12.92f : (float)Math.pow((value + 0.055f) / 1.055f, 2.4f);
	}

	private static float toSrgb(float value)
	{
		value = Math.max(0.0f, value);
		return value <= 0.0031308f ? value * 12.92f : 1.055f * (float)Math.pow(value, 1.0 / 2.4) - 0.055f;
	}

	private static float cubeRoot(float value)
	{
		return (float)Math.cbrt(value);
	}

	private static int lerp(int first, int second, float amount)
	{
		return Math.round(first + (second - first) * amount);
	}
}