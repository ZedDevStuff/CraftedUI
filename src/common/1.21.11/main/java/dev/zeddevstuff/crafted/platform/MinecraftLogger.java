package dev.zeddevstuff.crafted.platform;

public class MinecraftLogger implements ILogger
{
	private final org.slf4j.Logger logger;

	public MinecraftLogger(String name)
	{
		this.logger = org.slf4j.LoggerFactory.getLogger(name);
	}

	@Override
	public void info(String message)
	{
		logger.info(message);
	}

	@Override
	public void warn(String message)
	{
		logger.warn(message);
	}

	@Override
	public void error(String message)
	{
		logger.error(message);
	}

	@Override
	public void debug(String message)
	{
		logger.debug(message);
	}

	@Override
	public void info(String message, Throwable throwable)
	{
		logger.info(message, throwable);
	}

	@Override
	public void warn(String message, Throwable throwable)
	{
		logger.warn(message, throwable);
	}

	@Override
	public void error(String message, Throwable throwable)
	{
		logger.error(message, throwable);
	}

	@Override
	public void debug(String message, Throwable throwable)
	{
		logger.debug(message, throwable);
	}
}
