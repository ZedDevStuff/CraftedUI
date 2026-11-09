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
import dev.zeddevstuff.crafted.rendering.IRendersType;
import dev.zeddevstuff.crafted.types.*;
import dev.zeddevstuff.crafted.ui.elements.CraftedRoot;
import dev.zeddevstuff.crafted.utils.Event;
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
	private long treeVersion;
	private long inputOrderTreeVersion = -1;
	private List<ElementOrder.Entry> inputOrder;
	public List<CraftedElement> getChildren() { return Collections.unmodifiableList(children); }
	public long getTreeVersion() { return treeVersion; }
	private void markTreeChanged()
	{
		treeVersion++;
		if(parent != null)
			parent.markTreeChanged();
	}

	public final InteractionState state = new InteractionState(INTERNAL_KEY);

	private LayoutResult layout = new LayoutResult();
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
	protected <T, TProp extends IProp<T>> TProp registerProp(Class<?> type, String name, BiFunction<String, T, T> parser)
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
	protected <T> Prop<T> registerProp(Class<?> type, String name, T value, BiFunction<String, T, T> parser)
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
	protected <T> ProxyProp<T> registerProp(Class<?> type, String name, Supplier<T> getter, Consumer<T> setter, BiFunction<String, T, T> parser)
	{
		if(props.containsKey(name))
			throw new IllegalArgumentException("Property with name '" + name + "' is already registered for this element.");
		var prop = new ProxyProp<>(type, name, setter, getter, parser);
		props.put(name, prop);
		return prop;
	}
	@Description("Unique identifier for this element")
	public final Prop<String> id = registerProp("id", "", Parsers::parseString);
	@Description("Used by some renderers to determine how to render this element")
	public final Prop<String> style = registerProp("style", "", Parsers::parseString);
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

	public final Event<FocusEvent> focusGained = new Event<>(INTERNAL_KEY, FocusEvent.class);
	public final Prop<IFocusEventHandler> onFocusGained = registerProp(IFocusEventHandler.class, "onFocusGained", this::parseFocusCallback);
	public final Event<FocusEvent> focusLost = new Event<>(INTERNAL_KEY, FocusEvent.class);
	public final Prop<IFocusEventHandler> onFocusLost = registerProp(IFocusEventHandler.class, "onFocusLost", this::parseFocusCallback);

	public final Event<MouseEvent> mouseMove = new Event<>(INTERNAL_KEY, MouseEvent.class);
	public final Prop<IMouseEventHandler> onMouseMove = registerProp(IMouseEventHandler.class, "onMouseMove", this::parseMouseCallback);
	public final Event<MouseEvent> mouseScroll = new Event<>(INTERNAL_KEY, MouseEvent.class);
	public final Prop<IMouseEventHandler> onMouseScroll = registerProp(IMouseEventHandler.class, "onMouseScroll", this::parseMouseCallback);
	public final Event<MouseEvent> mouseEnter = new Event<>(INTERNAL_KEY, MouseEvent.class);
	public final Prop<IMouseEventHandler> onMouseEnter = registerProp(IMouseEventHandler.class, "onMouseEnter", this::parseMouseCallback);
	public final Event<MouseEvent> mouseExit = new Event<>(INTERNAL_KEY, MouseEvent.class);
	public final Prop<IMouseEventHandler> onMouseExit = registerProp(IMouseEventHandler.class, "onMouseExit", this::parseMouseCallback);
	public final Event<MouseEvent> mousePress = new Event<>(INTERNAL_KEY, MouseEvent.class);
	public final Prop<IMouseEventHandler> onMousePress = registerProp(IMouseEventHandler.class, "onMousePress", this::parseMouseCallback);
	public final Event<MouseEvent> mouseRelease = new Event<>(INTERNAL_KEY, MouseEvent.class);
	public final Prop<IMouseEventHandler> onMouseRelease = registerProp(IMouseEventHandler.class, "onMouseRelease", this::parseMouseCallback);

	public final Event<ClickEvent> click = new Event<>(INTERNAL_KEY, ClickEvent.class);
	public final Prop<IClickEventHandler> onClick = registerProp(IClickEventHandler.class, "onClick", this::parseClickCallback);

	public final Event<KeyEvent> keyPress = new Event<>(INTERNAL_KEY, KeyEvent.class);
	public final Prop<IKeyEventHandler> onKeyPress = registerProp(IKeyEventHandler.class, "onKeyPress", this::parseKeyCallback);
	public final Event<KeyEvent> keyRelease = new Event<>(INTERNAL_KEY, KeyEvent.class);
	public final Prop<IKeyEventHandler> onKeyRelease = registerProp(IKeyEventHandler.class, "onKeyRelease", this::parseKeyCallback);
	public final Event<KeyEvent> keyType = new Event<>(INTERNAL_KEY, KeyEvent.class);
	public final Prop<IKeyEventHandler> onKeyType = registerProp(IKeyEventHandler.class, "onKeyType", this::parseKeyCallback);

	@Description("The renderer to use for this element.")
	public final Prop<IRenders> renderer;

	public CraftedElement(CraftedContext ctx, Map<String, String> attributes)
	{
		this.ctx = ctx;
		if(!(this instanceof CraftedRoot))
			this.renderer = registerProp(IRenders.class, "renderer", ctx.crafted.renderers.getRendererOrDefault(this.getClass(), attributes.getOrDefault("renderer", ctx.currentRenderer)), this::getRenderer);
		else
			this.renderer = null;
		this.node.setStyle(initLayoutNode(node.getStyle(), attributes));
		zIndex.addListener((prop, oldValue, newValue) -> {
			markTreeChanged();
		});
//		disabled.addListener((prop, oldValue, newValue) -> {
//			if(Boolean.TRUE.equals(newValue))
//				ctx.inputHandler.detachElement(this);
//		});
		onFocusGained.addListener((prop, oldValue, newValue) -> {
			handleCallbackChange(focusGained, oldValue, newValue);
		});
		onFocusLost.addListener((prop, oldValue, newValue) -> {
			handleCallbackChange(focusLost, oldValue, newValue);
		});
		onMouseMove.addListener((prop, oldValue, newValue) -> {
			handleCallbackChange(mouseMove, oldValue, newValue);
		});
		onMouseScroll.addListener((prop, oldValue, newValue) -> {
			handleCallbackChange(mouseScroll, oldValue, newValue);
		});
		onMouseEnter.addListener((prop, oldValue, newValue) -> {
			handleCallbackChange(mouseEnter, oldValue, newValue);
		});
		onMouseExit.addListener((prop, oldValue, newValue) -> {
			handleCallbackChange(mouseExit, oldValue, newValue);
		});
		onMousePress.addListener((prop, oldValue, newValue) -> {
			handleCallbackChange(mousePress, oldValue, newValue);
		});
		onMouseRelease.addListener((prop, oldValue, newValue) -> {
			handleCallbackChange(mouseRelease, oldValue, newValue);
		});
		onClick.addListener((prop, oldValue, newValue) -> {
			handleCallbackChange(click, oldValue, newValue);
		});
		onKeyPress.addListener((prop, oldValue, newValue) -> {
			handleCallbackChange(keyPress, oldValue, newValue);
		});
		onKeyRelease.addListener((prop, oldValue, newValue) -> {
			handleCallbackChange(keyRelease, oldValue, newValue);
		});
		onKeyType.addListener((prop, oldValue, newValue) -> {
			handleCallbackChange(keyType, oldValue, newValue);
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

	<TEvent extends UIEvent, THandler extends Event.IEventListener<TEvent>> void handleCallbackChange(Event<TEvent> event, THandler oldValue, THandler newValue)
	{
		if(oldValue == newValue)
			return;
		if(oldValue != null)
			event.removeListener(oldValue);
		event.addListener(newValue);
	}

	IFocusEventHandler parseFocusCallback(String name, IFocusEventHandler defaultValue)
	{
		return ctx.getCallback(IFocusEventHandler.class, name);
	}
	IMouseEventHandler parseMouseCallback(String name, IMouseEventHandler defaultValue)
	{
		return ctx.getCallback(IMouseEventHandler.class, name);
	}
	IClickEventHandler parseClickCallback(String name, IClickEventHandler defaultValue)
	{
		return ctx.getCallback(IClickEventHandler.class, name);
	}
	IKeyEventHandler parseKeyCallback(String name, IKeyEventHandler defaultValue)
	{
		return ctx.getCallback(IKeyEventHandler.class, name);
	}
	IRenders getRenderer(String name, IRenders defaultValue)
	{
		return ctx.crafted.renderers.getRendererOrDefault(this.getClass(), name);
	}
	protected Color parseColor(String s, Color defaultValue)
	{
		var color = ctx.theme.getColorByName(s);
		if(color != null)
			return color;
		return Parsers.parseColor(s, defaultValue);
	}

	/**
	 * Override this method to initialize the layout node for this element with non default values.
	 */
	protected YogaStyle initLayoutNode(YogaStyle style, Map<String, String> attributes) { return style; }
	public boolean contains(float x, float y)
	{
		return layout.contains(x, y);
	}
	public int getAncestorRenderOffsetX()
	{
		return parent == null ? 0 : parent.getAncestorRenderOffsetX() + parent.getRenderOffsetX();
	}
	public int getAncestorRenderOffsetY()
	{
		return parent == null ? 0 : parent.getAncestorRenderOffsetY() + parent.getRenderOffsetY();
	}
	protected int getRenderOffsetX() { return 0; }
	protected int getRenderOffsetY() { return 0; }
	public void scrollIntoView(CraftedElement element) {}
	protected float transformChildInputX(float x) { return x; }
	protected float transformChildInputY(float y) { return y; }

	// Thank you Wulferis for the base method. It's been butchered a bit though. Probably won't recognize it anymore lol
	public CraftedElement findTopmostAt(float x, float y)
	{
		if(inputOrderTreeVersion != treeVersion)
		{
			inputOrder = ElementOrder.collect(this);
			inputOrderTreeVersion = treeVersion;
		}
		for(int i = inputOrder.size() - 1; i >= 0; i--)
		{
			var candidate = inputOrder.get(i).element();
			if(candidate.receivesInputEvents()
				&& !candidate.state.isDisabled()
				&& isInputPathVisible(candidate, x, y))
				return candidate;
		}
		return null;
	}
	private boolean isInputPathVisible(CraftedElement target, float x, float y)
	{
		if(this == target)
			return contains(x, y);
		if(!target.isDescendantOf(this))
			return false;
		if(!contains(x, y) && overflow.get() != YogaOverflow.VISIBLE)
			return false;

		float childX = transformChildInputX(x);
		float childY = transformChildInputY(y);
		for(var child : children)
		{
			if(child == target || target.isDescendantOf(child))
				return child.isInputPathVisible(target, childX, childY);
		}
		return false;
	}
	public boolean isAncestorOf(CraftedElement element)
	{
		if(element == null)
			return false;
		if(element.parent == this)
			return true;
		return element.parent != null && isAncestorOf(element.parent);
	}
	public boolean isDescendantOf(CraftedElement element)
	{
		if(element == null)
			return false;
		if(parent == element)
			return true;
		return parent != null && parent.isDescendantOf(element);
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
		collectElements(predicate, results);
		return results.toArray(new CraftedElement[0]);
	}
	private void collectElements(Predicate<CraftedElement> predicate, List<CraftedElement> results)
	{
		if(predicate.test(this))
			results.add(this);
		for(var child : children)
			child.collectElements(predicate, results);
	}

	public void setParent(CraftedElement newParent)
	{
		if(newParent == null)
			throw new IllegalArgumentException("Parent cannot be null.");
		if(newParent == this || isAncestorOf(newParent))
			throw new IllegalArgumentException("Recursive parent-child relationship detected. Cannot set parent to a descendant.");
		if(this.parent == newParent)
			return;
		newParent.addChild(this);
	}
	public void addChild(CraftedElement child)
	{
		if(child == null)
			return;
		if(children.contains(child))
			return;
		if(child == this)
			throw new IllegalArgumentException("Cannot add self as child.");
		if(child.isAncestorOf(this))
			throw new IllegalArgumentException("Recursive parent-child relationship detected. Cannot add ancestor as child.");
		if(child.parent != null && child.parent != this)
			child.parent.removeChild(child);
		children.add(child);
		child.parent = this;
		node.addChildAt(child.node, node.getChildCount());
		markTreeChanged();
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
	public void insertChildAt(CraftedElement child, int index)
	{
		if(child == null)
			return;
		if(children.contains(child))
			return;
		if(child == this)
			throw new IllegalArgumentException("Cannot add self as child.");
		if(child.isAncestorOf(this))
			throw new IllegalArgumentException("Recursive parent-child relationship detected. Cannot add ancestor as child.");
		if(child.parent != null && child.parent != this)
			child.parent.removeChild(child);
		if(index < 0 || index > children.size())
			throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + children.size());
		if(index > node.getChildCount())
			throw new IndexOutOfBoundsException("Index: " + index + ", Node Child Count: " + node.getChildCount());
		children.add(index, child);
		child.parent = this;
		node.addChildAt(child.node, index);
		markTreeChanged();
	}
	public void removeChild(CraftedElement child)
	{
		if(child == null)
			return;
		if(!children.contains(child))
			return;
        ctx.inputHandler.detachElement(child);
		children.remove(child);
		child.parent = null;
		node.removeChildAndInvalidate(child.node);
		if(child.node.getOwner() == node)
			child.node.setOwner(null);
		markTreeChanged();
	}
	public void removeChildAt(int index)
	{
		if(index < 0 || index >= children.size())
			throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + children.size());
		var child = children.get(index);
		removeChild(child);
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

	public CraftedElement cloneElement(boolean deep)
	{
		var tag = ctx.crafted.elements.getTagForClass(getClass());
		if(tag == null)
			throw new IllegalStateException("Cannot clone unregistered element class: " + getClass().getName());

		var clone = ctx.crafted.elements.createElement(ctx, tag, Map.of());
		copyPropsTo(clone);
		copyExtraStateTo(clone);
		if(deep)
			for(var child : children)
				clone.addChild(child.cloneElement(true));
		return clone;
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	private void copyPropsTo(CraftedElement target)
	{
		for(var entry : props.entrySet())
		{
			var targetProp = target.props.get(entry.getKey());
			if(targetProp != null)
				((IProp)targetProp).set(((IProp)entry.getValue()).get());
		}
	}

	/**
	 * Hook for subclasses to copy transient state that is not stored in props (or are in unregistered props) <br>
	 * Called by {@link #cloneElement(boolean)} after registered props have been copied.
	 */
	protected void copyExtraStateTo(CraftedElement clone) {}

	public void setRenderer(String renderer)
	{
		if(this.renderer != null)
			this.renderer.set(ctx.crafted.renderers.getRendererOrDefault(this.getClass(), renderer));
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
		layout = parent == null || node.getPositionType() == YogaPositionType.ABSOLUTE
			? new LayoutResult(this)
			: new LayoutResult(this, parent.layout);
		if(node.hasNewLayout())
			node.markLayoutSeen();
		for(var child : children)
			child.layoutElement();
	}
	public void renderSelf(ICanvas canvas)
	{
		if(renderer != null && renderer.get().getElementType().isInstance(this))
			((IRendersType<?>)renderer.get()).forceRender(canvas, this);
	}
	public void beginRenderSelf(ICanvas canvas)
	{
		if(renderer != null && renderer.get().getElementType().isInstance(this))
			((IRendersType<?>)renderer.get()).forceBeginRender(canvas, this);
	}
	public void afterRenderSelf(ICanvas canvas)
	{
		if(renderer != null)
			((IRendersType<?>)renderer.get()).forceAfterRender(canvas, this);
	}

	/**
	 * Override this method to indicate that this element should receive input events. <br>
	 * By default, elements do not receive input events.
	 */
	public boolean receivesInputEvents() { return false; }
	public boolean isFocusable() { return false; }

	public void handleFocusGained(FocusEvent event)
	{
		if(!receivesInputEvents())
			return;
		onFocusGained(event);
		if(event.isImmediatePropagationStopped())
			return;
		focusGained.fire(INTERNAL_KEY, event);
	}
	public void onFocusGained(FocusEvent event) {}

	public void handleFocusLost(FocusEvent event)
	{
		if(!receivesInputEvents())
			return;
		onFocusLost(event);
		if(event.isImmediatePropagationStopped())
			return;
		focusLost.fire(INTERNAL_KEY, event);
	}
	public void onFocusLost(FocusEvent event) {}

	public final void handleMouseEnter(MouseEvent event)
	{
		if(!receivesInputEvents())
			return;
		onMouseEnter(event);
		if(event.isImmediatePropagationStopped())
			return;
		mouseEnter.fire(INTERNAL_KEY, event);
	}
	public void onMouseEnter(MouseEvent event) {}

	public final void handleMouseExit(MouseEvent event)
	{
		if(!receivesInputEvents())
			return;
		onMouseExit(event);
		if(event.isImmediatePropagationStopped())
			return;
		mouseExit.fire(INTERNAL_KEY, event);
	}
	public void onMouseExit(MouseEvent event) {}

	public final void handleMouseMove(MouseEvent event)
	{
		if(!receivesInputEvents())
			return;
		onMouseMove(event);
		if(event.isImmediatePropagationStopped())
			return;
		mouseMove.fire(INTERNAL_KEY, event);
	}
	public void onMouseMove(MouseEvent event) {}

	public final void handleMouseScroll(MouseEvent event)
	{
		if(!receivesInputEvents())
			return;
		onMouseScroll(event);
		if(event.isImmediatePropagationStopped())
			return;
		mouseScroll.fire(INTERNAL_KEY, event);
	}
	public void onMouseScroll(MouseEvent event) {}

	public final void handleMousePress(MouseEvent event)
	{
		if(!receivesInputEvents())
			return;
		onMousePress(event);
		if(event.isImmediatePropagationStopped())
			return;
		mousePress.fire(INTERNAL_KEY, event);
	}
	public void onMousePress(MouseEvent event) {}

	public final void handleMouseRelease(MouseEvent event)
	{
		if(!receivesInputEvents())
			return;
		onMouseRelease(event);
		if(event.isImmediatePropagationStopped())
			return;
		mouseRelease.fire(INTERNAL_KEY, event);
	}
	public void onMouseRelease(MouseEvent event) {}

	public final void handleClick(ClickEvent event)
	{
		if(!receivesInputEvents())
			return;
		onClick(event);
		if(event.isImmediatePropagationStopped())
			return;
		click.fire(INTERNAL_KEY, event);
	}
	public void onClick(ClickEvent event) {}

	public final void handleKeyPress(KeyEvent event)
	{
		if(!receivesInputEvents())
			return;
		onKeyPress(event);
		if(event.isImmediatePropagationStopped())
			return;
		keyPress.fire(INTERNAL_KEY, event);
	}
	public void onKeyPress(KeyEvent event) {}

	public final void handleKeyRelease(KeyEvent event)
	{
		if(!receivesInputEvents())
			return;
		onKeyRelease(event);
		if(event.isImmediatePropagationStopped())
			return;
		keyRelease.fire(INTERNAL_KEY, event);
	}
	public void onKeyRelease(KeyEvent event) {}

	public final void handleCharTyped(KeyEvent event)
	{
		if(!receivesInputEvents())
			return;
		onCharTyped(event);
		if(event.isImmediatePropagationStopped())
			return;
		keyType.fire(INTERNAL_KEY, event);
	}
	public void onCharTyped(KeyEvent event) {}

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

		private LayoutResult()
		{
			this.x = 0;
			this.y = 0;
			this.width = 0;
			this.height = 0;
			this.margin = new EdgeInsets<>(0, 0, 0, 0);
			this.padding = new EdgeInsets<>(0, 0, 0, 0);
			this.border = new EdgeInsets<>(0, 0, 0, 0);
			this.innerX = x + margin.left() + padding.left() + border.left();
			this.innerY = y + margin.top() + padding.top() + border.top();
			this.innerWidth = width - margin.left() - margin.right() - padding.left() -padding.right() - border.left() - border.right();
			this.innerHeight = height - margin.top() - margin.bottom() - padding.top() - padding.bottom() - border.top() - border.bottom();
		}

		private LayoutResult(CraftedElement element)
		{
			this.x = (int)element.node.getLayoutX();
			this.y = (int)element.node.getLayoutY();
			this.width = (int)element.node.getLayoutWidth();
			this.height = (int)element.node.getLayoutHeight();
			this.margin = new EdgeInsets<>((int)element.node.getLayoutMargin(YogaEdge.LEFT), (int)element.node.getLayoutMargin(YogaEdge.TOP), (int)element.node.getLayoutMargin(YogaEdge.RIGHT), (int)element.node.getLayoutMargin(YogaEdge.BOTTOM));
			this.padding = new EdgeInsets<>((int)element.node.getLayoutPadding(YogaEdge.LEFT), (int)element.node.getLayoutPadding(YogaEdge.TOP), (int)element.node.getLayoutPadding(YogaEdge.RIGHT), (int)element.node.getLayoutPadding(YogaEdge.BOTTOM));
			this.border = new EdgeInsets<>((int)element.node.getLayoutBorder(YogaEdge.LEFT), (int)element.node.getLayoutBorder(YogaEdge.TOP), (int)element.node.getLayoutBorder(YogaEdge.RIGHT), (int)element.node.getLayoutBorder(YogaEdge.BOTTOM));
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
			this.margin = new EdgeInsets<>((int)element.node.getLayoutMargin(YogaEdge.LEFT), (int)element.node.getLayoutMargin(YogaEdge.TOP), (int)element.node.getLayoutMargin(YogaEdge.RIGHT), (int)element.node.getLayoutMargin(YogaEdge.BOTTOM));
			this.padding = new EdgeInsets<>((int)element.node.getLayoutPadding(YogaEdge.LEFT), (int)element.node.getLayoutPadding(YogaEdge.TOP), (int)element.node.getLayoutPadding(YogaEdge.RIGHT), (int)element.node.getLayoutPadding(YogaEdge.BOTTOM));
			this.border = new EdgeInsets<>((int)element.node.getLayoutBorder(YogaEdge.LEFT), (int)element.node.getLayoutBorder(YogaEdge.TOP), (int)element.node.getLayoutBorder(YogaEdge.RIGHT), (int)element.node.getLayoutBorder(YogaEdge.BOTTOM));
			this.innerX = x + margin.left() + padding.left() + border.left();
			this.innerY = y + margin.top() + padding.top() + border.top();
			this.innerWidth = width - margin.left() - margin.right() - padding.left() -padding.right() - border.left() - border.right();
			this.innerHeight = height - margin.top() - margin.bottom() - padding.top() - padding.bottom() - border.top() - border.bottom();
		}

		public boolean contains(float x, float y)
		{
			return x >= this.x && x <= this.x + this.width && y >= this.y && y <= this.y + this.height;
		}
		public boolean containsInner(float x, float y)
		{
			return x >= this.innerX && x <= this.innerX + this.innerWidth && y >= this.innerY && y <= this.innerY + this.innerHeight;
		}
	}
}
