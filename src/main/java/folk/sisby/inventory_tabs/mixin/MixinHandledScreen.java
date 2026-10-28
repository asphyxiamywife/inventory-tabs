package folk.sisby.inventory_tabs.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import folk.sisby.inventory_tabs.InventoryTabs;
import folk.sisby.inventory_tabs.ScreenSupport;
import folk.sisby.inventory_tabs.TabManager;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractContainerScreen.class)
public abstract class MixinHandledScreen {
    @Inject(method = "hasClickedOutside", at = @At("RETURN"), cancellable = true)
    protected void isClickOutsideBounds(double mouseX, double mouseY, CallbackInfoReturnable<Boolean> cir) {
        if (ScreenSupport.allowTabs((Screen) (Object) this) && cir.getReturnValue()) {
            cir.setReturnValue(TabManager.isClickOutsideBounds(mouseX, mouseY));
        }
    }

	@ModifyExpressionValue(method = "<init>(Lnet/minecraft/world/inventory/AbstractContainerMenu;Lnet/minecraft/world/entity/player/Inventory;Lnet/minecraft/network/chat/Component;II)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Inventory;getDisplayName()Lnet/minecraft/network/chat/Component;"))
	private Component removeCompactPlayerInventoryTitle(Component original) {
		AbstractContainerScreen<?> self = (AbstractContainerScreen<?>) (Object) this;
		if (InventoryTabs.CONFIG.consistentContainers && InventoryTabs.CONFIG.compactLargeContainers && self.getMenu() instanceof ChestMenu gcsh && gcsh.getRowCount() == 6) {
			return Component.empty();
		}
		return original;
	}

}
