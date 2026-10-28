package dev.zeddevstuff.crafted.platform;

public interface ILocalization
{
	String get(String key);
	String getFormatted(String key, Object... args);
	String currentLocale();
}
