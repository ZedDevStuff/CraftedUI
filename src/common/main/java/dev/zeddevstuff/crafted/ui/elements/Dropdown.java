package dev.zeddevstuff.crafted.ui.elements;

import dev.zeddevstuff.crafted.Prop;
import dev.zeddevstuff.crafted.ProxyProp;
import dev.zeddevstuff.crafted.annotations.DefineElement;
import dev.zeddevstuff.crafted.input.MouseButton;
import dev.zeddevstuff.crafted.input.events.ClickEvent;
import dev.zeddevstuff.crafted.input.events.FocusEvent;
import dev.zeddevstuff.crafted.platform.ICanvas;
import dev.zeddevstuff.crafted.rendering.IRenders;
import dev.zeddevstuff.crafted.rendering.IRendersType;
import dev.zeddevstuff.crafted.types.Color;
import dev.zeddevstuff.crafted.ui.CraftedContext;
import dev.zeddevstuff.crafted.ui.CraftedElement;
import dev.zeddevstuff.crafted.utils.ColorUtils;
import dev.zeddevstuff.crafted.utils.TypedList;
import org.appliedenergistics.yoga.YogaDimension;
import org.appliedenergistics.yoga.YogaPositionType;
import org.appliedenergistics.yoga.style.StyleLength;
import org.appliedenergistics.yoga.style.StyleSizeLength;
import org.appliedenergistics.yoga.style.YogaStyle;

import java.util.Locale;
import java.util.Map;

// This whole thing is a mess but it works so i'm gonna ignore everything about it until it blows up in my face sometime in the future :)
@DefineElement("Dropdown")
public class Dropdown extends CraftedElement
{
	public final Prop<TypedList<?>> options = registerProp(TypedList.class, "options", null, this::parseList);
	public final Prop<IStringProvider> labelProvider = registerProp(IStringProvider.class, "labelProvider", Object::toString, this::parseFunction);
	public final Prop<Object> selectedValue = new Prop<>(Object.class, "selectedValue", null, (a,b) -> null);
	public final ProxyProp<Class<?>> valueType = new ProxyProp<>(Class.class, "valueType", s -> {},  () -> options.get() != null ? options.get().getElementType() : null);
	private IRendersType<Object> customOptionRenderer = null;

	private final ScrollView popup;

	private boolean isOpen = false;

