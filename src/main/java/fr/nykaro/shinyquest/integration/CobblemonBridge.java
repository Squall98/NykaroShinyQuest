package fr.nykaro.shinyquest.integration;

import com.cobblemon.mod.common.api.Priority;
import com.cobblemon.mod.common.api.events.CobblemonEvents;
import com.cobblemon.mod.common.api.events.battles.BattleVictoryEvent;
import com.cobblemon.mod.common.api.events.entity.SpawnEvent;
import com.cobblemon.mod.common.api.events.pokemon.LevelUpEvent;
import com.cobblemon.mod.common.api.events.pokemon.PokemonCapturedEvent;
import com.cobblemon.mod.common.api.events.pokemon.evolution.EvolutionCompleteEvent;
import com.cobblemon.mod.common.api.types.ElementalType;
import com.cobblemon.mod.common.battles.actor.PlayerBattleActor;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.Pokemon;
import fr.nykaro.shinyquest.NykaroShinyQuest;
import fr.nykaro.shinyquest.odds.ShinyOddsService;
import fr.nykaro.shinyquest.progression.ProgressionManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

import java.util.HashSet;
import java.util.Set;

/**
 * SEUL point de couplage avec l'API Cobblemon.
 *
 * <p>S'abonne aux evenements Cobblemon (spawn, capture, evolution, niveau,
 * victoire de combat) pour piloter la progression et surcharger les chances de
 * shiny au spawn. Aucun mixin : uniquement l'API publique d'evenements.</p>
 *
 * <p>Si l'API Cobblemon evolue, c'est le seul fichier a ajuster ; le reste du
 * mod ne depend pas de Cobblemon.</p>
 */
public final class CobblemonBridge {

    private CobblemonBridge() {}

    public static void init() {
        try {
            subscribeSpawn();
            subscribeCapture();
            subscribeEvolution();
            subscribeLevelUp();
            subscribeBattleVictory();
            NykaroShinyQuest.LOGGER.info("Pont Cobblemon initialise.");
        } catch (Throwable t) {
            NykaroShinyQuest.LOGGER.error("Echec de l'initialisation du pont Cobblemon", t);
        }
    }

    /** Surcharge des chances de shiny au spawn d'un Pokemon. */
    private static void subscribeSpawn() {
        CobblemonEvents.POKEMON_ENTITY_SPAWN.subscribe(Priority.NORMAL, (SpawnEvent<PokemonEntity> event) -> {
            PokemonEntity entity = event.getEntity();
            World world = entity.getWorld();
            if (world instanceof ServerWorld serverWorld) {
                Pokemon pokemon = entity.getPokemon();
                if (!pokemon.getShiny()) {
                    double odds = ShinyOddsService.oddsForSpawn(
                        serverWorld, entity.getX(), entity.getY(), entity.getZ());
                    boolean makeShiny = odds >= 100.0
                        || (odds > 0.0 && serverWorld.getRandom().nextDouble() < odds / 100.0);
                    if (makeShiny) {
                        pokemon.setShiny(true);
                    }
                }
            }
            return kotlin.Unit.INSTANCE;
        });
    }

    /** Capture -> compte + types + shiny + legendaire + niveau. */
    private static void subscribeCapture() {
        CobblemonEvents.POKEMON_CAPTURED.subscribe(Priority.NORMAL, (PokemonCapturedEvent event) -> {
            ServerPlayerEntity player = event.getPlayer();
            Pokemon pokemon = event.getPokemon();
            ProgressionManager.onCatch(player, typesOf(pokemon),
                pokemon.getShiny(), pokemon.isLegendary(), pokemon.getLevel());
            return kotlin.Unit.INSTANCE;
        });
    }

    private static void subscribeEvolution() {
        CobblemonEvents.EVOLUTION_COMPLETE.subscribe(Priority.NORMAL, (EvolutionCompleteEvent event) -> {
            ServerPlayerEntity player = event.getPokemon().getOwnerPlayer();
            if (player != null) {
                ProgressionManager.onEvolve(player);
            }
            return kotlin.Unit.INSTANCE;
        });
    }

    private static void subscribeLevelUp() {
        CobblemonEvents.LEVEL_UP_EVENT.subscribe(Priority.NORMAL, (LevelUpEvent event) -> {
            ServerPlayerEntity player = event.getPokemon().getOwnerPlayer();
            if (player != null) {
                ProgressionManager.onLevel(player, event.getNewLevel());
            }
            return kotlin.Unit.INSTANCE;
        });
    }

    /** Victoire en combat contre un Pokemon sauvage -> compte une defaite infligee. */
    private static void subscribeBattleVictory() {
        CobblemonEvents.BATTLE_VICTORY.subscribe(Priority.NORMAL, (BattleVictoryEvent event) -> {
            boolean wildDefeated = event.getLosers().stream()
                .anyMatch(actor -> !(actor instanceof PlayerBattleActor));
            if (wildDefeated) {
                for (var winner : event.getWinners()) {
                    if (winner instanceof PlayerBattleActor playerActor) {
                        ServerPlayerEntity player = playerActor.getEntity();
                        if (player != null) {
                            ProgressionManager.onDefeatWild(player);
                        }
                    }
                }
            }
            return kotlin.Unit.INSTANCE;
        });
    }

    private static Set<String> typesOf(Pokemon pokemon) {
        Set<String> types = new HashSet<>();
        ElementalType primary = pokemon.getPrimaryType();
        if (primary != null) types.add(primary.getName());
        ElementalType secondary = pokemon.getSecondaryType();
        if (secondary != null) types.add(secondary.getName());
        return types;
    }
}
