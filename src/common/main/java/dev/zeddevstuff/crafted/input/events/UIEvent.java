package dev.zeddevstuff.crafted.input.events;

import dev.zeddevstuff.crafted.ui.CraftedElement;

public abstract class UIEvent
{
	private final CraftedElement target;
	public CraftedElement getTarget() { return target; }
	private CraftedElement currentTarget;
	public CraftedElement getCurrentTarget() { return currentTarget; }
	public void setCurrentTarget(CraftedElement currentTarget) { this.currentTarget = currentTarget; }
	private boolean propagationStopped;
	public boolean isPropagationStopped() { return propagationStopped; }
	public void stopPropagation() { propagationStopped = true; }

	public UIEvent(CraftedElement target)
	{
		this.target = target;
	}
}
