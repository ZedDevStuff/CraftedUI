package dev.zeddevstuff.crafted.input.events;

import dev.zeddevstuff.crafted.utils.Event;

public interface ISubmitEventHandler extends Event.IEventListener<SubmitEvent>, IUIEventHandler
{
	void onEvent(SubmitEvent event);
}
