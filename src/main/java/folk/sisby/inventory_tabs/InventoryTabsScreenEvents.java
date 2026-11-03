package folk.sisby.inventory_tabs;

import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenKeyboardEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;

public final class InventoryTabsScreenEvents {
    private InventoryTabsScreenEvents() {}

    public static void register() {
        ScreenEvents.AFTER_INIT.register((client, screen, width, height) -> {
            if (!(screen instanceof AbstractContainerScreen<?> container) || !ScreenSupport.allowTabs(screen)) return;

            TabManager.initScreen(client, container);
            ScreenEvents.afterExtract(screen).register((s, graphics) -> {
                TabManager.lastFrameInputType = client.getLastInputType();
                TabManager.render(graphics, graphics.mouseX(), graphics.mouseY());
            });
            ScreenMouseEvents.allowMouseClick(screen).register((s, click) ->
                    !TabManager.mouseClicked(click.x(), click.y(), click.button()));
            ScreenMouseEvents.allowMouseRelease(screen).register((s, click) ->
                    !TabManager.mouseReleased(click.x(), click.y(), click.button()));
            ScreenKeyboardEvents.allowKeyPress(screen).register((s, input) ->
                    !TabManager.keyPressed(input));
        });
    }
}
