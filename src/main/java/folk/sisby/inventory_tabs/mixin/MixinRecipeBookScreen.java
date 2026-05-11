package folk.sisby.inventory_tabs.mixin;

import folk.sisby.inventory_tabs.TabManager;
import folk.sisby.inventory_tabs.ScreenSupport;
import net.minecraft.client.gui.screens.Screen;
import folk.sisby.inventory_tabs.duck.InventoryTabsScreen;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractRecipeBookScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.entity.player.Inventory;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractRecipeBookScreen.class)
public abstract class MixinRecipeBookScreen {
    @Unique Boolean inventoryTabs$allowTabs = false;

    // protected MixinRecipeScreen(Component title) {
    // super(title);
    // }

    @Inject(method = "init", at = @At("TAIL"))
    private void inventorytabs$checkSupported(CallbackInfo ci) {
        inventoryTabs$allowTabs = ScreenSupport.allowTabs((Screen)(Object)this);
    }

    @Inject(method = "extractRenderState", at = @At("TAIL"))
    protected void render(GuiGraphicsExtractor drawContext, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (!inventoryTabs$allowTabs) return;
        TabManager.render(drawContext, mouseX, mouseY);
    }

    // @Inject(method = "isClickOutsideBounds", at = @At("RETURN"), cancellable = true)
    // protected void isClickOutsideBounds(double mouseX, double mouseY, int left, int top, CallbackInfoReturnable<Boolean> cir) {
    //     System.out.println("[InventoryTabs DEBUG] MixinRecipeBookScreen isClickOutsideBounds called: " + this.getClass().getHoverName());
    //     if (inventoryTabs$allowTabs && cir.getReturnValue()) {
    //         cir.setReturnValue(TabManager.isClickOutsideBounds(mouseX, mouseY));
    //     }
    // }

}
