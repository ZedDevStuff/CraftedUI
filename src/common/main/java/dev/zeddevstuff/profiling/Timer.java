package dev.zeddevstuff.profiling;

public class Timer
{
	private long startTime;
	@SuppressWarnings("FieldCanBeLocal") // Not sure how much time allocation takes, so just to be safe this is a field.
	private long endTime;
	private long elapsedTime;
	public long getElapsedTime() { return elapsedTime; }
	public long getElapsedTimeSeconds() { return elapsedTime / 1_000_000_000; }
	public long getElapsedTimeMillis() { return elapsedTime / 1_000_000; }
	public long getElapsedTimeMicros() { return elapsedTime / 1_000; }

	public void start()
	{
		startTime = System.nanoTime();
	}
	public void stop()
	{
		endTime = System.nanoTime();
		elapsedTime = endTime - startTime;
	}

	public void printElapsedTime(String name)
	{
		System.out.println("Timer " + name + " took " + getElapsedTimeMillis() + " ms (" + getElapsedTime() + " ns)");
	}
}
