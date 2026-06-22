package folk.sisby.inventory_tabs;

import com.mojang.datafixers.util.Pair;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.HorseInventoryScreen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.resources.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Predicate;

public class ScreenSupport {
    public static Map<Identifier, Predicate<AbstractContainerScreen<?>>> DENY = new HashMap<>();
    public static Map<Identifier, Predicate<AbstractContainerScreen<?>>> ALLOW = new HashMap<>();
    public static Map<Identifier, Pair<Integer, Integer>> SCREEN_BOUND_OFFSETS = new HashMap<>();
    public static Map<Identifier, Boolean> SCREEN_INVERTS = new HashMap<>();

    public static Boolean allowTabs(Identifier type) {
        if (InventoryTabs.CONFIG.screenOverrides.entrySet().stream().filter(e -> !e.getValue()).anyMatch(e -> Objects.equals(e.getKey(), type.toString()))) return false;
        if (InventoryTabs.CONFIG.screenOverrides.entrySet().stream().filter(Map.Entry::getValue).anyMatch(e -> Objects.equals(e.getKey(), type.toString()))) return true;
        return null;
    }

	public static MenuType<?> getScreenHandlerType(AbstractContainerMenu handler) {
		try {
			return handler.getType();
		} catch (UnsupportedOperationException | NoSuchElementException ignored) {
			return null;
		}
	}

    public static boolean allowTabs(Screen screen) {
        if (screen instanceof AbstractContainerScreen<?> hs && hs.getMenu() != null) {
            if (DENY.values().stream().anyMatch(p -> p.test(hs))) return false;
            if (ALLOW.values().stream().anyMatch(p -> p.test(hs))) return true;
	        MenuType<?> type = getScreenHandlerType(hs.getMenu());
	        if (type != null) {
		        Identifier key = BuiltInRegistries.MENU.getKey(type);
				if (key != null) {
					Boolean override = allowTabs(key);
					if (override != null) return override;
				}
	        }
            return InventoryTabs.CONFIG.allowScreensByDefault;
        }
        return false;
    }

    static {
        DENY.put(InventoryTabs.id("creative_screen"), hs -> hs instanceof CreativeModeInventoryScreen);
        ALLOW.put(InventoryTabs.id("horse_screen"), hs -> hs instanceof HorseInventoryScreen);
        InventoryTabs.CONFIG.leftBoundOffsetOverride.forEach((screenHandlerId, offset) -> SCREEN_BOUND_OFFSETS.put(screenHandlerId.equals("null") ? null : Identifier.parse(screenHandlerId), Pair.of(offset, 0)));
        InventoryTabs.CONFIG.rightBoundOffsetOverride.forEach((screenHandlerId, offset) -> SCREEN_BOUND_OFFSETS.merge(screenHandlerId.equals("null") ? null : Identifier.parse(screenHandlerId), Pair.of(0, offset), (o, n) -> Pair.of(o.getFirst(), n.getSecond())));
        InventoryTabs.CONFIG.invertedTabsOverride.forEach((screenHandlerId, doInvert) -> SCREEN_INVERTS.put(screenHandlerId.equals("null") ? null : Identifier.parse(screenHandlerId), doInvert));
    }
}
