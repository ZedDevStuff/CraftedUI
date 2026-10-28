package dev.zeddevstuff.crafted;

public interface IProp<T>
{
	Class<T> getType();
	String getName();
	T get();
	void set(T newValue);
	void setFromString(String str);
}