package dev.zeddevstuff.crafted;

import java.util.ArrayList;
import java.util.Objects;
import java.util.function.BiFunction;

public class Prop<T>
{
	public final Class<T> type;
	public final String name;
	private T value;
	public final BiFunction<String, T, T> parser;
	public T get() { return value; }
	public void set(T newValue)
	{
		if(newValue == null || !type.isAssignableFrom(newValue.getClass()))
			throw new IllegalArgumentException("Invalid value type for prop " + name + ": " + newValue);
		if(Objects.equals(value, newValue))
			return;
		T oldValue = this.value;
		this.value = newValue;
		for(var listener : listeners)
			listener.onPropChanged(this, oldValue, newValue);
	}
	public void setUnchecked(T newValue)
	{
		if(Objects.equals(this.value, newValue))
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
	public void setFromStringOrDefault(String str, T defaultValue)
	{
		set(parser.apply(str, defaultValue));
	}
	public void setFromStringOrDefaultObject(String str, Object defaultValue)
	{
		if(!type.isAssignableFrom(defaultValue.getClass()))
			throw new IllegalArgumentException("Invalid default value type for prop " + name + ": " + defaultValue);
		set(parser.apply(str, type.cast(defaultValue)));
	}
	public void setFromStringOrDefaultObjectUnchecked(String str, Object defaultValue)
	{
		set(parser.apply(str, type.cast(defaultValue)));
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
	public Prop(String name, T value)
	{
		this((Class<T>)value.getClass(), name, value);
	}
	@SuppressWarnings("unchecked")
	public Prop(String name, T value, BiFunction<String, T, T> parser)
	{
		this((Class<T>)value.getClass(), name, value, parser);
	}

	public void addListener(PropChangeListener<T> listener)
	{
		if(!listeners.contains(listener))
			listeners.add(listener);
	}
	public void removeListener(PropChangeListener<T> listener) { listeners.remove(listener); }

	public interface PropChangeListener<T>
	{
		void onPropChanged(Prop<T> prop, T oldValue, T newValue);
	}
}

