# ✨ Nykaro's Shiny Quest

📖 **Présentation illustrée** : https://squall98.github.io/NykaroShinyQuest/ (aussi dans `docs/index.html`)


> Un mod **Fabric** pour **Cobblemon** qui augmente progressivement tes chances de rencontrer des Pokémon **shiny** au fil des quêtes que tu accomplis.

![Minecraft](https://img.shields.io/badge/Minecraft-1.21.1-brightgreen)
![Loader](https://img.shields.io/badge/Loader-Fabric-blue)
![Requires](https://img.shields.io/badge/Requires-Cobblemon%201.6%2B-orange)
![License](https://img.shields.io/badge/License-MIT-yellow)

---

## 🎯 Le concept

Au début, tes chances de shiny sont normales. En accomplissant des **quêtes**
(capturer, combattre, faire évoluer, monter en niveau…), tu débloques des
**paliers** qui augmentent tes chances de shiny — jusqu'à **100 %** pour les
dresseurs les plus accomplis.

| Palier | Nom | Chances de shiny |
|:---:|---|:---:|
| 1 | Premiers pas | 2 % |
| 2 | Apprenti Dresseur | 5 % |
| 3 | Chasseur aguerri | 10 % |
| 4 | Expert Cobblemon | 25 % |
| 5 | Maître de la Chasse | 50 % |
| 6 | Légende Chromatique | 100 % |

*(Valeurs entièrement configurables.)*

## ✨ Fonctionnalités

- 📜 **Quêtes progressives** réparties en 6 paliers.
- 👤 **Progression par joueur** (ou globale au serveur, au choix).
- ⚙️ **Configuration JSON** : courbe de chances, objectifs, options.
- 🖥️ **Interface graphique** des quêtes (raccourci clavier).
- 📊 **HUD** affichant tes chances de shiny en jeu.
- 🔊 **Feedback** : son + particules à chaque quête terminée.
- 💬 **Commandes** : `/shinyquest odds | progress | set | reset`.
- 🌍 Traductions **français** et **anglais**.

## 📦 Prérequis

- Minecraft **1.21.1**
- [Fabric Loader](https://fabricmc.net/) **0.16.9+**
- [Fabric API](https://modrinth.com/mod/fabric-api)
- [Cobblemon](https://modrinth.com/mod/cobblemon) **1.6+**
- [Fabric Language Kotlin](https://modrinth.com/mod/fabric-language-kotlin) *(requis par Cobblemon)*

## 🚀 Installation

1. Installe Fabric pour Minecraft 1.21.1.
2. Place dans ton dossier `mods/` : Fabric API, Fabric Language Kotlin, Cobblemon,
   et `nykaros-shiny-quest-x.y.z.jar`.
3. Lance le jeu, ouvre le menu des quêtes avec le raccourci (par défaut **G**).

## ⚙️ Configuration

Le fichier `config/nykaroshiny.json` est généré au premier lancement.
Tu peux y régler la courbe de chances, les objectifs des quêtes, la
progression par joueur, l'affichage du HUD et le feedback.

## 🛠️ Compilation depuis les sources

```bash
git clone https://github.com/NYKARO_GITHUB/nykaros-shiny-quest.git
cd nykaros-shiny-quest
./gradlew build
```

Le jar final se trouve dans `build/libs/`.

## 📄 Licence

Distribué sous licence [MIT](LICENSE) © 2026 Nykaro.

> Mod **original**, écrit intégralement par Nykaro. Non affilié à Cobblemon.
