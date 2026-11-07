package dev.zeddevstuff.crafted.mixin.dev;

import dev.zeddevstuff.crafted.CraftedPortal;
import dev.zeddevstuff.crafted.CraftedScreen;
import dev.zeddevstuff.crafted.Entry;
import dev.zeddevstuff.crafted.input.events.ClickEvent;
import dev.zeddevstuff.crafted.input.events.IClickEventHandler;
import dev.zeddevstuff.crafted.input.events.ISubmitEventHandler;
import dev.zeddevstuff.crafted.types.Rect;
import dev.zeddevstuff.crafted.ui.CraftedContext;
import dev.zeddevstuff.crafted.ui.elements.Input;
import dev.zeddevstuff.crafted.utils.TypedList;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin extends Screen
{
	private CraftedContext ctx;
	protected TitleScreenMixin(Component component)
	{
		super(component);
	}

	@Inject(method = "init", at = @At("TAIL"))
	public void init(CallbackInfo ci)
	{
		if(ctx == null)
		{
			ctx = new CraftedContext();
			ctx.registerCallback(IClickEventHandler.class, "openGallery", this::crafted$galleryButtonPressed);
		}
		this.addRenderableWidget(new CraftedPortal(
			Rect.posAndSize(20, this.height - 40, 20, 20),
			ctx,
			"""
				<Crafted renderer="crafted" title="Portal Test">
					<Button onClick="openGallery" size="20">C</Button>
				</Crafted>
				"""));
	}

	@Unique
	private void crafted$galleryButtonPressed(ClickEvent event)
	{
		var ctx = new CraftedContext();
		ctx.registerCallback(IClickEventHandler.class, "sayHi", e -> System.out.println("Hi!"));
		ctx.registerCallback(ISubmitEventHandler.class, "printSubmit", e -> {
			System.out.println("Submitted value: '" + event.getTargetAs(Input.class).value.get() + "'");
		});
		ctx.setVar("testOptions", new TypedList<>(String.class, List.of("Option 1", "Option 2", "Option 3")));
		var screen = new CraftedScreen(ctx, Entry.gallery);
		minecraft.setScreen(screen);
	}
}
