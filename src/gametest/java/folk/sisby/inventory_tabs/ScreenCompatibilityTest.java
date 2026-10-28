package folk.sisby.inventory_tabs;

import com.mojang.blaze3d.platform.InputConstants;
import folk.sisby.inventory_tabs.mixin.HandledScreenAccessor;
import folk.sisby.inventory_tabs.tabs.BlockTab;
import folk.sisby.inventory_tabs.tabs.PlayerInventoryTab;
import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.gui.screens.inventory.ShulkerBoxScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.ShulkerBoxMenu;
import net.minecraft.world.level.block.Blocks;

public class ScreenCompatibilityTest implements FabricClientGameTest {
    @Override
    public void runTest(ClientGameTestContext context) {
        try (var world = context.worldBuilder().create()) {
            world.getConnection().waitForChunksDownload();
            boolean compactBefore = InventoryTabs.CONFIG.compactLargeContainers;
            try {
                for (boolean compact : new boolean[]{false, true}) {
                    context.runOnClient(client -> InventoryTabs.CONFIG.compactLargeContainers = compact);
                    MenuType<?>[] types = {MenuType.GENERIC_9x1, MenuType.GENERIC_9x2, MenuType.GENERIC_9x3,
                            MenuType.GENERIC_9x4, MenuType.GENERIC_9x5, MenuType.GENERIC_9x6};
                    for (int rows = 1; rows <= 6; rows++) {
                        int rowCount = rows;
                        context.setScreen(() -> {
                            var inventory = world.getConnection().getClientPlayer().getInventory();
                            return new ContainerScreen(new ChestMenu(types[rowCount - 1], 1, inventory,
                                    new SimpleContainer(rowCount * 9), rowCount), inventory, Component.literal("Chest"));
                        });
                        context.waitTicks(2);
                        context.runOnClient(client -> {
                            var screen = (ContainerScreen) client.gui.screen();
                            check(TabManager.currentScreen == screen, "Chest did not initialize tabs");
                            int height = 114 + rowCount * 18 - (compact && rowCount == 6 ? 30 : 2);
                            check(((HandledScreenAccessor) screen).getBackgroundHeight() == height, "Incorrect chest height");
                        });
                    }
                }

                context.setScreen(() -> {
                    var inventory = world.getConnection().getClientPlayer().getInventory();
                    return new ShulkerBoxScreen(new ShulkerBoxMenu(1, inventory), inventory, Component.literal("Shulker"));
                });
                context.waitTicks(2);
                context.runOnClient(client -> check(((HandledScreenAccessor) client.gui.screen()).getBackgroundHeight() == 166,
                        "Incorrect shulker height"));

                context.setScreen(() -> new InventoryScreen(world.getConnection().getClientPlayer()));
                context.getInput().resizeWindow(960, 640);
                context.waitTicks(2);
                boolean enabledBefore = context.computeOnClient(client -> TabManager.enabled);
                context.getInput().pressKey(InventoryTabs.TOGGLE_TABS);
                context.runOnClient(client -> check(TabManager.enabled != enabledBefore, "Toggle did not fire once after resize"));
                context.getInput().pressKey(InventoryTabs.TOGGLE_TABS);
                context.runOnClient(client -> {
                    check(TabManager.enabled == enabledBefore, "Toggle did not restore state");
                    var conflict = new KeyMapping("key.inventory_tabs.test_conflict", InputConstants.KEY_TAB, KeyMapping.Category.INVENTORY);
                    var tabKey = InputConstants.Type.KEYBOARD.getOrCreate(InputConstants.KEY_TAB);
                    KeyMapping.set(tabKey, true);
                    check(InventoryTabs.NEXT_TAB.isDown() && conflict.isDown(), "Shared bindings lost their pressed state");
                    KeyMapping.set(tabKey, false);
                });

                var chestPos = context.computeOnClient(client -> client.player.blockPosition().offset(1, 0, 0));
                world.getServer().runOnServer(server ->
                        world.getConnection().getServerLevel().setBlockAndUpdate(chestPos, Blocks.CHEST.defaultBlockState()));
                context.waitFor(client -> TabManager.tabs.stream().anyMatch(tab -> tab instanceof BlockTab block && block.pos.equals(chestPos)));
                context.getInput().pressKey(InventoryTabs.NEXT_TAB);
                context.waitForScreen(ContainerScreen.class);
                context.waitFor(client -> TabManager.nextTab == null && TabManager.currentTab instanceof BlockTab);
                context.waitFor(client -> TabManager.holdTabCooldown == 0);
                context.getInput().holdShift();
                context.getInput().pressKey(InventoryTabs.NEXT_TAB);
                context.getInput().releaseShift();
                context.waitForScreen(InventoryScreen.class);
                context.runOnClient(client -> check(TabManager.currentTab instanceof PlayerInventoryTab && TabManager.nextTab == null,
                        "Returning to inventory did not finish the tab transition"));
                context.setScreen(() -> null);
                context.runOnClient(client -> check(TabManager.nextTab == null && TabManager.currentTab == null,
                        "Closing the screen did not discard tabs"));
                InventoryTabs.LOGGER.info("Screen compatibility checks passed");
            } finally {
                context.runOnClient(client -> InventoryTabs.CONFIG.compactLargeContainers = compactBefore);
            }
        }
    }

    private static void check(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
}
