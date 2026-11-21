package dev.zeddevstuff.crafted.input.events;

import dev.zeddevstuff.crafted.input.MouseButton;
import dev.zeddevstuff.crafted.ui.CraftedElement;

public class ClickEvent extends UIEvent
{
    public final Source source;
    public final float x;
    public final float y;
    public final MouseButton button;

    public ClickEvent(CraftedElement target, Source source, float x, float y, MouseButton button)
    {
        super(target);
        this.source = source;
        this.x = x;
        this.y = y;
        this.button = button;
    }

    public float localX()
    {
        return localX(0);
    }
    public float localX(float extraOffset)
    {
        var element = getCurrentTarget() != null ? getCurrentTarget() : getTarget();
        return element.toLocalX(x) + extraOffset;
    }
    public float localY()
    {
        return localY(0);
    }
    public float localY(float extraOffset)
    {
        var element = getCurrentTarget() != null ? getCurrentTarget() : getTarget();
        return element.toLocalY(y) + extraOffset;
    }

    public enum Source
    {
        MOUSE,
        KEYBOARD
    }
}
