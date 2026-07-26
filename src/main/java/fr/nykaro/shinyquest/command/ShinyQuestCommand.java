package fr.nykaro.shinyquest.command;

import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.context.CommandContext;
import fr.nykaro.shinyquest.network.NetworkHandler;
import fr.nykaro.shinyquest.quest.PlayerQuestData;
import fr.nykaro.shinyquest.quest.QuestRegistry;
import fr.nykaro.shinyquest.util.QuestStateManager;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.Collection;
import java.util.Locale;

/** Commande {@code /shinyquest} : consultation et administration de la progression. */
public final class ShinyQuestCommand {

    private ShinyQuestCommand() {}

    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, access, env) ->
            dispatcher.register(CommandManager.literal("shinyquest")
                .then(CommandManager.literal("odds").executes(ShinyQuestCommand::showOdds))
                .then(CommandManager.literal("progress").executes(ShinyQuestCommand::showProgress))
                .then(CommandManager.literal("set")
                    .requires(src -> src.hasPermissionLevel(2))
                    .then(CommandManager.argument("targets", EntityArgumentType.players())
                        .then(CommandManager.argument("value", DoubleArgumentType.doubleArg(0, 100))
                            .executes(ShinyQuestCommand::setOdds))))
                .then(CommandManager.literal("reset")
                    .requires(src -> src.hasPermissionLevel(2))
                    .then(CommandManager.argument("targets", EntityArgumentType.players())
                        .executes(ShinyQuestCommand::reset)))
            ));
    }

    private static int showOdds(CommandContext<ServerCommandSource> ctx) {
        ServerPlayerEntity player = ctx.getSource().getPlayer();
        if (player == null) {
            ctx.getSource().sendError(Text.translatable("nykaroshiny.cmd.players_only"));
            return 0;
        }
        PlayerQuestData data = data(player);
        ctx.getSource().sendFeedback(() ->
            Text.translatable("nykaroshiny.cmd.odds", fmt(data.getOddsPercent())), false);
        return 1;
    }

    private static int showProgress(CommandContext<ServerCommandSource> ctx) {
        ServerPlayerEntity player = ctx.getSource().getPlayer();
        if (player == null) {
            ctx.getSource().sendError(Text.translatable("nykaroshiny.cmd.players_only"));
            return 0;
        }
        PlayerQuestData data = data(player);
        int total = QuestRegistry.DEFAULT_ODDS.length;
        int tier = Math.min(data.getCurrentTierIndex() + 1, total);
        ctx.getSource().sendFeedback(() -> Text.translatable("nykaroshiny.cmd.progress",
            tier, total, data.getCompletedQuestIds().size(), fmt(data.getOddsPercent())), false);
        return 1;
    }

    private static int setOdds(CommandContext<ServerCommandSource> ctx) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        Collection<ServerPlayerEntity> targets = EntityArgumentType.getPlayers(ctx, "targets");
        double value = DoubleArgumentType.getDouble(ctx, "value");
        for (ServerPlayerEntity p : targets) {
            PlayerQuestData data = data(p);
            data.setOddsPercent(value);
            QuestStateManager.get(p.getServer()).touch();
            NetworkHandler.sendSnapshot(p);
            ctx.getSource().sendFeedback(() ->
                Text.translatable("nykaroshiny.cmd.set", fmt(value), p.getName()), true);
        }
        return targets.size();
    }

    private static int reset(CommandContext<ServerCommandSource> ctx) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        Collection<ServerPlayerEntity> targets = EntityArgumentType.getPlayers(ctx, "targets");
        for (ServerPlayerEntity p : targets) {
            data(p).reset();
            QuestStateManager.get(p.getServer()).touch();
            NetworkHandler.sendSnapshot(p);
            ctx.getSource().sendFeedback(() ->
                Text.translatable("nykaroshiny.cmd.reset", p.getName()), true);
        }
        return targets.size();
    }

    private static PlayerQuestData data(ServerPlayerEntity player) {
        return QuestStateManager.get(player.getServer()).getData(player.getUuid());
    }

    private static String fmt(double v) {
        return String.format(Locale.ROOT, "%.0f", v);
    }
}
