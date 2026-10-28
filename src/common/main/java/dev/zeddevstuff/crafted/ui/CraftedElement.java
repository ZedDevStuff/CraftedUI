package dev.zeddevstuff.crafted.ui;

import dev.zeddevstuff.crafted.Prop;
import dev.zeddevstuff.crafted.annotations.Description;
import dev.zeddevstuff.crafted.parsing.Parsers;
import dev.zeddevstuff.crafted.platform.ICanvas;
import dev.zeddevstuff.crafted.rendering.IRenders;
import dev.zeddevstuff.crafted.types.EdgeInsets;
import dev.zeddevstuff.crafted.types.StyleLength2;
import dev.zeddevstuff.crafted.types.StyleLength4;
import org.appliedenergistics.yoga.*;
import org.appliedenergistics.yoga.style.StyleLength;
import org.appliedenergistics.yoga.style.StyleSizeLength;
import org.appliedenergistics.yoga.style.YogaStyle;

import java.util.*;
import java.util.function.BiFunction;

public abstract class CraftedElement
{
	private final CraftedContext ctx;
	private final YogaNode node;


	protected CraftedElement parent;
	public CraftedElement getParent() { return parent; }
	protected final List<CraftedElement> children = new ArrayList<>();

	private LayoutResult layout;
	public LayoutResult getLayout() { return layout; }

	protected final HashMap<String, Prop<?>> props = new HashMap<>();
	protected <T> Prop<T> registerProp(String name, T value, BiFunction<String, T, T> parser)
	{
		var prop = new Prop<>(name, value, parser);
		props.put(name, prop);
		return prop;
	}
	protected <T> Prop<T> registerProp(Class<T> type, String name, T value, BiFunction<String, T, T> parser)
	{
		var prop = new Prop<>(type, name, value, parser);
		props.put(name, prop);
		return prop;
	}
	@Description("Unique identifier for this element")
	public final Prop<String> id = registerProp("id", "", Parsers::parseString);
	public final Prop<Integer> zIndex = registerProp("zIndex", 0, Parsers::parseInt);

	public final Prop<YogaDirection> direction = registerProp("direction", YogaDirection.INHERIT, Parsers::parseYogaDirection);
	public final Prop<YogaFlexDirection> flexDirection = registerProp("flexDirection", YogaFlexDirection.COLUMN, Parsers::parseYogaFlexDirection);
	public final Prop<YogaJustify> justifyContent = registerProp("justifyContent", YogaJustify.FLEX_START, Parsers::parseYogaJustify);
	public final Prop<YogaAlign> alignContent = registerProp("alignContent", YogaAlign.FLEX_START, Parsers::parseYogaAlign);
	public final Prop<YogaAlign> alignItems = registerProp("alignItems", YogaAlign.STRETCH, Parsers::parseYogaAlign);
	public final Prop<YogaAlign> alignSelf = registerProp("alignSelf", YogaAlign.AUTO, Parsers::parseYogaAlign);
	public final Prop<YogaPositionType> positionType = registerProp("positionType", YogaPositionType.RELATIVE, Parsers::parseYogaPositionType);
	public final Prop<YogaWrap> flexWrap = registerProp("flexWrap", YogaWrap.NO_WRAP, Parsers::parseYogaWrap);
	public final Prop<YogaOverflow> overflow = registerProp("overflow", YogaOverflow.VISIBLE, Parsers::parseYogaOverflow);
	public final Prop<YogaDisplay> display = registerProp("display", YogaDisplay.FLEX, Parsers::parseYogaDisplay);
	public final Prop<YogaBoxSizing> boxSizing = registerProp("boxSizing", YogaBoxSizing.BORDER_BOX, Parsers::parseYogaBoxSizing);

	public final Prop<Float> flex = registerProp("flex", 0f, Parsers::parseFloat);
	public final Prop<Float> flexGrow = registerProp("flexGrow", 0f, Parsers::parseFloat);
	public final Prop<Float> flexShrink = registerProp("flexShrink", 1f, Parsers::parseFloat);
	public final Prop<StyleSizeLength> flexBasis =  registerProp("flexBasis", StyleSizeLength.ofAuto(), Parsers::parseStyleSizeLength);

	public final Prop<StyleLength4> margin = registerProp("margin", new StyleLength4(StyleLength.ofAuto(), StyleLength.ofAuto(), StyleLength.ofAuto(), StyleLength.ofAuto()), Parsers::parseStyleLength4);
	public final Prop<StyleLength> marginTop = registerProp("marginTop", StyleLength.ofAuto(), Parsers::parseStyleLength);
	public final Prop<StyleLength> marginRight = registerProp("marginRight", StyleLength.ofAuto(), Parsers::parseStyleLength);
	public final Prop<StyleLength> marginBottom = registerProp("marginBottom", StyleLength.ofAuto(), Parsers::parseStyleLength);
	public final Prop<StyleLength> marginLeft = registerProp("marginLeft", StyleLength.ofAuto(), Parsers::parseStyleLength);

