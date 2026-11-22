package dev.zeddevstuff.crafted.ui.elements;

import dev.zeddevstuff.crafted.Crafted;
import dev.zeddevstuff.crafted.Prop;
import dev.zeddevstuff.crafted.annotations.DefineElement;
import dev.zeddevstuff.crafted.annotations.Description;
import dev.zeddevstuff.crafted.input.ImportantKeys;
import dev.zeddevstuff.crafted.input.MouseButton;
import dev.zeddevstuff.crafted.input.events.ClickEvent;
import dev.zeddevstuff.crafted.input.events.KeyEvent;
import dev.zeddevstuff.crafted.input.events.MouseEvent;
import dev.zeddevstuff.crafted.parsing.DocumentBuilder;
import dev.zeddevstuff.crafted.parsing.ParsedNode;
import dev.zeddevstuff.crafted.parsing.Parsers;
import dev.zeddevstuff.crafted.platform.ICanvas;
import dev.zeddevstuff.crafted.platform.IText;
import dev.zeddevstuff.crafted.platform.ITexture;
import dev.zeddevstuff.crafted.rendering.IRendersType;
import dev.zeddevstuff.crafted.types.Color;
import dev.zeddevstuff.crafted.types.Rect;
import dev.zeddevstuff.crafted.ui.CraftedContext;
import dev.zeddevstuff.crafted.ui.CraftedElement;
import dev.zeddevstuff.crafted.ui.MetaElement;
import org.appliedenergistics.yoga.YogaEdge;
import org.appliedenergistics.yoga.YogaFlexDirection;
import org.appliedenergistics.yoga.YogaPhysicalEdge;
import org.appliedenergistics.yoga.numeric.FloatOptional;
import org.appliedenergistics.yoga.style.StyleLength;
import org.appliedenergistics.yoga.style.YogaStyle;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@DefineElement("Tabs")
@Description("A tabbed container element")
public class Tabs extends CraftedElement implements MetaElement
{
	public final Prop<Integer> currentTabIndex = registerProp(Integer.class, "currentTabIndex", 0, Parsers::parseInt);
	public final Prop<YogaPhysicalEdge> tabBarPosition = registerProp(YogaPhysicalEdge.class, "tabBarPosition", YogaPhysicalEdge.TOP, Parsers::parseYogaPhysicalEdge);
	public final Prop<Integer> tabBarHeight = registerProp(Integer.class, "tabBarHeight", 20, Parsers::parseInt);
	public final Prop<Integer> minTabWidth = registerProp(Integer.class, "minTabWidth", 0, Parsers::parseInt);
	public final Prop<Integer> maxTabWidth = registerProp(Integer.class, "maxTabWidth", Integer.MAX_VALUE, Parsers::parseInt);

	private final List<Tab> tabs = new ArrayList<>();
	public Tab getTab(int index)
	{
		if(index < 0 || index >= tabs.size())
			return null;
		return tabs.get(index);
	}

	public Tabs(CraftedContext ctx, Map<String, String> attributes)
	{
		super(ctx, attributes);
		tabBarHeight.addListener((prop, oldValue, newValue) -> {
			node.setPadding(fromPhysicalEdge(tabBarPosition.get()), StyleLength.points(newValue));
		});
		tabBarPosition.addListener((prop, oldValue, newValue) -> updateBarPadding(oldValue, newValue));
		currentTabIndex.addListener(this::updateActiveTab);
	}

	@Override
	protected void copyExtraStateTo(CraftedElement clone)
	{
		if(clone instanceof Tabs tabsClone)
		{
			tabsClone.tabs.clear();
			tabsClone.tabs.addAll(tabs);
			tabsClone.updateActiveTab(tabsClone.currentTabIndex, 0, tabsClone.currentTabIndex.get());
		}
	}

	private void updateBarPadding(YogaPhysicalEdge oldEdge, YogaPhysicalEdge newEdge)
	{
		if(oldEdge != null)
			node.setPadding(fromPhysicalEdge(oldEdge), StyleLength.points(0));
		node.setPadding(fromPhysicalEdge(newEdge), StyleLength.points(tabBarHeight.get()));
	}

