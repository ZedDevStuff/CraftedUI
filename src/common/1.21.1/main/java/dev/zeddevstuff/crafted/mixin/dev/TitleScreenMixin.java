package dev.zeddevstuff.crafted.mixin.dev;

//import dev.zeddevstuff.crafted.screen.GlazeScreen;
//import dev.zeddevstuff.crafted.ui.Gallery;

import dev.zeddevstuff.crafted.Crafted;
import dev.zeddevstuff.crafted.CraftedScreen;
import dev.zeddevstuff.crafted.Entry;
import dev.zeddevstuff.crafted.input.events.IClickEventHandler;
import dev.zeddevstuff.crafted.input.events.ISubmitEventHandler;
import dev.zeddevstuff.crafted.ui.CraftedContext;
import dev.zeddevstuff.crafted.ui.elements.Input;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin extends Screen
{
	protected TitleScreenMixin(Component component)
	{
		super(component);
	}

	@Inject(method = "init", at = @At("TAIL"))
	public void init(CallbackInfo ci)
	{
		this.addRenderableWidget(Button.builder(Component.literal("C"), this::crafted$galleryButtonPressed)
			.pos(20, this.height - 40)
			.size(20, 20)
			.build());
	}

	@Unique
	long crafted$start = 0;
	@Unique
	long crafted$end = 0;
	@Unique
	private void crafted$galleryButtonPressed(Button button)
	{
		var ctx = new CraftedContext();
		ctx.registerCallback(IClickEventHandler.class, "sayHi", event -> {
			System.out.println("Hi!");
		});
		ctx.registerCallback(ISubmitEventHandler.class, "printSubmit", event -> {
			System.out.println("Submitted value: '" + event.getTargetAs(Input.class).value.get() + "'");
		});
		crafted$start = System.nanoTime();
		var screen = new CraftedScreen(ctx, Entry.gallery);
		crafted$end = System.nanoTime();
		System.out.println("Time to build screen: " + (crafted$end - crafted$start) / 1_000_000.0 + " ms");
		minecraft.setScreen(screen);
	}
}
