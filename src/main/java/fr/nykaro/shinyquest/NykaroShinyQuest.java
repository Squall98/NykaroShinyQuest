package fr.nykaro.shinyquest;

import fr.nykaro.shinyquest.command.ShinyQuestCommand;
import fr.nykaro.shinyquest.config.ShinyQuestConfig;
import fr.nykaro.shinyquest.integration.CobblemonBridge;
import fr.nykaro.shinyquest.network.NetworkHandler;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Initialiseur principal de <b>Nykaro's Shiny Quest</b>.
 * Mod original par Nykaro — augmente les chances de shiny Cobblemon via des quetes.
 */
public class NykaroShinyQuest implements ModInitializer {

    public static final String MOD_ID = "nykaroshiny";
    public static final Logger LOGGER = LoggerFactory.getLogger("Nykaro's Shiny Quest");

    @Override
    public void onInitialize() {
        ShinyQuestConfig.load();
        NetworkHandler.registerCommon();
        ShinyQuestCommand.register();
        CobblemonBridge.init();

        // Envoie l'instantane de progression a la connexion du joueur.
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) ->
            NetworkHandler.sendSnapshot(handler.player));

        LOGGER.info("Nykaro's Shiny Quest initialise !");
    }
}
