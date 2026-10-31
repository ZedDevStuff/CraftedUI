package dev.zeddevstuff.crafted.input;

import dev.zeddevstuff.crafted.input.events.ClickEvent;
import dev.zeddevstuff.crafted.input.events.MouseEvent;
import dev.zeddevstuff.crafted.ui.CraftedElement;

public class InputHandler
{
	private CraftedElement rootElement = null;
	public void setRootElement(CraftedElement element) { rootElement = element; }
	private CraftedElement hoveredElement;
	public CraftedElement getHoveredElement() { return hoveredElement; }
	private CraftedElement pressedElement;
	public CraftedElement getPressedElement() { return pressedElement; }
	private CraftedElement focusedElement;
	public CraftedElement getFocusedElement() { return focusedElement; }
	private MouseButton pressedButton;
	public MouseButton getPressedButton() { return pressedButton; }

	public void handleMouseMoved(float x, float y)
	{
		if(rootElement == null)
			return;
		var target = rootElement.findTopmostAt(x, y);
		if (target != hoveredElement)
		{
			if (hoveredElement != null)
			{
				hoveredElement.state.set(InteractionState.HOVERED, false);
				dispatchEnterExit(hoveredElement, MouseEvent.exit(hoveredElement, x, y));
			}
			hoveredElement = target;
			if (hoveredElement != null)
			{
				hoveredElement.state.set(InteractionState.HOVERED, true);
				dispatchEnterExit(hoveredElement, MouseEvent.enter(hoveredElement, x, y));
			}
		}
		// update armed: pressed && still over the press target
		if (pressedElement != null)
		{
			boolean over = pressedElement.contains(x, y);
			pressedElement.state.set(InteractionState.ARMED, over);
		}
		if(target != null)
		{
			var event = MouseEvent.move(target, x, y);
			var currentTarget = target;
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
	private void dispatchEnterExit(CraftedElement target, MouseEvent event)
	{
		var currentTarget = target;
		while (!event.isPropagationStopped())
		{
			event.setCurrentTarget(currentTarget);
			if (event.type == MouseEvent.Type.ENTER)
				currentTarget.handleMouseEnter(event);
			else if (event.type == MouseEvent.Type.EXIT)
				currentTarget.handleMouseExit(event);
			currentTarget = currentTarget.getParent();
			if (currentTarget == null)
				break;
		}
	}
	public void handleMouseScrolled(float x, float y, float scrollX, float scrollY)
	{
		if(rootElement == null)
			return;
		var target = rootElement.findTopmostAt(x, y);
		if(target != null)
		{
			var event = MouseEvent.scroll(target, x, y, scrollX, scrollY);
			var currentTarget = target;
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
		var target = rootElement.findTopmostAt(x, y);
		if(target != null)
		{
			pressedElement = target;
			pressedButton = button;
			target.state.set(InteractionState.PRESSED, true);
			target.state.set(InteractionState.ARMED, true);
			if (target.capturesKeyboardWhenFocused() || target.isFocusable())
				focusedElement = target;
			var event = MouseEvent.press(target, x, y, button);
			var currentTarget = target;
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
		if (pressedElement == null || button != pressedButton) return;
		var target = pressedElement; // capture: release goes to the press target
		boolean clicked = target.state.isArmed();
		target.state.set(InteractionState.PRESSED, false);
		target.state.set(InteractionState.ARMED, false);
		pressedElement = null;
		var event = MouseEvent.release(target, x, y, button);
		var currentTarget = target;
		while (!event.isPropagationStopped())
		{
			event.setCurrentTarget(currentTarget);
			currentTarget.handleMouseRelease(event);
			currentTarget = currentTarget.getParent();
			if (currentTarget == null)
				break;
		}
		if (clicked)
			target.handleClick(new ClickEvent(target, x, y, button)); // new event type
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
