package dev.zeddevstuff.crafted.input.events;

import dev.zeddevstuff.crafted.utils.Event;

public interface IClickEventHandler extends Event.IEventListener<ClickEvent>
{
    void onEvent(ClickEvent event);
}
