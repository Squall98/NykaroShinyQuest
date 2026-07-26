package fr.nykaro.shinyquest.client.gui;

import fr.nykaro.shinyquest.client.ClientState;
import fr.nykaro.shinyquest.network.Snapshot;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.Locale;

/** Interface listant les quetes du palier en cours et les chances de shiny. */
public class QuestScreen extends Screen {

    private static final int WHITE = 0xFFFFFFFF;
    private static final int GOLD = 0xFFFFD700;
    private static final int GREEN = 0xFF55FF55;
    private static final int GRAY = 0xFFAAAAAA;

    public QuestScreen() {
        super(Text.translatable("nykaroshiny.gui.title"));
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        int cx = this.width / 2;
        int y = this.height / 6;

        context.drawCenteredTextWithShadow(this.textRenderer, this.title, cx, y, GOLD);
        y += 22;

        Snapshot snap = ClientState.get();
        if (snap == null || snap.quests == null || snap.quests.isEmpty()) {
            context.drawCenteredTextWithShadow(this.textRenderer,
                Text.translatable("nykaroshiny.gui.no_quests"), cx, y, GRAY);
            return;
        }

        // Palier + chances actuelles
        Text tierLine = Text.literal(snap.tierIndex + "/" + snap.tierTotal + " - ")
            .append(Text.translatable(snap.tierNameKey));
        context.drawCenteredTextWithShadow(this.textRenderer, tierLine, cx, y, WHITE);
        y += 14;

        context.drawCenteredTextWithShadow(this.textRenderer,
            Text.translatable("nykaroshiny.gui.current_odds",
                String.format(Locale.ROOT, "%.0f", snap.odds)), cx, y, GREEN);
        y += 24;

        // Liste des quetes du palier
        for (Snapshot.Line line : snap.quests) {
            boolean done = line.progress >= line.target;
            String suffix = " (" + line.progress + "/" + line.target + ")" + (done ? "  ✔" : "");
            Text questText = Text.translatable(line.key).append(Text.literal(suffix));
            context.drawCenteredTextWithShadow(this.textRenderer, questText, cx, y, done ? GREEN : WHITE);
            y += 14;
        }

        if (snap.allCompleted) {
            y += 12;
            context.drawCenteredTextWithShadow(this.textRenderer,
                Text.translatable("nykaroshiny.gui.all_completed"), cx, y, GOLD);
        }
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