	public final Prop<StyleLength4> position = registerProp("position", new StyleLength4(StyleLength.ofAuto(), StyleLength.ofAuto(), StyleLength.ofAuto(), StyleLength.ofAuto()), Parsers::parseStyleLength4);
	public final Prop<StyleLength> top = registerProp("top", StyleLength.ofAuto(), Parsers::parseStyleLength);
	public final Prop<StyleLength> right = registerProp("right", StyleLength.ofAuto(), Parsers::parseStyleLength);
	public final Prop<StyleLength> bottom = registerProp("bottom", StyleLength.ofAuto(), Parsers::parseStyleLength);
	public final Prop<StyleLength> left = registerProp("left", StyleLength.ofAuto(), Parsers::parseStyleLength);

	public final Prop<StyleLength4> padding = registerProp("padding", new StyleLength4(StyleLength.ofAuto(), StyleLength.ofAuto(), StyleLength.ofAuto(), StyleLength.ofAuto()), Parsers::parseStyleLength4);
	public final Prop<StyleLength> paddingTop = registerProp("paddingTop", StyleLength.ofAuto(), Parsers::parseStyleLength);
	public final Prop<StyleLength> paddingRight = registerProp("paddingRight", StyleLength.ofAuto(), Parsers::parseStyleLength);
	public final Prop<StyleLength> paddingBottom = registerProp("paddingBottom", StyleLength.ofAuto(), Parsers::parseStyleLength);
	public final Prop<StyleLength> paddingLeft = registerProp("paddingLeft", StyleLength.ofAuto(), Parsers::parseStyleLength);

	public final Prop<StyleLength4> border = registerProp("border", new StyleLength4(StyleLength.ofAuto(), StyleLength.ofAuto(), StyleLength.ofAuto(), StyleLength.ofAuto()), Parsers::parseStyleLength4);
	public final Prop<StyleLength> borderTop = registerProp("borderTop", StyleLength.ofAuto(), Parsers::parseStyleLength);
	public final Prop<StyleLength> borderRight = registerProp("borderRight", StyleLength.ofAuto(), Parsers::parseStyleLength);
	public final Prop<StyleLength> borderBottom = registerProp("borderBottom", StyleLength.ofAuto(), Parsers::parseStyleLength);
	public final Prop<StyleLength> borderLeft = registerProp("borderLeft", StyleLength.ofAuto(), Parsers::parseStyleLength);

	public final Prop<StyleLength2> gap = registerProp("gap", new StyleLength2(StyleLength.ofAuto(), StyleLength.ofAuto()), Parsers::parseStyleLength2);
	public final Prop<StyleLength> rowGap = registerProp("rowGap", StyleLength.ofAuto(), Parsers::parseStyleLength);
	public final Prop<StyleLength> columnGap = registerProp("columnGap", StyleLength.ofAuto(), Parsers::parseStyleLength);

	public final Prop<StyleSizeLength> width = registerProp("width", StyleSizeLength.ofAuto(), Parsers::parseStyleSizeLength);
	public final Prop<StyleSizeLength> height = registerProp("height", StyleSizeLength.ofAuto(), Parsers::parseStyleSizeLength);
	public final Prop<StyleSizeLength> minWidth = registerProp("minWidth", StyleSizeLength.ofAuto(), Parsers::parseStyleSizeLength);
	public final Prop<StyleSizeLength> minHeight = registerProp("minHeight", StyleSizeLength.ofAuto(), Parsers::parseStyleSizeLength);
	public final Prop<StyleSizeLength> maxWidth = registerProp("maxWidth", StyleSizeLength.ofAuto(), Parsers::parseStyleSizeLength);
	public final Prop<StyleSizeLength> maxHeight = registerProp("maxHeight", StyleSizeLength.ofAuto(), Parsers::parseStyleSizeLength);

	private IRenders<?> renderer;

