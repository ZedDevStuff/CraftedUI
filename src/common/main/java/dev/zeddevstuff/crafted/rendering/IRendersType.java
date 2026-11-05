package dev.zeddevstuff.crafted.rendering;

import dev.zeddevstuff.crafted.platform.ICanvas;
import dev.zeddevstuff.crafted.ui.CraftedElement;
import org.appliedenergistics.yoga.YogaOverflow;


/**
 * A renderer for a specific type of element.
 * @param <T>
 */
public interface IRendersType<T> extends IRenders
{
	default void beginRender(ICanvas canvas, T element) {}

	/**
	 * Renders the given element to the given canvas.
	 * @param canvas The canvas to render to.
	 * @param element The element to render.
	 */
	void render(ICanvas canvas, T element);

	/**
	 * Called after the element has been rendered. <br>
	 * This is typically used to end scopes like {@link ICanvas#endClip()} or {@link ICanvas#endTransform()}. <br>
	 * {@link ICanvas#endClip()} needs to be here for z-indexing to work properly with nested elements.
	 * @param canvas The canvas to render to.
	 * @param element The element that was rendered.
	 */
	default void afterRender(ICanvas canvas, T element) {}

	@SuppressWarnings("unchecked")
	default void forceRender(ICanvas canvas, Object element) { render(canvas, (T) element); }
	@SuppressWarnings("unchecked")
	default void forceBeginRender(ICanvas canvas, Object element) { beginRender(canvas, (T) element); }
	@SuppressWarnings("unchecked")
	default void forceAfterRender(ICanvas canvas, Object element) { afterRender(canvas, (T) element); }

	static void beginClip(ICanvas canvas, CraftedElement element, int xOffset, int yOffset)
	{
		var layout = element.getLayout();
		if(element.overflow.get() != YogaOverflow.VISIBLE)
			canvas.beginClip(
				layout.x - element.getAncestorRenderOffsetX(),
				layout.y - element.getAncestorRenderOffsetY(),
				layout.width,
				layout.height);
		if(element.overflow.get() == YogaOverflow.SCROLL && (xOffset != 0 || yOffset != 0))
		{
			canvas.beginTransform();
			canvas.translate(-xOffset, -yOffset);
		}
	}

	static void endClip(ICanvas canvas, CraftedElement element, int xOffset, int yOffset)
	{
		if(element.overflow.get() == YogaOverflow.SCROLL && (xOffset != 0 || yOffset != 0))
			canvas.endTransform();
		if(element.overflow.get() != YogaOverflow.VISIBLE)
			canvas.endClip();
	}
}
