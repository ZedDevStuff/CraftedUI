package dev.zeddevstuff.testing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class TestRunner
{
	public static void runTests(Class<?> testClass)
	{
		try
		{
			Object testInstance = testClass.getDeclaredConstructor().newInstance();
			var methods = Arrays.stream(testClass.getDeclaredMethods()).filter(method -> method.isAnnotationPresent(Test.class)).toArray(java.lang.reflect.Method[]::new);
			var successfulTests = new ArrayList<String>();
			var failedTests = new ArrayList<String>();
			var timings = new HashMap<String, Long>();
			System.out.println("Running " + methods.length + " tests in class: " + testClass.getName());
			for(var method : methods)
			{
				long startTime = 0;
				long endTime = 0;
				try
				{

					method.invoke(testInstance);
					endTime = System.nanoTime();
					timings.put(method.getName(), endTime - startTime);
					successfulTests.add(method.getName());
					System.out.println("Test \"" + method.getName() + "\" passed in " + (endTime - startTime / 1_000_000.0) + " ms");
				}
				catch(Throwable e)
				{
					endTime = System.nanoTime();
					timings.put(method.getName(), endTime - startTime);
					failedTests.add(method.getName());
					System.err.println("Test \"" + method.getName() + "\" failed" + " in " + (endTime - startTime / 1_000_000.0) + " ms\": " + e.getCause() );
					e.getCause().printStackTrace();
				}

			}
			long totalTime = timings.values().stream().mapToLong(Long::longValue).sum();
			System.out.println("Total time taken for all tests: " + (totalTime / 1_000_000.0) + " ms");
			System.out.println("Test run complete in " + (totalTime / 1_000_000.0) + " ms");
			System.out.println("Successful tests: " + successfulTests.size() + "/" + methods.length);
			System.out.println("Failed tests: " + failedTests.size() + "/" + methods.length);
			if(!failedTests.isEmpty())
			{
				System.err.println("Failed tests: " + String.join(", ", failedTests));
			}
		}
		catch(Exception e)
		{
			throw new RuntimeException("Failed to run tests in class: " + testClass.getName(), e);
		}
	}
}
