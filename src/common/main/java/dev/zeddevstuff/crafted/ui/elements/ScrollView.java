package dev.zeddevstuff.crafted.ui.elements;

import dev.zeddevstuff.crafted.Crafted;
import dev.zeddevstuff.crafted.Prop;
import dev.zeddevstuff.crafted.annotations.DefineElement;
import dev.zeddevstuff.crafted.parsing.Parsers;
import dev.zeddevstuff.crafted.platform.ICanvas;
import dev.zeddevstuff.crafted.input.events.MouseEvent;
import dev.zeddevstuff.crafted.rendering.IRendersType;
import dev.zeddevstuff.crafted.types.Color;
import dev.zeddevstuff.crafted.ui.CraftedContext;
import dev.zeddevstuff.crafted.ui.CraftedElement;
import org.appliedenergistics.yoga.YogaEdge;
import org.appliedenergistics.yoga.YogaOverflow;
import org.appliedenergistics.yoga.style.YogaStyle;

import java.util.Map;

import static dev.zeddevstuff.crafted.input.MouseButton.LEFT;

@DefineElement("ScrollView")
public class ScrollView extends Box
{
	public final Prop<Integer> scrollX = registerProp("scrollX", 0, this::parseInteger);
	public final Prop<Integer> scrollY = registerProp("scrollY", 0, this::parseInteger);

	public final Prop<Boolean> horizontalScrollbar = registerProp("horizontalScrollbar", true, Parsers::parseBoolean);
	public final Prop<Boolean> verticalScrollbar = registerProp("verticalScrollbar", true, Parsers::parseBoolean);

	public final Prop<Integer> scrollbarSize = registerProp("scrollbarSize", 6, this::parseInteger);
	private boolean draggingHorizontal;
	private boolean draggingVertical;
	private float dragStartX;
	private float dragStartY;
	private int dragStartScrollX;
	private int dragStartScrollY;

	public ScrollView(CraftedContext ctx, Map<String, String> attributes)
	{
		super(ctx, attributes);
		color.set(ctx.theme.surface());
		horizontalScrollbar.addListener((prop, oldValue, newValue) -> {
			if(!newValue)
			{
				node.setPadding(YogaEdge.BOTTOM, 0);
				scrollX.set(0);
			}
			else
			{
				node.setPadding(YogaEdge.BOTTOM, scrollbarSize.get());
			}
		});
		verticalScrollbar.addListener((prop, oldValue, newValue) -> {
			if(!newValue)
			{
				node.setPadding(YogaEdge.RIGHT, 0);
				scrollY.set(0);
			}
			else
			{
				node.setPadding(YogaEdge.RIGHT, scrollbarSize.get());
			}
		});
		scrollbarSize.addListener((prop, oldValue, newValue) -> updateScrollbarPadding());
		updateScrollbarPadding();
	}

	@Override
	protected YogaStyle initLayoutNode(YogaStyle style, Map<String, String> attributes)
	{
		style.setOverflow(YogaOverflow.SCROLL);
		return style;
	}

	private Integer parseInteger(String value, Integer defaultValue)
	{
		try
		{
			return Math.max(0, Integer.parseInt(value));
		}
		catch(NumberFormatException e)
		{
			return defaultValue;
		}
	}

	private void updateScrollbarPadding()
	{
		node.setPadding(YogaEdge.BOTTOM, horizontalScrollbar.get() ? scrollbarSize.get() : 0);
		node.setPadding(YogaEdge.RIGHT, verticalScrollbar.get() ? scrollbarSize.get() : 0);
	}

	private int maxScrollX()
	{
		var layout = getLayout();
		int contentRight = layout.innerX;
		for(var child : children)
			contentRight = Math.max(contentRight, child.getLayout().x + child.getLayout().width);
		return Math.max(0, contentRight - (layout.innerX + layout.innerWidth));
	}

