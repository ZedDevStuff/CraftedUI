package dev.zeddevstuff.crafted.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import dev.zeddevstuff.crafted.CraftedPortal;
import dev.zeddevstuff.crafted.ScreenUtils;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.CharacterEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyboardHandler.class)
public class KeyboardHandlerMixin
{
	@Inject(method = "charTyped", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;charTyped(Lnet/minecraft/client/input/CharacterEvent;)Z"))
	private void crafted$onCharTyped(long $$0, CharacterEvent event, CallbackInfo ci, @Local(name = "$$2") Screen screen)
	{
		ScreenUtils.forEachWidget((Screen) screen, widget -> {
			if(widget instanceof CraftedPortal portal)
				portal.handleCharTyped((char)event.codepoint(), event.modifiers());
		});
	}
}
