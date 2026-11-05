package dev.zeddevstuff.crafted.ui;

import dev.zeddevstuff.crafted.Crafted;
import dev.zeddevstuff.crafted.input.InputHandler;
import dev.zeddevstuff.crafted.types.ColorTheme;
import dev.zeddevstuff.crafted.utils.Typed;

import javax.annotation.Nonnull;
import java.util.HashMap;

/**
 * Context class holding useful data and methods. <br>
 * This is usually tied to a specific screen or UI instance.
 */
public class CraftedContext
{
	public final Crafted crafted;
	public final InputHandler inputHandler = new InputHandler();
	private final HashMap<String, Object> callbacks = new HashMap<>();
	private final HashMap<String, Typed<?>> vars = new HashMap<>();
	public ColorTheme theme = ColorTheme.BLUE;
	/**
	 * The renderer to use for future elements created in this context.
	 */
	public String currentRenderer = "default";

	public CraftedContext()
	{
		this(Crafted.defaultInstance());
	}
	public CraftedContext(Crafted crafted)
	{
		this.crafted = crafted;
	}

	public <T> void registerCallback(Class<T> type, String name, T callback)
	{
		callbacks.put(name, type.cast(callback));
	}
	public boolean unregisterCallback(String name)
	{
		return callbacks.remove(name) != null;
	}
	public <T> T getCallback(Class<T> type, String name)
	{
		var callback = callbacks.get(name);
		return callback == null
			? null
			: type.cast(callback);
	}

	public void setVar(String name, Typed<?> value)
	{
		vars.put(name, value);
	}
	public <T> void setVar(String name, Class<T> type, T value)
	{
		vars.put(name, new Typed<>(type, value));
	}
	@SuppressWarnings("unchecked")
	public <T> void setVar(String name, @Nonnull T value)
	{
		vars.put(name, new Typed<>((Class<T>)value.getClass(), value));
	}
	@SuppressWarnings("unchecked")
	public <T> Typed<T> getVar(Class<T> type, String name)
	{
		var result = vars.get(name);
		if (result != null)
		{
			if(!type.isAssignableFrom(result.type))
				return null;
			return (Typed<T>) result;
		}
		return null;
	}
	public <T> Typed<T> getVarOrDefault(Class<T> type, String name, T defaultValue)
	{
		var result = getVar(type, name);
		if(result != null)
			return result;
		var newVar = new Typed<>(type, defaultValue);
		setVar(name, newVar);
		return newVar;
	}
}
