package dev.zeddevstuff.crafted.ui.elements;

import dev.zeddevstuff.crafted.Crafted;
import dev.zeddevstuff.crafted.Prop;
import dev.zeddevstuff.crafted.annotations.DefineElement;
import dev.zeddevstuff.crafted.annotations.Description;
import dev.zeddevstuff.crafted.parsing.ParsedNode;
import dev.zeddevstuff.crafted.parsing.Parsers;
import dev.zeddevstuff.crafted.platform.ICanvas;
import dev.zeddevstuff.crafted.platform.IText;
import dev.zeddevstuff.crafted.rendering.IRendersType;
import dev.zeddevstuff.crafted.ui.CraftedContext;
import dev.zeddevstuff.crafted.ui.CraftedElement;
import dev.zeddevstuff.crafted.ui.MetaElement;
import org.appliedenergistics.yoga.YogaEdge;
import org.appliedenergistics.yoga.YogaGutter;
import org.appliedenergistics.yoga.YogaPhysicalEdge;

import java.util.Map;

@DefineElement("Tabs")
@Description("A tabbed container element")
public class Tabs extends CraftedElement
{
	public final Prop<Integer> currentTabIndex = registerProp(Integer.class, "currentTabIndex", 0, Parsers::parseInt);
	public final Prop<YogaPhysicalEdge> tabBarPosition = registerProp(YogaPhysicalEdge.class, "tabBarPosition", YogaPhysicalEdge.TOP, Parsers::parseYogaPhysicalEdge);

	public Tabs(CraftedContext ctx, Map<String, String> attributes)
	{
		super(ctx, attributes);
	}

	@Override
	protected boolean acceptsChild(CraftedElement child)
	{
		if(child instanceof Tab)
			return true;
		ctx.crafted.logger.debug("Tabs element does not accept child of type " + child.getClass().getName());
		return false;
	}

	public static void register(Crafted crafted)
	{
		crafted.elements.registerElement(Tabs.class, "Tabs", Tabs::new);
		crafted.elements.registerElement(Tab.class, "Tab", Tab::new);
//		crafted.renderers.registerRenderer(Tabs.class, "default", new DefaultTabsRenderer());
//		crafted.renderers.registerRenderer(Tabs.class, "crafted", new CraftedTabsRenderer());
	}

	public static class DefaultTabsRenderer implements IRendersType<Tabs>
	{
		@Override
		public Class<?> getElementType() { return Tabs.class; }

		@Override
		public void render(ICanvas canvas, Tabs element)
		{

		}
	}

	@DefineElement("Tab")
	@Description("A tab in a Tabs element")
	public static class Tab extends ScrollView implements MetaElement
	{
		private CraftedElement[] content;

		public final Prop<IText> title = registerProp(IText.class, "title", IText.literal("Tab"), Parsers::parseText);

		public Tab(CraftedContext ctx, Map<String, String> attributes)
		{
			super(ctx, attributes);
		}

		@Override
		public void setTemplateNodes(ParsedNode[] templateNodes)
		{

		}
	}
}
