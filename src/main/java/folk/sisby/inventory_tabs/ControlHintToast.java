package folk.sisby.inventory_tabs;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastManager;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.resources.Identifier;

public class ControlHintToast implements Toast {
    private static final Identifier TEXTURE = Identifier.parse("toast/advancement");
    protected Component title;
    protected Component keyHint;
    protected KeyMapping keyBinding;
    protected int titleWidth;
    protected int hintWidth;
	private Toast.Visibility visibility = Toast.Visibility.HIDE;

    public ControlHintToast(Component title, KeyMapping keybinding) {
        this.title = title;
        this.keyBinding = keybinding;
        keyHint = Component.translatable("toast.inventory_tabs.disabled.key_hint", keyBinding.getTranslatedKeyMessage().copy().withStyle(ChatFormatting.YELLOW)).withStyle(ChatFormatting.BLUE);
        titleWidth = Minecraft.getInstance().font.width(title);
        hintWidth = Minecraft.getInstance().font.width(keyHint);
    }

	@Override
	public Visibility getWantedVisibility() {
		return this.visibility;
	}

	@Override
	public void update(ToastManager manager, long elapsedTime) {
		double time = 2000 * manager.getNotificationDisplayTimeMultiplier();

		this.visibility = elapsedTime >= time ? Visibility.HIDE : Visibility.SHOW;
	}

	@Override
	public void extractRenderState(GuiGraphicsExtractor context, Font textRenderer, long startTime) {
		context.blitSprite(RenderPipelines.GUI_TEXTURED, TEXTURE, 0, 0, width(), height());
		context.text(textRenderer, title, (width() - titleWidth) / 2, 7, 0xFFFFFF, false);
		context.text(textRenderer, keyHint, (width() - hintWidth) / 2, 18, 0xFFFFFF, false);
	}

	@Override
    public int width() {
        return Math.max(titleWidth, hintWidth) + 24;
    }
}
