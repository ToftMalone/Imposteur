# 🕵️ Imposteur

[![Android CI](https://github.com/ToftMalone/Imposteur/actions/workflows/android.yml/badge.svg?branch=claude%2Fwizardly-ride-d93oat)](https://github.com/ToftMalone/Imposteur/actions/workflows/android.yml)

Jeu de soirée **hors-ligne** pour Android, à un seul téléphone que l'on se passe
autour de la table. Tous les joueurs reçoivent le même mot secret… sauf les
imposteurs. Décrivez votre mot sans le dire, démasquez l'intrus — ou bluffez
jusqu'au bout.

3 à 20 joueurs · le jeu fonctionne entièrement hors-ligne · français.

---

## ✨ Fonctionnalités

| | |
|---|---|
| 🃏 **Révélation à la main** | On passe le téléphone, chacun découvre son rôle puis **glisse la carte vers le haut** pour voir son mot. |
| 🎯 **Mots vraiment proches** | Le contenu est écrit par **paires** : *Pizza / Quiche*, *Dauphin / Baleine*, *Oslo / Stockholm*, *Psychiatre / Psychologue*. L'imposteur reçoit le partenaire du mot des civils, jamais un mot pris au hasard — son indice sonne juste et il faut vraiment écouter. |
| 🎭 **3 modes d'imposteur** | *Mot proche* (le partenaire de la paire), *Thème seulement* (juste la catégorie), *Sans indice* (mode expert, l'imposteur ne sait rien). |
| 👥 **Imposteurs multiples** | Jusqu'à la moitié de la table moins un, avec option « les imposteurs se connaissent ». |
| 🎨 **Illustrations maison** | 16 portraits de joueurs, 24 icônes de packs et 7 icônes d'interface, dessinés en vectoriel (`VectorDrawable`) : nets à toute taille, aucune dépendance aux emoji système. |
| 🗂️ **24 packs de mots** | Animaux, Lieux, Métiers, Nourriture, Sports, Films & Séries, Marques, Transports, Objets, Musique, Jeux vidéo, Célébrités, École, Maison, Technologie, Fêtes, Corps, Vêtements, Histoire & Mythologie, Situations, Émotions, Villes & France, Fantastique, Météo. **2 177 paires**, soit 4 354 mots. |
| 🃏 **Révélation sans fuite** | Fond neutre tant que le téléphone circule : la couleur du rôle n'apparaît qu'une fois la carte retournée, pour que le joueur précédent ne devine rien. |
| 🗣️ **Ordre de parole** | Aléatoire ou dans l'ordre de la liste, affiché pendant la discussion. |
| 🗳️ **Vote & élimination** | La table désigne un suspect, son rôle est révélé, la manche continue ou s'arrête. |
| 🎯 **Dernière chance** | Un imposteur démasqué peut deviner le mot des civils pour voler la victoire (comparaison insensible à la casse, aux accents et à la ponctuation). |
| 🏆 **Scores de session** | Classement cumulé entre les manches, remis à zéro à chaque lancement de l'app. |
| 🔔 **Mises à jour** | Au lancement, l'app demande à GitHub s'il existe une release plus récente, montre ses nouveautés, la télécharge et la confie à l'installateur d'Android. Vérifiable aussi à la main depuis les paramètres. |
| 💾 **Réglages sauvegardés** | Joueurs, avatars et options survivent à la fermeture de l'app (DataStore) — les scores, non. |

---

## 📱 Écrans

```
Accueil ──┬─► Configuration ──► Partie ──► Révélation ──► Discussion ──► Vote
          │     (joueurs,                     ▲              │           │
          │      imposteurs,                  └──────────────┴──► Élimination
          │      rôle, packs)                                          │
          ├─► Packs                                      Dernière chance ┤
          ├─► Comment jouer                                             │
          └─► Paramètres                                 Fin de manche ◄┘
```

---

## 🏗️ Architecture

```
app/src/main/java/com/toftmalone/imposteur/
├── MainActivity.kt              # activité unique, Compose
├── data/
│   ├── Models.kt                # Player, WordPair, WordPack, GameSettings, Round…
│   ├── WordPacks.kt             # les 24 packs intégrés
│   ├── Avatars.kt               # avatars emoji + prénoms par défaut
│   └── ImposteurRepository.kt   # persistance DataStore (joueurs + réglages)
├── game/
│   ├── GameEngine.kt            # règles pures : distribution, votes, score
│   └── GameViewModel.kt         # état de la partie, minuterie, navigation de phase
└── ui/
    ├── ImposteurApp.kt          # NavHost + orchestration des phases de jeu
    ├── theme/                   # couleurs, typographie, thème Material 3
    ├── components/Common.kt     # boutons, avatars, steppers, interrupteurs
    └── screens/                 # 8 écrans
```

**Choix techniques**

- **Jetpack Compose + Material 3**, thème sombre unique (l'app se joue en soirée,
  et une seule palette garantit que les cartes de rôle s'affichent pareil partout).
- **`GameEngine` sans dépendance Android** : les règles sont du Kotlin pur, donc
  testables sur la JVM sans émulateur.
- **État unique** (`GameUiState`) exposé en `StateFlow` depuis le `GameViewModel` ;
  les écrans sont des fonctions d'affichage sans état métier.
- **Contenu figé** : les packs sont compilés dans l'app, il n'y a pas
  d'éditeur de packs. Une paire mal fichue se corrige dans `WordPacks.kt`,
  où les tests la valident.
- **Deux permissions**, toutes deux pour les mises à jour : `INTERNET` pour
  demander à GitHub la dernière version publiée et la télécharger,
  `REQUEST_INSTALL_PACKAGES` pour la confier à l'installateur d'Android, qui
  demande confirmation. Le jeu lui-même ne transmet rien et fonctionne sans
  connexion.
- **Notes de version uniques** : `app/src/main/assets/RELEASE_NOTES.md` sert à
  la fois de texte de la release GitHub et de fenêtre « Quoi de neuf ? » dans
  l'app (sans son titre, ses emoji ni la section d'installation).
- **Toute l'imagerie est vectorielle et embarquée** : les emoji rendent
  différemment selon le constructeur et la version d'Android, les
  `VectorDrawable` non.

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
- le mot de l'imposteur est **toujours** le partenaire de celui des civils, et
  les deux sens d'une paire sortent au fil des parties ;
- les trois modes d'imposteur, dont la garantie que le mode *Sans indice* ne
  laisse fuiter **ni mot ni thème** ;
- validation (trop peu de joueurs, imposteurs majoritaires, aucun pack) ;
- conditions de victoire et poursuite de la manche après un vote ;
- normalisation de la réponse de l'imposteur (casse, accents, ponctuation) ;
- attribution et cumul des points ;
- intégrité des packs (identifiants uniques, pas de doublon, pas de mot vide,
  chaque paire formée de deux mots distincts et non vides).

```bash
./gradlew testDebugUnitTest
```

Le workflow `.github/workflows/android.yml` rejoue tout à chaque push :
compilation Kotlin/Compose, tests unitaires, puis assemblage de l'APK de debug,
publié comme artefact `imposteur-debug-apk`. Une étape dédiée vérifie que des
tests ont bien été exécutés — un `BUILD SUCCESSFUL` sur zéro test est traité
comme un échec.

---

## ⚖️ Règles du jeu

1. Chaque joueur découvre son rôle et son mot en privé. Les imposteurs
   reçoivent un mot **très proche** de celui des civils.
2. À tour de rôle, chacun donne **un seul indice** sur son mot.
3. La table débat puis vote pour éliminer un suspect ; son rôle est révélé.
4. Les **civils** gagnent quand tous les imposteurs sont éliminés
   (**+2 points** chacun).
5. Les **imposteurs** gagnent dès qu'ils sont aussi nombreux que les civils
   (**+3 points** chacun).
6. Un imposteur démasqué peut tenter de deviner le mot : s'il réussit, il vole
   la manche (**+2 points**).
