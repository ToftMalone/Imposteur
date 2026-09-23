# Imposteur 0.2

## 🐛 Le rôle du joueur suivant ne fuite plus

Au moment d'appuyer sur « Passer au suivant », la carte se retournait à
l'envers et laissait apparaître un court instant la face du joueur suivant —
avec la couleur de son rôle. Tout le monde pouvait donc savoir qui était
l'imposteur avant même qu'il ait pris le téléphone.

C'est corrigé :

- chaque joueur a désormais son propre écran de révélation, qui repart de
  zéro : rien de la carte précédente (animation, couleur du bouton) ne reste
  affiché ;
- la carte ne se retourne plus que dans un sens, vers le rôle, et seulement
  après que le joueur concerné a appuyé sur « Je suis prêt ».

Après « Passer au suivant », on retombe directement sur l'écran neutre
« Passe le téléphone à… », sans aucune couleur.

## 📲 Installation

1. Télécharge `Imposteur-0.2.apk` ci-dessous.
2. Ouvre le fichier sur ton téléphone Android.
3. Autorise l'installation depuis cette source si Android le demande.

Android 7.0 (API 24) minimum. S'installe par-dessus la 0.1 ou la 0.1.1 sans
perdre les joueurs ni les réglages. Si tu as la 0.1.1, l'app te signalera
d'elle-même que la 0.2 est disponible.
