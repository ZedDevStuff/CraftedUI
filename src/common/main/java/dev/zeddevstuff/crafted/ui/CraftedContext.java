package dev.zeddevstuff.crafted.ui;

import dev.zeddevstuff.crafted.Crafted;
import dev.zeddevstuff.crafted.input.InputHandler;
import dev.zeddevstuff.crafted.input.events.IUIEventHandler;
import dev.zeddevstuff.crafted.input.events.UIEvent;

import java.util.HashMap;
import java.util.function.Consumer;

/**
 * Context class holding useful data and methods. <br>
 * This is usually tied to a specific screen or UI instance.
 */
public class CraftedContext
{
	public final Crafted crafted;
	public final InputHandler inputHandler = new InputHandler();
	private final HashMap<String, IUIEventHandler> callbacks = new HashMap<>();
	/**
	 * The renderer to use for future elements created in this context.
	 */
	public String currentRenderer = "default";

	public CraftedContext(Crafted crafted)
	{
		this.crafted = crafted;
	}

	public <T extends IUIEventHandler> void registerCallback(String name, T callback)
	{
		callbacks.put(name, callback);
	}
	public <T extends IUIEventHandler> void registerCallback(Class<T> type, String name, T callback)
	{
		callbacks.put(name, callback);
	}
	public boolean unregisterCallback(String name)
	{
		return callbacks.remove(name) != null;
	}
	@SuppressWarnings("unchecked")
	public <T extends IUIEventHandler> T getCallback(Class<T> type, String name)
	{
		var result = callbacks.get(name);
		if (result != null && !type.isAssignableFrom(result.getClass()))
			return null;
		return (T) result;
	}
}
