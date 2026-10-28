package dev.zeddevstuff.crafted.mixin;

import dev.zeddevstuff.crafted.Constants;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class MixinManager implements IMixinConfigPlugin
{
	@Override
	public void onLoad(String mixinPackage)
	{
		System.out.println("[Crafted] Dev mode is enabled, adding gallery button to title screen.");
	}

	@Override
	public String getRefMapperConfig()
	{
		return "";
	}

	@Override
	public boolean shouldApplyMixin(String targetClassName, String mixinClassName)
	{
		if(mixinClassName.startsWith("dev.zeddevstuff.crafted.mixin.dev"))
			return Constants.DEV_MODE;
		return true;
	}

	@Override
	public void acceptTargets(Set<String> myTargets, Set<String> otherTargets)
	{

	}

	@Override
	public List<String> getMixins()
	{
		return List.of();
	}

	@Override
	public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo)
	{

	}

	@Override
	public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo)
	{

	}
}
