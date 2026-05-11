package folk.sisby.inventory_tabs.providers;

import folk.sisby.inventory_tabs.tabs.PlayerInventoryTab;
import folk.sisby.inventory_tabs.tabs.Tab;
import net.minecraft.client.player.LocalPlayer;

import java.util.function.Consumer;

public class PlayerInventoryTabProvider implements TabProvider {
    @Override
    public void addAvailableTabs(LocalPlayer player, Consumer<Tab> addTab) {
        addTab.accept(new PlayerInventoryTab());
    }
}
