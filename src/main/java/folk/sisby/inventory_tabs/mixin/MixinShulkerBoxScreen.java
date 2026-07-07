package folk.sisby.inventory_tabs.mixin;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.ShulkerBoxScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.ShulkerBoxMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(ShulkerBoxScreen.class)
public abstract class MixinShulkerBoxScreen extends AbstractContainerScreen<ChestMenu> {
    @Shadow @Final private static Identifier CONTAINER_TEXTURE;

    public MixinShulkerBoxScreen(ChestMenu handler, Inventory inventory, Component title) {
        super(handler, inventory, title);
    }

    @ModifyArgs(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/inventory/AbstractContainerScreen;<init>(Lnet/minecraft/world/inventory/AbstractContainerMenu;Lnet/minecraft/world/entity/player/Inventory;Lnet/minecraft/network/chat/Component;II)V"))
    private static void containerTextHeight(Args args) {
        args.set(4, (Integer) args.get(4) - 1);
    }

    @Inject(method = "extractBackground", at = @At("TAIL"))
    public void containerHeader(GuiGraphicsExtractor drawContext, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        drawContext.blit(RenderPipelines.GUI_TEXTURED, CONTAINER_TEXTURE, (this.width - this.imageWidth) / 2, (this.height - this.imageHeight) / 2, 0, 0, this.imageWidth, 7, 256, 256);
    }

    @ModifyArg(method = "extractBackground", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;blit(Lcom/mojang/renderpearl/api/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIFFIIII)V", ordinal = 0), index = 3)
    public int containerY(int original) {
        return original + 7;
    }

    @ModifyArg(method = "extractBackground", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;blit(Lcom/mojang/renderpearl/api/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIFFIIII)V", ordinal = 0), index = 5)
    public float containerV(float original) {
        return original + 8;
    }

    @ModifyArg(method = "extractBackground", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;blit(Lcom/mojang/renderpearl/api/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIFFIIII)V", ordinal = 0), index = 7)
    public int containerHeight(int original) {
        return 64;
    }

    @Inject(method = "extractBackground", at = @At("TAIL"))
    public void containerInventory(GuiGraphicsExtractor drawContext, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        drawContext.blit(RenderPipelines.GUI_TEXTURED, CONTAINER_TEXTURE, (this.width - this.imageWidth) / 2, (this.height - this.imageHeight) / 2 + 71, 0, 71, this.imageWidth, 96, 256, 256);
    }
}
