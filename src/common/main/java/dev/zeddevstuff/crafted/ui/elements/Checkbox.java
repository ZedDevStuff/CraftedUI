package dev.zeddevstuff.crafted.ui.elements;

import dev.zeddevstuff.crafted.Crafted;
import dev.zeddevstuff.crafted.Prop;
import dev.zeddevstuff.crafted.annotations.DefineElement;
import dev.zeddevstuff.crafted.annotations.Description;
import dev.zeddevstuff.crafted.input.events.ClickEvent;
import dev.zeddevstuff.crafted.parsing.Parsers;
import dev.zeddevstuff.crafted.platform.ICanvas;
import dev.zeddevstuff.crafted.platform.ITexture;
import dev.zeddevstuff.crafted.platform.ITextureManager;
import dev.zeddevstuff.crafted.rendering.IRendersType;
import dev.zeddevstuff.crafted.types.Color;
import dev.zeddevstuff.crafted.types.Rect;
import dev.zeddevstuff.crafted.ui.CraftedContext;
import dev.zeddevstuff.crafted.ui.CraftedElement;
import dev.zeddevstuff.crafted.utils.ColorUtils;

import java.util.Map;

@DefineElement("Checkbox")
@Description("Checkbox element, can be checked or unchecked")
public class Checkbox extends CraftedElement
{
	@Description("Whether the checkbox is checked or not. Can be null for an indeterminate state")
	public final Prop<Boolean> checked = registerProp(Boolean.class, "checked", false, this::parseBoolean);
	@Description("Whether the checkbox supports an indeterminate state. Checked will allow null if this is true")
	public final Prop<Boolean> indeterminate = registerProp(Boolean.class, "indeterminate", false, Parsers::parseBoolean);
	public Checkbox(CraftedContext ctx, Map<String, String> attributes)
	{
		super(ctx, attributes);
		indeterminate.addListener((prop, oldValue, newValue) -> {
			if(!newValue && checked.get() == null)
				checked.set(false);
		});
	}

	@Override
	public boolean receivesInputEvents() { return true; }
	@Override
	public boolean isFocusable() { return true; }

	@Override
	public void onClick(ClickEvent event)
	{
		if(!state.isDisabled())
		{
			if(indeterminate.get())
			{
				if(checked.get() == null)
					checked.set(false);
				else if(!checked.get())
					checked.set(true);
				else
					checked.set(null);
			}
			else
			{
				if(checked.get() == null)
					checked.set(true);
				else
					checked.set(!checked.get());
			}
		}
	}

	private Boolean parseBoolean(String value, Boolean defaultValue)
	{
		if(indeterminate.get())
			return Parsers.parseBooleanIndeterminate(value, defaultValue);
		else
			return Parsers.parseBoolean(value, false);
	}

	public static void register(Crafted crafted)
	{
		crafted.elements.registerElement(Checkbox.class, "Checkbox", Checkbox::new);
		crafted.renderers.registerRenderer(Checkbox.class, "default", new DefaultRenderer(crafted.textures));
		crafted.renderers.registerRenderer(Checkbox.class, "crafted", new CraftedRenderer());
	}

	public static class DefaultRenderer implements IRendersType<Checkbox>
	{
		@Override
		public Class<?> getElementType() { return Checkbox.class; }
		private final ITexture uncheckedTexture;
		private final ITexture uncheckedHighlightedTexture;
		private final ITexture checkedTexture;
		private final ITexture checkedHighlightedTexture;

