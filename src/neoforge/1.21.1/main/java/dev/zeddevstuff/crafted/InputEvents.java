package dev.zeddevstuff.crafted;

import net.minecraft.client.KeyboardHandler;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ScreenEvent;

@EventBusSubscriber
public class InputEvents
{
//	public static void onMouseMoved(ScreenEvent. event)
//	{
//
//	}
	@SubscribeEvent
	public static void onMouseScrolled(ScreenEvent.MouseScrolled.Post event)
	{
		ScreenUtils.forEachWidget(event.getScreen(), widget -> {
			if (widget instanceof CraftedPortal portal)
				portal.handleMouseScrolled(event.getMouseX(), event.getMouseY(), event.getScrollDeltaX(), event.getScrollDeltaY());
		});
	}
	@SubscribeEvent
	public static void onMousePressed(ScreenEvent.MouseButtonPressed.Post event)
	{
		ScreenUtils.forEachWidget(event.getScreen(), widget -> {
			if (widget instanceof CraftedPortal portal)
				portal.handleMouseClicked(event.getMouseX(), event.getMouseY(), event.getButton());
		});
	}
	@SubscribeEvent
	public static void onMouseReleased(ScreenEvent.MouseButtonReleased.Post event)
	{
		ScreenUtils.forEachWidget(event.getScreen(), widget -> {
			if (widget instanceof CraftedPortal portal)
				portal.handleMouseReleased(event.getMouseX(), event.getMouseY(), event.getButton());
		});
	}

	@SubscribeEvent
	public static void onKeyPressed(ScreenEvent.KeyPressed.Post event)
	{
		ScreenUtils.forEachWidget(event.getScreen(), widget -> {
			if (widget instanceof CraftedPortal portal)
				portal.handleKeyPressed(event.getKeyCode(), event.getScanCode(), event.getModifiers());
		});
	}
	@SubscribeEvent
	public static void onKeyReleased(ScreenEvent.KeyReleased.Post event)
	{
		ScreenUtils.forEachWidget(event.getScreen(), widget -> {
			if (widget instanceof CraftedPortal portal)
				portal.handleKeyReleased(event.getKeyCode(), event.getScanCode(), event.getModifiers());
		});
	}
	@SubscribeEvent
	public static void onCharTyped(ScreenEvent.CharacterTyped.Post event)
	{
		ScreenUtils.forEachWidget(event.getScreen(), widget -> {
			if (widget instanceof CraftedPortal portal)
				portal.handleCharTyped(event.getCodePoint(), event.getModifiers());
		});
	}
}
