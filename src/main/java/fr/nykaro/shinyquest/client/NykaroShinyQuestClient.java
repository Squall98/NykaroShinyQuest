package fr.nykaro.shinyquest.client;

import fr.nykaro.shinyquest.client.gui.QuestScreen;
import fr.nykaro.shinyquest.client.hud.OddsHud;
import fr.nykaro.shinyquest.network.SnapshotPayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

/** Initialiseur client : reseau, raccourci clavier, HUD. */
public class NykaroShinyQuestClient implements ClientModInitializer {

    public static KeyBinding openQuestsKey;

    @Override
    public void onInitializeClient() {
        // Reception de l'instantane de progression
        ClientPlayNetworking.registerGlobalReceiver(SnapshotPayload.ID, (payload, context) -> {
            String json = payload.json();
            context.client().execute(() -> ClientState.update(json));
        });

        // Raccourci "Ouvrir les quetes" (G par defaut)
        openQuestsKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.nykaroshiny.open_quests",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_G,
            "key.categories.nykaroshiny"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openQuestsKey.wasPressed()) {
                client.setScreen(new QuestScreen());
            }
        });

        // HUD des chances de shiny
        HudRenderCallback.EVENT.register(new OddsHud());
    }
}
