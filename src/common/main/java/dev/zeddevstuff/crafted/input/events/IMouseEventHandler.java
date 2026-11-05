package dev.zeddevstuff.crafted.input.events;

import dev.zeddevstuff.crafted.utils.Event;

public interface IMouseEventHandler extends Event.IEventListener<MouseEvent>, IUIEventHandler
{
	void onEvent(MouseEvent event);
}