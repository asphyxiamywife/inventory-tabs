package folk.sisby.inventory_tabs.mixin;

import folk.sisby.inventory_tabs.InventoryTabs;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.GuiPipeline;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(ContainerScreen.class)
public abstract class MixinGenericContainerScreen extends AbstractContainerScreen<ChestMenu> {
    @Shadow @Final private int containerRows;
    @Shadow @Final private static Identifier CONTAINER_BACKGROUND;

    public MixinGenericContainerScreen(ChestMenu handler, Inventory inventory, Component title) {
        super(handler, inventory, title);
    }

	@ModifyArgs(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/inventory/AbstractContainerScreen;<init>(Lnet/minecraft/world/inventory/AbstractContainerMenu;Lnet/minecraft/world/entity/player/Inventory;Lnet/minecraft/network/chat/Component;II)V"))
	private static void containerHideTitle(Args args) {
		if (((ChestMenu) args.get(0)).getRowCount() == 6 && InventoryTabs.CONFIG.compactLargeContainers) {
			args.set(2, Component.empty());
			args.set(4, (Integer) args.get(4) - 30);
		} else {
			args.set(4, (Integer) args.get(4) - 2);
		}
	}

    @Inject(method = "extractBackground", at = @At("HEAD"), cancellable = true)
    private void extractConsistentBackground(GuiGraphicsExtractor graphics, CallbackInfo ci) {
        super.extractBackground(graphics);
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        boolean compact = containerRows == 6 && InventoryTabs.CONFIG.compactLargeContainers;
        int trim = compact ? 17 : 8;
        graphics.blit(GuiPipeline.TEXTURED, CONTAINER_BACKGROUND, x, y, 0, 0, imageWidth, 7, 256, 256);
        graphics.blit(GuiPipeline.TEXTURED, CONTAINER_BACKGROUND, x, y + 7, 0, trim,
                imageWidth, containerRows * 18 + 17 - trim, 256, 256);
        graphics.blit(GuiPipeline.TEXTURED, CONTAINER_BACKGROUND, x, y + containerRows * 18 + (compact ? 7 : 16),
                0, compact ? 135 : 126, imageWidth, compact ? 87 : 96, 256, 256);
        ci.cancel();
    }
}
