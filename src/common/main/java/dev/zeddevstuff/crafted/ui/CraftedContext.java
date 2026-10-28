package dev.zeddevstuff.crafted.ui;

import dev.zeddevstuff.crafted.Crafted;
import dev.zeddevstuff.crafted.types.ICraftedCallback;

import java.util.HashMap;
import java.util.function.Consumer;

/**
 * Context class holding useful data and methods. <br>
 * This is usually tied to a specific screen or UI instance.
 */
public class CraftedContext
{
	public final Crafted crafted;
	private final HashMap<String, CallbackEntry<?>> callbacks = new HashMap<>();
	/**
	 * The renderer to use for future elements created in this context.
	 */
	public String currentRenderer = "default";

	public CraftedContext(Crafted crafted)
	{
		this.crafted = crafted;
	}

	public <T extends ICraftedCallback> void registerCallback(Class<T> type, String name, Consumer<T> callback)
	{
		callbacks.put(name, new CallbackEntry<>(type, callback));
	}
	public boolean unregisterCallback(String name)
	{
		return callbacks.remove(name) != null;
	}
	@SuppressWarnings("unchecked")
	public <T extends ICraftedCallback> Consumer<T> getCallback(Class<T> type, String name)
	{
		var result = callbacks.get(name);
		if (result.type() != type)
			return null;
		return (Consumer<T>) result.callback();
	}

	record CallbackEntry<T extends ICraftedCallback>(Class<?> type, Consumer<T> callback) {}
}
