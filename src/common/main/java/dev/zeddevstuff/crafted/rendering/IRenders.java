package dev.zeddevstuff.crafted.rendering;

import dev.zeddevstuff.crafted.platform.ICanvas;

/**
 * A renderer for a specific type of element.
 * @param <T>
 */
public interface IRenders<T>
{
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
	default void forceAfterRender(ICanvas canvas, Object element) { afterRender(canvas, (T) element); }
}
