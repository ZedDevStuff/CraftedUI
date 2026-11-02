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
		var paths = FabricLoader.getInstance().getModContainer(Constants.MOD_ID).get().getRootPaths().stream().toList();
		var path = paths.size() > 1
			// Assume we're running in a development environment
			? paths.stream().filter(p -> p.toString().contains("resources")).findFirst().orElseThrow(() -> new RuntimeException("Could not find resources path for CraftedUI"))
			: paths.getFirst();
		ClientLifecycleEvents.CLIENT_STARTED.register(event -> Entry.init(path));
	}
}
