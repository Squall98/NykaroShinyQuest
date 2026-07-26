package fr.nykaro.shinyquest.quest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Construit la liste des paliers et de leurs quetes.
 *
 * <p>Design 100% original (Nykaro) : 6 paliers de 3 quetes, toutes pilotees
 * par des evenements Cobblemon (capture, combat, evolution, niveau), sans
 * aucun mixin. Les valeurs de chances et les objectifs sont surchargeables
 * via la configuration.</p>
 */
public final class QuestRegistry {

    private QuestRegistry() {}

    /** Chances de shiny par defaut pour les 6 paliers. */
    public static final double[] DEFAULT_ODDS = {2.0, 5.0, 10.0, 25.0, 50.0, 100.0};

    /**
     * Construit les paliers en appliquant d'eventuelles surcharges de config.
     *
     * @param oddsPerTier     chances (%) par palier (longueur >= nombre de paliers)
     * @param targetOverrides surcharge d'objectif par identifiant de quete
     */
    public static List<Tier> build(double[] oddsPerTier, Map<String, Integer> targetOverrides) {
        List<Tier> tiers = new ArrayList<>();

        tiers.add(tier(1, "nykaroshiny.tier.1", odds(oddsPerTier, 0), targetOverrides, List.of(
            quest("catch_10", "nykaroshiny.quest.catch_10", QuestType.CATCH_TOTAL, 10),
            quest("defeat_15_wild", "nykaroshiny.quest.defeat_15_wild", QuestType.DEFEAT_WILD_TOTAL, 15),
            quest("reach_level_20", "nykaroshiny.quest.reach_level_20", QuestType.REACH_LEVEL, 20)
        )));

        tiers.add(tier(2, "nykaroshiny.tier.2", odds(oddsPerTier, 1), targetOverrides, List.of(
            quest("catch_5_types", "nykaroshiny.quest.catch_5_types", QuestType.CATCH_DISTINCT_TYPES, 5),
            quest("evolve_3", "nykaroshiny.quest.evolve_3", QuestType.EVOLVE_TOTAL, 3),
            quest("catch_25", "nykaroshiny.quest.catch_25", QuestType.CATCH_TOTAL, 25)
        )));

        tiers.add(tier(3, "nykaroshiny.tier.3", odds(oddsPerTier, 2), targetOverrides, List.of(
            quest("catch_water", "nykaroshiny.quest.catch_water", QuestType.CATCH_TYPE, 1, "water"),
            quest("catch_fire", "nykaroshiny.quest.catch_fire", QuestType.CATCH_TYPE, 1, "fire"),
            quest("reach_level_50", "nykaroshiny.quest.reach_level_50", QuestType.REACH_LEVEL, 50)
        )));

        tiers.add(tier(4, "nykaroshiny.tier.4", odds(oddsPerTier, 3), targetOverrides, List.of(
            quest("defeat_50_wild", "nykaroshiny.quest.defeat_50_wild", QuestType.DEFEAT_WILD_TOTAL, 50),
            quest("catch_10_types", "nykaroshiny.quest.catch_10_types", QuestType.CATCH_DISTINCT_TYPES, 10),
            quest("evolve_10", "nykaroshiny.quest.evolve_10", QuestType.EVOLVE_TOTAL, 10)
        )));

        tiers.add(tier(5, "nykaroshiny.tier.5", odds(oddsPerTier, 4), targetOverrides, List.of(
            quest("catch_first_shiny", "nykaroshiny.quest.catch_first_shiny", QuestType.CATCH_SHINY, 1),
            quest("reach_level_75", "nykaroshiny.quest.reach_level_75", QuestType.REACH_LEVEL, 75),
            quest("catch_100", "nykaroshiny.quest.catch_100", QuestType.CATCH_TOTAL, 100)
        )));

        tiers.add(tier(6, "nykaroshiny.tier.6", odds(oddsPerTier, 5), targetOverrides, List.of(
            quest("catch_all_types", "nykaroshiny.quest.catch_all_types", QuestType.CATCH_DISTINCT_TYPES, 18),
            quest("catch_legendary", "nykaroshiny.quest.catch_legendary", QuestType.CATCH_LEGENDARY, 1),
            quest("reach_level_100", "nykaroshiny.quest.reach_level_100", QuestType.REACH_LEVEL, 100)
        )));

        return tiers;
    }

    // ---- helpers ----

    private static double odds(double[] arr, int i) {
        if (arr != null && i < arr.length) return arr[i];
        return DEFAULT_ODDS[i];
    }

    private static Tier tier(int index, String key, double oddsPercent,
                             Map<String, Integer> overrides, List<Quest> quests) {
        // Applique les surcharges d'objectif
        List<Quest> resolved = new ArrayList<>(quests.size());
        for (Quest q : quests) {
            Integer ov = overrides == null ? null : overrides.get(q.id());
            resolved.add(ov != null ? new Quest(q.id(), q.translationKey(), q.type(), ov, q.param()) : q);
        }
        return new Tier(index, key, oddsPercent, List.copyOf(resolved));
    }

    private static Quest quest(String id, String key, QuestType type, int target) {
        return new Quest(id, key, type, target);
    }

    private static Quest quest(String id, String key, QuestType type, int target, String param) {
        return new Quest(id, key, type, target, param);
    }
}
