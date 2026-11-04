package dev.zeddevstuff.crafted.rendering;

import dev.zeddevstuff.crafted.platform.ICanvas;
import dev.zeddevstuff.crafted.ui.CraftedElement;
import dev.zeddevstuff.crafted.ui.elements.CraftedRoot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CraftedRenderer
{
	private final CraftedRoot rootElement;
	private List<DrawCall> drawCalls;
	private long cachedTreeVersion = -1;

	public CraftedRenderer(CraftedRoot rootElement)
	{
		this.rootElement = rootElement;
	}

	public void render(ICanvas canvas)
	{
		if(cachedTreeVersion != rootElement.getTreeVersion())
			rebuildDrawCalls();
		var activeScopes = new ArrayList<CraftedElement>();
		for(var drawCall : drawCalls)
			drawCall.execute(canvas, activeScopes);
		while(!activeScopes.isEmpty())
			new DrawCall(activeScopes.remove(activeScopes.size() - 1), 0, 0, Phase.AFTER_RENDER).execute(canvas, activeScopes);
	}

	private void rebuildDrawCalls()
	{
		var renderCalls = new ArrayList<DrawCall>();
		collectDrawCalls(rootElement, rootElement.zIndex.get(), renderCalls);
		renderCalls.sort(Comparator.comparingLong(DrawCall::absoluteZIndex));

		drawCalls = List.copyOf(renderCalls);
		cachedTreeVersion = rootElement.getTreeVersion();
	}

	private void collectDrawCalls(CraftedElement parent, long parentZIndex, List<DrawCall> calls)
	{
		for(var child : parent.getChildren())
		{
			long absoluteZIndex = parentZIndex + child.zIndex.get();
			if(child.renderer != null)
			{
				var drawCall = DrawCall.render(child, absoluteZIndex, getDepth(child));
				calls.add(drawCall);
			}
			collectDrawCalls(child, absoluteZIndex, calls);
		}
	}

	private int getDepth(CraftedElement element)
	{
		int depth = 0;
		for(var parent = element.getParent(); parent != null; parent = parent.getParent())
			depth++;
		return depth;
	}

	private record DrawCall(CraftedElement element, long absoluteZIndex, int depth, Phase phase)
	{
		private static DrawCall render(CraftedElement element, long absoluteZIndex, int depth)
		{
			return new DrawCall(element, absoluteZIndex, depth, Phase.RENDER);
		}

		private void execute(ICanvas canvas, List<CraftedElement> activeScopes)
		{
			if(phase == Phase.AFTER_RENDER)
			{
				element.afterRenderSelf(canvas);
				return;
			}

		var path = new ArrayList<CraftedElement>();
		for(var current = element; current != null; current = current.getParent())
			path.add(current);
		Collections.reverse(path);
		int common = 0;
		while(common < activeScopes.size() && common < path.size() && activeScopes.get(common) == path.get(common))
			common++;
		while(activeScopes.size() > common)
			new DrawCall(activeScopes.remove(activeScopes.size() - 1), 0, 0, Phase.AFTER_RENDER).execute(canvas, activeScopes);
		while(activeScopes.size() < path.size())
		{
			var scope = path.get(activeScopes.size());
			scope.beginRenderSelf(canvas);
			activeScopes.add(scope);
		}
		element.renderSelf(canvas);
		}
	}

	private enum Phase
	{
		RENDER,
		AFTER_RENDER
	}
}
