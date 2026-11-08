package dev.zeddevstuff.crafted;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;


public class ScreenUtils
{
	public static void forEachWidget(ContainerEventHandler container, WidgetConsumer consumer)
	{
		for(var widget : container.children())
		{
			consumer.accept(widget);
			if(widget instanceof ContainerEventHandler containerEventHandler)
				forEachWidget(containerEventHandler, consumer);
		}
	}
	public interface WidgetConsumer
	{
		void accept(GuiEventListener widget);
	}
}
