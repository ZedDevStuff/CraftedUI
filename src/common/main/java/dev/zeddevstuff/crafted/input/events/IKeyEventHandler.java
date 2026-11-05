package dev.zeddevstuff.crafted.input.events;

import dev.zeddevstuff.crafted.utils.Event;

public interface IKeyEventHandler extends Event.IEventListener<KeyEvent>
{
	void onEvent(KeyEvent event);
}
