package dev.zeddevstuff.crafted;

import dev.zeddevstuff.crafted.ui.CraftedElement;

import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Implementation of {@link IProp} that delegates all operations to a getter and setter. <br>
 * You'll notice this is used in {@link CraftedElement} for aliases and composite props.
 * @param <T>
 */
public class ProxyProp<T> implements IProp<T>
{
	private final Consumer<T> setter;
	private final Supplier<T> getter;
	private final BiFunction<String, T, T> parser;
	private final Class<T> type;
	@Override
	public Class<T> getType() { return type; }
	private final String name;
	@Override
	public String getName() { return name; }

	public ProxyProp(Class<T> type, String name, Consumer<T> setter, Supplier<T> getter)
	{
		this(type, name, setter, getter, (str, defaultVal) -> defaultVal);
	}
	public ProxyProp(Class<T> type, String name, Consumer<T> setter, Supplier<T> getter, BiFunction<String, T, T> parser)
	{
		this.type = type;
		this.name = name;
		this.setter = setter;
		this.getter = getter;
		this.parser = parser;
	}
	@Override
	public T get() { return getter.get(); }
	@Override
	public void set(T newValue) { setter.accept(newValue); }

	@Override
	public void setFromString(String str) { set(parser.apply(str, get())); }
}
