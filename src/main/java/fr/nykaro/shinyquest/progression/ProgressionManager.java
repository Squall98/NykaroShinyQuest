package fr.nykaro.shinyquest.progression;

import fr.nykaro.shinyquest.config.ShinyQuestConfig;
import fr.nykaro.shinyquest.network.NetworkHandler;
import fr.nykaro.shinyquest.quest.PlayerQuestData;
import fr.nykaro.shinyquest.quest.Quest;
import fr.nykaro.shinyquest.quest.QuestRegistry;
import fr.nykaro.shinyquest.quest.Tier;
import fr.nykaro.shinyquest.util.QuestStateManager;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;
import java.util.Set;

/**
 * Point d'entree cote serveur : recoit les actions de jeu (via le pont
 * Cobblemon), met a jour la progression et declenche paliers, messages et
 * retours audio/visuels.
 */
public final class ProgressionManager {

    private ProgressionManager() {}

    public static void onCatch(ServerPlayerEntity player, Set<String> types,
                               boolean shiny, boolean legendary, int level) {
        PlayerQuestData data = data(player);
        data.addCatch();
        for (String t : types) data.addCaughtType(t);
        if (shiny) data.markShinyCaught();
        if (legendary) data.markLegendaryCaught();
        if (level > 0) data.reachLevel(level);
        recompute(player, data);
    }

    public static void onDefeatWild(ServerPlayerEntity player) {
        PlayerQuestData data = data(player);
        data.addDefeat();
        recompute(player, data);
    }

    public static void onEvolve(ServerPlayerEntity player) {
        PlayerQuestData data = data(player);
        data.addEvolve();
        recompute(player, data);
    }

    public static void onLevel(ServerPlayerEntity player, int level) {
        PlayerQuestData data = data(player);
        data.reachLevel(level);
        recompute(player, data);
    }

    // ---- coeur de la progression ----

    private static PlayerQuestData data(ServerPlayerEntity player) {
        return QuestStateManager.get(player.getServer()).getData(player.getUuid());
    }

    /** Reevalue les quetes/paliers apres une mise a jour et notifie le joueur. */
    public static void recompute(ServerPlayerEntity player, PlayerQuestData data) {
        ShinyQuestConfig cfg = ShinyQuestConfig.get();
        List<Tier> tiers = QuestRegistry.build(cfg.oddsPerTier, cfg.targetOverrides);

        // 1) messages "quete terminee" (une seule fois par quete)
        for (Tier tier : tiers) {
            for (Quest q : tier.quests()) {
                if (q.isComplete(data) && data.getCompletedQuestIds().add(q.id())) {
                    player.sendMessage(Text.translatable("nykaroshiny.quest.completed",
                        q.displayName()).formatted(Formatting.GREEN), false);
                    feedbackQuest(player);
                }
            }
        }

        // 2) montee de paliers
        int idx = data.getCurrentTierIndex();
        while (idx < tiers.size() && tiers.get(idx).isComplete(data)) {
            Tier tier = tiers.get(idx);
            data.setOddsPercent(tier.oddsPercent());
            idx++;
            data.setCurrentTierIndex(idx);
            player.sendMessage(Text.translatable("nykaroshiny.quest.tier_up",
                tier.displayName(), fmt(tier.oddsPercent())).formatted(Formatting.GOLD), false);
            feedbackTier(player);
        }

        QuestStateManager.get(player.getServer()).touch();
        NetworkHandler.sendSnapshot(player);
    }

    // ---- feedback serveur (son + particules) ----

    private static void feedbackQuest(ServerPlayerEntity player) {
        if (!ShinyQuestConfig.get().enableFeedback) return;
        ServerWorld world = player.getServerWorld();
        world.playSound(null, player.getX(), player.getY(), player.getZ(),
            SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS, 0.8f, 1.2f);
        world.spawnParticles(ParticleTypes.HAPPY_VILLAGER,
            player.getX(), player.getY() + 1.0, player.getZ(), 8, 0.4, 0.6, 0.4, 0.01);
    }

    private static void feedbackTier(ServerPlayerEntity player) {
        if (!ShinyQuestConfig.get().enableFeedback) return;
        ServerWorld world = player.getServerWorld();
        world.playSound(null, player.getX(), player.getY(), player.getZ(),
            SoundEvents.ENTITY_PLAYER_LEVELUP, SoundCategory.PLAYERS, 1.0f, 1.0f);
        world.spawnParticles(ParticleTypes.TOTEM_OF_UNDYING,
            player.getX(), player.getY() + 1.0, player.getZ(), 30, 0.5, 0.8, 0.5, 0.15);
    }

    private static String fmt(double v) {
        return String.format(java.util.Locale.ROOT, "%.0f", v);
    }
}