		public DefaultRenderer(ITextureManager textureManager)
		{
			this.uncheckedTexture = textureManager.get("minecraft", "widget/checkbox");
			this.uncheckedHighlightedTexture = textureManager.get("minecraft", "widget/checkbox_highlighted");
			this.checkedTexture = textureManager.get("minecraft", "widget/checkbox_selected");
			this.checkedHighlightedTexture = textureManager.get("minecraft", "widget/checkbox_selected_highlighted");
		}
		@Override
		public void render(ICanvas canvas, Checkbox element)
		{
			var layout = element.getLayout();
			if(element.state.isHovered() || element.state.isFocused() && !element.state.isDisabled())
			{
				if(element.checked.get() != null)
				{
					if (element.checked.get())
						canvas.drawTexture(checkedHighlightedTexture, Rect.posAndSize(layout.x, layout.y, layout.width, layout.height));
					else
						canvas.drawTexture(uncheckedHighlightedTexture, Rect.posAndSize(layout.x, layout.y, layout.width, layout.height));
				}
				else
				{
					canvas.drawTexture(uncheckedHighlightedTexture, Rect.posAndSize(layout.x, layout.y, layout.width, layout.height));
					canvas.drawRect(layout.x + 4, layout.y + 4, layout.width - 8, layout.height - 8, ColorUtils.adjustLightness(Color.White, -0.5f));
				}
			}
			else
			{
				if(element.checked.get() != null)
				{
					if (element.checked.get())
						canvas.drawTexture(checkedTexture, Rect.posAndSize(layout.x, layout.y, layout.width, layout.height));
					else
						canvas.drawTexture(uncheckedTexture, Rect.posAndSize(layout.x, layout.y, layout.width, layout.height));
				}
				else
				{
					canvas.drawTexture(uncheckedTexture, Rect.posAndSize(layout.x, layout.y, layout.width, layout.height));
					canvas.drawRect(layout.x + 4, layout.y + 4, layout.width - 8, layout.height - 8, ColorUtils.adjustLightness(Color.White, -0.5f));
				}
			}
		}
	}

	public static class CraftedRenderer implements IRendersType<Checkbox>
	{
		@Override
		public Class<?> getElementType() { return Checkbox.class; }
		@Override
		public void render(ICanvas canvas, Checkbox element)
		{
			var theme = element.ctx.theme;
			Color borderColor, backgroundColor;
			switch (element.style.get())
			{
				case "secondary" -> {
					borderColor = element.state.isDisabled()
						? theme.disabled()
						: (element.state.isFocused() || element.state.isHovered())
						? ColorUtils.bestContrast(theme.onSecondary(), Color.White, Color.Black)
						: theme.onSecondary();
					backgroundColor = theme.secondary();
				}
				case "error" -> {
					borderColor = element.state.isDisabled()
						? theme.disabled()
						: (element.state.isFocused() || element.state.isHovered())
							? ColorUtils.bestContrast(theme.onError(), Color.White, Color.Black)
							: theme.onError();
					backgroundColor = theme.error();
				}
				case "warning" -> {
					borderColor = element.state.isDisabled()
						? theme.disabled()
						: (element.state.isFocused() || element.state.isHovered())
							? ColorUtils.bestContrast(theme.onWarning(), Color.White, Color.Black)
							: theme.onWarning();
					backgroundColor = theme.warning();
				}
				case "success" -> {
					borderColor = element.state.isDisabled()
						? theme.disabled()
						: (element.state.isFocused() || element.state.isHovered())
							? ColorUtils.bestContrast(theme.onSuccess(), Color.White, Color.Black)
							: theme.onSuccess();
					backgroundColor = theme.success();
				}
				default -> {
					borderColor = element.state.isDisabled()
						? theme.disabled()
						: (element.state.isFocused() || element.state.isHovered())
						? ColorUtils.bestContrast(theme.onPrimary(), Color.White, Color.Black)
						: theme.onPrimary();
					backgroundColor = theme.primary();
				}
			}
			var layout = element.getLayout();
			canvas.drawRect(layout.x, layout.y, layout.width, layout.height, borderColor);
			canvas.drawRect(layout.x + 1, layout.y + 1, layout.width - 2, layout.height - 2, backgroundColor);
			if(element.checked.get() != null && element.checked.get())
				canvas.drawRect(layout.x + 4, layout.y + 4, layout.width - 8, layout.height - 8, ColorUtils.adjustLightness(backgroundColor, -0.5f));
			else if (element.checked.get() == null) // Dash
				canvas.drawRect(layout.x + 4, layout.y + layout.height / 2 - 1, layout.width - 8, 2, ColorUtils.adjustLightness(backgroundColor, -0.5f));
		}
	}
}