	public CraftedElement(CraftedContext ctx, Map<String, String> attributes)
	{
		this.ctx = ctx;
		this.renderer = ctx.crafted.renderers.getRenderer(this.getClass(), attributes.getOrDefault("renderer", "default"));
		this.node = new YogaNode();

		direction.addListener((prop, oldValue, newValue) -> node.setDirection(newValue));
		flexDirection.addListener((prop, oldValue, newValue) -> node.setFlexDirection(newValue));
		justifyContent.addListener((prop, oldValue, newValue) -> node.setJustifyContent(newValue));
		alignContent.addListener((prop, oldValue, newValue) -> node.setAlignContent(newValue));
		alignItems.addListener((prop, oldValue, newValue) -> node.setAlignItems(newValue));
		alignSelf.addListener((prop, oldValue, newValue) -> node.setAlignSelf(newValue));
		positionType.addListener((prop, oldValue, newValue) -> node.setPositionType(newValue));
		flexWrap.addListener((prop, oldValue, newValue) -> node.setWrap(newValue));
		overflow.addListener((prop, oldValue, newValue) -> node.setOverflow(newValue));
		display.addListener((prop, oldValue, newValue) -> node.setDisplay(newValue));
		boxSizing.addListener((prop, oldValue, newValue) -> node.setBoxSizing(newValue));

		flex.addListener((prop, oldValue, newValue) -> node.setFlex(newValue));
		flexGrow.addListener((prop, oldValue, newValue) -> node.setFlexGrow(newValue));
		flexShrink.addListener((prop, oldValue, newValue) -> node.setFlexShrink(newValue));
		flexBasis.addListener((prop, oldValue, newValue) -> node.getStyle().setFlexBasis(newValue));

		margin.addListener((prop, oldValue, newValue) -> {
			node.setMargin(YogaEdge.TOP, newValue.top());
			node.setMargin(YogaEdge.RIGHT, newValue.right());
			node.setMargin(YogaEdge.BOTTOM, newValue.bottom());
			node.setMargin(YogaEdge.LEFT, newValue.left());
		});
		marginTop.addListener((prop, oldValue, newValue) -> node.setMargin(YogaEdge.TOP, newValue));
		marginRight.addListener((prop, oldValue, newValue) -> node.setMargin(YogaEdge.RIGHT, newValue));
		marginBottom.addListener((prop, oldValue, newValue) -> node.setMargin(YogaEdge.BOTTOM, newValue));
		marginLeft.addListener((prop, oldValue, newValue) -> node.setMargin(YogaEdge.LEFT, newValue));

		position.addListener((prop, oldValue, newValue) -> {
			node.setPosition(YogaEdge.TOP, newValue.top());
			node.setPosition(YogaEdge.RIGHT, newValue.right());
			node.setPosition(YogaEdge.BOTTOM, newValue.bottom());
			node.setPosition(YogaEdge.LEFT, newValue.left());
		});
		top.addListener((prop, oldValue, newValue) -> node.setPosition(YogaEdge.TOP, newValue));
		right.addListener((prop, oldValue, newValue) -> node.setPosition(YogaEdge.RIGHT, newValue));
		bottom.addListener((prop, oldValue, newValue) -> node.setPosition(YogaEdge.BOTTOM, newValue));
		left.addListener((prop, oldValue, newValue) -> node.setPosition(YogaEdge.LEFT, newValue));

		padding.addListener((prop, oldValue, newValue) -> {
			node.setPadding(YogaEdge.TOP, newValue.top());
			node.setPadding(YogaEdge.RIGHT, newValue.right());
			node.setPadding(YogaEdge.BOTTOM, newValue.bottom());
			node.setPadding(YogaEdge.LEFT, newValue.left());
		});
		paddingTop.addListener((prop, oldValue, newValue) -> node.setPadding(YogaEdge.TOP, newValue));
		paddingRight.addListener((prop, oldValue, newValue) -> node.setPadding(YogaEdge.RIGHT, newValue));
		paddingBottom.addListener((prop, oldValue, newValue) -> node.setPadding(YogaEdge.BOTTOM, newValue));
		paddingLeft.addListener((prop, oldValue, newValue) -> node.setPadding(YogaEdge.LEFT, newValue));

		border.addListener((prop, oldValue, newValue) -> {
			var style = node.getStyle();
			style.setBorder(YogaEdge.TOP, newValue.top());
			style.setBorder(YogaEdge.RIGHT, newValue.right());
			style.setBorder(YogaEdge.BOTTOM, newValue.bottom());
			style.setBorder(YogaEdge.LEFT, newValue.left());
		});
		borderTop.addListener((prop, oldValue, newValue) -> node.getStyle().setBorder(YogaEdge.TOP, newValue));
		borderRight.addListener((prop, oldValue, newValue) -> node.getStyle().setBorder(YogaEdge.RIGHT, newValue));
		borderBottom.addListener((prop, oldValue, newValue) -> node.getStyle().setBorder(YogaEdge.BOTTOM, newValue));
		borderLeft.addListener((prop, oldValue, newValue) -> node.getStyle().setBorder(YogaEdge.LEFT, newValue));

		gap.addListener((prop, oldValue, newValue) -> {
			node.setGap(YogaGutter.ROW, newValue.x());
			node.setGap(YogaGutter.COLUMN, newValue.y());
		});
		rowGap.addListener((prop, oldValue, newValue) -> node.setGap(YogaGutter.ROW, newValue));
		columnGap.addListener((prop, oldValue, newValue) -> node.setGap(YogaGutter.COLUMN, newValue));

		width.addListener((prop, oldValue, newValue) -> node.setWidth(newValue));
		height.addListener((prop, oldValue, newValue) -> node.setHeight(newValue));
		minWidth.addListener((prop, oldValue, newValue) -> node.setMinWidth(newValue));
		minHeight.addListener((prop, oldValue, newValue) -> node.setMinHeight(newValue));
		maxWidth.addListener((prop, oldValue, newValue) -> node.setMaxWidth(newValue));
		maxHeight.addListener((prop, oldValue, newValue) -> node.setMaxHeight(newValue));
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
