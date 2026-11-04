package dev.zeddevstuff.crafted.utils;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

public final class TypedList<T> extends AbstractList<T>
{
	private final Class<T> elementType;
	private final ArrayList<T> values = new ArrayList<>();

	public TypedList(Class<T> elementType)
	{
		this.elementType = Objects.requireNonNull(elementType);
	}
	public TypedList(Class<T> elementType, Collection<T> values)
	{
		this(elementType);
		for(var value : values)
			validate(value);
		this.values.addAll(values);
	}

	public Class<T> getElementType()
	{
		return elementType;
	}

	@Override
	public T get(int index)
	{
		return values.get(index);
	}

	@Override
	public int size()
	{
		return values.size();
	}

	@Override
	public void add(int index, T value)
	{
		validate(value);
		values.add(index, value);
	}

	@Override
	public T set(int index, T value)
	{
		validate(value);
		return values.set(index, value);
	}

	private void validate(T value)
	{
		if(value != null && !elementType.isInstance(value))
			throw new IllegalArgumentException(
				"Expected " + elementType.getName() + ", got " + value.getClass().getName()
			);
	}
}
