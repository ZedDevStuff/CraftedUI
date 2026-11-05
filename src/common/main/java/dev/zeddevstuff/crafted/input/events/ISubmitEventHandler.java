package dev.zeddevstuff.crafted.input.events;

import dev.zeddevstuff.crafted.utils.Event;

public interface ISubmitEventHandler extends Event.IEventListener<SubmitEvent>
{
	void onEvent(SubmitEvent event);
}
