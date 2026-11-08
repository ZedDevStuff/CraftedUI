package dev.zeddevstuff.crafted;

import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenKeyboardEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents;

public class InputEvents
{
	static void register()
	{
		ScreenEvents.BEFORE_INIT.register((minecraft, screen, width, height) -> {
			if(screen instanceof CraftedScreen)
				return;
			// For some reason both NeoForge and FabricAPI don't have an event for mouse movement
			ScreenMouseEvents.afterMouseScroll(screen).register((s, mouseX, mouseY, scrollDeltaX, scrollDeltaY) -> {
				ScreenUtils.forEachWidget(screen, widget -> {
					if (widget instanceof CraftedPortal portal)
						portal.handleMouseScrolled(mouseX, mouseY, scrollDeltaX, scrollDeltaY);
				});
			});
			ScreenMouseEvents.afterMouseClick(screen).register((s, mouseX, mouseY, button) -> {
				ScreenUtils.forEachWidget(screen, widget -> {
					if (widget instanceof CraftedPortal portal)
						portal.handleMouseClicked(mouseX, mouseY, button);
				});
			});
			ScreenMouseEvents.afterMouseRelease(screen).register((s, mouseX, mouseY, button) -> {
				ScreenUtils.forEachWidget(screen, widget -> {
					if (widget instanceof CraftedPortal portal)
						portal.handleMouseReleased(mouseX, mouseY, button);
				});
			});
			ScreenKeyboardEvents.afterKeyPress(screen).register((s, keyCode, scanCode, modifiers) -> {
				ScreenUtils.forEachWidget(screen, widget -> {
					if (widget instanceof CraftedPortal portal)
						portal.handleKeyPressed(keyCode, scanCode, modifiers);
				});
			});
			ScreenKeyboardEvents.afterKeyRelease(screen).register((s, keyCode, scanCode, modifiers) -> {
				ScreenUtils.forEachWidget(screen, widget -> {
					if (widget instanceof CraftedPortal portal)
						portal.handleKeyReleased(keyCode, scanCode, modifiers);
				});
			});
			// And Fabric has no event for typed characters. It recently got added for newer versions. Yay... See KeyboardHandlerMixin
		});
	}
}
