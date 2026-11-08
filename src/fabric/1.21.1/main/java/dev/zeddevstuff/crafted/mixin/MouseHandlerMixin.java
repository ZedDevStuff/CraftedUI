package dev.zeddevstuff.crafted.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import dev.zeddevstuff.crafted.CraftedPortal;
import dev.zeddevstuff.crafted.ScreenUtils;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MouseHandler.class)
public class MouseHandlerMixin
{
	@Inject(method = "handleAccumulatedMovement", at= @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;wrapScreenError(Ljava/lang/Runnable;Ljava/lang/String;Ljava/lang/String;)V"))
	private void crafted$onHandleAccumulatedMovement(CallbackInfo ci, @Local(name = "$$2") Screen screen, @Local(name = "$$3") double mouseX, @Local(name = "$$4") double mouseY)
	{
		ScreenUtils.forEachWidget(screen, widget -> {
			if(widget instanceof CraftedPortal portal)
				portal.handleMouseMoved(mouseX, mouseY);
		});
	}
}