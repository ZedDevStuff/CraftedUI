package dev.zeddevstuff.crafted.mixin;

import dev.zeddevstuff.crafted.CraftedPortal;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiEventListener.class)
public interface GuiEventListenerMixin
{
	@Inject(method = "mouseMoved", at = @At("HEAD"))
	default void crafted$onMouseMoved(double mouseX, double mouseY, CallbackInfo ci)
	{
		if(this instanceof Screen screen)
			for(var widget : screen.children())
				if(widget instanceof CraftedPortal portal)
					portal.mouseMoved(mouseX, mouseY);
	}
}
