package dev.zeddevstuff.crafted.ui;

import dev.zeddevstuff.crafted.IProp;
import dev.zeddevstuff.crafted.Prop;
import dev.zeddevstuff.crafted.ProxyProp;
import dev.zeddevstuff.crafted.annotations.Description;
import dev.zeddevstuff.crafted.parsing.Parsers;
import dev.zeddevstuff.crafted.platform.ICanvas;
import dev.zeddevstuff.crafted.rendering.IRenders;
import dev.zeddevstuff.crafted.types.EdgeInsets;
import dev.zeddevstuff.crafted.types.StyleLength2;
import dev.zeddevstuff.crafted.types.StyleLength4;
import dev.zeddevstuff.crafted.types.StyleSizeLength2;
import org.appliedenergistics.yoga.*;
import org.appliedenergistics.yoga.style.StyleLength;
import org.appliedenergistics.yoga.style.StyleSizeLength;
import org.appliedenergistics.yoga.style.YogaStyle;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class CraftedElement
{
	private final CraftedContext ctx;
	private final YogaNode node = new YogaNode();

	protected CraftedElement parent;
	public CraftedElement getParent() { return parent; }
	protected final List<CraftedElement> children = new ArrayList<>();

	private LayoutResult layout;
	public LayoutResult getLayout() { return layout; }

	protected final HashMap<String, IProp<?>> props = new HashMap<>();

	/**
	 * Registers a property for this element. <br>
	 * This will allow the property to be set via attributes in the XML.
	 * @return The registered property.
	 */
	protected <T, TProp extends IProp<T>> TProp registerProp(TProp prop)
	{
		if(props.containsKey(prop.getName()))
			throw new IllegalArgumentException("Property with name '" + prop.getName() + "' is already registered for this element.");
		props.put(prop.getName(), prop);
		return prop;
	}
	/**
	 * Registers a property for this element. <br>
	 * This will allow the property to be set via attributes in the XML.
	 * @return The registered property.
	 */
	protected <T> Prop<T> registerProp(String name, T value, BiFunction<String, T, T> parser)
	{
		if(props.containsKey(name))
			throw new IllegalArgumentException("Property with name '" + name + "' is already registered for this element.");
		var prop = new Prop<>(name, value, parser);
		props.put(name, prop);
		return prop;
	}
	/**
	 * Registers a property for this element. <br>
	 * This will allow the property to be set via attributes in the XML.
	 * @return The registered property.
	 */
	protected <T> Prop<T> registerProp(Class<T> type, String name, T value, BiFunction<String, T, T> parser)
	{
		if(props.containsKey(name))
			throw new IllegalArgumentException("Property with name '" + name + "' is already registered for this element.");
		var prop = new Prop<>(type, name, value, parser);
		props.put(name, prop);
		return prop;
	}
	/**
	 * Registers a property for this element. <br>
	 * This will allow the property to be set via attributes in the XML.
	 * @return The registered property.
	 */
	protected <T> ProxyProp<T> registerProp(Class<T> type, String name, Supplier<T> getter, Consumer<T> setter, BiFunction<String, T, T> parser)
	{
		if(props.containsKey(name))
			throw new IllegalArgumentException("Property with name '" + name + "' is already registered for this element.");
		var prop = new ProxyProp<>(type, name, setter, getter, parser);
		props.put(name, prop);
		return prop;
	}
	@Description("Unique identifier for this element")
	public final Prop<String> id = registerProp("id", "", Parsers::parseString);
	public final Prop<Integer> zIndex = registerProp("zIndex", 0, Parsers::parseInt);

	public final ProxyProp<YogaDirection> direction = registerProp(YogaDirection.class, "direction", node::getLayoutDirection, node::setDirection, Parsers::parseYogaDirection);
	public final ProxyProp<YogaFlexDirection> flexDirection = registerProp(YogaFlexDirection.class, "flexDirection", node::getFlexDirection, node::setFlexDirection, Parsers::parseYogaFlexDirection);
	public final ProxyProp<YogaJustify> justifyContent = registerProp(YogaJustify.class, "justifyContent", node::getJustifyContent, node::setJustifyContent, Parsers::parseYogaJustify);
	public final ProxyProp<YogaAlign> alignContent = registerProp(YogaAlign.class, "alignContent", node::getAlignContent, node::setAlignContent, Parsers::parseYogaAlign);
	public final ProxyProp<YogaAlign> alignItems = registerProp(YogaAlign.class, "alignItems", node::getAlignItems, node::setAlignItems, Parsers::parseYogaAlign);
	public final ProxyProp<YogaAlign> alignSelf = registerProp(YogaAlign.class, "alignSelf", node::getAlignSelf, node::setAlignSelf, Parsers::parseYogaAlign);
	public final ProxyProp<YogaPositionType> positionType = registerProp(YogaPositionType.class, "positionType", node::getPositionType, node::setPositionType, Parsers::parseYogaPositionType);
	public final ProxyProp<YogaWrap> flexWrap = registerProp(YogaWrap.class, "flexWrap", node::getWrap, node::setWrap, Parsers::parseYogaWrap);
	public final ProxyProp<YogaOverflow> overflow = registerProp(YogaOverflow.class, "overflow", node::getOverflow, node::setOverflow, Parsers::parseYogaOverflow);
	public final ProxyProp<YogaDisplay> display = registerProp(YogaDisplay.class, "display", node::getDisplay, node::setDisplay, Parsers::parseYogaDisplay);
	public final ProxyProp<YogaBoxSizing> boxSizing = registerProp(YogaBoxSizing.class, "boxSizing", node::getBoxSizing, node::setBoxSizing, Parsers::parseYogaBoxSizing);

	public final ProxyProp<Float> flex = registerProp(Float.class, "flex", node::getFlex, node::setFlex, Parsers::parseFloat);
	public final ProxyProp<Float> flexGrow = registerProp(Float.class, "flexGrow", node::getFlexGrow, node::setFlexGrow, Parsers::parseFloat);
	public final ProxyProp<Float> flexShrink = registerProp(Float.class, "flexShrink", node::getFlexShrink, node::setFlexShrink, Parsers::parseFloat);
	public final ProxyProp<StyleSizeLength> flexBasis = registerProp(StyleSizeLength.class, "flexBasis", () -> node.getStyle().getFlexBasis(), val -> node.getStyle().setFlexBasis(val), Parsers::parseStyleSizeLength);

	public final ProxyProp<StyleLength4> margin = registerProp(StyleLength4.class, "margin",
		() -> {
			var style = node.getStyle();
			return new StyleLength4(style.getMargin(YogaEdge.TOP), style.getMargin(YogaEdge.RIGHT), style.getMargin(YogaEdge.BOTTOM), style.getMargin(YogaEdge.LEFT));
		},
		val -> {
			if (val == null)
				val = new StyleLength4(StyleLength.ofAuto(), StyleLength.ofAuto(), StyleLength.ofAuto(), StyleLength.ofAuto());
			node.setMargin(YogaEdge.TOP, val.top());
			node.setMargin(YogaEdge.RIGHT, val.right());
			node.setMargin(YogaEdge.BOTTOM, val.bottom());
			node.setMargin(YogaEdge.LEFT, val.left());
		}, Parsers::parseStyleLength4);
	public final ProxyProp<StyleLength> marginTop = registerProp(StyleLength.class, "marginTop", () -> node.getStyle().getMargin(YogaEdge.TOP), val -> node.setMargin(YogaEdge.TOP, val), Parsers::parseStyleLength);
	public final ProxyProp<StyleLength> marginRight = registerProp(StyleLength.class, "marginRight", () -> node.getStyle().getMargin(YogaEdge.RIGHT), val -> node.setMargin(YogaEdge.RIGHT, val), Parsers::parseStyleLength);
	public final ProxyProp<StyleLength> marginBottom = registerProp(StyleLength.class, "marginBottom", () -> node.getStyle().getMargin(YogaEdge.BOTTOM), val -> node.setMargin(YogaEdge.BOTTOM, val), Parsers::parseStyleLength);
	public final ProxyProp<StyleLength> marginLeft = registerProp(StyleLength.class, "marginLeft", () -> node.getStyle().getMargin(YogaEdge.LEFT), val -> node.setMargin(YogaEdge.LEFT, val), Parsers::parseStyleLength);

	public final ProxyProp<StyleLength4> position = registerProp(StyleLength4.class, "position",
		() -> {
			var style = node.getStyle();
			return new StyleLength4(style.getPosition(YogaEdge.TOP), style.getPosition(YogaEdge.RIGHT), style.getPosition(YogaEdge.BOTTOM), style.getPosition(YogaEdge.LEFT));
		},
		val -> {
			if (val == null)
				val = new StyleLength4(StyleLength.ofAuto(), StyleLength.ofAuto(), StyleLength.ofAuto(), StyleLength.ofAuto());
			node.setPosition(YogaEdge.TOP, val.top());
			node.setPosition(YogaEdge.RIGHT, val.right());
			node.setPosition(YogaEdge.BOTTOM, val.bottom());
			node.setPosition(YogaEdge.LEFT, val.left());
		}, Parsers::parseStyleLength4);
	public final ProxyProp<StyleLength> top = registerProp(StyleLength.class, "top", () -> node.getStyle().getPosition(YogaEdge.TOP), val -> node.setPosition(YogaEdge.TOP, val), Parsers::parseStyleLength);
	public final ProxyProp<StyleLength> right = registerProp(StyleLength.class, "right", () -> node.getStyle().getPosition(YogaEdge.RIGHT), val -> node.setPosition(YogaEdge.RIGHT, val), Parsers::parseStyleLength);
	public final ProxyProp<StyleLength> bottom = registerProp(StyleLength.class, "bottom", () -> node.getStyle().getPosition(YogaEdge.BOTTOM), val -> node.setPosition(YogaEdge.BOTTOM, val), Parsers::parseStyleLength);
	public final ProxyProp<StyleLength> left = registerProp(StyleLength.class, "left", () -> node.getStyle().getPosition(YogaEdge.LEFT), val -> node.setPosition(YogaEdge.LEFT, val), Parsers::parseStyleLength);

	public final ProxyProp<StyleLength4> padding = registerProp(StyleLength4.class, "padding",
		() -> {
			var style = node.getStyle();
			return new StyleLength4(style.getPadding(YogaEdge.TOP), style.getPadding(YogaEdge.RIGHT), style.getPadding(YogaEdge.BOTTOM), style.getPadding(YogaEdge.LEFT));
		},
		val -> {
			if (val == null)
				val = new StyleLength4(StyleLength.ofAuto(), StyleLength.ofAuto(), StyleLength.ofAuto(), StyleLength.ofAuto());
			node.setPadding(YogaEdge.TOP, val.top());
			node.setPadding(YogaEdge.RIGHT, val.right());
			node.setPadding(YogaEdge.BOTTOM, val.bottom());
			node.setPadding(YogaEdge.LEFT, val.left());
		}, Parsers::parseStyleLength4);
	public final ProxyProp<StyleLength> paddingTop = registerProp(StyleLength.class, "paddingTop", () -> node.getStyle().getPadding(YogaEdge.TOP), val -> node.setPadding(YogaEdge.TOP, val), Parsers::parseStyleLength);
	public final ProxyProp<StyleLength> paddingRight = registerProp(StyleLength.class, "paddingRight", () -> node.getStyle().getPadding(YogaEdge.RIGHT), val -> node.setPadding(YogaEdge.RIGHT, val), Parsers::parseStyleLength);
	public final ProxyProp<StyleLength> paddingBottom = registerProp(StyleLength.class, "paddingBottom", () -> node.getStyle().getPadding(YogaEdge.BOTTOM), val -> node.setPadding(YogaEdge.BOTTOM, val), Parsers::parseStyleLength);
	public final ProxyProp<StyleLength> paddingLeft = registerProp(StyleLength.class, "paddingLeft", () -> node.getStyle().getPadding(YogaEdge.LEFT), val -> node.setPadding(YogaEdge.LEFT, val), Parsers::parseStyleLength);

	public final ProxyProp<StyleLength4> border = registerProp(StyleLength4.class, "border",
		() -> {
			var style = node.getStyle();
			return new StyleLength4(style.getBorder(YogaEdge.TOP), style.getBorder(YogaEdge.RIGHT), style.getBorder(YogaEdge.BOTTOM), style.getBorder(YogaEdge.LEFT));
		},
		val -> {
			if (val == null)
				val = new StyleLength4(StyleLength.ofAuto(), StyleLength.ofAuto(), StyleLength.ofAuto(), StyleLength.ofAuto());
			var style = node.getStyle();
			style.setBorder(YogaEdge.TOP, val.top());
			style.setBorder(YogaEdge.RIGHT, val.right());
			style.setBorder(YogaEdge.BOTTOM, val.bottom());
			style.setBorder(YogaEdge.LEFT, val.left());
		}, Parsers::parseStyleLength4);
	public final ProxyProp<StyleLength> borderTop = registerProp(StyleLength.class, "borderTop", () -> node.getStyle().getBorder(YogaEdge.TOP), val -> node.getStyle().setBorder(YogaEdge.TOP, val), Parsers::parseStyleLength);
	public final ProxyProp<StyleLength> borderRight = registerProp(StyleLength.class, "borderRight", () -> node.getStyle().getBorder(YogaEdge.RIGHT), val -> node.getStyle().setBorder(YogaEdge.RIGHT, val), Parsers::parseStyleLength);
	public final ProxyProp<StyleLength> borderBottom = registerProp(StyleLength.class, "borderBottom", () -> node.getStyle().getBorder(YogaEdge.BOTTOM), val -> node.getStyle().setBorder(YogaEdge.BOTTOM, val), Parsers::parseStyleLength);
	public final ProxyProp<StyleLength> borderLeft = registerProp(StyleLength.class, "borderLeft", () -> node.getStyle().getBorder(YogaEdge.LEFT), val -> node.getStyle().setBorder(YogaEdge.LEFT, val), Parsers::parseStyleLength);

	public final ProxyProp<StyleLength2> gap = registerProp(StyleLength2.class, "gap",
		() -> new StyleLength2(node.getStyle().getGap(YogaGutter.ROW), node.getStyle().getGap(YogaGutter.COLUMN)),
		val -> {
			if (val == null)
				val = new StyleLength2(StyleLength.ofAuto(), StyleLength.ofAuto());
			node.setGap(YogaGutter.ROW, val.x());
			node.setGap(YogaGutter.COLUMN, val.y());
		}, Parsers::parseStyleLength2);
	public final ProxyProp<StyleLength> rowGap = registerProp(StyleLength.class, "rowGap", () -> node.getStyle().getGap(YogaGutter.ROW), val -> node.setGap(YogaGutter.ROW, val), Parsers::parseStyleLength);
	public final ProxyProp<StyleLength> columnGap = registerProp(StyleLength.class, "columnGap", () -> node.getStyle().getGap(YogaGutter.COLUMN), val -> node.setGap(YogaGutter.COLUMN, val), Parsers::parseStyleLength);

	public final ProxyProp<StyleSizeLength2> size = registerProp(StyleSizeLength2.class, "size",
		() -> new StyleSizeLength2(node.getStyle().getDimension(YogaDimension.WIDTH), node.getStyle().getDimension(YogaDimension.HEIGHT)),
		val -> {
			if (val == null)
				val = new StyleSizeLength2(StyleSizeLength.ofAuto(), StyleSizeLength.ofAuto());
			node.setWidth(val.width());
			node.setHeight(val.height());
		}, Parsers::parseStyleSizeLength2);
	public final ProxyProp<StyleSizeLength> width = registerProp(StyleSizeLength.class, "width", () -> node.getStyle().getDimension(YogaDimension.WIDTH), node::setWidth, Parsers::parseStyleSizeLength);
	public final ProxyProp<StyleSizeLength> height = registerProp(StyleSizeLength.class, "height", () -> node.getStyle().getDimension(YogaDimension.HEIGHT), node::setHeight, Parsers::parseStyleSizeLength);

	public final ProxyProp<StyleSizeLength2> minSize = registerProp(StyleSizeLength2.class, "minSize",
		() -> new StyleSizeLength2(node.getStyle().getMinDimension(YogaDimension.WIDTH), node.getStyle().getMinDimension(YogaDimension.HEIGHT)),
		val -> {
			if (val == null)
				val = new StyleSizeLength2(StyleSizeLength.ofAuto(), StyleSizeLength.ofAuto());
			node.setMinWidth(val.width());
			node.setMinHeight(val.height());
		}, Parsers::parseStyleSizeLength2);
	public final ProxyProp<StyleSizeLength> minWidth = registerProp(StyleSizeLength.class, "minWidth", () -> node.getStyle().getMinDimension(YogaDimension.WIDTH), node::setMinWidth, Parsers::parseStyleSizeLength);
	public final ProxyProp<StyleSizeLength> minHeight = registerProp(StyleSizeLength.class, "minHeight", () -> node.getStyle().getMinDimension(YogaDimension.HEIGHT), node::setMinHeight, Parsers::parseStyleSizeLength);

	public final ProxyProp<StyleSizeLength2> maxSize = registerProp(StyleSizeLength2.class, "maxSize",
		() -> new StyleSizeLength2(node.getStyle().getMaxDimension(YogaDimension.WIDTH), node.getStyle().getMaxDimension(YogaDimension.HEIGHT)),
		val -> {
			if (val == null)
				val = new StyleSizeLength2(StyleSizeLength.ofAuto(), StyleSizeLength.ofAuto());
			node.setMaxWidth(val.width());
			node.setMaxHeight(val.height());
		}, Parsers::parseStyleSizeLength2);
	public final ProxyProp<StyleSizeLength> maxWidth = registerProp(StyleSizeLength.class, "maxWidth", () -> node.getStyle().getMaxDimension(YogaDimension.WIDTH), node::setMaxWidth, Parsers::parseStyleSizeLength);
	public final ProxyProp<StyleSizeLength> maxHeight = registerProp(StyleSizeLength.class, "maxHeight", () -> node.getStyle().getMaxDimension(YogaDimension.HEIGHT), node::setMaxHeight, Parsers::parseStyleSizeLength);

	private IRenders<?> renderer;

	public CraftedElement(CraftedContext ctx, Map<String, String> attributes)
	{
		this.ctx = ctx;
		this.renderer = ctx.crafted.renderers.getRenderer(this.getClass(), attributes.getOrDefault("renderer", "default"));
		this.node.setStyle(initLayoutNode(node.getStyle(), attributes));
	}

	void parseAttributes(Map<String, String> attributes)
	{
		for(var entry : attributes.entrySet())
		{
			var prop = props.get(entry.getKey());
			if(prop != null)
				prop.setFromString(entry.getValue());
		}
	}

	/**
	 * Use this method for initializing the layout node with the given attributes.
	 */
	protected abstract YogaStyle initLayoutNode(YogaStyle style, Map<String, String> attributes);

	public void setParent(CraftedElement parent)
	{
		if(this.parent != null && this.parent.children.contains(this))
		{
			this.parent.children.remove(this);
			this.parent.node.removeChild(this.node);
		}
	}
	public void addChild(CraftedElement child)
	{
		if(child == null)
			return;
		if(children.contains(child))
			return;
		children.add(child);
		child.parent = this;
		node.addChildAt(child.node, node.getChildCount());
	}
	public void removeChild(CraftedElement child)
	{
		if(child == null)
			return;
		if(!children.contains(child))
			return;
		children.remove(child);
		child.parent = null;
		node.removeChild(child.node);
	}

	public void setRenderer(String renderer)
	{
		this.renderer = ctx.crafted.renderers.getRendererOrDefault(this.getClass(), renderer);
	}
	public void setRendererRecursive(String renderer)
	{
		setRenderer(renderer);
		for(var child : children)
			child.setRendererRecursive(renderer);
	}

	public void layoutTree(float width, float height)
	{
		node.calculateLayout(width, height);
		layout = new LayoutResult(this);
		for(var child : children)
			child.layoutElement();

	}
	private void layoutElement()
	{
		if(parent == null && node.hasNewLayout())
		{
			layout = new LayoutResult(this);
			node.markLayoutSeen();
		}
		else if(node.hasNewLayout())
		{
			layout = new LayoutResult(this, parent.layout);
			node.markLayoutSeen();
		}
		for(var child : children)
			child.layoutElement();
	}
	public void renderTree(ICanvas canvas)
	{
		List<RenderCommand> renderCommands = new ArrayList<>();
		collectRenderCommands(renderCommands);
		for(var cmd : renderCommands)
			cmd.Execute(canvas);
	}
	private void collectRenderCommands(List<RenderCommand> list)
	{
//		if (hidden())
//			return;
//		ensureRenderersRegistered();
		if(renderer != null)
			list.add(new RenderCommand(this, RenderCommand.Phase.RENDER));
		var sortedChildren = children.stream().sorted(Comparator.comparingInt(a -> a.zIndex.get())).toArray(CraftedElement[]::new);
		for(var child : sortedChildren)
			child.collectRenderCommands(list);
		if(renderer != null)
			list.add(new RenderCommand(this, RenderCommand.Phase.AFTER_RENDER));
	}

	private record RenderCommand(CraftedElement element, Phase commandPhase)
	{

		public void Execute(ICanvas canvas)
		{
			switch (commandPhase)
			{
				case Phase.RENDER:
					element.renderer.forceRender(canvas, element);
					break;
				case Phase.AFTER_RENDER:
					element.renderer.forceAfterRender(canvas, element);
					break;
			}
		}

		public enum Phase
		{
			RENDER,
			AFTER_RENDER
		}
	}
	public class LayoutResult
	{
		public final int width;
		public final int innerWidth;
		public final int height;
		public final int innerHeight;
		public final int x;
		public final int innerX;
		public final int y;
		public final int innerY;

		public final EdgeInsets<Integer> margin;
		public final EdgeInsets<Integer> padding;
		public final EdgeInsets<Integer> border;

		public LayoutResult(int width, int innerWidth, int height, int innerHeight, int x, int innerX, int y, int innerY, EdgeInsets<Integer> margin, EdgeInsets<Integer> padding, EdgeInsets<Integer> border)
		{
			this.width = width;
			this.innerWidth = innerWidth;
			this.height = height;
			this.innerHeight = innerHeight;
			this.x = x;
			this.innerX = innerX;
			this.y = y;
			this.innerY = innerY;
			this.margin = margin;
			this.padding = padding;
			this.border = border;
		}
		private LayoutResult(CraftedElement element)
		{
			this.x = (int)element.node.getLayoutX();
			this.y = (int)element.node.getLayoutY();
			this.width = (int)element.node.getLayoutWidth();
			this.height = (int)element.node.getLayoutHeight();
			this.margin = new EdgeInsets<>((int)element.node.getLayoutMargin(YogaEdge.TOP), (int)element.node.getLayoutMargin(YogaEdge.RIGHT), (int)element.node.getLayoutMargin(YogaEdge.BOTTOM), (int)element.node.getLayoutMargin(YogaEdge.LEFT));
			this.padding = new EdgeInsets<>((int)element.node.getLayoutPadding(YogaEdge.TOP), (int)element.node.getLayoutPadding(YogaEdge.RIGHT), (int)element.node.getLayoutPadding(YogaEdge.BOTTOM), (int)element.node.getLayoutPadding(YogaEdge.LEFT));
			this.border = new EdgeInsets<>((int)element.node.getLayoutBorder(YogaEdge.TOP), (int)element.node.getLayoutBorder(YogaEdge.RIGHT), (int)element.node.getLayoutBorder(YogaEdge.BOTTOM), (int)element.node.getLayoutBorder(YogaEdge.LEFT));
			this.innerX = x + margin.left() + padding.left() + border.left();
			this.innerY = y + margin.top() + padding.top() + border.top();
			this.innerWidth = width - margin.left() - margin.right() - padding.left() -padding.right() - border.left() - border.right();
			this.innerHeight = height - margin.top() - margin.bottom() - padding.top() - padding.bottom() - border.top() - border.bottom();
		}
		private LayoutResult(CraftedElement element, LayoutResult container)
		{
			this.x = container.innerX + (int)element.node.getLayoutX();
			this.y = container.innerY + (int)element.node.getLayoutY();
			this.width = (int)element.node.getLayoutWidth();
			this.height = (int)element.node.getLayoutHeight();
			this.margin = new EdgeInsets<>((int)element.node.getLayoutMargin(YogaEdge.TOP), (int)element.node.getLayoutMargin(YogaEdge.RIGHT), (int)element.node.getLayoutMargin(YogaEdge.BOTTOM), (int)element.node.getLayoutMargin(YogaEdge.LEFT));
			this.padding = new EdgeInsets<>((int)element.node.getLayoutPadding(YogaEdge.TOP), (int)element.node.getLayoutPadding(YogaEdge.RIGHT), (int)element.node.getLayoutPadding(YogaEdge.BOTTOM), (int)element.node.getLayoutPadding(YogaEdge.LEFT));
			this.border = new EdgeInsets<>((int)element.node.getLayoutBorder(YogaEdge.TOP), (int)element.node.getLayoutBorder(YogaEdge.RIGHT), (int)element.node.getLayoutBorder(YogaEdge.BOTTOM), (int)element.node.getLayoutBorder(YogaEdge.LEFT));
			this.innerX = x + margin.left() + padding.left() + border.left();
			this.innerY = y + margin.top() + padding.top() + border.top();
			this.innerWidth = width - margin.left() - margin.right() - padding.left() -padding.right() - border.left() - border.right();
			this.innerHeight = height - margin.top() - margin.bottom() - padding.top() - padding.bottom() - border.top() - border.bottom();
		}
	}
}
