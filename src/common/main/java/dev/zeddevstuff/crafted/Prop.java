package dev.zeddevstuff.crafted;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Objects;
import java.util.function.BiFunction;

/**
 * Implementation of {@link IProp} that stores a value and notifies listeners when it changes.
 * @param <T>
 */
public class Prop<T> implements IObservableProp<T>
{
	private final Class<?> type;
	public final Class<?> getType() { return type; }
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
        var copy = new ArrayList<>(listeners);
		for(var listener : copy)
			listener.onPropChanged(this, oldValue, newValue);
		var changeCopy = new ArrayList<>(changeListeners);
		for(var listener : changeCopy)
			listener.accept(newValue);
	}

	public void setFromString(String str)
	{
		set(parser.apply(str, value));
	}

	private final ArrayList<PropChangeListener<T>> listeners = new ArrayList<>();

	private final ArrayList<java.util.function.Consumer<T>> changeListeners = new ArrayList<>();
	public Prop(Class<?> type, String name, T value)
	{
		this.type = type;
		this.name = name;
		validate(value);
		this.value = value;
		if(type == String.class)
			parser = (str, defaultVal) -> (T) str;
		else
			parser = (str, defaultVal) -> defaultVal;
	}
	public Prop(Class<?> type, String name, T value, BiFunction<String, T, T> parser)
	{
		this.type = type;
		this.name = name;
		validate(value);
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

	private void validate(T value)
	{
		if(value != null && !type.isInstance(value))
			throw new IllegalArgumentException("Invalid value type for prop " + name + ": " + value);
	}

	public void addListener(PropChangeListener<T> listener)
	{
		if(!listeners.contains(listener))
			listeners.add(listener);
	}
	public void removeListener(PropChangeListener<T> listener) { listeners.remove(listener); }

	@Override
	public void addChangeListener(java.util.function.Consumer<T> listener)
	{
		if(!changeListeners.contains(listener))
			changeListeners.add(listener);
	}
	@Override
	public void removeChangeListener(java.util.function.Consumer<T> listener)
	{
		changeListeners.remove(listener);
	}
	public String toString()
	{
		return value == null ? "null" : value.toString();
	}

	public interface PropChangeListener<T>
	{
		void onPropChanged(Prop<T> prop, T oldValue, T newValue);
	}
}

