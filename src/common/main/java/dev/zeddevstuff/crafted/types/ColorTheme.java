package dev.zeddevstuff.crafted.types;

import dev.zeddevstuff.crafted.utils.ColorUtils;

import java.util.Objects;

public record ColorTheme(
    Color seed,
    boolean dark,

    Color primary,
    Color onPrimary,
    Color primaryContainer,
    Color onPrimaryContainer,

    Color secondary,
    Color onSecondary,

    Color background,
    Color onBackground,
    Color surface,
    Color onSurface,
    Color surfaceVariant,

    Color outline,
    Color disabled,
    Color scrim,

    Color error,
    Color onError,
    Color warning,
    Color onWarning,
    Color success,
    Color onSuccess,
    Color info,
    Color onInfo
) 
{
	public static final ColorTheme RED = fromSeed(Color.Tomato, true);
	public static final ColorTheme GREEN = fromSeed(Color.SpringGreen, true);
	public static final ColorTheme BLUE = fromSeed(Color.DodgerBlue, true);

	public Color getColorByName(String name)
	{
		return switch(name)
		{
			case "primary" -> primary;
			case "onPrimary" -> onPrimary;
			case "primaryContainer" -> primaryContainer;
			case "onPrimaryContainer" -> onPrimaryContainer;
			case "secondary" -> secondary;
			case "onSecondary" -> onSecondary;
			case "background" -> background;
			case "onBackground" -> onBackground;
			case "surface" -> surface;
			case "onSurface" -> onSurface;
			case "surfaceVariant" -> surfaceVariant;
			case "outline" -> outline;
			case "disabled" -> disabled;
			case "scrim" -> scrim;
			case "error" -> error;
			case "onError" -> onError;
			case "warning" -> warning;
            case "onWarning" -> onWarning;
			case "success" -> success;
            case "onSuccess" -> onSuccess;
            case "info" -> info;
            case "onInfo" -> onInfo;
			default -> null;
		};
	}

    public static ColorTheme fromSeed(Color seed, boolean dark)
    {
        Objects.requireNonNull(seed, "seed");

        var seedColor = ColorUtils.toOklch(seed);
        float hue = seedColor.hue();
        float chroma = Math.clamp(seedColor.chroma() * 1.25f, 0.04f, 0.18f);
        float secondaryHue = hue + 0.08f;

        Color primary = tone(dark ? 0.78f : 0.50f, chroma, hue);
        Color primaryContainer = tone(dark ? 0.32f : 0.90f, chroma * 0.75f, hue);
        Color secondary = tone(dark ? 0.78f : 0.48f, chroma * 0.70f, secondaryHue);

        Color background = neutral(dark ? 0.12f : 0.98f, hue, 0.012f);
        Color surface = neutral(dark ? 0.16f : 0.96f, hue, 0.016f);
        Color surfaceVariant = neutral(dark ? 0.25f : 0.90f, hue, 0.025f);
        Color onBackground = contrasting(background);
        Color onSurface = contrasting(surface);
        Color onPrimary = contrasting(primary);
        Color onPrimaryContainer = contrasting(primaryContainer);
        Color onSecondary = contrasting(secondary);

        Color outline = neutral(dark ? 0.60f : 0.50f, hue, 0.02f);
        Color disabled = ColorUtils.mix(onSurface, surface, 0.50f).withAlpha(150);
        Color scrim = Color.Black.withAlpha(140);

        Color error = new Color(255, 98, 102);//tone(dark ? 0.76f : 0.58f, 0.16f, 0.02f);
        Color warning = new Color(252, 183, 0);//tone(dark ? 0.82f : 0.68f, 0.15f, 0.14f);
        Color success = new Color(0, 211, 144);//tone(dark ? 0.76f : 0.52f, 0.13f, 0.40f);
        Color info = new Color(0, 186, 254);//tone(dark ? 0.78f : 0.58f, 0.14f, 0.58f);

        return new ColorTheme(
            seed,
            dark,
            primary,
            onPrimary,
            primaryContainer,
            onPrimaryContainer,
            secondary,
            onSecondary,
            background,
            onBackground,
            surface,
            onSurface,
            surfaceVariant,
            outline,
            disabled,
            scrim,
            error,
            contrasting(error),
            warning,
            contrasting(warning),
            success,
            contrasting(success),
            info,
            contrasting(info)
        );
    }

    private static Color tone(float lightness, float chroma, float hue)
    {
        return ColorUtils.fromOklch(new ColorUtils.Oklch(
            Math.clamp(lightness, 0.0f, 1.0f),
            Math.max(0.0f, chroma),
            hue,
            1.0f
        ));
    }

    private static Color neutral(float lightness, float hue, float chroma)
    {
        return tone(lightness, chroma, hue);
    }

    private static Color contrasting(Color background)
    {
        return ColorUtils.bestContrast(background, Color.Black, Color.White);
    }
}
