package dev.zeddevstuff.crafted.ui;

import dev.zeddevstuff.crafted.IProp;
import dev.zeddevstuff.crafted.Prop;
import dev.zeddevstuff.crafted.ProxyProp;
import dev.zeddevstuff.crafted.annotations.Description;
import dev.zeddevstuff.crafted.input.InteractionState;
import dev.zeddevstuff.crafted.input.events.*;
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

import javax.annotation.Nonnull;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

public abstract class CraftedElement
{
	// Just a hack. Don't think about it too much.
	private final static Object INTERNAL_KEY = new Object();
	public final CraftedContext ctx;
	protected final YogaNode node = new YogaNode();

	protected CraftedElement parent;
	public CraftedElement getParent() { return parent; }
	protected final List<CraftedElement> children = new ArrayList<>();
	private CraftedElement[] sortedChildrenCache = null;
	private CraftedElement[] getSortedChildrenCache()
	{
		if(sortedChildrenCache == null)
			sortedChildrenCache = children.stream()
				.sorted(Comparator.<CraftedElement>comparingInt(a -> a.zIndex.get()).reversed())
					.toArray(CraftedElement[]::new);
		return sortedChildrenCache;
	}
	private CraftedElement[] renderSortedChildrenCache = null;
	private CraftedElement[] getRenderSortedChildrenCache()
	{
		if(renderSortedChildrenCache == null)
			renderSortedChildrenCache = children.stream()
				.sorted(Comparator.<CraftedElement>comparingInt(a -> a.zIndex.get()))
					.toArray(CraftedElement[]::new);
		return renderSortedChildrenCache;
	}

	public final InteractionState state = new InteractionState(INTERNAL_KEY);

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

	@SuppressWarnings("unchecked")
	protected <T, TProp extends IProp<T>> TProp registerProp(Class<T> type, String name, BiFunction<String, T, T> parser)
	{
		if(props.containsKey(name))
			throw new IllegalArgumentException("Property with name '" + name + "' is already registered for this element.");
		var prop = new Prop<>(type, name, null, parser);
		props.put(name, prop);
		return (TProp) prop;
	}

	/**
	 * Registers a property for this element. <br>
	 * This will allow the property to be set via attributes in the XML. <br>
	 * Note that this overload does not allow a null default value, as it cannot infer the type of the property from a null value.
	 * @return The registered property.
	 */
	protected <T> Prop<T> registerProp(String name, @Nonnull T value, BiFunction<String, T, T> parser)
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
	public final ProxyProp<Boolean> disabled = registerProp(Boolean.class, "disabled", state::isDisabled, val -> state.set(INTERNAL_KEY, InteractionState.DISABLED, val), Parsers::parseBoolean);

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

	public final Prop<IFocusEventHandler> onFocusGained = registerProp(IFocusEventHandler.class, "onFocusGained", this::getFocusCallback);
	public final Prop<IFocusEventHandler> onFocusLost = registerProp(IFocusEventHandler.class, "onFocusLost", this::getFocusCallback);

	public final Prop<IMouseEventHandler> onMouseMove = registerProp(IMouseEventHandler.class, "onMouseMove", this::getMouseCallback);
	public final Prop<IMouseEventHandler> onMouseScroll = registerProp(IMouseEventHandler.class, "onMouseScroll", this::getMouseCallback);
	public final Prop<IMouseEventHandler> onMouseEnter = registerProp(IMouseEventHandler.class, "onMouseEnter", this::getMouseCallback);
	public final Prop<IMouseEventHandler> onMouseExit = registerProp(IMouseEventHandler.class, "onMouseExit", this::getMouseCallback);
	public final Prop<IMouseEventHandler> onMousePress = registerProp(IMouseEventHandler.class, "onMousePress", this::getMouseCallback);
	public final Prop<IMouseEventHandler> onMouseRelease = registerProp(IMouseEventHandler.class, "onMouseRelease", this::getMouseCallback);

	public final Prop<IClickEventHandler> onClick = registerProp(IClickEventHandler.class, "onClick", this::getClickCallback);

	public final Prop<IKeyEventHandler> onKeyPress = registerProp(IKeyEventHandler.class, "onKeyPress", this::getKeyCallback);
	public final Prop<IKeyEventHandler> onKeyRelease = registerProp(IKeyEventHandler.class, "onKeyRelease", this::getKeyCallback);
	public final Prop<IKeyEventHandler> onKeyType = registerProp(IKeyEventHandler.class, "onKeyType", this::getKeyCallback);

	private IRenders<?> renderer;

