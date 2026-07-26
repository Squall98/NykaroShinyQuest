package fr.nykaro.shinyquest.quest;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Statistiques et progression d'un joueur (ou du serveur en mode global).
 * Les quetes se calculent depuis ces agregats, ce qui rend la sauvegarde
 * legere et robuste.
 */
public class PlayerQuestData {

    private int totalCatches = 0;
    private int totalDefeats = 0;
    private int totalEvolves = 0;
    private int highestLevel = 0;
    private boolean caughtShiny = false;
    private boolean caughtLegendary = false;
    private final Set<String> caughtTypes = new LinkedHashSet<>();

    private int currentTierIndex = 0;
    private double oddsPercent = 0.0;
    private final Set<String> completedQuestIds = new HashSet<>();

    // ---- getters ----
    public int getTotalCatches() { return totalCatches; }
    public int getTotalDefeats() { return totalDefeats; }
    public int getTotalEvolves() { return totalEvolves; }
    public int getHighestLevel() { return highestLevel; }
    public boolean hasCaughtShiny() { return caughtShiny; }
    public boolean hasCaughtLegendary() { return caughtLegendary; }
    public Set<String> getCaughtTypes() { return caughtTypes; }
    public int getCurrentTierIndex() { return currentTierIndex; }
    public double getOddsPercent() { return oddsPercent; }
    public Set<String> getCompletedQuestIds() { return completedQuestIds; }

    // ---- mutations (utilisees par le ProgressionManager) ----
    public void addCatch() { totalCatches++; }
    public void addDefeat() { totalDefeats++; }
    public void addEvolve() { totalEvolves++; }
    public void reachLevel(int lvl) { if (lvl > highestLevel) highestLevel = lvl; }
    public void markShinyCaught() { caughtShiny = true; }
    public void markLegendaryCaught() { caughtLegendary = true; }
    public void addCaughtType(String type) { if (type != null) caughtTypes.add(type); }
    public void setCurrentTierIndex(int i) { currentTierIndex = i; }
    public void setOddsPercent(double v) { oddsPercent = Math.max(0.0, Math.min(100.0, v)); }

    public void reset() {
        totalCatches = totalDefeats = totalEvolves = highestLevel = 0;
        caughtShiny = caughtLegendary = false;
        caughtTypes.clear();
        completedQuestIds.clear();
        currentTierIndex = 0;
        oddsPercent = 0.0;
    }

    // ---- serialisation NBT ----
    public NbtCompound writeNbt(NbtCompound nbt) {
        nbt.putInt("catches", totalCatches);
        nbt.putInt("defeats", totalDefeats);
        nbt.putInt("evolves", totalEvolves);
        nbt.putInt("highestLevel", highestLevel);
        nbt.putBoolean("caughtShiny", caughtShiny);
        nbt.putBoolean("caughtLegendary", caughtLegendary);
        nbt.putInt("tierIndex", currentTierIndex);
        nbt.putDouble("odds", oddsPercent);
        nbt.put("caughtTypes", toStringList(caughtTypes));
        nbt.put("completedQuests", toStringList(completedQuestIds));
        return nbt;
    }

    public static PlayerQuestData readNbt(NbtCompound nbt) {
        PlayerQuestData d = new PlayerQuestData();
        d.totalCatches = nbt.getInt("catches");
        d.totalDefeats = nbt.getInt("defeats");
        d.totalEvolves = nbt.getInt("evolves");
        d.highestLevel = nbt.getInt("highestLevel");
        d.caughtShiny = nbt.getBoolean("caughtShiny");
        d.caughtLegendary = nbt.getBoolean("caughtLegendary");
        d.currentTierIndex = nbt.getInt("tierIndex");
        d.oddsPercent = nbt.getDouble("odds");
        readStringList(nbt.getList("caughtTypes", NbtElement.STRING_TYPE), d.caughtTypes);
        readStringList(nbt.getList("completedQuests", NbtElement.STRING_TYPE), d.completedQuestIds);
        return d;
    }

    private static NbtList toStringList(Set<String> set) {
        NbtList list = new NbtList();
        for (String s : set) list.add(NbtString.of(s));
        return list;
    }

    private static void readStringList(NbtList list, Set<String> into) {
        for (int i = 0; i < list.size(); i++) into.add(list.getString(i));
    }
}
