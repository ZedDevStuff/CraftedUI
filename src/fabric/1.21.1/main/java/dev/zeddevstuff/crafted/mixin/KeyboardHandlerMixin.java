package dev.zeddevstuff.crafted.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import dev.zeddevstuff.crafted.CraftedPortal;
import dev.zeddevstuff.crafted.ScreenUtils;
import dev.zeddevstuff.crafted.input.KeyModifiers;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyboardHandler.class)
public class KeyboardHandlerMixin
{
	@Inject(method = "charTyped", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;wrapScreenError(Ljava/lang/Runnable;Ljava/lang/String;Ljava/lang/String;)V"))
	private void crafted$onCharTyped(long windowHandle, int codePoint, int modifiers, CallbackInfo ci, @Local(name = "$$3") GuiEventListener screen)
	{
		ScreenUtils.forEachWidget((Screen) screen, widget -> {
			if(widget instanceof CraftedPortal portal)
				portal.handleCharTyped((char)codePoint, modifiers);
		});
	}
}
