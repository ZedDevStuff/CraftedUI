package dev.zeddevstuff.crafted.rendering;

import java.util.HashMap;

public class RendererRegistry
{
	private final HashMap<Class<?>, HashMap<String, IRendersType<?>>> registry = new HashMap<>();
	public <T> void registerRenderer(Class<T> elementClass, String rendererName, IRendersType<T> renderer)
	{
		var map = registry.computeIfAbsent(elementClass, k -> new HashMap<>());
        if(map.containsKey(rendererName))
            throw new IllegalArgumentException("A renderer is already registered for element class: " + elementClass.getName() + " and renderer name: " + rendererName);
        map.put(rendererName, renderer);
	}
	@SuppressWarnings("unchecked")
	public <T> IRendersType<T> getRenderer(Class<T> elementClass, String rendererName)
	{
		var renderer = findRenderer(elementClass, rendererName);
		if (renderer == null)
		{
			System.err.println("No registered renderer compatible with element class: " + elementClass.getName());
			return null;
		}
		return (IRendersType<T>) renderer;
	}

	private IRendersType<?> findRenderer(Class<?> elementClass, String rendererName)
	{
		var rendererMap = registry.get(elementClass);
		var renderer = rendererMap == null
			? null
			: rendererMap.get(rendererName);
		if (renderer != null)
			return renderer;

		var parent = elementClass.getSuperclass();
		return parent == null
			? null
			: findRenderer(parent, rendererName);
	}

	@SuppressWarnings("unchecked")
	public <T> IRendersType<T> getRendererOrDefault(Class<T> elementClass, String rendererName)
	{
		var renderer = findRenderer(elementClass, rendererName);
		if (renderer == null)
			renderer = findRenderer(elementClass, "default");
		if (renderer == null)
		{
			throw new RuntimeException("No registered renderer compatible with element class: " + elementClass.getName());
		}
		return (IRendersType<T>) renderer;
	}
}
