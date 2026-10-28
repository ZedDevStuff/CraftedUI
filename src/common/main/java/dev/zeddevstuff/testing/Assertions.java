package dev.zeddevstuff.testing;

public class Assertions
{
	public static void assertTrue(boolean condition, String message)
	{
		if(!condition)
			throw new AssertionError(message);
	}
	public static void assertFalse(boolean condition, String message)
	{
		if(condition)
			throw new AssertionError(message);
	}
	public static void assertEquals(Object expected, Object actual, String message)
	{
		if(expected == null && actual == null)
			return;
		if(expected != null && expected.equals(actual))
			return;
		throw new AssertionError(message + " Expected: " + expected + ", Actual: " + actual);
	}
	public static void assertNotNull(Object object, String message)
	{
		if(object == null)
			throw new AssertionError(message);
	}
	public static void assertNull(Object object, String message)
	{
		if(object != null)
			throw new AssertionError(message);
	}
	public static void fail(String message)
	{
		throw new AssertionError(message);
	}
	public static void assertThrows(Class<? extends Throwable> expectedException, Runnable code, String message)
	{
		try
		{
			code.run();
		}
		catch(Throwable e)
		{
			if(expectedException.isInstance(e))
				return;
			else
				throw new AssertionError(message + " Expected exception: " + expectedException.getName() + ", but got: " + e.getClass().getName(), e);
		}
		throw new AssertionError(message + " Expected exception: " + expectedException.getName() + ", but no exception was thrown.");
	}
}
