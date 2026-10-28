package dev.zeddevstuff.crafted;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;

@Mod(value = Constants.MOD_ID, dist = Dist.CLIENT)
public class CraftedNeoForge
{
	public CraftedNeoForge(IEventBus modEventBus)
	{
		modEventBus.addListener((FMLLoadCompleteEvent e) -> Entry.init());
	}
}
