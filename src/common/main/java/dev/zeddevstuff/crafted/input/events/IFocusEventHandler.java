package dev.zeddevstuff.crafted.input.events;

import dev.zeddevstuff.crafted.utils.Event;

public interface IFocusEventHandler extends Event.IEventListener<FocusEvent>
{
	void onEvent(FocusEvent event);
}