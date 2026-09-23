# Imposteur 0.2.1

## 📥 Les mises à jour s'installent depuis l'app

Quand une nouvelle version sort, une fenêtre s'ouvre au lancement avec ses
nouveautés. Un appui sur **Mettre à jour** la télécharge directement dans
l'app, avec une barre de progression, puis Android propose de l'installer.
Plus besoin de passer par GitHub.

- Le fichier est vérifié avant d'être proposé : même taille et même empreinte
  SHA-256 que la version publiée.
- Rien ne s'installe sans ton accord : c'est Android qui demande de confirmer.
- La première fois, Android demande aussi d'autoriser Imposteur à installer
  des applications. Ce n'est à faire qu'une fois.
- **Plus tard** ferme la fenêtre ; le bandeau de l'accueil permet d'y revenir.

## ✨ Fenêtre « Quoi de neuf ? »

Au premier lancement après une mise à jour, l'app présente les nouveautés de
la version installée. On peut les relire à tout moment depuis
**Paramètres → Infos de l'app**.

## 🔐 Permissions

L'app demande désormais `REQUEST_INSTALL_PACKAGES`, pour pouvoir confier une
mise à jour téléchargée à l'installateur d'Android. Aucune donnée de jeu ne
quitte le téléphone.

## 📲 Installation

1. Télécharge `Imposteur-0.2.1.apk` ci-dessous.
2. Ouvre le fichier sur ton téléphone Android.
3. Autorise l'installation depuis cette source si Android le demande.

Android 7.0 (API 24) minimum. S'installe par-dessus les versions précédentes
sans perdre les joueurs ni les réglages. La 0.2 et les versions précédentes
renvoient encore vers cette page ; le téléchargement dans l'app fonctionnera
à partir de la mise à jour qui suivra la 0.2.1.
