package fr.nykaro.shinyquest.client;

import com.google.gson.Gson;
import fr.nykaro.shinyquest.network.Snapshot;

/** Conserve cote client le dernier instantane de progression recu du serveur. */
public final class ClientState {

    private static final Gson GSON = new Gson();
    private static Snapshot current;

    private ClientState() {}

    public static void update(String json) {
        try {
            current = GSON.fromJson(json, Snapshot.class);
        } catch (Exception ignored) {
            // JSON invalide -> on garde l'ancien etat
        }
    }

    public static Snapshot get() {
        return current;
    }
}
