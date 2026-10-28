package dev.zeddevstuff.crafted.ui;

import dev.zeddevstuff.crafted.Crafted;
import dev.zeddevstuff.crafted.rendering.IRenders;
import dev.zeddevstuff.crafted.rendering.RendererRegistry;
import dev.zeddevstuff.crafted.types.CraftedEventData;

import java.util.HashMap;
import java.util.function.Consumer;

/**
 * Context class holding useful data and methods. <br>
 * This is usually tied to a specific screen or UI instance.
 */
public class CraftedContext
{
	public final Crafted crafted;
	private final HashMap<String, Consumer<CraftedEventData>> callbacks = new HashMap<>();
	/**
	 * The renderer to use for future elements created in this context.
	 */
	public String currentRenderer = "default";

	public CraftedContext(Crafted crafted)
	{
		this.crafted = crafted;
	}

	public void registerCallback(String name, Consumer<CraftedEventData> callback)
	{
		callbacks.put(name, callback);
	}
	public Consumer<CraftedEventData> getCallback(String name)
	{
		var result = callbacks.get(name);
		if (result instanceof Consumer<CraftedEventData> consumer)
			return null;
		return result;
	}
}
