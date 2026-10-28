package folk.sisby.inventory_tabs;

import com.mojang.datafixers.util.Pair;
import folk.sisby.inventory_tabs.mixin.HandledScreenAccessor;
import folk.sisby.inventory_tabs.util.WidgetPosition;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;

import java.util.ArrayList;
import java.util.List;

public final class ScreenLayout {
    private ScreenLayout() {}

    public static List<WidgetPosition> getTabPositions(AbstractContainerScreen<?> screen, int tabWidth) {
        List<WidgetPosition> list = new ArrayList<>();
        Identifier screenHandlerId = BuiltInRegistries.MENU.getKey(ScreenSupport.getScreenHandlerType(screen.getMenu()));
        Pair<Integer, Integer> offsets = ScreenSupport.SCREEN_BOUND_OFFSETS.getOrDefault(screenHandlerId, Pair.of(0, 0));
        boolean invert = ScreenSupport.SCREEN_INVERTS.getOrDefault(screenHandlerId, InventoryTabs.CONFIG.invertTabsByDefault);
        int width = ((HandledScreenAccessor) screen).getBackgroundWidth() + offsets.getFirst() + offsets.getSecond();
        int left = Math.max(((HandledScreenAccessor) screen).getX() - offsets.getFirst(), 0);

        int count = width / tabWidth;
        int margins = width - tabWidth * count;

        for (int i = 0; i < count; i++) {
            list.add(new WidgetPosition(left + margins / 2 + i * tabWidth, invert ? ((HandledScreenAccessor) screen).getY() + ((HandledScreenAccessor) screen).getBackgroundHeight() : ((HandledScreenAccessor) screen).getY(), !invert));
        }

        return list;
    }
}
