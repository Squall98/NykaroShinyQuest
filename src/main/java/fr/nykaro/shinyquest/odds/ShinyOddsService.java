package fr.nykaro.shinyquest.odds;

import fr.nykaro.shinyquest.config.ShinyQuestConfig;
import fr.nykaro.shinyquest.quest.PlayerQuestData;
import fr.nykaro.shinyquest.util.QuestStateManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;

/**
 * Calcule les chances de shiny applicables a un spawn donne.
 *
 * <p>En mode global, on utilise l'entree de progression partagee. En mode
 * par-joueur, on applique les chances du joueur le plus proche du spawn.</p>
 */
public final class ShinyOddsService {

    private ShinyOddsService() {}

    /** Chances de shiny (0-100) applicables a un spawn aux coordonnees donnees. */
    public static double oddsForSpawn(ServerWorld world, double x, double y, double z) {
        ShinyQuestConfig cfg = ShinyQuestConfig.get();
        MinecraftServer server = world.getServer();
        if (server == null) return 0.0;
        QuestStateManager mgr = QuestStateManager.get(server);

        if (!cfg.perPlayerProgression) {
            return clampAndScale(mgr.getData(QuestStateManager.GLOBAL).getOddsPercent(), cfg);
        }

        PlayerEntity nearest = world.getClosestPlayer(x, y, z, -1.0D, false);
        if (nearest == null) return 0.0;
        PlayerQuestData data = mgr.getData(nearest.getUuid());
        return clampAndScale(data.getOddsPercent(), cfg);
    }

    /** Applique le multiplicateur global et borne le resultat a [0, 100]. */
    public static double clampAndScale(double base, ShinyQuestConfig cfg) {
        double v = base * cfg.globalOddsMultiplier;
        return Math.max(0.0, Math.min(100.0, v));
    }

    /** Effectue le tirage : true = le spawn devient shiny. */
    public static boolean roll(double oddsPercent, java.util.Random random) {
        if (oddsPercent <= 0.0) return false;
        if (oddsPercent >= 100.0) return true;
        return random.nextDouble() < (oddsPercent / 100.0);
    }
}