	@Override
	public void setTemplateNodes(ParsedNode[] templateNodes)
	{
		for(var node : templateNodes)
		{
			var child = DocumentBuilder.buildTree(ctx, node);
			if(child instanceof Tab tab)
				tabs.add(tab);
			else
				throw new IllegalArgumentException("Tabs element can only contain Tab elements as children.");
		}
		updateActiveTab(currentTabIndex, 0, currentTabIndex.get());
	}

	private void updateActiveTab(Prop<Integer> prop, Integer oldValue, Integer newValue)
	{
		var oldTab = getTab(oldValue);
		var newTab = getTab(newValue);
		if(newTab == null)
			return;
		if(oldTab != null && oldTab != newTab)
			removeChild(oldTab, false);
		addChild(newTab);
	}

	private YogaEdge fromPhysicalEdge(YogaPhysicalEdge edge)
	{
		return switch(edge)
		{
			case LEFT -> YogaEdge.LEFT;
			case TOP -> YogaEdge.TOP;
			case RIGHT -> YogaEdge.RIGHT;
			case BOTTOM -> YogaEdge.BOTTOM;
		};
	}

	@Override
	protected YogaStyle initLayoutNode(YogaStyle style, Map<String, String> attributes)
	{
		style.setFlexDirection(YogaFlexDirection.COLUMN);
		style.setPadding(YogaEdge.TOP, StyleLength.points(20));
		return style;
	}

	@Override
	protected boolean acceptsChild(CraftedElement child)
	{
		if(child instanceof Tab)
		{
			if (tabs.contains(child))
				return true;
			tabs.add((Tab) child);
			return false;
		}
		return false;
	}

	private record TabRect(int x, int y, int width, int height, int index) {}

	private TabRect[] tabRects()
	{
		var layout = getLayout();
		int count = tabs.size();
		if(count == 0)
			return new TabRect[0];

		int barHeight = Math.max(1, tabBarHeight.get());
		boolean horizontal = tabBarPosition.get() == YogaPhysicalEdge.TOP
			|| tabBarPosition.get() == YogaPhysicalEdge.BOTTOM;
		int availableWidth = Math.max(0, layout.innerWidth);
		int availableHeight = Math.max(0, layout.innerHeight);
		var result = new TabRect[count];

		if(horizontal)
		{
			int height = Math.min(barHeight, availableHeight + barHeight);
			height = Math.max(1, height);
			int y = tabBarPosition.get() == YogaPhysicalEdge.TOP
				? 0
				: layout.height - height;

			int equalWidth = availableWidth / count;
			int remainder = availableWidth - equalWidth * count;
			int x = layout.innerX - layout.x;
			for(int i = 0; i < count; i++)
			{
				int width = equalWidth + (i < remainder ? 1 : 0);
				width = clampWidth(width);
				result[i] = new TabRect(x, y, width, height, i);
				x += width;
			}
		}
		else
		{
			int width = Math.min(barHeight, availableWidth + barHeight);
			width = Math.max(1, width);
			int x = tabBarPosition.get() == YogaPhysicalEdge.LEFT
				? 0
				: layout.width - width;
			int equalHeight = availableHeight / count;
			int remainder = availableHeight - equalHeight * count;
			int y = layout.innerY - layout.y;
			for(int i = 0; i < count; i++)
			{
				int height = equalHeight + (i < remainder ? 1 : 0);
				result[i] = new TabRect(x, y, width, height, i);
				y += height;
			}
		}
		return result;
	}

	private int clampWidth(int width)
	{
		int min = Math.max(0, minTabWidth.get());
		int max = Math.max(min, maxTabWidth.get());
		return Math.clamp(width, min, max);
	}

	private TabRect findTabAt(float x, float y)
	{
		for(var rect : tabRects())
			if(x >= rect.x && x < rect.x + rect.width && y >= rect.y && y < rect.y + rect.height)
				return rect;
		return null;
	}
	@Override
	public boolean receivesInputEvents() { return true; }
	@Override
	public boolean isFocusable() { return true; }

	private Tab hoveredTab = null;
	@Override
	public void onMouseMove(MouseEvent event)
	{
		var rect = findTabAt(event.localX(), event.localY());
		hoveredTab = rect == null ? null : getTab(rect.index);
	}

