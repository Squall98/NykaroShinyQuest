package fr.nykaro.shinyquest.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import fr.nykaro.shinyquest.NykaroShinyQuest;
import fr.nykaro.shinyquest.quest.QuestRegistry;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Configuration du mod, serialisee en {@code config/nykaroshiny.json}.
 */
public class ShinyQuestConfig {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path PATH =
        FabricLoader.getInstance().getConfigDir().resolve("nykaroshiny.json");

    private static ShinyQuestConfig instance;

    // ---- champs serialises ----

    /** Chances de shiny (%) accordees a la fin de chaque palier. */
    public double[] oddsPerTier = QuestRegistry.DEFAULT_ODDS.clone();

    /** Multiplicateur applique aux chances finales (1.0 = normal). */
    public double globalOddsMultiplier = 1.0;

    /** true = chaque joueur a sa propre progression ; false = progression globale au serveur. */
    public boolean perPlayerProgression = true;

    /** Afficher le HUD des chances de shiny en jeu. */
    public boolean showHud = true;

    /** Sons + particules a la completion d'une quete / montee de palier. */
    public boolean enableFeedback = true;

    /** Surcharge d'objectif par identifiant de quete (ex: {"catch_100": 50}). */
    public Map<String, Integer> targetOverrides = new LinkedHashMap<>();

    // ---- acces ----

    public static ShinyQuestConfig get() {
        if (instance == null) load();
        return instance;
    }

    public static void load() {
        try {
            if (Files.exists(PATH)) {
                try (Reader r = Files.newBufferedReader(PATH)) {
                    instance = GSON.fromJson(r, ShinyQuestConfig.class);
                }
                if (instance == null) instance = new ShinyQuestConfig();
            } else {
                instance = new ShinyQuestConfig();
            }
        } catch (IOException e) {
            NykaroShinyQuest.LOGGER.error("Impossible de lire la config, valeurs par defaut utilisees", e);
            instance = new ShinyQuestConfig();
        }
        instance.sanitize();
        instance.save();
    }

    public void save() {
        try {
            Files.createDirectories(PATH.getParent());
            try (Writer w = Files.newBufferedWriter(PATH)) {
                GSON.toJson(this, w);
            }
        } catch (IOException e) {
            NykaroShinyQuest.LOGGER.error("Impossible d'ecrire la config", e);
        }
    }

    private void sanitize() {
        if (oddsPerTier == null || oddsPerTier.length == 0) {
            oddsPerTier = QuestRegistry.DEFAULT_ODDS.clone();
        }
        if (globalOddsMultiplier <= 0) globalOddsMultiplier = 1.0;
        if (targetOverrides == null) targetOverrides = new LinkedHashMap<>();
    }
}
