package dev.zeddevstuff.crafted;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Objects;
import java.util.function.BiFunction;

/**
 * Implementation of {@link IProp} that stores a value and notifies listeners when it changes.
 * @param <T>
 */
public class Prop<T> implements IProp<T>
{
	private final Class<T> type;
	public final Class<T> getType() { return type; }
	private final String name;
	public final String getName() { return name; }
	private T value;
	public final BiFunction<String, T, T> parser;
	public T get() { return value; }
	public void set(T newValue)
	{
		if(newValue != null && !type.isAssignableFrom(newValue.getClass()))
			throw new IllegalArgumentException("Invalid value type for prop " + name + ": " + newValue);
		if(Objects.equals(value, newValue))
			return;
		T oldValue = this.value;
		this.value = newValue;
		for(var listener : listeners)
			listener.onPropChanged(this, oldValue, newValue);
	}

	public void setFromString(String str)
	{
		set(parser.apply(str, value));
	}

	private final ArrayList<PropChangeListener<T>> listeners = new ArrayList<>();

	public Prop(Class<T> type, String name, T value)
	{
		this.type = type;
		this.name = name;
		this.value = value;
		parser = (str, defaultVal) -> defaultVal;
	}
	public Prop(Class<T> type, String name, T value, BiFunction<String, T, T> parser)
	{
		this.type = type;
		this.name = name;
		this.value = value;
		this.parser = parser;
	}

	@SuppressWarnings("unchecked")
	public Prop(String name, @Nonnull T value)
	{
		this((Class<T>)value.getClass(), name, value);
	}
	@SuppressWarnings("unchecked")
	public Prop(String name, @Nonnull T value, BiFunction<String, T, T> parser)
	{
		this((Class<T>)value.getClass(), name, value, parser);
	}

	public void addListener(PropChangeListener<T> listener)
	{
		if(!listeners.contains(listener))
			listeners.add(listener);
	}
	public void removeListener(PropChangeListener<T> listener) { listeners.remove(listener); }

	@Override
	public String toString()
	{
		return value == null ? "null" : value.toString();
	}

	public interface PropChangeListener<T>
	{
		void onPropChanged(Prop<T> prop, T oldValue, T newValue);
	}
}

