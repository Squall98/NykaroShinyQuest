package fr.nykaro.shinyquest.util;

import fr.nykaro.shinyquest.config.ShinyQuestConfig;
import fr.nykaro.shinyquest.quest.PlayerQuestData;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Etat persistant du monde stockant la progression de chaque joueur
 * (ou une entree globale unique en mode serveur partage).
 */
public class QuestStateManager extends PersistentState {

    private static final String KEY = "nykaroshiny_quests";
    /** UUID special reserve a la progression globale (mode serveur partage). */
    public static final UUID GLOBAL = new UUID(0L, 0L);

    private final Map<UUID, PlayerQuestData> players = new HashMap<>();

    public static QuestStateManager get(MinecraftServer server) {
        ServerWorld overworld = server.getOverworld();
        PersistentStateManager psm = overworld.getPersistentStateManager();
        return psm.getOrCreate(
            new Type<>(QuestStateManager::new, QuestStateManager::readNbt, null),
            KEY
        );
    }

    /** Renvoie les donnees du joueur (ou globales selon la config), en les creant au besoin. */
    public PlayerQuestData getData(UUID playerUuid) {
        UUID key = ShinyQuestConfig.get().perPlayerProgression ? playerUuid : GLOBAL;
        return players.computeIfAbsent(key, k -> new PlayerQuestData());
    }

    public void touch() {
        markDirty();
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        NbtCompound entries = new NbtCompound();
        for (Map.Entry<UUID, PlayerQuestData> e : players.entrySet()) {
            entries.put(e.getKey().toString(), e.getValue().writeNbt(new NbtCompound()));
        }
        nbt.put("players", entries);
        return nbt;
    }

    private static QuestStateManager readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        QuestStateManager m = new QuestStateManager();
        NbtCompound entries = nbt.getCompound("players");
        for (String key : entries.getKeys()) {
            try {
                m.players.put(UUID.fromString(key), PlayerQuestData.readNbt(entries.getCompound(key)));
            } catch (IllegalArgumentException ignored) {
                // cle invalide -> ignoree
            }
        }
        return m;
    }
}
