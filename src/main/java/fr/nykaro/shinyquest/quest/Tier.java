package fr.nykaro.shinyquest.quest;

import net.minecraft.text.Text;

import java.util.List;

/**
 * Un palier : un ensemble de quetes a completer pour debloquer une nouvelle
 * valeur de chances de shiny.
 *
 * @param index          numero du palier (1-based, pour l'affichage)
 * @param translationKey cle de traduction du nom du palier
 * @param oddsPercent    chances de shiny (%) accordees quand le palier est complete
 * @param quests         quetes de ce palier
 */
public record Tier(int index, String translationKey, double oddsPercent, List<Quest> quests) {

    public Text displayName() {
        return Text.translatable(translationKey);
    }

    /** Vrai si toutes les quetes du palier sont terminees pour ce joueur. */
    public boolean isComplete(PlayerQuestData data) {
        return quests.stream().allMatch(q -> q.isComplete(data));
    }

    public long completedCount(PlayerQuestData data) {
        return quests.stream().filter(q -> q.isComplete(data)).count();
    }
}
