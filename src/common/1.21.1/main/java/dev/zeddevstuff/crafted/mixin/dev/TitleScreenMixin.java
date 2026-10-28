package dev.zeddevstuff.crafted.mixin.dev;

//import dev.zeddevstuff.crafted.screen.GlazeScreen;
//import dev.zeddevstuff.crafted.ui.Gallery;
import dev.zeddevstuff.crafted.Entry;
import dev.zeddevstuff.crafted.CraftedScreen;
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
	private void crafted$galleryButtonPressed(Button button)
	{
		minecraft.setScreen(new CraftedScreen(Entry.rootElement));
	}
}