	public Dropdown(CraftedContext ctx, Map<String, String> attributes)
	{
		super(ctx, attributes);
		popup = new ScrollView(ctx, Map.of());
		popup.horizontalScrollbar.set(false);
		popup.color.set(Color.Black);
		popup.positionType.set(YogaPositionType.ABSOLUTE);
		popup.zIndex.set(1000);
		options.addListener(this::optionsUpdated);
		renderer.addListener((a,b, newValue) -> rendererChangeHandler(newValue));
		rendererChangeHandler(renderer.get());
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

	private void rendererChangeHandler(IRenders newValue)
	{
		// This relies on my naming convention. Respect it or this will break. You're also free to make your own Dropdown.
		String rendererName = newValue.getClass().getSimpleName().toLowerCase(Locale.ROOT).replace("renderer", "");
		updatePopupRenderers(rendererName);
	}
	private void updatePopupRenderers(String renderer)
	{
		popup.setRenderer(renderer);
		for(var child : popup.getChildren())
			if(child instanceof DropdownOption option)
				option.setRenderer(renderer);
	}

	public void setSelected(Object value)
	{
		var optionsList = options.get();
		if(optionsList != null && optionsList.contains(value))
			selectedValue.set(value);
		if(isOpen)
			togglePopup();
	}

	private void optionsUpdated(Prop<TypedList<?>> prop, TypedList<?> oldValue, TypedList<?> newValue)
	{
		if(isOpen)
			togglePopup();
		popup.removeChildren(popup.getChildren());
		if(newValue == null)
			return;
		for(var option : newValue)
		{
			var optionElement = new DropdownOption(option);
			popup.addChild(optionElement);
		}
		setSelected(newValue.getFirst());
	}

	public void togglePopup()
	{
		isOpen = !isOpen;
		if(isOpen)
		{
			setPopupLayout();
			popup.scrollY.set(0);
			popup.scrollX.set(0);
			addChild(popup);
		}
		else
		{
			removeChild(popup);
			popup.scrollY.set(0);
			popup.scrollX.set(0);
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
		if(isOpen && (event.newFocus == null || !isAncestorOf(event.newFocus)))
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
		crafted.renderers.registerRenderer(Dropdown.class, "default", new DefaultRenderer());
		crafted.renderers.registerRenderer(Dropdown.class, "crafted", new CraftedRenderer());
		crafted.renderers.registerRenderer(DropdownOption.class, "default", new DropdownOption.DefaultRenderer());
		crafted.renderers.registerRenderer(DropdownOption.class, "crafted", new DropdownOption.CraftedRenderer());
	}

	public static class DefaultRenderer implements IRendersType<Dropdown>
	{
		@Override
		public Class<?> getElementType() { return Dropdown.class; }
		@Override
		public void render(ICanvas canvas, Dropdown element)
		{
			element.setPopupLayout();
			var layout = element.getLayout();
			var label = element.labelProvider.get().getString(element.selectedValue.get());
			var textSize = element.ctx.crafted.defaultTextSettings.font().measureText(label, 1, 0);
			int centeredX = (int) (layout.x + (layout.width - textSize.width()) / 2);
			int centeredY = (int) (layout.y + (layout.height - textSize.height()) / 2);
			var theme = element.ctx.theme;
			Color borderColor = element.state.isDisabled()
				? theme.disabled()
				: (element.state.isFocused() || element.state.isHovered())
					? Color.White
					: Color.Gray;
			canvas.drawRect(layout.x, layout.y, layout.width, layout.height, borderColor);
			canvas.drawRect(layout.x + 1, layout.y + 1, layout.width - 2, layout.height - 2, Color.Black);
			canvas.drawText(label, centeredX, centeredY, element.ctx.crafted.defaultTextSettings, Color.White);
		}
	}
	public static class CraftedRenderer implements IRendersType<Dropdown>
	{
		@Override
		public Class<?> getElementType() { return Dropdown.class; }
		@Override
		public void render(ICanvas canvas, Dropdown element)
		{
			element.setPopupLayout();
			var layout = element.getLayout();
			var label = element.labelProvider.get().getString(element.selectedValue.get());
			var textSize = element.ctx.crafted.defaultTextSettings.font().measureText(label, 1, 0);
			int centeredX = (int) (layout.x + (layout.width - textSize.width()) / 2);
			int centeredY = (int) (layout.y + (layout.height - textSize.height()) / 2);
			var theme = element.ctx.theme;
			Color borderColor, backgroundColor, textColor;
			switch (element.style.get())
			{
				case "secondary" -> {
					borderColor = element.state.isDisabled()
						? theme.disabled()
						: element.state.isHoveredOrFocused()
							? ColorUtils.bestContrast(theme.onSecondary(), Color.White, Color.Black)
							: theme.onSecondary();
					backgroundColor = theme.secondary();
					textColor = theme.onSecondary();
				}
				case "error" -> {
					borderColor = element.state.isDisabled()
						? theme.disabled()
						: element.state.isHoveredOrFocused()
							? ColorUtils.bestContrast(theme.onError(), Color.White, Color.Black)
							: theme.onError();
					backgroundColor = theme.error();
					textColor = theme.onError();
				}
				case "warning" -> {
					borderColor = element.state.isDisabled()
						? theme.disabled()
						: element.state.isHoveredOrFocused()
							? ColorUtils.bestContrast(theme.onWarning(), Color.White, Color.Black)
							: theme.onWarning();
					backgroundColor = theme.warning();
					textColor = theme.onWarning();
				}
				case "success" -> {
					borderColor = element.state.isDisabled()
						? theme.disabled()
						: element.state.isHoveredOrFocused()
							? ColorUtils.bestContrast(theme.onSuccess(), Color.White, Color.Black)
							: theme.onSuccess();
					backgroundColor = theme.success();
					textColor = theme.onSuccess();
				}
				case "info" -> {
					borderColor = element.state.isDisabled()
						? theme.disabled()
						: element.state.isHoveredOrFocused()
							? ColorUtils.bestContrast(theme.onInfo(), Color.White, Color.Black)
							: theme.onInfo();
					backgroundColor = theme.info();
					textColor = theme.onInfo();
				}
				default -> {
					borderColor = element.state.isDisabled()
						? theme.disabled()
						: element.state.isHoveredOrFocused()
							? ColorUtils.bestContrast(theme.onPrimary(), Color.White, Color.Black)
							: theme.onPrimary();
					backgroundColor = theme.primary();
					textColor = theme.onPrimary();
				}
			}
			canvas.drawRect(layout.x, layout.y, layout.width, layout.height, borderColor);
			canvas.drawRect(layout.x + 1, layout.y + 1, layout.width - 2, layout.height - 2, backgroundColor);
			canvas.drawText(label, centeredX, centeredY, element.ctx.crafted.defaultTextSettings, textColor);
		}
	}

	public interface IStringProvider
	{
		String getString(Object obj);
	}

	private class DropdownOption extends CraftedElement
	{
		public final Object value;
		public DropdownOption(Object value)
		{
			super(Dropdown.this.ctx, Map.of());
			this.value = value;
			String renderer = Dropdown.this.renderer.get().getClass().getSimpleName().toLowerCase(Locale.ROOT).replace("renderer", "");
			setRenderer(renderer);
		}

		public boolean isSelected()
		{
			return value.equals(Dropdown.this.selectedValue.get());
		}

		@Override
		protected YogaStyle initLayoutNode(YogaStyle style, Map<String, String> attributes)
		{
			style.setDimension(YogaDimension.HEIGHT, StyleSizeLength.points(20));
			return style;
		}

		public IRendersType<Object> customRenderer()
		{
			return customOptionRenderer;
		}
		public IStringProvider labelProvider()
		{
			return labelProvider.get();
		}

		public void onClick(ClickEvent event)
		{
			if(event.button == null || event.button == MouseButton.LEFT)
				setSelected(value);
			event.consume();
		}

		@Override
		public boolean receivesInputEvents() { return true; }
		@Override
		public boolean isFocusable() { return true; }

		private static class DefaultRenderer implements IRendersType<DropdownOption>
		{
			@Override
			public Class<?> getElementType() { return DropdownOption.class; }
			@Override
			public void render(ICanvas canvas, DropdownOption element)
			{
				var label = element.labelProvider().getString(element.value);
				var textSize = element.ctx.crafted.defaultFont.measureText(label, 1, 0);
				var layout = element.getLayout();
				int centeredX = (int) (layout.x + (layout.width - textSize.width()) / 2);
				int centeredY = (int) (layout.y + (layout.height - textSize.height()) / 2);
				canvas.drawRect(layout.x, layout.y, layout.width, layout.height, element.state.isHoveredOrFocused() || element.isSelected() ? ColorUtils.adjustLightness(Color.Gray, 0.2f) : Color.Black);
				canvas.drawText(label, centeredX, centeredY, element.ctx.crafted.defaultTextSettings, Color.White);
			}
		}

		private static class CraftedRenderer implements IRendersType<DropdownOption>
		{
			@Override
			public Class<?> getElementType() { return DropdownOption.class; }
			@Override
			public void render(ICanvas canvas, DropdownOption element)
			{
				var layout = element.getLayout();
				var textSize = element.ctx.crafted.defaultFont.measureText(element.labelProvider().getString(element.value), 1, 0);
				int centeredX = (int) (layout.x + (layout.width - textSize.width()) / 2);
				int centeredY = (int) (layout.y + (layout.height - textSize.height()) / 2);
				Color backgroundColor, textColor;
				switch (element.style.get())
				{
					case "secondary" -> {
						backgroundColor = element.state.isHoveredOrFocused() || element.isSelected() ? element.ctx.theme.secondary() : ColorUtils.adjustLightness(element.ctx.theme.secondary(), -0.15f);
						textColor = element.ctx.theme.onSecondary();
					}
					case "error" -> {
						backgroundColor = element.state.isHoveredOrFocused() || element.isSelected() ? element.ctx.theme.error() : ColorUtils.adjustLightness(element.ctx.theme.error(), -0.15f);
						textColor = element.ctx.theme.onError();
					}
					case "warning" -> {
						backgroundColor = element.state.isHoveredOrFocused() || element.isSelected() ? element.ctx.theme.warning() : ColorUtils.adjustLightness(element.ctx.theme.warning(), -0.15f);
						textColor = element.ctx.theme.onWarning();
					}
					case "success" -> {
						backgroundColor = element.state.isHoveredOrFocused() || element.isSelected() ? element.ctx.theme.success() : ColorUtils.adjustLightness(element.ctx.theme.success(), -0.15f);
						textColor = element.ctx.theme.onSuccess();
					}
					case "info" -> {
						backgroundColor = element.state.isHoveredOrFocused() || element.isSelected() ? element.ctx.theme.info() : ColorUtils.adjustLightness(element.ctx.theme.info(), -0.15f);
						textColor = element.ctx.theme.onInfo();
					}
					default -> {
						backgroundColor = element.state.isHoveredOrFocused() || element.isSelected() ? element.ctx.theme.primary() : ColorUtils.adjustLightness(element.ctx.theme.primary(), -0.15f);
						textColor = element.ctx.theme.onPrimary();
					}
				}
				canvas.drawRect(layout.x, layout.y, layout.width, layout.height, backgroundColor);
				canvas.drawText(element.labelProvider().getString(element.value), centeredX, centeredY, element.ctx.crafted.defaultTextSettings, textColor);
			}
		}
	}
}
