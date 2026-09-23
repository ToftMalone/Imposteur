# Imposteur 1.0.1

Une version consacrée à la sécurité des mises à jour, suite à un audit du
code et de l'application.

## 🔐 Seul Imposteur peut s'installer comme mise à jour

Avant de proposer l'installation, l'app vérifie désormais que le fichier
téléchargé est bien **Imposteur**, dans une version **plus récente**, et
signé avec **la même clé** que l'app installée. Tout autre fichier est refusé
et supprimé, même s'il était publié sur la page des versions.

## 🌐 Téléchargements limités à GitHub

- Seules les adresses de GitHub et de son stockage de fichiers sont
  contactées, redirections comprises, et uniquement en HTTPS.
- Seuls les fichiers attachés aux versions d'Imposteur sont pris en compte.
- La page ouverte dans le navigateur reste celle des versions d'Imposteur.

## 📦 Tailles plafonnées

Un fichier de plus de 50 Mo est refusé, et la réponse de GitHub est limitée
à 1 Mo : une version piégée ne peut pas remplir le téléphone.

## ⚙️ Chaîne de publication renforcée

Les outils utilisés pour compiler et signer l'app sont figés sur une version
exacte, et la compilation automatique n'a plus que des droits de lecture.

## 📲 Installation

1. Télécharge `Imposteur-1.0.1.apk` ci-dessous.
2. Ouvre le fichier sur ton téléphone Android.
3. Autorise l'installation depuis cette source si Android le demande.

Android 7.0 (API 24) minimum. S'installe par-dessus toute version
précédente sans perdre les joueurs ni les réglages. Depuis la 1.0, la mise à
jour se fait directement dans l'app.
