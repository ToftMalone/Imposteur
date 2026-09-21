# 🕵️ Imposteur

Jeu de soirée **hors-ligne** pour Android, à un seul téléphone que l'on se passe
autour de la table. Tous les joueurs reçoivent le même mot secret… sauf les
imposteurs. Décrivez votre mot sans le dire, démasquez l'intrus — ou bluffez
jusqu'au bout.

3 à 20 joueurs · 100 % local, aucune connexion réseau · français.

---

## ✨ Fonctionnalités

| | |
|---|---|
| 🃏 **Révélation à la main** | On passe le téléphone, chacun découvre son rôle puis **glisse la carte vers le haut** pour voir son mot. |
| 🎭 **3 modes d'imposteur** | *Mot différent* (un autre mot du même thème), *Thème seulement* (juste la catégorie), *Sans indice* (mode expert, l'imposteur ne sait rien). |
| 👥 **Imposteurs multiples** | Jusqu'à la moitié de la table moins un, avec option « les imposteurs se connaissent ». |
| 🗂️ **20 packs de mots** | Animaux, Lieux, Métiers, Nourriture, Sports, Films & Séries, Marques, Transports, Objets, Musique, Jeux vidéo, Célébrités, École, Maison, Technologie, Fêtes, Corps, Vêtements, Histoire & Mythologie, Situations. **1 051 mots** au total. |
| ➕ **Packs personnalisés** | Créez vos propres thèmes (nom, icône, liste de mots), modifiables et supprimables. |
| ⏱️ **Chronomètre** | Discussion minutée (désactivable, de 1 à 10 min) avec anneau de progression. |
| 🗣️ **Ordre de parole** | Aléatoire ou dans l'ordre de la liste, affiché pendant la discussion. |
| 🗳️ **Vote & élimination** | La table désigne un suspect, son rôle est révélé, la manche continue ou s'arrête. |
| 🎯 **Dernière chance** | Un imposteur démasqué peut deviner le mot des civils pour voler la victoire (comparaison insensible à la casse, aux accents et à la ponctuation). |
| 🏆 **Scores persistants** | Classement cumulé entre les manches, remise à zéro depuis les paramètres. |
| 💾 **Tout est sauvegardé** | Joueurs, avatars, réglages et packs personnalisés survivent à la fermeture de l'app (DataStore). |

---

## 📱 Écrans

```
Accueil ──┬─► Configuration ──► Partie ──► Révélation ──► Discussion ──► Vote
          │        │                           ▲              │           │
          │        └─► Packs ──► Éditeur       └──────────────┴──► Élimination
          │                                                            │
          ├─► Comment jouer                              Dernière chance ┤
          └─► Paramètres                                                 │
                                                          Fin de manche ◄┘
```

---

## 🏗️ Architecture

```
app/src/main/java/com/toftmalone/imposteur/
├── MainActivity.kt              # activité unique, Compose
├── data/
│   ├── Models.kt                # Player, WordPack, GameSettings, Round, Assignment…
│   ├── WordPacks.kt             # les 20 packs intégrés
│   ├── Avatars.kt               # avatars emoji + prénoms par défaut
│   └── ImposteurRepository.kt   # persistance DataStore + kotlinx.serialization
├── game/
│   ├── GameEngine.kt            # règles pures : distribution, votes, score
│   └── GameViewModel.kt         # état de la partie, minuterie, navigation de phase
└── ui/
    ├── ImposteurApp.kt          # NavHost + orchestration des phases de jeu
    ├── theme/                   # couleurs, typographie, thème Material 3
    ├── components/Common.kt     # boutons, avatars, steppers, interrupteurs
    └── screens/                 # 9 écrans
```

**Choix techniques**

- **Jetpack Compose + Material 3**, thème sombre unique (l'app se joue en soirée,
  et une seule palette garantit que les cartes de rôle s'affichent pareil partout).
- **`GameEngine` sans dépendance Android** : les règles sont du Kotlin pur, donc
  testables sur la JVM sans émulateur.
- **État unique** (`GameUiState`) exposé en `StateFlow` depuis le `GameViewModel` ;
  les écrans sont des fonctions d'affichage sans état métier.
- **Aucune permission** demandée, aucun accès réseau.

---

## 🔨 Compiler

```bash
# Débogage
./gradlew assembleDebug        # → app/build/outputs/apk/debug/app-debug.apk

# Tests unitaires du moteur de jeu
./gradlew testDebugUnitTest

# Installation sur un appareil branché
./gradlew installDebug
```

Prérequis : JDK 17, Android SDK 35 (`compileSdk = 35`, `minSdk = 24`).
Le plus simple est d'ouvrir le dossier dans Android Studio (Ladybug ou plus récent).

---

## ✅ Tests

`app/src/test/java/` couvre le moteur de jeu et le contenu :

- distribution des rôles (nombre d'imposteurs, mots des civils vs imposteurs) ;
- les trois modes d'imposteur, dont la garantie que le mode *Sans indice* ne
  laisse fuiter **ni mot ni thème** ;
- validation (trop peu de joueurs, imposteurs majoritaires, aucun pack) ;
- conditions de victoire et poursuite de la manche après un vote ;
- normalisation de la réponse de l'imposteur (casse, accents, ponctuation) ;
- attribution et cumul des points ;
- intégrité des packs (identifiants uniques, pas de doublon, pas de mot vide).

```bash
./gradlew testDebugUnitTest
```

---

## ⚖️ Règles du jeu

1. Chaque joueur découvre son rôle et son mot en privé.
2. À tour de rôle, chacun donne **un seul indice** sur son mot.
3. La table débat puis vote pour éliminer un suspect ; son rôle est révélé.
4. Les **civils** gagnent quand tous les imposteurs sont éliminés
   (**+2 points** chacun).
5. Les **imposteurs** gagnent dès qu'ils sont aussi nombreux que les civils
   (**+3 points** chacun).
6. Un imposteur démasqué peut tenter de deviner le mot : s'il réussit, il vole
   la manche (**+2 points**).
