package dev.zeddevstuff.crafted.ui;

import java.util.Map;
import java.util.function.Function;

public final class BindingScope
{
	private final Function<String, Object> resolver;
	private final String prefix;

	public BindingScope(Function<String, Object> resolver)
	{
		this(resolver, "");
	}

	public BindingScope(Map<String, ?> values)
	{
		this(values::get);
	}

	public BindingScope(Function<String, Object> resolver, String prefix)
	{
		this.resolver = resolver;
		this.prefix = prefix == null ? "" : prefix;
	}

	public Object resolve(String name)
	{
		var value = resolver.apply(name);
		if(value != null)
			return value;
		return prefix.isEmpty() ? null : resolver.apply(prefix + name);
	}

	public String getPrefix()
	{
		return prefix;
	}
}