	@Override
	public void onMouseExit(MouseEvent event)
	{
		hoveredTab = null;
	}

	@Override
	public void onClick(ClickEvent event)
	{
		if(event.button == MouseButton.LEFT)
		{
			var rect = findTabAt(event.localX(), event.localY());
			if(rect != null)
				currentTabIndex.set(rect.index);
		}
		event.consume();
	}

	@Override
	public void onKeyPress(KeyEvent event)
	{
		if(this.state.isFocused())
		{
			if(tabBarPosition.get() == YogaPhysicalEdge.TOP || tabBarPosition.get() == YogaPhysicalEdge.BOTTOM)
			{
				if (event.keyCode == ImportantKeys.LEFT)
				{
					currentTabIndex.set(Math.max(0, currentTabIndex.get() - 1));
					event.consume();
				} else if (event.keyCode == ImportantKeys.RIGHT)
				{
					currentTabIndex.set(Math.min(tabs.size() - 1, currentTabIndex.get() + 1));
					event.consume();
				}
			}
			else if(tabBarPosition.get() == YogaPhysicalEdge.LEFT || tabBarPosition.get() == YogaPhysicalEdge.RIGHT)
			{
				if (event.keyCode == ImportantKeys.UP)
				{
					currentTabIndex.set(Math.max(0, currentTabIndex.get() - 1));
					event.consume();
				} else if (event.keyCode == ImportantKeys.DOWN)
				{
					currentTabIndex.set(Math.min(tabs.size() - 1, currentTabIndex.get() + 1));
					event.consume();
				}
			}
		}
	}

	public static void register(Crafted crafted)
	{
		crafted.elements.registerElement(Tabs.class, "Tabs", Tabs::new);
		crafted.elements.registerElement(Tab.class, "Tab", Tab::new);
		crafted.renderers.registerRenderer(Tabs.class, "default", new DefaultTabsRenderer());
		crafted.renderers.registerRenderer(Tabs.class, "crafted", new CraftedTabsRenderer());
	}

	public static class DefaultTabsRenderer implements IRendersType<Tabs>
	{
		private static final int UNSELECTED_TEXT_OFFSET = 4;
		@Override
		public Class<?> getElementType() { return Tabs.class; }

		private final ITexture tab, tabDisabled, tabFocused, tabDisabledFocused;

		public DefaultTabsRenderer()
		{
			tab = Crafted.defaultInstance().textures.get("minecraft", "widget/tab_selected");
			tabDisabled = Crafted.defaultInstance().textures.get("minecraft", "widget/tab");
			tabFocused = Crafted.defaultInstance().textures.get("minecraft", "widget/tab_selected_highlighted");
			tabDisabledFocused = Crafted.defaultInstance().textures.get("minecraft", "widget/tab_highlighted");
		}

		@Override
		public void render(ICanvas canvas, Tabs element)
		{
			var layout = element.getLayout();
			canvas.beginTransform();
			canvas.translate(layout.x, layout.y);
			switch (element.tabBarPosition.get())
			{
				case LEFT, RIGHT -> renderVertical(canvas, element, element.tabBarPosition.get() == YogaPhysicalEdge.LEFT);
				case TOP, BOTTOM -> renderHorizontal(canvas, element, element.tabBarPosition.get() == YogaPhysicalEdge.BOTTOM);
			}
			canvas.endTransform();
		}

		@SuppressWarnings("SuspiciousNameCombination")
		private void renderVertical(ICanvas canvas, Tabs element, boolean isLeft)
		{
			for(var rect : element.tabRects())
			{
				var tab = element.getTab(rect.index);
				if(tab == null)
					continue;
				boolean selected = rect.index == element.currentTabIndex.get();
				boolean hovered = tab == element.hoveredTab;
				ITexture texture = selected
					? (hovered || element.state.isFocused() ? tabFocused : this.tab)
					: (hovered ? tabDisabledFocused : tabDisabled);

				canvas.beginTransform();
				canvas.translate(rect.x + rect.width / 2f, rect.y + rect.height / 2f);
				canvas.rotate(isLeft ? -90 : 90);
				canvas.translate(-rect.height / 2f, -rect.width / 2f);
				canvas.enableBlend();
				canvas.drawTexture(texture, Rect.posAndSize(0, 0, rect.height, rect.width));
				canvas.disableBlend();
				canvas.endTransform();
				drawVerticalCenteredText(canvas, element, tab, rect, isLeft, selected);
			}
		}

