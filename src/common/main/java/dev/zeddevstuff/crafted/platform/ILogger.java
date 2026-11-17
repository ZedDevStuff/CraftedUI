package dev.zeddevstuff.crafted.platform;

public interface ILogger
{
	void info(String message);
	void warn(String message);
	void error(String message);
	void debug(String message);
	void info(String message, Throwable throwable);
	void warn(String message, Throwable throwable);
	void error(String message, Throwable throwable);
	void debug(String message, Throwable throwable);

	interface IFactory
	{
		ILogger create(String name);
	}
}
