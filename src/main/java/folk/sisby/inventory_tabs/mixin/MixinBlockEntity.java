package folk.sisby.inventory_tabs.mixin;

import com.mojang.serialization.JsonOps;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.RegistryOps;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockEntity.class)
public class MixinBlockEntity {
    @Inject(method = "getUpdateTag", at = @At("RETURN"))
    public void sendCustomNames(HolderLookup.Provider registries, CallbackInfoReturnable<CompoundTag> cir) {
        if (((BlockEntity) (Object) this) instanceof BaseContainerBlockEntity lcbe) {
            Component name = lcbe.getCustomName();
            if (name != null) {
                var ops = RegistryOps.create(JsonOps.INSTANCE, RegistryAccess.EMPTY);
                String json = ComponentSerialization.CODEC.encodeStart(ops, name)
                    .getOrThrow()
                    .toString();

                cir.getReturnValue().putString("CustomName", json);
            }
        }
    }
}
//Rewrote Lockable Containers
