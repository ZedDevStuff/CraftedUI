package dev.zeddevstuff.crafted.input;

import dev.zeddevstuff.crafted.ui.CraftedElement;

public class InputHandler
{
	private CraftedElement rootElement = null;
	public void setRootElement(CraftedElement element) { rootElement = element; }

	public void handleMouseMoved(float x, float y)
	{
		if(rootElement == null)
			return;
		throw new UnsupportedOperationException("Not implemented yet");
	}
	public void handleMouseScrolled(float x, float y, float scrollX, float scrollY)
	{
		if(rootElement == null)
			return;
		throw new UnsupportedOperationException("Not implemented yet");
	}
	public void handleMousePressed(float x, float y, MouseButton button)
	{
		if(rootElement == null)
			return;
		throw new UnsupportedOperationException("Not implemented yet");
	}
	public void handleMouseReleased(float x, float y, MouseButton button)
	{
		if(rootElement == null)
			return;
		throw new UnsupportedOperationException("Not implemented yet");
	}

	public void handleCharTyped(char character, KeyModifiers modifiers)
	{
		if(rootElement == null)
			return;
		throw new UnsupportedOperationException("Not implemented yet");
	}
	public void handleKeyPressed(int keyCode, int scanCode, KeyModifiers modifiers)
	{
		if(rootElement == null)
			return;
		throw new UnsupportedOperationException("Not implemented yet");
	}
	public void handleKeyReleased(int keyCode, int scanCode, KeyModifiers modifiers)
	{
		if(rootElement == null)
			return;
		throw new UnsupportedOperationException("Not implemented yet");
	}
}
