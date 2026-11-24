package dev.zeddevstuff.crafted.ui.elements;

import dev.zeddevstuff.crafted.Crafted;
import dev.zeddevstuff.crafted.Prop;
import dev.zeddevstuff.crafted.annotations.DefineElement;
import dev.zeddevstuff.crafted.annotations.Description;
import dev.zeddevstuff.crafted.parsing.UIDocumentBuilder;
import dev.zeddevstuff.crafted.parsing.ParsedNode;
import dev.zeddevstuff.crafted.parsing.Parsers;
import dev.zeddevstuff.crafted.rendering.IRendersType;
import dev.zeddevstuff.crafted.platform.ICanvas;
import dev.zeddevstuff.crafted.ui.UIContext;
import dev.zeddevstuff.crafted.ui.CraftedElement;
import dev.zeddevstuff.crafted.ui.MetaElement;

import java.util.Map;

@DefineElement("Conditional")
@Description("Displays its single child element only while the 'expanded' property is true.")
public class Conditional extends CraftedElement implements MetaElement
{
	public final Prop<Boolean> expanded = registerProp("expanded", false, Parsers::parseBoolean);

	private ParsedNode[] templateNodes;
	private CraftedElement child;
	private boolean attached;

	public Conditional(UIContext ctx, Map<String, String> attributes)
	{
		super(ctx, attributes);
		expanded.addListener((prop, oldValue, newValue) -> updateChild());
	}

	@Override
	public void setTemplateNodes(ParsedNode[] templateNodes)
	{
		if(templateNodes.length != 1)
			throw new IllegalArgumentException("Conditional requires exactly one child template.");
		this.templateNodes = templateNodes.clone();

		if(child != null)
		{
			if(attached)
				removeChild(child, false);
			child.unbind();
			child = null;
			attached = false;
		}

		updateChild();
	}

	private void updateChild()
	{
		if(templateNodes == null)
			return;

		if(Boolean.TRUE.equals(expanded.get()))
		{
			if(child == null)
				child = UIDocumentBuilder.buildTree(ctx, templateNodes[0]);

			if(!attached)
			{
				addChild(child);
				attached = true;
			}
		}
		else if(attached)
		{
			removeChild(child, false);
			attached = false;
		}
	}

	@Override
	public void unbind()
	{
		super.unbind();
		if(child != null)
			child.unbind();
	}

	@Override
	protected void copyExtraStateTo(CraftedElement clone)
	{
		if(clone instanceof Conditional conditional && templateNodes != null)
			conditional.setTemplateNodes(templateNodes);
	}

	public static void register(Crafted crafted)
	{
		crafted.elements.registerElement(Conditional.class, "Conditional", Conditional::new);
		crafted.renderers.registerRenderer(Conditional.class, "default", new Renderer());
	}

	private static class Renderer implements IRendersType<Conditional>
	{
		@Override
		public Class<?> getElementType() { return Conditional.class; }
		@Override
		public void render(ICanvas canvas, Conditional element) {}
	}
}