	public CraftedElement(CraftedContext ctx, Map<String, String> attributes)
	{
		this.ctx = ctx;
		this.renderer = ctx.crafted.renderers.getRenderer(this.getClass(), attributes.getOrDefault("renderer", "default"));
		this.node.setStyle(initLayoutNode(node.getStyle(), attributes));
		zIndex.addListener((prop, oldValue, newValue) -> {
			if(parent != null)
				parent.udpateSortedChildrenCaches();
		});
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

	IFocusEventHandler getFocusCallback(String name, IFocusEventHandler defaultValue)
	{
		return ctx.getCallback(IFocusEventHandler.class, name);
	}
	IMouseEventHandler getMouseCallback(String name, IMouseEventHandler defaultValue)
	{
		return ctx.getCallback(IMouseEventHandler.class, name);
	}
	IClickEventHandler getClickCallback(String name, IClickEventHandler defaultValue)
	{
		return ctx.getCallback(IClickEventHandler.class, name);
	}
	IKeyEventHandler getKeyCallback(String name, IKeyEventHandler defaultValue)
	{
		return ctx.getCallback(IKeyEventHandler.class, name);
	}

	/**
	 * Override this method to initialize the layout node for this element with non default values.
	 */
	protected YogaStyle initLayoutNode(YogaStyle style, Map<String, String> attributes) { return style; }
	public boolean contains(float x, float y)
	{
		if(overflow.get() == YogaOverflow.HIDDEN)
			return x >= layout.x && x <= layout.x + layout.width && y >= layout.y && y <= layout.y + layout.height;
		else
			// TODO: Figure out the best way to do this
			return x >= layout.x && x <= layout.x + layout.width && y >= layout.y && y <= layout.y + layout.height;
	}

	// Thank you Wulferis
	public CraftedElement findTopmostAt(float x, float y)
	{
		if (!contains(x, y)) // Clip parent, so even the one calling this clips and doesnt get its clip/bounds ignored
			return null;

		// check each child, starting with the topmost one
		// don't filter out children that don't receive input events themselves they may still contain children that do
		for (var child : getSortedChildrenCache())
		{
			// recursively search the child's subtree for the topmost input target
			// this will perform its own bounds check before searching deeper
			var target = child.findTopmostAt(x, y);

			// return immediately when a valid target is found
			// since children are sorted by Z-index, this is the topmost valid target
			if (target != null)
				return target;
		}

		// no child contained a valid input target
		// fall back to this element if it receives input events
		// otherwise, return null so the caller can check another child
		return receivesInputEvents() ? this : null;
	}
	private void udpateSortedChildrenCaches()
	{
		sortedChildrenCache = children.stream()
			.sorted(Comparator.<CraftedElement>comparingInt(a -> a.zIndex.get()).reversed())
			.toArray(CraftedElement[]::new);
		renderSortedChildrenCache = children.stream()
			.sorted(Comparator.<CraftedElement>comparingInt(a -> a.zIndex.get()))
			.toArray(CraftedElement[]::new);
		if(parent != null)
			parent.udpateSortedChildrenCaches();
	}

	public CraftedElement getElementById(String id)
	{
		if(this.id.get().equals(id))
			return this;
		for(var child : children)
		{
			var result = child.getElementById(id);
			if(result != null)
				return result;
		}
		return null;
	}
	public CraftedElement[] findElements(Predicate<CraftedElement> predicate)
	{
		List<CraftedElement> results = new ArrayList<>();
		if(predicate.test(this))
			results.add(this);
		for(var child : children)
			results.addAll(Arrays.asList(child.findElements(predicate)));
		return results.toArray(new CraftedElement[0]);
	}

	public void setParent(CraftedElement parent)
	{
		if(this.parent != null)
			this.parent.removeChild(this);
		parent.addChild(this);
	}
	public void addChild(CraftedElement child)
	{
		if(child == null)
			return;
		if(children.contains(child))
			return;
		if(child.parent != null)
			child.parent.removeChild(child);
		children.add(child);
		child.parent = this;
		node.addChildAt(child.node, node.getChildCount());
	}
	public void addChildren(Collection<CraftedElement> children)
	{
		for(var child : children)
			addChild(child);
	}
	public void addChildren(CraftedElement... children)
	{
		for(var child : children)
			addChild(child);
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
	public void removeChildren(Collection<CraftedElement> children)
	{
		for(var child : children)
			removeChild(child);
	}
	public void removeChildren(CraftedElement... children)
	{
		for(var child : children)
			removeChild(child);
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

	public final void layoutTree(float width, float height)
	{
		node.calculateLayout(width, height);
		layout = new LayoutResult(this);
		for(var child : children)
			child.layoutElement();
	}
	public final void refreshLayoutTree()
	{
		refreshLayout();
		for(var child : children)
			child.refreshLayoutTree();
	}

	protected void refreshLayout() {}

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
		if(renderer != null)
			list.add(new RenderCommand(this, RenderCommand.Phase.RENDER));
//		var sortedChildren = children.stream()
//			.sorted(Comparator.comparingInt(a -> a.zIndex.get()))
//			.toArray(CraftedElement[]::new);
		for(var child : getRenderSortedChildrenCache())
			child.collectRenderCommands(list);
		if(renderer != null)
			list.add(new RenderCommand(this, RenderCommand.Phase.AFTER_RENDER));
	}

	/**
	 * Override this method to indicate that this element should receive input events. <br>
	 * By default, elements do not receive input events.
	 */
	public boolean receivesInputEvents() { return false; }
	public boolean isFocusable() { return false; }

	public boolean capturesKeyboardWhenFocused() { return false; }

	public void handleFocusGained(FocusEvent event)
	{
		if(!receivesInputEvents())
			return;
		onFocusGained(event);
		var callback = onFocusGained.get();
		if(callback != null)
			callback.onEvent(event);
	}
	public void onFocusGained(FocusEvent event) {}

	public void handleFocusLost(FocusEvent event)
	{
		if(!receivesInputEvents())
			return;
		onFocusLost(event);
		var callback = onFocusLost.get();
		if(callback != null)
			callback.onEvent(event);
	}
	public void onFocusLost(FocusEvent event) {}

	public final void handleMouseEnter(MouseEvent event)
	{
		if(!receivesInputEvents())
			return;
		onMouseEnter(event);
		var callback = onMouseEnter.get();
		if(callback != null)
			callback.onEvent(event);
	}
	public void onMouseEnter(MouseEvent event) {}

	public final void handleMouseExit(MouseEvent event)
	{
		if(!receivesInputEvents())
			return;
		onMouseExit(event);
		var callback = onMouseExit.get();
		if(callback != null)
			callback.onEvent(event);
	}
	public void onMouseExit(MouseEvent event) {}

	public final void handleMouseMove(MouseEvent event)
	{
		if(!receivesInputEvents())
			return;
		onMouseMove(event);
		var callback = onMouseMove.get();
		if(callback != null)
			callback.onEvent(event);
	}
	public void onMouseMove(MouseEvent event) {}

	public final void handleMouseScroll(MouseEvent event)
	{
		if(!receivesInputEvents())
			return;
		onMouseScroll(event);
		var callback = onMouseScroll.get();
		if(callback != null)
			callback.onEvent(event);
	}
	public void onMouseScroll(MouseEvent event) {}

	public final void handleMousePress(MouseEvent event)
	{
		if(!receivesInputEvents())
			return;
		onMousePress(event);
		var callback = onMousePress.get();
		if(callback != null)
			callback.onEvent(event);
	}
	public void onMousePress(MouseEvent event) {}

	public final void handleMouseRelease(MouseEvent event)
	{
		if(!receivesInputEvents())
			return;
		onMouseRelease(event);
		var callback = onMouseRelease.get();
		if(callback != null)
			callback.onEvent(event);
	}
	public void onMouseRelease(MouseEvent event) {}

	public final void handleClick(ClickEvent event)
	{
		if(!receivesInputEvents())
			return;
		onClick(event);
		var callback = onClick.get();
		if(callback != null)
			callback.onEvent(event);
	}
	public void onClick(ClickEvent event) {}

	public final void handleKeyPress(KeyEvent event)
	{
		if(!receivesInputEvents())
			return;
		onKeyPress(event);
		var callback = onKeyPress.get();
		if(callback != null)
			callback.onEvent(event);
	}
	public void onKeyPress(KeyEvent event) {}

	public final void handleKeyRelease(KeyEvent event)
	{
		if(!receivesInputEvents())
			return;
		onKeyRelease(event);
		var callback = onKeyRelease.get();
		if(callback != null)
			callback.onEvent(event);
	}
	public void onKeyRelease(KeyEvent event) {}

	public final void handleCharTyped(KeyEvent event)
	{
		if(!receivesInputEvents())
			return;
		onCharTyped(event);
		var callback = onKeyType.get();
		if(callback != null)
			callback.onEvent(event);
	}
	public void onCharTyped(KeyEvent event) {}


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
	public static class LayoutResult
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