	private int maxScrollY()
	{
		var layout = getLayout();
		int contentBottom = layout.innerY;
		for(var child : children)
			contentBottom = Math.max(contentBottom, child.getLayout().y + child.getLayout().height);
		return Math.max(0, contentBottom - (layout.innerY + layout.innerHeight));
	}

	private int horizontalTrackWidth()
	{
		return getLayout().width - (verticalScrollbar.get() ? scrollbarSize.get() : 0);
	}

	private int verticalTrackHeight()
	{
		return getLayout().height - (horizontalScrollbar.get() ? scrollbarSize.get() : 0);
	}

	private int horizontalThumbWidth()
	{
		var layout = getLayout();
		return Math.max(scrollbarSize.get(), Math.round(horizontalTrackWidth() * (float) layout.innerWidth
			/ Math.max(layout.innerWidth, layout.innerWidth + maxScrollX())));
	}

	private int verticalThumbHeight()
	{
		var layout = getLayout();
		return Math.max(scrollbarSize.get(), Math.round(verticalTrackHeight() * (float) layout.innerHeight
			/ Math.max(layout.innerHeight, layout.innerHeight + maxScrollY())));
	}

	private int horizontalThumbX()
	{
		int available = horizontalTrackWidth() - horizontalThumbWidth();
		return getLayout().x + Math.round(available * (maxScrollX() == 0 ? 0 : (float) scrollX.get() / maxScrollX()));
	}

	private int verticalThumbY()
	{
		int available = verticalTrackHeight() - verticalThumbHeight();
		return getLayout().y + Math.round(available * (maxScrollY() == 0 ? 0 : (float) scrollY.get() / maxScrollY()));
	}
    
	private int horizontalThumbScreenX()
	{
		return horizontalThumbX() - getAncestorRenderOffsetX();
	}
    
	private int verticalThumbScreenY()
	{
		return verticalThumbY() - getAncestorRenderOffsetY();
	}

	private boolean containsHorizontalThumb(float x, float y)
	{
		var layout = getLayout();
		return horizontalScrollbar.get()
			&& x >= horizontalThumbScreenX() && x <= horizontalThumbScreenX() + horizontalThumbWidth()
			&& y >= layout.y - getAncestorRenderOffsetY() + layout.height - scrollbarSize.get()
			&& y <= layout.y - getAncestorRenderOffsetY() + layout.height;
	}

	private boolean containsVerticalThumb(float x, float y)
	{
		var layout = getLayout();
		return verticalScrollbar.get()
			&& x >= layout.x - getAncestorRenderOffsetX() + layout.width - scrollbarSize.get()
			&& x <= layout.x - getAncestorRenderOffsetX() + layout.width
			&& y >= verticalThumbScreenY() && y <= verticalThumbScreenY() + verticalThumbHeight();
	}

	private void clampScroll()
	{
		scrollX.set(Math.clamp(scrollX.get(), 0, maxScrollX()));
		scrollY.set(Math.clamp(scrollY.get(), 0, maxScrollY()));
	}

	@Override
	public void scrollIntoView(CraftedElement element)
	{
		var target = element.getLayout();
		var viewport = getLayout();
		int ancestorOffsetX = getAncestorRenderOffsetX();
		int ancestorOffsetY = getAncestorRenderOffsetY();
		int targetBaseX = target.x - element.getAncestorRenderOffsetX() + scrollX.get();
		int targetBaseY = target.y - element.getAncestorRenderOffsetY() + scrollY.get();
		int viewportX = viewport.innerX - ancestorOffsetX;
		int viewportY = viewport.innerY - ancestorOffsetY;
		int targetLeft = targetBaseX - scrollX.get();
		int targetTop = targetBaseY - scrollY.get();
		int targetRight = targetLeft + target.width;
		int targetBottom = targetTop + target.height;
		int viewportRight = viewportX + viewport.innerWidth;
		int viewportBottom = viewportY + viewport.innerHeight;

		if(targetLeft < viewportX)
			scrollX.set(targetBaseX - viewportX);
		else if(targetRight > viewportRight)
			scrollX.set(targetBaseX + target.width - viewportRight);
		if(targetTop < viewportY)
			scrollY.set(targetBaseY - viewportY);
		else if(targetBottom > viewportBottom)
			scrollY.set(targetBaseY + target.height - viewportBottom);
		clampScroll();
	}

