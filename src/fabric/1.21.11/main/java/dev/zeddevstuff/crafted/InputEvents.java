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
			ScreenMouseEvents.afterMouseScroll(screen).register((s, mouseX, mouseY, scrollDeltaX, scrollDeltaY, a) -> {
				ScreenUtils.forEachWidget(screen, widget -> {
					if (widget instanceof CraftedPortal portal)
						portal.handleMouseScrolled(mouseX, mouseY, scrollDeltaX, scrollDeltaY);
				});
				return true;
			});
			ScreenMouseEvents.afterMouseClick(screen).register((s, event, doubleClick) -> {
				ScreenUtils.forEachWidget(screen, widget -> {
					if (widget instanceof CraftedPortal portal)
						portal.handleMouseClicked(event.x(), event.y(), event.button());
				});
				return true;
			});
			ScreenMouseEvents.afterMouseRelease(screen).register((s, event, doubleClick) -> {
				ScreenUtils.forEachWidget(screen, widget -> {
					if (widget instanceof CraftedPortal portal)
						portal.handleMouseReleased(event.x(), event.y(), event.button());
				});
				return true;
			});
			ScreenKeyboardEvents.afterKeyPress(screen).register((s, event) -> {
				ScreenUtils.forEachWidget(screen, widget -> {
					if (widget instanceof CraftedPortal portal)
						portal.handleKeyPressed(event.key(), event.scancode(), event.modifiers());
				});
			});
			ScreenKeyboardEvents.afterKeyRelease(screen).register((s, event) -> {
				ScreenUtils.forEachWidget(screen, widget -> {
					if (widget instanceof CraftedPortal portal)
						portal.handleKeyReleased(event.key(), event.scancode(), event.modifiers());
				});
			});
			// And Fabric has no event for typed characters. It recently got added for newer versions. Yay... See KeyboardHandlerMixin
		});
	}
}
