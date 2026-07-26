package fr.nykaro.shinyquest.quest;

/**
 * Les differents types d'objectifs qu'une quete peut demander.
 * Chaque type est evalue a partir des statistiques agregees du joueur
 * ({@link fr.nykaro.shinyquest.quest.PlayerQuestData}), ce qui evite tout
 * mixin : la progression est entierement pilotee par les evenements Cobblemon.
 */
public enum QuestType {
    /** Capturer un nombre total de Pokemon. */
    CATCH_TOTAL,
    /** Vaincre un nombre de Pokemon sauvages. */
    DEFEAT_WILD_TOTAL,
    /** Faire evoluer un nombre de Pokemon. */
    EVOLVE_TOTAL,
    /** Atteindre un niveau donne avec un Pokemon. */
    REACH_LEVEL,
    /** Capturer un nombre de types differents. */
    CATCH_DISTINCT_TYPES,
    /** Capturer un Pokemon d'un type precis (param = nom du type). */
    CATCH_TYPE,
    /** Capturer un Pokemon shiny. */
    CATCH_SHINY,
    /** Capturer un Pokemon legendaire. */
    CATCH_LEGENDARY
}