	@Override
	protected float transformChildInputX(float x)
	{
		return x + scrollX.get();
	}

	@Override
	protected float transformChildInputY(float y)
	{
		return y + scrollY.get();
	}

	@Override
	protected int getRenderOffsetX() { return scrollX.get(); }

	@Override
	protected int getRenderOffsetY() { return scrollY.get(); }

	@Override
	public boolean receivesInputEvents() { return true; }

	@Override
	public void onMouseMove(MouseEvent event)
	{
		if(draggingHorizontal)
		{
			int available = horizontalTrackWidth() - horizontalThumbWidth();
			if(available > 0)
				scrollX.set(dragStartScrollX + Math.round((event.x - dragStartX) * maxScrollX() / available));
			clampScroll();
			event.consume();
			event.stopPropagation();
		}
		else if(draggingVertical)
		{
			int available = verticalTrackHeight() - verticalThumbHeight();
			if(available > 0)
				scrollY.set(dragStartScrollY + Math.round((event.y - dragStartY) * maxScrollY() / available));
			clampScroll();
			event.consume();
			event.stopPropagation();
		}
		super.onMouseMove(event);
	}

	@Override
	public void onMouseScroll(MouseEvent event)
	{
		int oldX = scrollX.get();
		int oldY = scrollY.get();
		scrollX.set(scrollX.get() - Math.round(event.scrollX * 10));
		scrollY.set(scrollY.get() - Math.round(event.scrollY * 10));
		clampScroll();
		if(oldX != scrollX.get() || oldY != scrollY.get())
		{
			event.stopPropagation();
			event.consume();
		}
	}

	@Override
	public void onMousePress(MouseEvent event)
	{
		if(event.button == LEFT && containsHorizontalThumb(event.x, event.y))
		{
			draggingHorizontal = true;
			dragStartX = event.x;
			dragStartScrollX = scrollX.get();
			event.consume();
			event.stopPropagation();
		}
		else if(event.button == LEFT && containsVerticalThumb(event.x, event.y))
		{
			draggingVertical = true;
			dragStartY = event.y;
			dragStartScrollY = scrollY.get();
			event.consume();
			event.stopPropagation();
		}
		super.onMousePress(event);
	}

	@Override
	public void onMouseRelease(MouseEvent event)
	{
		if(event.button == LEFT && (draggingHorizontal || draggingVertical))
		{
			draggingHorizontal = false;
			draggingVertical = false;
			event.consume();
			event.stopPropagation();
		}
		super.onMouseRelease(event);
	}

	public static void register(Crafted crafted)
	{
		crafted.elements.registerElement(ScrollView.class, "ScrollView", ScrollView::new);
		crafted.renderers.registerRenderer(ScrollView.class, "default", new DefaultRenderer());
		crafted.renderers.registerRenderer(ScrollView.class, "crafted", new CraftedRenderer());
	}

	public static class DefaultRenderer implements IRendersType<ScrollView>
	{
		private static final Color SCROLLBAR_THUMB_COLOR = new Color(192, 192, 192);
		private static final Color SCROLLBAR_THUMB_SHADOW_COLOR = Color.Gray;
		@Override
		public Class<?> getElementType() { return ScrollView.class; }
		@Override
		public void beginRender(ICanvas canvas, ScrollView element)
		{
			element.clampScroll();
			IRendersType.beginClip(canvas, element, element.scrollX.get(), element.scrollY.get());
		}

		@Override
		public void render(ICanvas canvas, ScrollView element) { }

