package dev.zeddevstuff.crafted.ui;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ElementRegistry
{
	private final HashMap<String, ElementFactory> factories = new HashMap<>();
	private final HashMap<Class<?>, String> classToTag = new HashMap<>();
	public Set<Map.Entry<Class<?>, String>> getClassToTagMap() { return classToTag.entrySet(); }
	public void registerElement(Class<?> elementClass, String tag, ElementFactory factory)
	{
		classToTag.put(elementClass, tag);
		factories.put(tag, factory);
	}
	public ElementFactory getElementFactory(String tag)
	{
		return factories.get(tag);
	}
	public CraftedElement createElement(CraftedContext ctx, String tag, Map<String, String> attributes)
	{
		var factory = getElementFactory(tag);
		if (factory == null)
			throw new RuntimeException("No element factory registered for tag: " + tag);
		var element = factory.create(ctx, attributes);
		element.parseAttributes(attributes);
		return element;
	}

	public interface ElementFactory
	{
		CraftedElement create(CraftedContext ctx, Map<String, String> attributes);
	}
}
