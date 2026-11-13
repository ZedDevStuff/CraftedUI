package dev.zeddevstuff.crafted;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Path;

public class CraftedFabric implements ClientModInitializer
{
	@Override
	public void onInitializeClient()
	{
		ClientLifecycleEvents.CLIENT_STARTED.register(event -> Entry.init());
		InputEvents.register();
	}
}
