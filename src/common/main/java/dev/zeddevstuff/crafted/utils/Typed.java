package dev.zeddevstuff.crafted.utils;

import javax.annotation.Nonnull;

public class Typed<T>
{
	public final Class<T> type;
	public T value;

	@SuppressWarnings("unchecked")
	public Typed(@Nonnull T value)
	{
		this((Class<T>)value.getClass(), value);
	}
	public Typed(Class<T> type, T value)
	{
		this.type = type;
		this.value = value;
	}
	public Class<T> getType() { return type; }
}