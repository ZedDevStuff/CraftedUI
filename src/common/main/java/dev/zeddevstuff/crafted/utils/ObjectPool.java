package dev.zeddevstuff.crafted.utils;

import java.util.ArrayList;

public class ObjectPool<T>
{
	private final ArrayList<T> available = new ArrayList<>();
	private final ArrayList<T> inUse = new ArrayList<>();

	public final IObjectFactory<T> factory;
	public final IObjectResetter<T> resetter;

	public ObjectPool(IObjectFactory<T> factory, IObjectResetter<T> resetter)
	{
		this.factory = factory;
		this.resetter = resetter;
	}

	public T acquire()
	{
		T obj;
		if(available.isEmpty())
			obj = factory.create();
		else
			obj = available.removeLast();
		inUse.add(obj);
		return obj;
	}

	public void release(T obj)
	{
		if(!inUse.remove(obj))
			throw new IllegalArgumentException("Object not in use: " + obj);
		resetter.reset(obj);
		available.add(obj);
	}

	public interface IObjectFactory<T>
	{
		T create();
	}
	public interface IObjectResetter<T>
	{
		void reset(T obj);
	}
}
