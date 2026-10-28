package dev.zeddevstuff.crafted.platform;

import net.minecraft.client.Minecraft;

public class MainThreadDispatcher implements IDispatcher
{
	@Override
	public void dispatch(Runnable runnable)
	{
		Minecraft.getInstance().execute(runnable);
	}
	@Override
	public void dispatchSync(Runnable runnable)
	{
		if(isSameThread())
			runnable.run();
		else
			Minecraft.getInstance().executeBlocking(runnable);
	}

	@Override
	public boolean isSameThread()
	{
		return Minecraft.getInstance().isSameThread();
	}
}
