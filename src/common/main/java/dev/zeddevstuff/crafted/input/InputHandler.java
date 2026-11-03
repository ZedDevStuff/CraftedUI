package dev.zeddevstuff.crafted.input;

import dev.zeddevstuff.crafted.input.events.ClickEvent;
import dev.zeddevstuff.crafted.input.events.FocusEvent;
import dev.zeddevstuff.crafted.input.events.KeyEvent;
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

	private CraftedElement[] focusableElements()
	{
		if (rootElement == null) return new CraftedElement[0];
		return rootElement.findElements(e -> e.isFocusable() && !e.state.isDisabled());
	}

	private CraftedElement directionalTarget(CraftedElement current, int dirX, int dirY)
	{
		if (current == null || current.getLayout() == null) return null;
		var cur = current.getLayout();
		boolean horizontal = dirX != 0;
		float currentStart = horizontal ? cur.y : cur.x;
		float currentEnd = horizontal ? cur.y + cur.height : cur.x + cur.width;
		float currentCenter = horizontal ? cur.x + cur.width / 2f : cur.y + cur.height / 2f;
		CraftedElement alignedBest = null;
		float alignedDistance = Float.POSITIVE_INFINITY;
		float alignedOffset = Float.POSITIVE_INFINITY;
		CraftedElement fallbackBest = null;
		float fallbackScore = Float.POSITIVE_INFINITY;
		for (var candidate : focusableElements())
		{
			if (candidate == current) continue;
			var l = candidate.getLayout();
			if (l == null) continue;
			float candidateStart = horizontal ? l.y : l.x;
			float candidateEnd = horizontal ? l.y + l.height : l.x + l.width;
			float candidateCenter = horizontal ? l.x + l.width / 2f : l.y + l.height / 2f;
			float primaryDistance = (candidateCenter - currentCenter) * (horizontal ? dirX : dirY);
			if (primaryDistance <= 0) continue;

			float offset = Math.abs((candidateStart + candidateEnd) / 2f - (currentStart + currentEnd) / 2f);
			boolean aligned = candidateEnd >= currentStart && candidateStart <= currentEnd;
			if (aligned && (primaryDistance < alignedDistance
				|| (primaryDistance == alignedDistance && offset < alignedOffset)))
			{
				alignedBest = candidate;
				alignedDistance = primaryDistance;
				alignedOffset = offset;
			}

			float score = primaryDistance + offset;
			if (score < fallbackScore)
			{
				fallbackBest = candidate;
				fallbackScore = score;
			}
		}
		return alignedBest != null ? alignedBest : fallbackBest;
	}

	public void setFocus(CraftedElement newFocus)
	{
		if (newFocus == focusedElement)
			return;
		if (newFocus != null && (!newFocus.isFocusable() || newFocus.state.isDisabled()))
			return;

		var previous = focusedElement;
		if (previous != null)
		{
			previous.state.set(InteractionState.FOCUSED, false);
			previous.handleFocusLost(FocusEvent.lost(previous, newFocus));
		}
		focusedElement = newFocus;
		if (newFocus != null)
		{
			newFocus.state.set(InteractionState.FOCUSED, true);
			newFocus.handleFocusGained(FocusEvent.gained(newFocus, previous));
		}
	}

	private void moveFocus(boolean forward)
	{
		var elements = focusableElements();
		if (elements.length == 0) { setFocus(null); return; }
		if (focusedElement == null)
		{
			setFocus(forward ? elements[0] : elements[elements.length - 1]);
			return;
		}
		int index = -1;
		for (int i = 0; i < elements.length; i++)
			if (elements[i] == focusedElement) { index = i; break; }
		if (index == -1)
		{
			setFocus(forward ? elements[0] : elements[elements.length - 1]);
			return;
		}
		setFocus(elements[(index + (forward ? 1 : elements.length - 1)) % elements.length]);
	}

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
			setFocus(target.isFocusable() ? target : null);
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
		else
		{
			pressedElement = null;
			pressedButton = null;
			setFocus(null);
		}
	}
	public void handleMouseReleased(float x, float y, MouseButton button)
	{
		if(rootElement == null)
			return;
		if (pressedElement == null || button != pressedButton) return;
		var target = pressedElement;
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
			target.handleClick(new ClickEvent(target, ClickEvent.Source.MOUSE, x, y, button));
	}

	public void handleCharTyped(char character, KeyModifiers modifiers)
	{
		if (rootElement == null)
			return;
		if (focusedElement != null)
		{
			var event = KeyEvent.type(focusedElement, 0, 0, character, modifiers);
			bubbleKey(event, KeyEvent.Type.TYPE);
		}
	}
	public void handleKeyPressed(int keyCode, int scanCode, KeyModifiers modifiers)
	{
		if (rootElement == null)
			return;
		if (focusedElement != null)
		{
			var event = KeyEvent.press(focusedElement, keyCode, scanCode, (char) 0, modifiers);
			bubbleKey(event, KeyEvent.Type.PRESS);
			if (event.isConsumed())
				return;
		}

		if (keyCode == ImportantKeys.TAB)
			moveFocus(!modifiers.shift());
		else if(keyCode == ImportantKeys.UP)
			setFocus(directionalTarget(focusedElement, 0, -1));
		else if(keyCode == ImportantKeys.DOWN)
			setFocus(directionalTarget(focusedElement, 0, 1));
		else if(keyCode == ImportantKeys.LEFT)
			setFocus(directionalTarget(focusedElement, -1, 0));
		else if(keyCode == ImportantKeys.RIGHT)
			setFocus(directionalTarget(focusedElement, 1, 0));
		else if (focusedElement != null && (keyCode == ImportantKeys.ENTER || keyCode == ImportantKeys.SPACE))
			focusedElement.handleClick(new ClickEvent(focusedElement, ClickEvent.Source.KEYBOARD, 0, 0, null));
	}
	private void bubbleKey(KeyEvent event, KeyEvent.Type type)
	{
		var currentTarget = focusedElement;
		while (!event.isPropagationStopped())
		{
			event.setCurrentTarget(currentTarget);
			if (type == KeyEvent.Type.PRESS)
				currentTarget.handleKeyPress(event);
			else if (type == KeyEvent.Type.RELEASE)
				currentTarget.handleKeyRelease(event);
			else if (type == KeyEvent.Type.TYPE)
				currentTarget.handleCharTyped(event);
			currentTarget = currentTarget.getParent();
			if (currentTarget == null)
				break;
		}
	}
	public void handleKeyReleased(int keyCode, int scanCode, KeyModifiers modifiers)
	{
		if(rootElement == null)
			return;
		if(focusedElement != null)
		{
			var event = KeyEvent.release(focusedElement, keyCode, scanCode, (char) 0, modifiers);
			bubbleKey(event, KeyEvent.Type.RELEASE);
		}
	}
}