		@Override
		public void afterRender(ICanvas canvas, ScrollView element)
		{
			IRendersType.endClip(canvas, element, element.scrollX.get(), element.scrollY.get());
			if(element.horizontalScrollbar.get())
			{
				var layout = element.getLayout();
				int trackWidth = layout.width - (element.verticalScrollbar.get() ? element.scrollbarSize.get() : 0);
				int thumbWidth = element.horizontalThumbWidth();
				int thumbX = element.horizontalThumbX();
				canvas.drawRect(layout.x, layout.y + layout.height - element.scrollbarSize.get(), trackWidth, element.scrollbarSize.get(), Color.Black);
				canvas.drawRect(thumbX, layout.y + layout.height - element.scrollbarSize.get(), thumbWidth, element.scrollbarSize.get(), SCROLLBAR_THUMB_SHADOW_COLOR);
				canvas.drawRect(thumbX, layout.y + layout.height - element.scrollbarSize.get(), thumbWidth - 1, element.scrollbarSize.get() - 1, SCROLLBAR_THUMB_COLOR);
			}
			if(element.verticalScrollbar.get())
			{
				var layout = element.getLayout();
				int trackHeight = layout.height - (element.horizontalScrollbar.get() ? element.scrollbarSize.get() : 0);
				int thumbHeight = element.verticalThumbHeight();
				int thumbY = element.verticalThumbY();
				canvas.drawRect(layout.x + layout.width - element.scrollbarSize.get(), layout.y, element.scrollbarSize.get(), trackHeight, Color.Black);
				canvas.drawRect(layout.x + layout.width - element.scrollbarSize.get(), thumbY, element.scrollbarSize.get(), thumbHeight, SCROLLBAR_THUMB_SHADOW_COLOR);
				canvas.drawRect(layout.x + layout.width - element.scrollbarSize.get(), thumbY, element.scrollbarSize.get() - 1, thumbHeight - 1, SCROLLBAR_THUMB_COLOR);
			}
		}
	}

	public static class CraftedRenderer implements IRendersType<ScrollView>
	{
		@Override
		public Class<?> getElementType() { return ScrollView.class; }
		@Override
		public void beginRender(ICanvas canvas, ScrollView element)
		{
			element.clampScroll();
			IRendersType.beginClip(canvas, element, element.scrollX.get(), element.scrollY.get());
		}

		@Override
		public void render(ICanvas canvas, ScrollView element)
		{
			var layout = element.getLayout();
			canvas.drawRect(layout.x, layout.y, layout.width, layout.height, element.color.get());
		}

		@Override
		public void afterRender(ICanvas canvas, ScrollView element)
		{
			IRendersType.endClip(canvas, element, element.scrollX.get(), element.scrollY.get());
			if(element.horizontalScrollbar.get())
			{
				var layout = element.getLayout();
				int trackWidth = layout.width - (element.verticalScrollbar.get() ? element.scrollbarSize.get() : 0);
				int thumbWidth = element.horizontalThumbWidth();
				int thumbX = element.horizontalThumbX();
				canvas.drawRect(layout.x, layout.y + layout.height - element.scrollbarSize.get(), trackWidth, element.scrollbarSize.get(), element.ctx.theme.surfaceVariant());
				canvas.drawRect(thumbX, layout.y + layout.height - element.scrollbarSize.get(), thumbWidth, element.scrollbarSize.get(), element.ctx.theme.primary());
			}
			if(element.verticalScrollbar.get())
			{
				var layout = element.getLayout();
				int trackHeight = layout.height - (element.horizontalScrollbar.get() ? element.scrollbarSize.get() : 0);
				int thumbHeight = element.verticalThumbHeight();
				int thumbY = element.verticalThumbY();
				canvas.drawRect(layout.x + layout.width - element.scrollbarSize.get(), layout.y, element.scrollbarSize.get(), trackHeight, element.ctx.theme.surfaceVariant());
				canvas.drawRect(layout.x + layout.width - element.scrollbarSize.get(), thumbY, element.scrollbarSize.get(), thumbHeight, element.ctx.theme.primary());
			}
		}
	}
}
