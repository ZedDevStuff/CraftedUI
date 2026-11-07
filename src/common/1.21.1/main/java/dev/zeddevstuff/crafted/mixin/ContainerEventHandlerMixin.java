package dev.zeddevstuff.crafted.mixin;

import dev.zeddevstuff.crafted.CraftedPortal;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ContainerEventHandler.class)
public interface ContainerEventHandlerMixin
{
	@Inject(method = "mouseReleased", at = @At("HEAD"))
	default void crafted$onMouseReleased(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> ci)
	{
		if (this instanceof Screen screen)
			for (var widget : screen.children())
				if (widget instanceof CraftedPortal portal)
					portal.mouseReleased(mouseX, mouseY, button);
	}

//	@Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
//	default void crafted$onKeyPressed(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> ci)
//	{
//		boolean result = false;
//		if (this instanceof Screen screen)
//			for (var widget : screen.children())
//				if (widget instanceof CraftedPortal portal)
//					result = portal.keyPressed(keyCode, scanCode, modifiers) || result;
//		if(result)
//			ci.setReturnValue(true);
//	}

	@Inject(method = "keyReleased", at = @At("HEAD"), cancellable = true)
	default void crafted$onKeyReleased(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> ci)
	{
		boolean result = false;
		if (this instanceof Screen screen)
			for (var widget : screen.children())
				if (widget instanceof CraftedPortal portal)
					result = portal.keyReleased(keyCode, scanCode, modifiers) || result;
		if(result)
			ci.setReturnValue(true);
	}

	@Inject(method = "charTyped", at = @At("HEAD"), cancellable = true)
	default void crafted$onCharTyped(char c, int modifiers, CallbackInfoReturnable<Boolean> ci)
	{
		boolean result = false;
		if (this instanceof Screen screen)
			for (var widget : screen.children())
				if (widget instanceof CraftedPortal portal)
					result = portal.charTyped(c, modifiers) || result;
		if(result)
			ci.setReturnValue(true);
	}
}
