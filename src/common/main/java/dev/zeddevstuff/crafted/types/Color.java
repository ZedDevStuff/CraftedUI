package dev.zeddevstuff.crafted.types;

import dev.zeddevstuff.crafted.utils.MathUtils;

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
	
	public Color(float r, float g, float b)
	{
		this((int)(MathUtils.clamp(r, 0.0f, 1.0f) * 255.0f), (int)(MathUtils.clamp(g, 0.0f, 1.0f) * 255.0f), (int)(MathUtils.clamp(b, 0.0f, 1.0f) * 255.0f), 1f);
	}
	public Color(float r, float g, float b, float a)
	{
		this((int)(MathUtils.clamp(r, 0.0f, 1.0f) * 255.0f), (int)(MathUtils.clamp(g, 0.0f, 1.0f) * 255.0f), (int)(MathUtils.clamp(b, 0.0f, 1.0f) * 255.0f), (int)(MathUtils.clamp(a, 0.0f, 1.0f) * 255.0f));
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
				switch (value.toLowerCase())
				{
					case "aliceblue": return AliceBlue;
					case "antiquewhite": return AntiqueWhite;
					case "aqua": return Aqua;
					case "aquamarine": return Aquamarine;
					case "azure": return Azure;
					case "beige": return Beige;
					case "bisque": return Bisque;
					case "black": return Black;
					case "blanchedalmond": return BlanchedAlmond;
					case "blue": return Blue;
					case "blueviolet": return BlueViolet;
					case "brown": return Brown;
					case "burlywood": return BurlyWood;
					case "cadetblue": return CadetBlue;
					case "chartreuse": return Chartreuse;
					case "chocolate": return Chocolate;
					case "coral": return Coral;
					case "cornflowerblue": return CornflowerBlue;
					case "cornsilk": return Cornsilk;
					case "crimson": return Crimson;
					case "cyan": return Cyan;
					case "darkblue": return DarkBlue;
					case "darkcyan": return DarkCyan;
					case "darkgoldenrod": return DarkGoldenrod;
					case "darkgray": return DarkGray;
					case "darkgreen": return DarkGreen;
					case "darkkhaki": return DarkKhaki;
					case "darkmagenta": return DarkMagenta;
					case "darkolivegreen": return DarkOliveGreen;
					case "darkorange": return DarkOrange;
					case "darkorchid": return DarkOrchid;
					case "darkred": return DarkRed;
					case "darksalmon": return DarkSalmon;
					case "darkseagreen": return DarkSeaGreen;
					case "darkslateblue": return DarkSlateBlue;
					case "darkslategray": return DarkSlateGray;
					case "darkturquoise": return DarkTurquoise;
					case "darkviolet": return DarkViolet;
					case "deeppink": return DeepPink;
					case "deepskyblue": return DeepSkyBlue;
					case "dimgray": return DimGray;
					case "dodgerblue": return DodgerBlue;
					case "firebrick": return Firebrick;
					case "floralwhite": return FloralWhite;
					case "forestgreen": return ForestGreen;
					case "fuchsia": return Fuchsia;
					case "gainsboro": return Gainsboro;
					case "ghostwhite": return GhostWhite;
					case "gold": return Gold;
					case "goldenrod": return Goldenrod;
					case "gray": return Gray;
					case "green": return Green;
					case "greenyellow": return GreenYellow;
					case "honeydew": return Honeydew;
					case "hotpink": return HotPink;
					case "indianred": return IndianRed;
					case "indigo": return Indigo;
					case "ivory": return Ivory;
					case "khaki": return Khaki;
					case "lavender": return Lavender;
					case "lavenderblush": return LavenderBlush;
					case "lawngreen": return LawnGreen;
					case "lemonchiffon": return LemonChiffon;
					case "lightblue": return LightBlue;
					case "lightcoral": return LightCoral;
					case "lightcyan": return LightCyan;
					case "lightgoldenrodyellow": return LightGoldenrodYellow;
					case "lightgreen": return LightGreen;
					case "lightgray": return LightGray;
					case "lightpink": return LightPink;
					case "lightsalmon": return LightSalmon;
					case "lightseagreen": return LightSeaGreen;
					case "lightskyblue": return LightSkyBlue;
					case "lightslategray": return LightSlateGray;
					case "lightsteelblue": return LightSteelBlue;
					case "lightyellow": return LightYellow;
					case "lime": return Lime;
					case "limegreen": return LimeGreen;
					case "linen": return Linen;
					case "magenta": return Magenta;
					case "maroon": return Maroon;
					case "mediumaquamarine": return MediumAquamarine;
					case "mediumblue": return MediumBlue;
					case "mediumorchid": return MediumOrchid;
					case "mediumpurple": return MediumPurple;
					case "mediumseagreen": return MediumSeaGreen;
					case "mediumslateblue": return MediumSlateBlue;
					case "mediumspringgreen": return MediumSpringGreen;
					case "mediumturquoise": return MediumTurquoise;
					case "mediumvioletred": return MediumVioletRed;
					case "midnightblue": return MidnightBlue;
					case "mintcream": return MintCream;
					case "mistyrose": return MistyRose;
					case "moccasin": return Moccasin;
					case "navajowhite": return NavajoWhite;
					case "navy": return Navy;
					case "oldlace": return OldLace;
					case "olive": return Olive;
					case "olivedrab": return OliveDrab;
					case "orange": return Orange;
					case "orangered": return OrangeRed;
					case "orchid": return Orchid;
					case "palegoldenrod": return PaleGoldenrod;
					case "palegreen": return PaleGreen;
					case "paleturquoise": return PaleTurquoise;
					case "palevioletred": return PaleVioletRed;
					case "papayawhip": return PapayaWhip;
					case "peachpuff": return PeachPuff;
					case "peru": return Peru;
					case "pink": return Pink;
					case "plum": return Plum;
					case "powderblue": return PowderBlue;
					case "purple": return Purple;
					case "red": return Red;
					case "rosybrown": return RosyBrown;
					case "royalblue": return RoyalBlue;
					case "saddlebrown": return SaddleBrown;
					case "salmon": return Salmon;
					case "sandybrown": return SandyBrown;
					case "seagreen": return SeaGreen;
					case "seashell": return SeaShell;
					case "sienna": return Sienna;
					case "silver": return Silver;
					case "skyblue": return SkyBlue;
					case "slateblue": return SlateBlue;
					case "slategray": return SlateGray;
					case "snow": return Snow;
					case "springgreen": return SpringGreen;
					case "steelblue": return SteelBlue;
					case "tan": return Tan;
					case "teal": return Teal;
					case "thistle": return Thistle;
					case "tomato": return Tomato;
					case "transparent": return Transparent;
					case "turquoise": return Turquoise;
					case "violet": return Violet;
					case "wheat": return Wheat;
					case "white": return White;
					case "whitesmoke": return WhiteSmoke;
					case "yellow": return Yellow;
					case "yellowgreen": return YellowGreen;
				}
			}
		}
		catch (NumberFormatException e)
		{
			return defaultValue;
		}
		return defaultValue;
	}

	// When I copied those from Clay, my WIP Voxel game framework, I thought there would be an easy way to make them use the Java naming.
	// I was wrong, and I'm not gonna waste money making AI just rename them so I guess they're PascalCase forever now
	public static Color AliceBlue = new Color(0.941176534f, 0.972549081f, 1.000000000f, 1.000000000f);
	public static Color AntiqueWhite = new Color(0.980392218f, 0.921568692f, 0.843137324f, 1.000000000f);
	public static Color Aqua = new Color(0.000000000f, 1.000000000f, 1.000000000f, 1.000000000f);
	public static Color Aquamarine = new Color(0.498039246f, 1.000000000f, 0.831372619f, 1.000000000f);
	public static Color Azure = new Color(0.941176534f, 1.000000000f, 1.000000000f, 1.000000000f);
	public static Color Beige = new Color(0.960784376f, 0.960784376f, 0.862745166f, 1.000000000f);
	public static Color Bisque = new Color(1.000000000f, 0.894117713f, 0.768627524f, 1.000000000f);
	public static Color Black = new Color(0.000000000f, 0.000000000f, 0.000000000f, 1.000000000f);
	public static Color BlanchedAlmond = new Color(1.000000000f, 0.921568692f, 0.803921640f, 1.000000000f);
	public static Color Blue = new Color(0.000000000f, 0.000000000f, 1.000000000f, 1.000000000f);
	public static Color BlueViolet = new Color(0.541176498f, 0.168627456f, 0.886274576f, 1.000000000f);
	public static Color Brown = new Color(0.647058845f, 0.164705887f, 0.164705887f, 1.000000000f);
	public static Color BurlyWood = new Color(0.870588303f, 0.721568644f, 0.529411793f, 1.000000000f);
	public static Color CadetBlue = new Color(0.372549027f, 0.619607866f, 0.627451003f, 1.000000000f);
	public static Color Chartreuse = new Color(0.498039246f, 1.000000000f, 0.000000000f, 1.000000000f);
	public static Color Chocolate = new Color(0.823529482f, 0.411764741f, 0.117647067f, 1.000000000f);
	public static Color Coral = new Color(1.000000000f, 0.498039246f, 0.313725501f, 1.000000000f);
	public static Color CornflowerBlue = new Color(0.392156899f, 0.584313750f, 0.929411829f, 1.000000000f);
	public static Color Cornsilk = new Color(1.000000000f, 0.972549081f, 0.862745166f, 1.000000000f);
	public static Color Crimson = new Color(0.862745166f, 0.078431375f, 0.235294133f, 1.000000000f);
	public static Color Cyan = new Color(0.000000000f, 1.000000000f, 1.000000000f, 1.000000000f);
	public static Color DarkBlue = new Color(0.000000000f, 0.000000000f, 0.545098066f, 1.000000000f);
	public static Color DarkCyan = new Color(0.000000000f, 0.545098066f, 0.545098066f, 1.000000000f);
	public static Color DarkGoldenrod = new Color(0.721568644f, 0.525490224f, 0.043137256f, 1.000000000f);
	public static Color DarkGray = new Color(0.662745118f, 0.662745118f, 0.662745118f, 1.000000000f);
	public static Color DarkGreen = new Color(0.000000000f, 0.392156899f, 0.000000000f, 1.000000000f);
	public static Color DarkKhaki = new Color(0.741176486f, 0.717647076f, 0.419607878f, 1.000000000f);
	public static Color DarkMagenta = new Color(0.545098066f, 0.000000000f, 0.545098066f, 1.000000000f);
	public static Color DarkOliveGreen = new Color(0.333333343f, 0.419607878f, 0.184313729f, 1.000000000f);
	public static Color DarkOrange = new Color(1.000000000f, 0.549019635f, 0.000000000f, 1.000000000f);
	public static Color DarkOrchid = new Color(0.600000024f, 0.196078449f, 0.800000072f, 1.000000000f);
	public static Color DarkRed = new Color(0.545098066f, 0.000000000f, 0.000000000f, 1.000000000f);
	public static Color DarkSalmon = new Color(0.913725555f, 0.588235319f, 0.478431404f, 1.000000000f);
	public static Color DarkSeaGreen = new Color(0.560784340f, 0.737254918f, 0.545098066f, 1.000000000f);
	public static Color DarkSlateBlue = new Color(0.282352954f, 0.239215702f, 0.545098066f, 1.000000000f);
	public static Color DarkSlateGray = new Color(0.184313729f, 0.309803933f, 0.309803933f, 1.000000000f);
	public static Color DarkTurquoise = new Color(0.000000000f, 0.807843208f, 0.819607913f, 1.000000000f);
	public static Color DarkViolet = new Color(0.580392182f, 0.000000000f, 0.827451050f, 1.000000000f);
	public static Color DeepPink = new Color(1.000000000f, 0.078431375f, 0.576470613f, 1.000000000f);
	public static Color DeepSkyBlue = new Color(0.000000000f, 0.749019623f, 1.000000000f, 1.000000000f);
	public static Color DimGray = new Color(0.411764741f, 0.411764741f, 0.411764741f, 1.000000000f);
	public static Color DodgerBlue = new Color(0.117647067f, 0.564705908f, 1.000000000f, 1.000000000f);
	public static Color Firebrick = new Color(0.698039234f, 0.133333340f, 0.133333340f, 1.000000000f);
	public static Color FloralWhite = new Color(1.000000000f, 0.980392218f, 0.941176534f, 1.000000000f);
	public static Color ForestGreen = new Color(0.133333340f, 0.545098066f, 0.133333340f, 1.000000000f);
	public static Color Fuchsia = new Color(1.000000000f, 0.000000000f, 1.000000000f, 1.000000000f);
	public static Color Gainsboro = new Color(0.862745166f, 0.862745166f, 0.862745166f, 1.000000000f);
	public static Color GhostWhite = new Color(0.972549081f, 0.972549081f, 1.000000000f, 1.000000000f);
	public static Color Gold = new Color(1.000000000f, 0.843137324f, 0.000000000f, 1.000000000f);
	public static Color Goldenrod = new Color(0.854902029f, 0.647058845f, 0.125490203f, 1.000000000f);
	public static Color Gray = new Color(0.501960814f, 0.501960814f, 0.501960814f, 1.000000000f);
	public static Color Green = new Color(0.000000000f, 0.501960814f, 0.000000000f, 1.000000000f);
	public static Color GreenYellow = new Color(0.678431392f, 1.000000000f, 0.184313729f, 1.000000000f);
	public static Color Honeydew = new Color(0.941176534f, 1.000000000f, 0.941176534f, 1.000000000f);
	public static Color HotPink = new Color(1.000000000f, 0.411764741f, 0.705882370f, 1.000000000f);
	public static Color IndianRed = new Color(0.803921640f, 0.360784322f, 0.360784322f, 1.000000000f);
	public static Color Indigo = new Color(0.294117659f, 0.000000000f, 0.509803951f, 1.000000000f);
	public static Color Ivory = new Color(1.000000000f, 1.000000000f, 0.941176534f, 1.000000000f);
	public static Color Khaki = new Color(0.941176534f, 0.901960850f, 0.549019635f, 1.000000000f);
	public static Color Lavender = new Color(0.901960850f, 0.901960850f, 0.980392218f, 1.000000000f);
	public static Color LavenderBlush = new Color(1.000000000f, 0.941176534f, 0.960784376f, 1.000000000f);
	public static Color LawnGreen = new Color(0.486274540f, 0.988235354f, 0.000000000f, 1.000000000f);
	public static Color LemonChiffon = new Color(1.000000000f, 0.980392218f, 0.803921640f, 1.000000000f);
	public static Color LightBlue = new Color(0.678431392f, 0.847058892f, 0.901960850f, 1.000000000f);
	public static Color LightCoral = new Color(0.941176534f, 0.501960814f, 0.501960814f, 1.000000000f);
	public static Color LightCyan = new Color(0.878431439f, 1.000000000f, 1.000000000f, 1.000000000f);
	public static Color LightGoldenrodYellow = new Color(0.980392218f, 0.980392218f, 0.823529482f, 1.000000000f);
	public static Color LightGreen = new Color(0.564705908f, 0.933333397f, 0.564705908f, 1.000000000f);
	public static Color LightGray = new Color(0.827451050f, 0.827451050f, 0.827451050f, 1.000000000f);
	public static Color LightPink = new Color(1.000000000f, 0.713725507f, 0.756862819f, 1.000000000f);
	public static Color LightSalmon = new Color(1.000000000f, 0.627451003f, 0.478431404f, 1.000000000f);
	public static Color LightSeaGreen = new Color(0.125490203f, 0.698039234f, 0.666666687f, 1.000000000f);
	public static Color LightSkyBlue = new Color(0.529411793f, 0.807843208f, 0.980392218f, 1.000000000f);
	public static Color LightSlateGray = new Color(0.466666698f, 0.533333361f, 0.600000024f, 1.000000000f);
	public static Color LightSteelBlue = new Color(0.690196097f, 0.768627524f, 0.870588303f, 1.000000000f);
	public static Color LightYellow = new Color(1.000000000f, 1.000000000f, 0.878431439f, 1.000000000f);
	public static Color Lime = new Color(0.000000000f, 1.000000000f, 0.000000000f, 1.000000000f);
	public static Color LimeGreen = new Color(0.196078449f, 0.803921640f, 0.196078449f, 1.000000000f);
	public static Color Linen = new Color(0.980392218f, 0.941176534f, 0.901960850f, 1.000000000f);
	public static Color Magenta = new Color(1.000000000f, 0.000000000f, 1.000000000f, 1.000000000f);
	public static Color Maroon = new Color(0.501960814f, 0.000000000f, 0.000000000f, 1.000000000f);
	public static Color MediumAquamarine = new Color(0.400000036f, 0.803921640f, 0.666666687f, 1.000000000f);
	public static Color MediumBlue = new Color(0.000000000f, 0.000000000f, 0.803921640f, 1.000000000f);
	public static Color MediumOrchid = new Color(0.729411781f, 0.333333343f, 0.827451050f, 1.000000000f);
	public static Color MediumPurple = new Color(0.576470613f, 0.439215720f, 0.858823597f, 1.000000000f);
	public static Color MediumSeaGreen = new Color(0.235294133f, 0.701960802f, 0.443137288f, 1.000000000f);
	public static Color MediumSlateBlue = new Color(0.482352972f, 0.407843173f, 0.933333397f, 1.000000000f);
	public static Color MediumSpringGreen = new Color(0.000000000f, 0.980392218f, 0.603921592f, 1.000000000f);
	public static Color MediumTurquoise = new Color(0.282352954f, 0.819607913f, 0.800000072f, 1.000000000f);
	public static Color MediumVioletRed = new Color(0.780392230f, 0.082352944f, 0.521568656f, 1.000000000f);
	public static Color MidnightBlue = new Color(0.098039225f, 0.098039225f, 0.439215720f, 1.000000000f);
	public static Color MintCream = new Color(0.960784376f, 1.000000000f, 0.980392218f, 1.000000000f);
	public static Color MistyRose = new Color(1.000000000f, 0.894117713f, 0.882353008f, 1.000000000f);
	public static Color Moccasin = new Color(1.000000000f, 0.894117713f, 0.709803939f, 1.000000000f);
	public static Color NavajoWhite = new Color(1.000000000f, 0.870588303f, 0.678431392f, 1.000000000f);
	public static Color Navy = new Color(0.000000000f, 0.000000000f, 0.501960814f, 1.000000000f);
	public static Color OldLace = new Color(0.992156923f, 0.960784376f, 0.901960850f, 1.000000000f);
	public static Color Olive = new Color(0.501960814f, 0.501960814f, 0.000000000f, 1.000000000f);
	public static Color OliveDrab = new Color(0.419607878f, 0.556862772f, 0.137254909f, 1.000000000f);
	public static Color Orange = new Color(1.000000000f, 0.647058845f, 0.000000000f, 1.000000000f);
	public static Color OrangeRed = new Color(1.000000000f, 0.270588249f, 0.000000000f, 1.000000000f);
	public static Color Orchid = new Color(0.854902029f, 0.439215720f, 0.839215755f, 1.000000000f);
	public static Color PaleGoldenrod = new Color(0.933333397f, 0.909803987f, 0.666666687f, 1.000000000f);
	public static Color PaleGreen = new Color(0.596078455f, 0.984313786f, 0.596078455f, 1.000000000f);
	public static Color PaleTurquoise = new Color(0.686274529f, 0.933333397f, 0.933333397f, 1.000000000f);
	public static Color PaleVioletRed = new Color(0.858823597f, 0.439215720f, 0.576470613f, 1.000000000f);
	public static Color PapayaWhip = new Color(1.000000000f, 0.937254965f, 0.835294187f, 1.000000000f);
	public static Color PeachPuff = new Color(1.000000000f, 0.854902029f, 0.725490212f, 1.000000000f);
	public static Color Peru = new Color(0.803921640f, 0.521568656f, 0.247058839f, 1.000000000f);
	public static Color Pink = new Color(1.000000000f, 0.752941251f, 0.796078503f, 1.000000000f);
	public static Color Plum = new Color(0.866666734f, 0.627451003f, 0.866666734f, 1.000000000f);
	public static Color PowderBlue = new Color(0.690196097f, 0.878431439f, 0.901960850f, 1.000000000f);
	public static Color Purple = new Color(0.501960814f, 0.000000000f, 0.501960814f, 1.000000000f);
	public static Color Red = new Color(1.000000000f, 0.000000000f, 0.000000000f, 1.000000000f);
	public static Color RosyBrown = new Color(0.737254918f, 0.560784340f, 0.560784340f, 1.000000000f);
	public static Color RoyalBlue = new Color(0.254901975f, 0.411764741f, 0.882353008f, 1.000000000f);
	public static Color SaddleBrown = new Color(0.545098066f, 0.270588249f, 0.074509807f, 1.000000000f);
	public static Color Salmon = new Color(0.980392218f, 0.501960814f, 0.447058856f, 1.000000000f);
	public static Color SandyBrown = new Color(0.956862807f, 0.643137276f, 0.376470625f, 1.000000000f);
	public static Color SeaGreen = new Color(0.180392161f, 0.545098066f, 0.341176480f, 1.000000000f);
	public static Color SeaShell = new Color(1.000000000f, 0.960784376f, 0.933333397f, 1.000000000f);
	public static Color Sienna = new Color(0.627451003f, 0.321568638f, 0.176470593f, 1.000000000f);
	public static Color Silver = new Color(0.752941251f, 0.752941251f, 0.752941251f, 1.000000000f);
	public static Color SkyBlue = new Color(0.529411793f, 0.807843208f, 0.921568692f, 1.000000000f);
	public static Color SlateBlue = new Color(0.415686309f, 0.352941185f, 0.803921640f, 1.000000000f);
	public static Color SlateGray = new Color(0.439215720f, 0.501960814f, 0.564705908f, 1.000000000f);
	public static Color Snow = new Color(1.000000000f, 0.980392218f, 0.980392218f, 1.000000000f);
	public static Color SpringGreen = new Color(0.000000000f, 1.000000000f, 0.498039246f, 1.000000000f);
	public static Color SteelBlue = new Color(0.274509817f, 0.509803951f, 0.705882370f, 1.000000000f);
	public static Color Tan = new Color(0.823529482f, 0.705882370f, 0.549019635f, 1.000000000f);
	public static Color Teal = new Color(0.000000000f, 0.501960814f, 0.501960814f, 1.000000000f);
	public static Color Thistle = new Color(0.847058892f, 0.749019623f, 0.847058892f, 1.000000000f);
	public static Color Tomato = new Color(1.000000000f, 0.388235331f, 0.278431386f, 1.000000000f);
	public static Color Transparent = new Color(0.000000000f, 0.000000000f, 0.000000000f, 0.000000000f);
	public static Color Turquoise = new Color(0.250980407f, 0.878431439f, 0.815686345f, 1.000000000f);
	public static Color Violet = new Color(0.933333397f, 0.509803951f, 0.933333397f, 1.000000000f);
	public static Color Wheat = new Color(0.960784376f, 0.870588303f, 0.701960802f, 1.000000000f);
	public static Color White = new Color(1.000000000f, 1.000000000f, 1.000000000f, 1.000000000f);
	public static Color WhiteSmoke = new Color(0.960784376f, 0.960784376f, 0.960784376f, 1.000000000f);
	public static Color Yellow = new Color(1.000000000f, 1.000000000f, 0.000000000f, 1.000000000f);
	public static Color YellowGreen = new Color(0.603921592f, 0.803921640f, 0.196078449f, 1.000000000f);
}
