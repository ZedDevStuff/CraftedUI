package dev.zeddevstuff.crafted.input.events;

import dev.zeddevstuff.crafted.ui.CraftedElement;

public abstract class UIEvent
{
	private final CraftedElement target;
	public CraftedElement getTarget() { return target; }
	public <T extends CraftedElement> T getTargetAs(Class<T> type) { return type.cast(target); }
	@SuppressWarnings("unchecked")
	public <T extends CraftedElement> T getTargetAs() { return (T) target; }
	private CraftedElement currentTarget;
	public CraftedElement getCurrentTarget() { return currentTarget; }
	public <T extends CraftedElement> T getCurrentTargetAs(Class<T> type) { return type.cast(currentTarget); }
	@SuppressWarnings("unchecked")
	public <T extends CraftedElement> T getCurrentTargetAs() { return (T) currentTarget; }
	public void setCurrentTarget(CraftedElement currentTarget) { this.currentTarget = currentTarget; }
	private boolean consumed;
	public boolean isConsumed() { return consumed; }
	public void consume() { consumed = true; }
	private boolean propagationStopped;
	public boolean isPropagationStopped() { return propagationStopped; }
	public void stopPropagation() { propagationStopped = true; }
	private boolean immediatePropagationStopped;
	public boolean isImmediatePropagationStopped() { return immediatePropagationStopped; }
	public void stopImmediatePropagation() { immediatePropagationStopped = true; }

	public UIEvent(CraftedElement target)
	{
		this.target = target;
	}
}
