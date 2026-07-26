package fr.nykaro.shinyquest.network;

import com.google.gson.Gson;
import fr.nykaro.shinyquest.config.ShinyQuestConfig;
import fr.nykaro.shinyquest.odds.ShinyOddsService;
import fr.nykaro.shinyquest.quest.PlayerQuestData;
import fr.nykaro.shinyquest.quest.Quest;
import fr.nykaro.shinyquest.quest.QuestRegistry;
import fr.nykaro.shinyquest.quest.Tier;
import fr.nykaro.shinyquest.util.QuestStateManager;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.List;

/** Enregistrement et envoi des paquets reseau cote serveur. */
public final class NetworkHandler {

    private static final Gson GSON = new Gson();

    private NetworkHandler() {}

    /** A appeler dans l'initialiseur principal (commun client + serveur). */
    public static void registerCommon() {
        PayloadTypeRegistry.playS2C().register(SnapshotPayload.ID, SnapshotPayload.CODEC);
    }

    /** Construit et envoie l'instantane de progression au joueur. */
    public static void sendSnapshot(ServerPlayerEntity player) {
        if (player.getServer() == null) return;
        ServerPlayNetworking.send(player, new SnapshotPayload(buildJson(player)));
    }

    private static String buildJson(ServerPlayerEntity player) {
        ShinyQuestConfig cfg = ShinyQuestConfig.get();
        List<Tier> tiers = QuestRegistry.build(cfg.oddsPerTier, cfg.targetOverrides);
        PlayerQuestData data = QuestStateManager.get(player.getServer()).getData(player.getUuid());

        Snapshot s = new Snapshot();
        s.tierTotal = tiers.size();
        s.allCompleted = data.getCurrentTierIndex() >= tiers.size();
        int shownIdx = Math.min(data.getCurrentTierIndex(), tiers.size() - 1);
        Tier shown = tiers.get(shownIdx);
        s.tierIndex = s.allCompleted ? tiers.size() : data.getCurrentTierIndex() + 1;
        s.tierNameKey = shown.translationKey();
        s.odds = ShinyOddsService.clampAndScale(data.getOddsPercent(), cfg);
        for (Quest q : shown.quests()) {
            s.quests.add(new Snapshot.Line(q.translationKey(), q.progress(data), q.target()));
        }
        return GSON.toJson(s);
    }
}
