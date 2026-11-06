package dev.zeddevstuff.crafted.utils;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

public final class TypedList<T> extends AbstractList<T>
{
	private final Class<T> elementType;
	private final ArrayList<T> values = new ArrayList<>();
	private final ArrayList<IValuesChangeListener<T>> listeners = new ArrayList<>();

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
        for(var listener : listeners)
            listener.onValuesChanged();
	}

	@Override
	public T set(int index, T value)
	{
		validate(value);
		var replaced = values.set(index, value);
        if(replaced != value)
            for(var listener : listeners)
                listener.onValuesChanged();
        return replaced;
	}

    @Override
    public boolean remove(Object o) 
    {
        var removed = values.remove(o);
        if(removed)
            for(var listener : listeners)
                listener.onValuesChanged();
        return removed;
    }

    @Override
    public void clear() 
    {
        if(!values.isEmpty())
        {
            values.clear();
            for(var listener : listeners)
                listener.onValuesChanged();
        }
    }

	public void addChangeListener(IValuesChangeListener<T> listener)
	{
		listeners.add(listener);
	}

	public void removeChangeListener(IValuesChangeListener<T> listener)
	{
		listeners.remove(listener);
	}

	private void validate(T value)
	{
		if(value != null && !elementType.isInstance(value))
			throw new IllegalArgumentException(
				"Expected " + elementType.getName() + ", got " + value.getClass().getName()
			);
	}

	public interface IValuesChangeListener<T>
	{
		void onValuesChanged();
	}
}
