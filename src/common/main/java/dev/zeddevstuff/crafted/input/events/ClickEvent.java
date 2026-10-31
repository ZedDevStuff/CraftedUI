package dev.zeddevstuff.crafted.input.events;

import dev.zeddevstuff.crafted.input.MouseButton;
import dev.zeddevstuff.crafted.ui.CraftedElement;

public class ClickEvent extends UIEvent
{
    public final float x;
    public final float y;
    public final MouseButton button;

    public ClickEvent(CraftedElement target, float x, float y, MouseButton button)
    {
        super(target);
        this.x = x;
        this.y = y;
        this.button = button;
    }
}
