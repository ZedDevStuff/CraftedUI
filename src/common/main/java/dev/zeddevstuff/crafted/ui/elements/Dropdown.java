package dev.zeddevstuff.crafted.ui.elements;

import dev.zeddevstuff.crafted.Prop;
import dev.zeddevstuff.crafted.annotations.DefineElement;
import dev.zeddevstuff.crafted.input.MouseButton;
import dev.zeddevstuff.crafted.input.events.ClickEvent;
import dev.zeddevstuff.crafted.input.events.FocusEvent;
import dev.zeddevstuff.crafted.platform.ICanvas;
import dev.zeddevstuff.crafted.rendering.IRenders;
import dev.zeddevstuff.crafted.types.Color;
import dev.zeddevstuff.crafted.ui.CraftedContext;
import dev.zeddevstuff.crafted.ui.CraftedElement;
import dev.zeddevstuff.crafted.utils.ColorUtils;
import dev.zeddevstuff.crafted.utils.ObjectPool;
import dev.zeddevstuff.crafted.utils.Typed;
import dev.zeddevstuff.crafted.utils.TypedList;
import org.appliedenergistics.yoga.YogaPositionType;
import org.appliedenergistics.yoga.style.StyleLength;
import org.appliedenergistics.yoga.style.StyleSizeLength;

import java.util.Map;

@DefineElement("Dropdown")
public class Dropdown extends CraftedElement
{
	public final Prop<TypedList<?>> options = registerProp(TypedList.class, "options", null, this::parseList);
	public final Prop<IStringProvider> labelProvider = registerProp(IStringProvider.class, "labelProvider", Object::toString, this::parseFunction);

	private final ScrollView popup;

	private boolean isOpen = false;

	public Dropdown(CraftedContext ctx, Map<String, String> attributes)
	{
		super(ctx, attributes);
		popup = new ScrollView(ctx, Map.of());
		popup.positionType.set(YogaPositionType.ABSOLUTE);
		popup.zIndex.set(1000);
	}

	private TypedList<?> parseList(String s, TypedList<?> list)
	{
		var obj = ctx.getVar(TypedList.class, s);
		if(obj != null)
			return obj.value;
		return null;
	}

	private IStringProvider parseFunction(String s, IStringProvider function)
	{
		var obj = ctx.getVar(IStringProvider.class, s);
		if(obj != null)
			return obj.value;
		return Object::toString;
	}

	public void togglePopup()
	{
		isOpen = !isOpen;
		if(isOpen)
		{
			setPopupLayout();
			addChild(popup);
		}
		else
		{
			removeChild(popup);
		}
	}

	private void setPopupLayout()
	{
		var layout = getLayout();
		popup.left.set(StyleLength.points(layout.x));
		popup.top.set(StyleLength.points(layout.y + layout.height));
		popup.width.set(StyleSizeLength.points(layout.width));
		popup.height.set(StyleSizeLength.points(50));
	}

	@Override
	public boolean receivesInputEvents() { return true; }
	@Override
	public boolean isFocusable() { return true; }

	@Override
	public void handleFocusLost(FocusEvent event)
	{
		if(isOpen)
			togglePopup();
	}

	@Override
	public void onClick(ClickEvent event)
	{
		if(event.button == MouseButton.LEFT)
			togglePopup();
		event.consume();
	}

	public static void register(dev.zeddevstuff.crafted.Crafted crafted)
	{
		crafted.elements.registerElement(Dropdown.class, "Dropdown", Dropdown::new);
		crafted.renderers.registerRenderer(Dropdown.class, "default", new DefaultDropdownRenderer());
		crafted.renderers.registerRenderer(DropdownOption.class, "default", new DropdownOption.DefaultRenderer());
		crafted.renderers.registerRenderer(DropdownOption.class, "crafted", new DropdownOption.CraftedRenderer());
	}

	public static class DefaultDropdownRenderer implements IRenders<Dropdown>
	{
		@Override
		public void render(ICanvas canvas, Dropdown element)
		{
			element.setPopupLayout();
			var layout = element.getLayout();
			var theme = element.ctx.theme;
			Color borderColor, backgroundColor, elevationColor;
			switch (element.style.get())
			{
				case "secondary" -> {
					borderColor = element.state.isDisabled()
						? theme.disabled()
						: (element.state.isFocused() || element.state.isHovered())
						? ColorUtils.bestContrast(theme.onSecondary(), Color.White, Color.Black)
						: theme.onSecondary();
					backgroundColor = theme.secondary();
					elevationColor = ColorUtils.adjustLightness(theme.secondary(), -0.15f);
				}
				case "error" -> {
					borderColor = element.state.isDisabled()
						? theme.disabled()
						: (element.state.isFocused() || element.state.isHovered())
						? ColorUtils.bestContrast(theme.onError(), Color.White, Color.Black)
						: theme.onError();
					backgroundColor = theme.error();
					elevationColor = ColorUtils.adjustLightness(theme.error(), -0.15f);
				}
				case "warning" -> {
					borderColor = element.state.isDisabled()
						? theme.disabled()
						: (element.state.isFocused() || element.state.isHovered())
						? ColorUtils.bestContrast(theme.onWarning(), Color.White, Color.Black)
						: theme.onWarning();
					backgroundColor = theme.warning();
					elevationColor = ColorUtils.adjustLightness(theme.warning(), -0.15f);
				}
				case "success" -> {
					borderColor = element.state.isDisabled()
						? theme.disabled()
						: (element.state.isFocused() || element.state.isHovered())
						? ColorUtils.bestContrast(theme.onSuccess(), Color.White, Color.Black)
						: theme.onSuccess();
					backgroundColor = theme.success();
					elevationColor = ColorUtils.adjustLightness(theme.success(), -0.15f);
				}
				default -> {
					borderColor = element.state.isDisabled()
						? theme.disabled()
						: (element.state.isFocused() || element.state.isHovered())
						? ColorUtils.bestContrast(theme.onPrimary(), Color.White, Color.Black)
						: theme.onPrimary();
					backgroundColor = theme.primary();
					elevationColor = ColorUtils.adjustLightness(theme.primary(), -0.15f);
				}
			}
			canvas.drawRect(layout.x, layout.y, layout.width, layout.height, borderColor);
			canvas.drawRect(layout.x + 1, layout.y + 1, layout.width - 2, layout.height - 2, backgroundColor);
		}
	}

	public interface IStringProvider
	{
		String getString(Object obj);
	}

	private class DropdownOption extends CraftedElement
	{
		public DropdownOption()
		{
			super(Dropdown.this.ctx, Map.of());
		}

		@Override
		public boolean receivesInputEvents()
		{
			return true;
		}

		@Override
		public boolean isFocusable()
		{
			return true;
		}

		private static class DefaultRenderer implements IRenders<DropdownOption>
		{
			@Override
			public void render(ICanvas canvas, DropdownOption element)
			{

			}

			@Override
			public void afterRender(ICanvas canvas, DropdownOption element)
			{

			}
		}

		private static class CraftedRenderer implements IRenders<DropdownOption>
		{
			@Override
			public void render(ICanvas canvas, DropdownOption element)
			{

			}

			@Override
			public void afterRender(ICanvas canvas, DropdownOption element)
			{

			}
		}
	}
}
