package dev.zeddevstuff.crafted.ui.elements;

import dev.zeddevstuff.crafted.Crafted;
import dev.zeddevstuff.crafted.Prop;
import dev.zeddevstuff.crafted.annotations.DefineElement;
import dev.zeddevstuff.crafted.annotations.Description;
import dev.zeddevstuff.crafted.parsing.DocumentBuilder;
import dev.zeddevstuff.crafted.parsing.ParsedNode;
import dev.zeddevstuff.crafted.parsing.Parsers;
import dev.zeddevstuff.crafted.rendering.IRendersType;
import dev.zeddevstuff.crafted.platform.ICanvas;
import dev.zeddevstuff.crafted.ui.CraftedContext;
import dev.zeddevstuff.crafted.ui.CraftedElement;
import dev.zeddevstuff.crafted.ui.MetaElement;

import java.util.Map;

@DefineElement("Either")
@Description("Displays one of the two child elements based on the 'value' property.")
public class Either extends CraftedElement implements MetaElement
{
	public final Prop<Boolean> value = registerProp("value", false, Parsers::parseBoolean);

	private ParsedNode[] templateNodes;
	private CraftedElement first;
	private CraftedElement second;
	private CraftedElement active;

	public Either(CraftedContext ctx, Map<String, String> attributes)
	{
		super(ctx, attributes);
		value.addListener((prop, oldValue, newValue) -> updateActive());
	}

	@Override
	public void setTemplateNodes(ParsedNode[] templateNodes)
	{
		if(templateNodes.length != 2)
			throw new IllegalArgumentException("Either requires exactly two child templates.");
		this.templateNodes = templateNodes.clone();
		first = DocumentBuilder.buildTree(ctx, templateNodes[0]);
		second = DocumentBuilder.buildTree(ctx, templateNodes[1]);
		updateActive();
	}

	private void updateActive()
	{
		if(first == null || second == null)
			return;
		var next = Boolean.TRUE.equals(value.get()) ? first : second;
		if(active == next)
			return;
		if(active != null)
			removeChild(active);
		active = next;
		addChild(active);
	}

	@Override
	protected void copyExtraStateTo(CraftedElement clone)
	{
		if(clone instanceof Either either && templateNodes != null)
			either.setTemplateNodes(templateNodes);
	}

	public static void register(Crafted crafted)
	{
		crafted.elements.registerElement(Either.class, "Either", Either::new);
		crafted.renderers.registerRenderer(Either.class, "default", new Renderer());
	}

	private static class Renderer implements IRendersType<Either>
	{
		@Override
		public Class<?> getElementType() { return Either.class; }
		@Override
		public void render(ICanvas canvas, Either element) {}
	}
}