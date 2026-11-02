package dev.zeddevstuff.crafted;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;

import java.net.URISyntaxException;
import java.nio.file.Path;

@Mod(value = Constants.MOD_ID, dist = Dist.CLIENT)
public class CraftedNeoForge
{
	public CraftedNeoForge(IEventBus modEventBus, ModContainer mod)
	{
		modEventBus.addListener((FMLLoadCompleteEvent e) -> Entry.init(mod.getModInfo()
			.getOwningFile()
			.getFile()
			.findResource("META-INF/neoforge.mods.toml")
			.getParent()
			.getParent()));
	}
}
