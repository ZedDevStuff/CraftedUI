package dev.zeddevstuff.crafted.platform;

import dev.zeddevstuff.crafted.Crafted;

public interface IText
{
	String get();
	String format(Object... args);

	String getRawText();


	public static IText literal(String text) { return new Literal(text); }
	public static IText localized(String key) { return new Localized(key); }

	class Literal implements IText
	{
		private final String text;

		public Literal(String text) { this.text = text; }

		@Override
		public String get() { return text; }

		@Override
		public String format(Object... args)
		{
			try
			{
				return String.format(text, args);
			}
			catch (Exception e)
			{
				return text;
			}
		}

		@Override
		public String getRawText() { return text; }

		@Override
		public String toString() { return "Literal(\"" + text + "\")"; }
	}
	class Localized implements IText
	{
		private final String key;

		public Localized(String key) { this.key = key; }

		@Override
		public String get() { return Crafted.defaultInstance().localization.get(key); }

		@Override
		public String format(Object... args) { return Crafted.defaultInstance().localization.getFormatted(key, args); }

		@Override
		public String getRawText() { return key; }

		@Override
		public String toString() { return "Localized(\"" + key + "\")"; }
	}
}