		private void renderHorizontal(ICanvas canvas, Tabs element, boolean isBottom)
		{
			for(var rect : element.tabRects())
			{
				var tab = element.getTab(rect.index);
				if(tab == null)
					continue;
				boolean selected = rect.index == element.currentTabIndex.get();
				boolean hovered = tab == element.hoveredTab;
				if(isBottom)
				{
					canvas.beginTransform();
					canvas.translate(rect.x, rect.y);
					canvas.rotate(180);
					canvas.translate(-rect.width, -rect.height);
					canvas.enableBlend();
					if(selected)
					{
						if(hovered || element.state.isFocused())
							canvas.drawTexture(tabFocused, Rect.posAndSize(0, 0, rect.width, rect.height));
						else
							canvas.drawTexture(this.tab, Rect.posAndSize(0, 0, rect.width, rect.height));
					}
					else
					{
						if(hovered)
							canvas.drawTexture(tabDisabledFocused, Rect.posAndSize(0, 0, rect.width, rect.height));
						else
							canvas.drawTexture(this.tabDisabled, Rect.posAndSize(0, 0, rect.width, rect.height));
					}
					canvas.disableBlend();
					canvas.endTransform();
				}
				else
				{
					canvas.enableBlend();
					if(selected)
					{
						if(hovered || element.state.isFocused())
							canvas.drawTexture(tabFocused, Rect.posAndSize(rect.x, rect.y, rect.width, rect.height));
						else
							canvas.drawTexture(this.tab, Rect.posAndSize(rect.x, rect.y, rect.width, rect.height));
					}
					else
					{
						if(hovered)
							canvas.drawTexture(tabDisabledFocused, Rect.posAndSize(rect.x, rect.y, rect.width, rect.height));
						else
							canvas.drawTexture(this.tabDisabled, Rect.posAndSize(rect.x, rect.y, rect.width, rect.height));
					}
					canvas.disableBlend();
				}
				drawCenteredText(canvas, element, tab, rect.x, rect.y, rect.width, rect.height, selected);
			}
		}

		private void drawCenteredText(ICanvas canvas, Tabs element, Tab tab, int x, int y, int width, int height, boolean selected)
		{
			var settings = element.ctx.crafted.defaultTextSettings;
			var textSize = settings.font().measureText(tab.title.get(), settings.size(), -1);
			int textX = (int)(x + (width - textSize.width()) / 2);
			int textY = (int)(y + (height - textSize.height()) / 2);
			if(!selected)
				textY += UNSELECTED_TEXT_OFFSET;
			canvas.drawText(tab.title.get(), textX, textY, settings, element.ctx.theme.onSurface());
		}

		private void drawVerticalCenteredText(ICanvas canvas, Tabs element, Tab tab, TabRect rect, boolean isLeft, boolean selected)
		{
			var settings = element.ctx.crafted.defaultTextSettings;
			var textSize = settings.font().measureText(tab.title.get(), settings.size(), -1);
			float offset = selected ? 0 : (isLeft ? UNSELECTED_TEXT_OFFSET : -UNSELECTED_TEXT_OFFSET);
			canvas.beginTransform();
			canvas.translate(rect.x + rect.width / 2f, rect.y + rect.height / 2f);
			canvas.rotate(isLeft ? -90 : 90);
			canvas.translate(-textSize.width() / 2f, -textSize.height() / 2f + offset);
			canvas.drawText(tab.title.get(), 0, 0, settings, element.ctx.theme.onSurface());
			canvas.endTransform();
		}
	}
	// TODO: Design a better look
	public static class CraftedTabsRenderer implements IRendersType<Tabs>
	{
		private static final int UNSELECTED_TEXT_OFFSET = 0;

		@Override
		public Class<?> getElementType() { return Tabs.class; }

