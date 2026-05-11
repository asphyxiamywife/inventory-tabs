package folk.sisby.inventory_tabs.providers;

import folk.sisby.inventory_tabs.tabs.Tab;
import net.minecraft.client.player.LocalPlayer;

import java.util.function.Consumer;

public interface TabProvider {
    void addAvailableTabs(LocalPlayer player, Consumer<Tab> addTab);
}
