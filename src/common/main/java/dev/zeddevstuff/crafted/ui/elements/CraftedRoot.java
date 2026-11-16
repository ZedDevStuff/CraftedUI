package dev.zeddevstuff.crafted.ui.elements;

import dev.zeddevstuff.crafted.Crafted;
import dev.zeddevstuff.crafted.Prop;
import dev.zeddevstuff.crafted.annotations.DefineElement;
import dev.zeddevstuff.crafted.annotations.Description;
import dev.zeddevstuff.crafted.parsing.Parsers;
import dev.zeddevstuff.crafted.platform.IText;
import dev.zeddevstuff.crafted.ui.CraftedContext;
import dev.zeddevstuff.crafted.ui.CraftedElement;
import org.appliedenergistics.yoga.style.StyleLength;

import java.util.Map;

@DefineElement("Crafted")
@Description("Root element of a Crafted document. All other elements must be children of this element")
public class CraftedRoot extends CraftedElement
{
	private static final float TOOLTIP_DELAY = 0.5f;
	private final Tooltip tooltipElement;
	private CraftedElement tooltipOwner;
	private float tooltipTime;

	public static void register(Crafted crafted)
	{
		crafted.elements.registerElement(CraftedRoot.class, "Crafted", CraftedRoot::new);
	}
	@Description("Title of the document, used for screen title and other purposes")
	public final Prop<IText> title = registerProp(IText.class, "title", IText.literal("Crafted Document"), Parsers::parseText);
	@Description("The renderer to use for elements in the tree")
	public final Prop<String> treeRenderer = registerProp("treeRenderer", "default", Parsers::parseString);

	public CraftedRoot(CraftedContext ctx, Map<String, String> attributes)
	{
		super(ctx, attributes);
		tooltipElement = createTooltip(ctx);
		treeRenderer.addListener((prop, oldValue, newValue) -> {
			ctx.currentRenderer = newValue;
			setRendererRecursive(newValue);
		});
	}

	protected Tooltip createTooltip(CraftedContext ctx)
	{
		return new Tooltip(ctx);
	}

	public void updateTooltip(float deltaTime)
	{
		var hovered = ctx.inputHandler.getHoveredElement();
		var nextOwner = hovered != null && hovered.tooltip.get() != null && !hovered.state.isDisabled()
			? hovered
			: null;

		if(nextOwner != tooltipOwner)
		{
			tooltipOwner = nextOwner;
			tooltipTime = 0;
			if(isTooltipVisible())
				removeChild(tooltipElement);
		}
		else
		{
			tooltipTime += deltaTime;
		}

		if(tooltipOwner != null && tooltipTime >= TOOLTIP_DELAY && !isTooltipVisible())
		{
			tooltipElement.setText(tooltipOwner.tooltip.get());
			addChild(tooltipElement);
		}
	}

	public void positionTooltip(float viewportWidth, float viewportHeight)
	{
		if(!isTooltipVisible())
			return;

		var ownerLayout = tooltipOwner.getLayout();
		var tooltipLayout = tooltipElement.getLayout();
		float x = Math.max(0, Math.min(ownerLayout.x, viewportWidth - tooltipLayout.width));
		float y = ownerLayout.y + ownerLayout.height + 4;
		if(y + tooltipLayout.height > viewportHeight)
			y = ownerLayout.y - tooltipLayout.height - 4;
		x = Math.max(0, Math.min(x, viewportWidth - tooltipLayout.width));
		tooltipElement.left.set(StyleLength.points(x));
		tooltipElement.top.set(StyleLength.points(Math.max(0, y)));
	}

	private boolean isTooltipVisible()
	{
		return tooltipElement.getParent() == this;
	}
}