		@Override
		public void render(ICanvas canvas, Tabs element)
		{
			var layout = element.getLayout();
			canvas.beginTransform();
			canvas.translate(layout.x, layout.y);
			switch (element.tabBarPosition.get())
			{
				case LEFT, RIGHT -> renderVertical(canvas, element, element.tabBarPosition.get() == YogaPhysicalEdge.LEFT);
				case TOP, BOTTOM -> renderHorizontal(canvas, element);
			}
			canvas.endTransform();
		}

		private void renderVertical(ICanvas canvas, Tabs element, boolean isLeft)
		{
			for(var rect : element.tabRects())
			{
				var tab = element.getTab(rect.index);
				if(tab == null)
					continue;
				boolean selected = rect.index == element.currentTabIndex.get();
				boolean hovered = tab == element.hoveredTab;
				Color color = selected
					? (hovered || element.state.isFocused() ? element.ctx.theme.primary() : Color.Gray)
					: (hovered ? Color.LightGray : Color.Gray);

				canvas.enableBlend();
				canvas.drawRect(rect.x, rect.y, rect.width, rect.height, color);
				canvas.disableBlend();

				drawVerticalCenteredText(canvas, element, tab, rect, isLeft, selected);
			}
		}

		private void renderHorizontal(ICanvas canvas, Tabs element)
		{
			for(var rect : element.tabRects())
			{
				var tab = element.getTab(rect.index);
				if(tab == null)
					continue;
				boolean selected = rect.index == element.currentTabIndex.get();
				boolean hovered = tab == element.hoveredTab;
				canvas.enableBlend();
				if(selected)
				{
					if(hovered || element.state.isFocused())
						canvas.drawRect(rect.x, rect.y, rect.width, rect.height, element.ctx.theme.primary());
					else
						canvas.drawRect(rect.x, rect.y, rect.width, rect.height, Color.Gray);
				}
				else
				{
					if(hovered)
						canvas.drawRect(rect.x, rect.y, rect.width, rect.height, Color.LightGray);
					else
						canvas.drawRect(rect.x, rect.y, rect.width, rect.height, Color.Gray);
				}
				canvas.disableBlend();
				drawCenteredText(canvas, element, tab, rect.x, rect.y, rect.width, rect.height, selected);
			}
		}

		private void drawCenteredText(ICanvas canvas, Tabs element, Tab tab, int x, int y, int width, int height, boolean selected)
		{
			var settings = element.ctx.crafted.defaultTextSettings;
			var textSize = settings.font().measureText(tab.title.get(), settings.size(), -1);
			int textX = (int)(x + (width - textSize.width()) / 2);
			int textY = (int)(y + (height - textSize.height()) / 2);
			if(!selected)
				textY += UNSELECTED_TEXT_OFFSET;
			canvas.drawText(tab.title.get(), textX, textY, settings, element.ctx.theme.onSurface());
		}

		private void drawVerticalCenteredText(ICanvas canvas, Tabs element, Tab tab, TabRect rect, boolean isLeft, boolean selected)
		{
			var settings = element.ctx.crafted.defaultTextSettings;
			var textSize = settings.font().measureText(tab.title.get(), settings.size(), -1);
			float offset = selected ? 0 : (isLeft ? UNSELECTED_TEXT_OFFSET : -UNSELECTED_TEXT_OFFSET);
			canvas.beginTransform();
			canvas.translate(rect.x + rect.width / 2f, rect.y + rect.height / 2f);
			canvas.rotate(isLeft ? -90 : 90);
			canvas.translate(-textSize.width() / 2f, -textSize.height() / 2f + offset);
			canvas.drawText(tab.title.get(), 0, 0, settings, element.ctx.theme.onSurface());
			canvas.endTransform();
		}
	}

	@DefineElement("Tab")
	@Description("A tab in a Tabs element")
	public static class Tab extends ScrollView
	{
		private CraftedElement[] content;

		public final Prop<IText> title = registerProp(IText.class, "title", IText.literal("Tab"), Parsers::parseText);

		public Tab(CraftedContext ctx, Map<String, String> attributes)
		{
			super(ctx, attributes);
		}

		@Override
		protected YogaStyle initLayoutNode(YogaStyle style, Map<String, String> attributes)
		{
			style.setFlexGrow(FloatOptional.of(1));
			style.setFlexShrink(FloatOptional.of(1));
			return style;
		}
	}
}
