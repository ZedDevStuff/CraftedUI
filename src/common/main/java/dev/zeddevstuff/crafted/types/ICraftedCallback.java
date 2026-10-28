package dev.zeddevstuff.crafted.types;

public interface ICraftedCallback
{
	Class<?> getType();
	default void setResult(Object result) { throw new UnsupportedOperationException("This callback does not support setting a result."); }
	default Object getResult() { throw new UnsupportedOperationException("This callback does not support getting a result."); }
}
