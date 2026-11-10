package dev.zeddevstuff.crafted.utils;

import java.util.function.Supplier;

public class Lazy<T>
{
	private T value;
	public T get()
	{
		if(value == null)
			value = supplier.get();
		return value;
	}

	private final Supplier<T> supplier;

	public Lazy(Supplier<T> supplier)
	{
		this.supplier = supplier;
	}
}
