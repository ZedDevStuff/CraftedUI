package dev.zeddevstuff.crafted.utils;

import java.util.ArrayList;

public class Event<T>
{
	private final Object key;
	public final Class<T> type;
	private final ArrayList<IEventListener<T>> listeners = new ArrayList<>();

	public Event(Object key, Class<T> type)
	{
		this.key = key;
		this.type = type;
	}

	public void addListener(IEventListener<T> listener)
	{
		listeners.add(listener);
	}
	public void removeListener(IEventListener<T> listener)
	{
		listeners.remove(listener);
	}
	public void clearListeners()
	{
		listeners.clear();
	}

	public void fire(Object key, T event)
	{
		if(this.key != key)
			throw new IllegalArgumentException("Invalid event key: " + key);
		if(!type.isInstance(event))
			throw new IllegalArgumentException("Invalid event type: " + event);
        var copy = new ArrayList<>(listeners);
		for(var listener : copy)
			listener.onEvent(event);
	}

	public interface IEventListener<T>
	{
		void onEvent(T event);
	}
}
