package dev.zeddevstuff.crafted.platform;

/**
 * An interface for dispatching tasks to the main thread.
 */
public interface IDispatcher
{
	/**
	 * Dispatches a task to be run on the main thread.
	 * @param runnable The task to be executed.
	 */
	void dispatch(Runnable runnable);
	/**
	 * Dispatches a task to be run on the main thread and waits for its completion.
	 * @param runnable The task to be executed.
	 */
	void dispatchSync(Runnable runnable);
	/**
	 * Checks if the current thread is the main thread.
	 * @return true if the current thread is the main thread, false otherwise.
	 */
	boolean isSameThread();
}
