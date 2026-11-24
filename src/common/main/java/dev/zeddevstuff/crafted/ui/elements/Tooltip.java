package dev.zeddevstuff.crafted.ui.elements;

import dev.zeddevstuff.crafted.platform.IText;
import dev.zeddevstuff.crafted.types.StyleLength4;
import dev.zeddevstuff.crafted.ui.UIContext;
import dev.zeddevstuff.crafted.ui.CraftedElement;
import org.appliedenergistics.yoga.YogaPositionType;
import org.appliedenergistics.yoga.style.StyleLength;

import java.util.Map;

public class Tooltip extends Box
{
	private final Text text;

	public Tooltip(UIContext ctx)
	{
		super(ctx, Map.of());
		positionType.set(YogaPositionType.ABSOLUTE);
		zIndex.set(Integer.MAX_VALUE);
		padding.set(new StyleLength4(
			StyleLength.points(4), StyleLength.points(4), StyleLength.points(4), StyleLength.points(4)));
		color.set(ctx.theme.surfaceVariant());

		text = new Text(ctx, Map.of());
		text.textWrap.set(true);
		text.text.set(IText.literal(""));
		text.color.set(ctx.theme.onSurface());
		addChild(text);
	}

	void setText(IText value)
	{
		text.text.set(value);
	}
	// TODO: Do something with this.
	void setContent(CraftedElement content)
	{
		if(content == text)
			return;
		if(children.contains(content))
			return;
		removeChild(text);
		addChild(content);
	}
}