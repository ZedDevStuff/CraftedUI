package dev.zeddevstuff.crafted.rendering;

import java.util.HashMap;
import java.util.Objects;

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
		var rendererMap = registry.get(elementClass);
		if (rendererMap == null)
		{
			System.err.println("No renderers registered for element class: " + elementClass.getName());
			return null;
		}
		var renderer = rendererMap.get(rendererName);
		if (renderer == null)
		{
			if(Objects.equals(rendererName, "default"))
				throw new RuntimeException("No default renderer registered for element class: " + elementClass.getName());
			return null;
		}
		return (IRendersType<T>) renderer;
	}
	@SuppressWarnings("unchecked")
	public <T> IRendersType<T> getRendererOrDefault(Class<T> elementClass, String rendererName)
	{
		var rendererMap = registry.get(elementClass);
		if (rendererMap == null)
		{
			throw new RuntimeException("No renderers registered for element class: " + elementClass.getName());
		}
		var renderer = rendererMap.get(rendererName);
		if (renderer == null)
		{
			renderer = rendererMap.get("default");
			if(renderer == null)
				throw new RuntimeException("No default renderer registered for element class: " + elementClass.getName());
		}
		return (IRendersType<T>) renderer;
	}
}
