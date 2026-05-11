package folk.sisby.inventory_tabs.providers;

import folk.sisby.inventory_tabs.InventoryTabs;
import folk.sisby.inventory_tabs.tabs.Tab;
import folk.sisby.inventory_tabs.tabs.VehicleInventoryTab;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.Container;
import net.minecraft.resources.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class VehicleInventoryTabProvider implements TabProvider {
    public final Map<Identifier, Predicate<Entity>> preclusions = new HashMap<>();

    public VehicleInventoryTabProvider() {
        preclusions.put(InventoryTabs.id("removed"), Entity::isRemoved);
        preclusions.put(InventoryTabs.id("vehicle"), e -> Minecraft.getInstance().player != null && e != Minecraft.getInstance().player.getVehicle());
    }

    @Override
    public void addAvailableTabs(LocalPlayer player, Consumer<Tab> addTab) {
        if (player.isPassenger() && player.getVehicle() instanceof Container) {
            addTab.accept(new VehicleInventoryTab(player.getVehicle(), preclusions));
        }
    }
}
