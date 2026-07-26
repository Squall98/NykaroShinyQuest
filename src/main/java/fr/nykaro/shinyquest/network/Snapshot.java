package fr.nykaro.shinyquest.network;

import java.util.ArrayList;
import java.util.List;

/**
 * Instantane leger de la progression, serialise en JSON pour etre envoye au
 * client (HUD + interface). Champs publics pour une (de)serialisation Gson simple.
 */
public class Snapshot {
    public double odds;
    public int tierIndex;     // numero 1-based du palier en cours
    public int tierTotal;
    public String tierNameKey;
    public boolean allCompleted;
    public List<Line> quests = new ArrayList<>();

    public static class Line {
        public String key;
        public int progress;
        public int target;

        public Line() {}

        public Line(String key, int progress, int target) {
            this.key = key;
            this.progress = progress;
            this.target = target;
        }
    }
}
