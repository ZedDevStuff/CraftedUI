package dev.zeddevstuff.crafted;

import java.util.function.Consumer;

public interface IObservableProp<T> extends IProp<T>
{
	void addChangeListener(Consumer<T> listener);
	void removeChangeListener(Consumer<T> listener);
}