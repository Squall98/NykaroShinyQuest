package fr.nykaro.shinyquest.client.hud;

import fr.nykaro.shinyquest.client.ClientState;
import fr.nykaro.shinyquest.network.Snapshot;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.text.Text;

import java.util.Locale;

/** Petit affichage en haut a gauche indiquant les chances de shiny actuelles. */
public class OddsHud implements HudRenderCallback {

    @Override
    public void onHudRender(DrawContext context, RenderTickCounter tickCounter) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.options.hudHidden) return;

        Snapshot snap = ClientState.get();
        if (snap == null) return;

        Text text = Text.translatable("nykaroshiny.hud.odds",
            String.format(Locale.ROOT, "%.0f", snap.odds));
        context.drawTextWithShadow(client.textRenderer, text, 6, 6, 0xFF55FFFF);
    }
}
