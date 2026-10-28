package dev.zeddevstuff.crafted.rendering;

import java.util.HashMap;
import java.util.Objects;

public class RendererRegistry
{
	private final HashMap<Class<?>, HashMap<String, IRenders<?>>> registry = new HashMap<>();
	public <T> void registerRenderer(Class<T> elementClass, String rendererName, IRenders<T> renderer)
	{
		registry.computeIfAbsent(elementClass, k -> new HashMap<>()).put(rendererName, renderer);
	}
	@SuppressWarnings("unchecked")
	public <T> IRenders<T> getRenderer(Class<T> elementClass, String rendererName)
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
		return (IRenders<T>) renderer;
	}
	@SuppressWarnings("unchecked")
	public <T> IRenders<T> getRendererOrDefault(Class<T> elementClass, String rendererName)
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
			renderer = rendererMap.get("default");
			if(renderer == null)
				throw new RuntimeException("No default renderer registered for element class: " + elementClass.getName());
		}
		return (IRenders<T>) renderer;
	}
}
