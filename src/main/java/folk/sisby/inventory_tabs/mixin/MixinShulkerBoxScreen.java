package folk.sisby.inventory_tabs.mixin;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.ShulkerBoxScreen;
import net.minecraft.client.gui.GuiPipeline;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ShulkerBoxMenu;
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

@Mixin(ShulkerBoxScreen.class)
public abstract class MixinShulkerBoxScreen extends AbstractContainerScreen<ShulkerBoxMenu> {
    @Shadow @Final private static Identifier CONTAINER_TEXTURE;

    public MixinShulkerBoxScreen(ShulkerBoxMenu handler, Inventory inventory, Component title) {
        super(handler, inventory, title);
    }

    @ModifyArgs(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/inventory/AbstractContainerScreen;<init>(Lnet/minecraft/world/inventory/AbstractContainerMenu;Lnet/minecraft/world/entity/player/Inventory;Lnet/minecraft/network/chat/Component;II)V"))
    private static void containerTextHeight(Args args) {
        args.set(4, (Integer) args.get(4) - 1);
    }

    @Inject(method = "extractBackground", at = @At("HEAD"), cancellable = true)
    private void extractConsistentBackground(GuiGraphicsExtractor graphics, CallbackInfo ci) {
        super.extractBackground(graphics);
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        graphics.blit(GuiPipeline.TEXTURED, CONTAINER_TEXTURE, x, y, 0, 0, imageWidth, 7, 256, 256);
        graphics.blit(GuiPipeline.TEXTURED, CONTAINER_TEXTURE, x, y + 7, 0, 8, imageWidth, 64, 256, 256);
        graphics.blit(GuiPipeline.TEXTURED, CONTAINER_TEXTURE, x, y + 71, 0, 71, imageWidth, 96, 256, 256);
        ci.cancel();
    }
}
