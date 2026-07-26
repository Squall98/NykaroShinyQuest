package fr.nykaro.shinyquest.quest;

import net.minecraft.text.Text;

/**
 * Definition immuable d'une quete. La progression n'est pas stockee ici :
 * elle est calculee a la volee depuis les statistiques du joueur.
 *
 * @param id             identifiant technique unique (ex: "catch_10")
 * @param translationKey cle de traduction pour le libelle affiche
 * @param type           type d'objectif
 * @param target         valeur a atteindre
 * @param param          parametre optionnel (ex: nom du type pour CATCH_TYPE)
 */
public record Quest(String id, String translationKey, QuestType type, int target, String param) {

    public Quest(String id, String translationKey, QuestType type, int target) {
        this(id, translationKey, type, target, null);
    }

    /** Progression actuelle du joueur pour cette quete, plafonnee a {@link #target}. */
    public int progress(PlayerQuestData data) {
        int raw = switch (type) {
            case CATCH_TOTAL -> data.getTotalCatches();
            case DEFEAT_WILD_TOTAL -> data.getTotalDefeats();
            case EVOLVE_TOTAL -> data.getTotalEvolves();
            case REACH_LEVEL -> data.getHighestLevel();
            case CATCH_DISTINCT_TYPES -> data.getCaughtTypes().size();
            case CATCH_TYPE -> data.getCaughtTypes().contains(param) ? 1 : 0;
            case CATCH_SHINY -> data.hasCaughtShiny() ? 1 : 0;
            case CATCH_LEGENDARY -> data.hasCaughtLegendary() ? 1 : 0;
        };
        return Math.min(raw, target);
    }

    public boolean isComplete(PlayerQuestData data) {
        return progress(data) >= target;
    }

    public Text displayName() {
        return Text.translatable(translationKey);
    }
}
