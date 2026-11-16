package dev.zeddevstuff.crafted.ui.elements;

import dev.zeddevstuff.crafted.platform.IText;
import dev.zeddevstuff.crafted.types.StyleLength4;
import dev.zeddevstuff.crafted.ui.CraftedContext;
import org.appliedenergistics.yoga.YogaPositionType;
import org.appliedenergistics.yoga.style.StyleLength;
import org.appliedenergistics.yoga.style.StyleSizeLength;

import java.util.Map;

public class Tooltip extends Box
{
	private final Text text;

	public Tooltip(CraftedContext ctx)
	{
		super(ctx, Map.of());
		positionType.set(YogaPositionType.ABSOLUTE);
		zIndex.set(Integer.MAX_VALUE);
		width.set(StyleSizeLength.points(220));
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
}