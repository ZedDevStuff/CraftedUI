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
		var paths = FabricLoader.getInstance().getModContainer(Constants.MOD_ID).get().getRootPaths().stream().filter(path -> path.toString().contains("resources")).toList();
		if(paths.isEmpty())
			throw new RuntimeException("Could not find resources path for CraftedUI");
		var resourcesPath = paths.getFirst();
		ClientLifecycleEvents.CLIENT_STARTED.register(event -> Entry.init(resourcesPath));
	}
}
