# Imposteur 0.1.1

Deux ajouts par rapport à la 0.1.

## 🔔 Détection des mises à jour

Au lancement, l'app demande à GitHub s'il existe une release plus récente. Si
oui, une bandeau discret apparaît sur l'écran d'accueil et ouvre la page de
téléchargement d'un appui. La vérification est aussi déclenchable à la main
depuis les paramètres.

Si le téléphone est hors-ligne, ou si la vérification échoue, l'app ne dit
rien et se lance normalement.

## ℹ️ Bloc « Infos de l'app »

Nouvelle section dans les paramètres : version installée, nombre de joueurs
possibles, nombre de packs et de paires de mots, rappel que rien ne quitte
l'appareil — et le crédit : **développé par ToftMalone**.

## 🔐 Ce qui change côté permissions

L'app demande désormais `INTERNET`, uniquement pour interroger l'API des
releases de GitHub. Aucune donnée de jeu n'est envoyée nulle part : la requête
ne transmet rien d'autre que la demande « quelle est la dernière version ? ».

## 📲 Installation

1. Télécharge `Imposteur-0.1.1.apk` ci-dessous.
2. Ouvre le fichier sur ton téléphone Android.
3. Autorise l'installation depuis cette source si Android le demande.

Android 7.0 (API 24) minimum. S'installe par-dessus la 0.1 sans perdre les
joueurs ni les réglages.
