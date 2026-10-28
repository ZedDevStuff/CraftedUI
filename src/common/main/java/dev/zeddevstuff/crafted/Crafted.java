package dev.zeddevstuff.crafted;

import dev.zeddevstuff.crafted.platform.IDispatcher;
import dev.zeddevstuff.crafted.platform.IFont;
import dev.zeddevstuff.crafted.platform.ILocalization;
import dev.zeddevstuff.crafted.rendering.RendererRegistry;
import dev.zeddevstuff.crafted.types.TextSettings;
import dev.zeddevstuff.crafted.ui.CraftedContext;
import dev.zeddevstuff.crafted.ui.CraftedElement;
import dev.zeddevstuff.crafted.ui.ElementRegistry;

import java.util.Map;

public class Crafted
{
	static Crafted instance;
	static void setDefaultInstance(Crafted instance)
	{
		Crafted.instance = instance;
	}

	public final ILocalization localization;
	public final IFont defaultFont;
	public final IDispatcher dispatcher;
	public final TextSettings defaultTextSettings;
	public final ElementRegistry elements = new ElementRegistry();
	public final RendererRegistry renderers = new RendererRegistry();


	public Crafted(ILocalization localization, IFont defaultFont, IDispatcher dispatcher)
	{
		this.localization = localization;
		this.defaultFont = defaultFont;
		this.dispatcher = dispatcher;
		this.defaultTextSettings = new TextSettings(defaultFont, 1.0f, -1);
	}

	public static Crafted defaultInstance()
	{
		if(instance == null)
			throw new RuntimeException("Either forgot to initialize crafted in the versioned entrypoint or called defaultInstance() too early");
		return instance;
	}

	public CraftedElement createElement(CraftedContext ctx, String tag, Map<String, String> attributes)
	{
		return elements.createElement(ctx, tag, attributes);
	}
}
