package dev.zeddevstuff.crafted;

public class Constants
{
	public static final String MOD_ID = "crafted_ui";
	public static final boolean DEV_MODE = System.getProperty("crafted.devMode", "false").equalsIgnoreCase("true");
	public static final boolean DEBUG = System.getProperty("crafted.debug", "false").equalsIgnoreCase("true");
	public static final boolean TEST = System.getProperty("crafted.test", "false").equalsIgnoreCase("true");
}
