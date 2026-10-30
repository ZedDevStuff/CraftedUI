package dev.zeddevstuff.crafted.input;

import dev.zeddevstuff.crafted.input.events.MouseEvent;
import dev.zeddevstuff.crafted.ui.CraftedElement;

public class InputHandler
{
	private CraftedElement rootElement = null;
	public void setRootElement(CraftedElement element) { rootElement = element; }

	public void handleMouseMoved(float x, float y)
	{
		if(rootElement == null)
			return;
		var child = rootElement.findTopmostAt(x, y);
		if(child != null)
		{
			var event = MouseEvent.move(child, x, y);
			var currentTarget = child;
			while (!event.isPropagationStopped())
			{
				event.setCurrentTarget(currentTarget);
				currentTarget.handleMouseMove(event);
				currentTarget = currentTarget.getParent();
				if (currentTarget == null)
					break;
			}
		}
	}
	public void handleMouseScrolled(float x, float y, float scrollX, float scrollY)
	{
		if(rootElement == null)
			return;
		var child = rootElement.findTopmostAt(x, y);
		if(child != null)
		{
			var event = MouseEvent.scroll(child, x, y, scrollX, scrollY);
			var currentTarget = child;
			while (!event.isPropagationStopped())
			{
				event.setCurrentTarget(currentTarget);
				currentTarget.handleMouseScroll(event);
				currentTarget = currentTarget.getParent();
				if (currentTarget == null)
					break;
			}
		}
	}
	public void handleMousePressed(float x, float y, MouseButton button)
	{
		if(rootElement == null)
			return;
		var child = rootElement.findTopmostAt(x, y);
		if(child != null)
		{
			var event = MouseEvent.press(child, x, y, button);
			var currentTarget = child;
			while(!event.isPropagationStopped())
			{
				event.setCurrentTarget(currentTarget);
				currentTarget.handleMousePress(event);
				currentTarget = currentTarget.getParent();
				if(currentTarget == null)
					break;
			}
		}
	}
	public void handleMouseReleased(float x, float y, MouseButton button)
	{
		if(rootElement == null)
			return;
		var child = rootElement.findTopmostAt(x, y);
		if(child != null)
		{
			var event = MouseEvent.release(child, x, y, button);
			var currentTarget = child;
			while (!event.isPropagationStopped())
			{
				event.setCurrentTarget(currentTarget);
				currentTarget.handleMouseRelease(event);
				currentTarget = currentTarget.getParent();
				if (currentTarget == null)
					break;
			}
		}
	}

	public void handleCharTyped(char character, KeyModifiers modifiers)
	{
		if(rootElement == null)
			return;
		//throw new UnsupportedOperationException("Not implemented yet");
	}
	public void handleKeyPressed(int keyCode, int scanCode, KeyModifiers modifiers)
	{
		if(rootElement == null)
			return;
		//throw new UnsupportedOperationException("Not implemented yet");
	}
	public void handleKeyReleased(int keyCode, int scanCode, KeyModifiers modifiers)
	{
		if(rootElement == null)
			return;
		//throw new UnsupportedOperationException("Not implemented yet");
	}
}
