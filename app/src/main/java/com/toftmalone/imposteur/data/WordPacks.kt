package com.toftmalone.imposteur.data

/**
 * The word packs that ship with the app. Everything is in French, which is the
 * language the game is designed around; custom packs let players add their own.
 */
object WordPacks {

    val ANIMAUX = WordPack(
        id = "animaux",
        name = "Animaux & Nature",
        emoji = "🦁",
        words = listOf(
            "Lion", "Éléphant", "Girafe", "Pingouin", "Dauphin", "Requin", "Papillon", "Abeille",
            "Coccinelle", "Hérisson", "Renard", "Loup", "Ours", "Kangourou", "Panda", "Koala",
            "Crocodile", "Serpent", "Tortue", "Grenouille", "Chauve-souris", "Hibou", "Aigle",
            "Perroquet", "Flamant rose", "Autruche", "Zèbre", "Hippopotame", "Rhinocéros",
            "Chameau", "Écureuil", "Castor", "Baleine", "Pieuvre", "Méduse", "Étoile de mer",
            "Homard", "Escargot", "Araignée", "Fourmi", "Chenille", "Libellule", "Cerf",
            "Sanglier", "Chèvre", "Mouton", "Poule", "Canard", "Cheval", "Lapin", "Hamster",
            "Forêt", "Volcan", "Cascade", "Arc-en-ciel", "Orage", "Désert", "Glacier", "Rivière",
            "Montagne", "Tournesol", "Cactus", "Champignon", "Chêne", "Bambou",
        ),
    )

    val LIEUX = WordPack(
        id = "lieux",
        name = "Monde & Lieux",
        emoji = "🌍",
        words = listOf(
            "Paris", "Tokyo", "New York", "Londres", "Rome", "Barcelone", "Le Caire", "Sydney",
            "Moscou", "Berlin", "Amsterdam", "Venise", "Istanbul", "Rio de Janeiro", "Marrakech",
            "Dubaï", "Bangkok", "Athènes", "Lisbonne", "Prague", "Tour Eiffel", "Colisée",
            "Pyramides de Gizeh", "Grande Muraille", "Taj Mahal", "Statue de la Liberté",
            "Machu Picchu", "Mont Fuji", "Everest", "Sahara", "Amazonie", "Grand Canyon",
            "Chutes du Niagara", "Bali", "Islande", "Maldives", "Alaska", "Sibérie", "Antarctique",
            "Hawaï", "Aéroport", "Plage", "Hôtel", "Musée", "Château", "Marché", "Gare",
            "Camping", "Île déserte", "Station de ski", "Phare", "Cathédrale", "Zoo",
            "Parc d'attractions", "Bibliothèque",
        ),
    )

    val METIERS = WordPack(
        id = "metiers",
        name = "Métiers",
        emoji = "🩺",
        words = listOf(
            "Médecin", "Infirmier", "Pompier", "Policier", "Avocat", "Juge", "Professeur",
            "Boulanger", "Boucher", "Cuisinier", "Serveur", "Pâtissier", "Coiffeur", "Plombier",
            "Électricien", "Menuisier", "Maçon", "Architecte", "Ingénieur", "Informaticien",
            "Pilote de ligne", "Hôtesse de l'air", "Chauffeur de taxi", "Facteur", "Jardinier",
            "Vétérinaire", "Dentiste", "Pharmacien", "Psychologue", "Journaliste", "Photographe",
            "Acteur", "Chanteur", "Danseur", "Peintre", "Écrivain", "Musicien", "Astronaute",
            "Militaire", "Marin", "Pêcheur", "Agriculteur", "Berger", "Bibliothécaire",
            "Comptable", "Banquier", "Vendeur", "Agent immobilier", "Garagiste", "Arbitre",
            "Entraîneur", "Magicien", "Clown", "Détective", "Archéologue", "Chercheur",
        ),
    )

    val NOURRITURE = WordPack(
        id = "nourriture",
        name = "Nourriture & Boissons",
        emoji = "🍔",
        words = listOf(
            "Pizza", "Burger", "Frites", "Sushi", "Pâtes", "Lasagnes", "Crêpe", "Gaufre",
            "Croissant", "Baguette", "Sandwich", "Kebab", "Tacos", "Paella", "Couscous",
            "Raclette", "Fondue", "Quiche", "Omelette", "Soupe", "Salade", "Riz", "Steak",
            "Poulet rôti", "Saumon", "Fromage", "Jambon", "Saucisson", "Yaourt", "Glace",
            "Chocolat", "Bonbon", "Gâteau", "Tarte aux pommes", "Macaron", "Éclair", "Madeleine",
            "Pop-corn", "Chips", "Céréales", "Miel", "Confiture", "Pomme", "Banane", "Fraise",
            "Ananas", "Pastèque", "Citron", "Avocat", "Tomate", "Carotte", "Café", "Thé",
            "Jus d'orange", "Limonade", "Smoothie", "Chocolat chaud", "Soda", "Eau gazeuse",
        ),
    )

    val SPORTS = WordPack(
        id = "sports",
        name = "Sports",
        emoji = "⚽",
        words = listOf(
            "Football", "Basketball", "Tennis", "Rugby", "Handball", "Volleyball", "Natation",
            "Athlétisme", "Marathon", "Cyclisme", "Ski", "Snowboard", "Patinage", "Hockey",
            "Boxe", "Judo", "Karaté", "Escrime", "Gymnastique", "Golf", "Surf", "Plongée",
            "Voile", "Aviron", "Équitation", "Escalade", "Randonnée", "Yoga", "Musculation",
            "Badminton", "Ping-pong", "Pétanque", "Bowling", "Fléchettes", "Skateboard",
            "Parachutisme", "Formule 1", "Motocross", "Tir à l'arc", "Baseball", "Cricket",
            "Lutte", "Haltérophilie", "Triathlon", "Curling", "Ballon", "Arbitre", "Stade",
            "Médaille", "Championnat", "Vestiaire", "Prolongation",
        ),
    )

    val FILMS = WordPack(
        id = "films",
        name = "Films & Séries",
        emoji = "🎬",
        words = listOf(
            "Titanic", "Avatar", "Le Parrain", "Star Wars", "Harry Potter",
            "Le Seigneur des Anneaux", "Jurassic Park", "Matrix", "Inception", "Interstellar",
            "Gladiator", "Forrest Gump", "Le Roi Lion", "La Reine des Neiges", "Toy Story",
            "Shrek", "Les Indestructibles", "Là-haut", "Ratatouille", "Le Voyage de Chihiro",
            "Pirates des Caraïbes", "Indiana Jones", "Retour vers le futur", "Rocky",
            "Terminator", "Alien", "Les Dents de la mer", "E.T.", "Batman", "Spider-Man",
            "Avengers", "Fast and Furious", "Mission Impossible", "James Bond", "Intouchables",
            "Le Fabuleux Destin d'Amélie Poulain", "Les Visiteurs", "Astérix", "Breaking Bad",
            "Game of Thrones", "Friends", "Stranger Things", "The Office", "La Casa de Papel",
            "Squid Game", "Lupin", "Black Mirror", "Peaky Blinders", "Sherlock", "Dexter",
            "Narcos", "The Walking Dead", "How I Met Your Mother",
        ),
    )

    val MARQUES = WordPack(
        id = "marques",
        name = "Marques",
        emoji = "🏷️",
        words = listOf(
            "Nike", "Adidas", "Puma", "Coca-Cola", "Pepsi", "McDonald's", "Burger King", "KFC",
            "Starbucks", "Apple", "Samsung", "Google", "Microsoft", "Amazon", "Netflix",
            "Spotify", "YouTube", "Instagram", "TikTok", "Facebook", "Snapchat", "WhatsApp",
            "Sony", "Nintendo", "PlayStation", "Xbox", "Tesla", "Ferrari", "Lamborghini", "BMW",
            "Mercedes", "Audi", "Volkswagen", "Toyota", "Renault", "Peugeot", "Citroën", "Ikea",
            "Lego", "Disney", "Chanel", "Dior", "Gucci", "Louis Vuitton", "Zara", "H&M", "Rolex",
            "Red Bull", "Nutella", "Lindt", "Danone", "Carrefour", "Decathlon", "Uber", "Airbnb",
        ),
    )

    val TRANSPORTS = WordPack(
        id = "transports",
        name = "Transports",
        emoji = "🚗",
        words = listOf(
            "Voiture", "Vélo", "Trottinette", "Moto", "Scooter", "Bus", "Tramway", "Métro",
            "Train", "TGV", "Avion", "Hélicoptère", "Montgolfière", "Fusée", "Bateau", "Voilier",
            "Ferry", "Paquebot", "Sous-marin", "Kayak", "Pédalo", "Jet-ski", "Camion",
            "Camionnette", "Tracteur", "Bulldozer", "Grue", "Ambulance", "Camion de pompiers",
            "Voiture de police", "Taxi", "Limousine", "Décapotable", "Caravane", "Camping-car",
            "Téléphérique", "Télésiège", "Ascenseur", "Escalator", "Patins à roulettes",
            "Skateboard", "Luge", "Traîneau", "Char à voile", "Quad", "Karting", "Dirigeable",
            "Navette spatiale", "Gondole", "Calèche",
        ),
    )

    val OBJETS = WordPack(
        id = "objets",
        name = "Objets du quotidien",
        emoji = "🧰",
        words = listOf(
            "Brosse à dents", "Parapluie", "Lunettes", "Montre", "Portefeuille", "Clés",
            "Sac à dos", "Valise", "Miroir", "Peigne", "Rasoir", "Serviette", "Savon",
            "Shampoing", "Ciseaux", "Couteau", "Fourchette", "Cuillère", "Assiette", "Verre",
            "Tasse", "Bouteille", "Casserole", "Poêle", "Éponge", "Balai", "Aspirateur",
            "Fer à repasser", "Machine à laver", "Réfrigérateur", "Micro-ondes", "Grille-pain",
            "Bouilloire", "Cafetière", "Télécommande", "Chargeur", "Écouteurs", "Lampe",
            "Bougie", "Réveil", "Oreiller", "Couverture", "Tapis", "Rideau", "Cadenas",
            "Marteau", "Tournevis", "Échelle", "Corde", "Pince à linge", "Bouchon", "Élastique",
        ),
    )

    val MUSIQUE = WordPack(
        id = "musique",
        name = "Musique",
        emoji = "🎵",
        words = listOf(
            "Guitare", "Piano", "Violon", "Batterie", "Flûte", "Saxophone", "Trompette", "Harpe",
            "Accordéon", "Ukulélé", "Basse", "Clarinette", "Trombone", "Violoncelle", "Orgue",
            "Harmonica", "Tambour", "Maracas", "Triangle", "Xylophone", "Rap", "Rock", "Jazz",
            "Blues", "Reggae", "Techno", "Musique classique", "Opéra", "Pop", "Country", "Métal",
            "Disco", "Salsa", "Chorale", "Concert", "Festival", "Karaoké", "Playlist",
            "Casque audio", "Vinyle", "Radio", "Micro", "Partition", "Chef d'orchestre",
            "Groupe de musique", "Tournée", "Refrain", "Mélodie",
        ),
    )

    val JEUX_VIDEO = WordPack(
        id = "jeuxvideo",
        name = "Jeux vidéo",
        emoji = "🎮",
        words = listOf(
            "Minecraft", "Fortnite", "Mario", "Zelda", "Pokémon", "Sonic", "Tetris", "Pac-Man",
            "Among Us", "Fall Guys", "Roblox", "Call of Duty", "GTA", "FIFA", "Rocket League",
            "Overwatch", "League of Legends", "Valorant", "Counter-Strike", "Assassin's Creed",
            "Les Sims", "Animal Crossing", "Candy Crush", "Angry Birds", "Clash Royale",
            "Subway Surfers", "Street Fighter", "Tomb Raider", "Resident Evil", "Dark Souls",
            "Elden Ring", "God of War", "Halo", "Portal", "Skyrim", "Mario Kart", "Just Dance",
            "Guitar Hero", "Wii Sports", "Manette", "Console", "Clavier", "Souris", "Arcade",
            "Speedrun", "Boss final", "Niveau bonus",
        ),
    )

    val CELEBRITES = WordPack(
        id = "celebrites",
        name = "Célébrités",
        emoji = "⭐",
        words = listOf(
            "Albert Einstein", "Napoléon", "Cléopâtre", "Léonard de Vinci", "Mozart",
            "Beethoven", "Van Gogh", "Picasso", "Shakespeare", "Marie Curie",
            "Charles de Gaulle", "Jeanne d'Arc", "Louis XIV", "Gandhi", "Nelson Mandela",
            "Martin Luther King", "Christophe Colomb", "Neil Armstrong", "Charlie Chaplin",
            "Marilyn Monroe", "Elvis Presley", "Michael Jackson", "Freddie Mercury",
            "Bob Marley", "Madonna", "Beyoncé", "Rihanna", "Taylor Swift", "Adele", "Lady Gaga",
            "Eminem", "Zinédine Zidane", "Kylian Mbappé", "Lionel Messi", "Cristiano Ronaldo",
            "Michael Jordan", "LeBron James", "Usain Bolt", "Roger Federer", "Rafael Nadal",
            "Teddy Riner", "Tony Parker", "Leonardo DiCaprio", "Brad Pitt", "Tom Cruise",
            "Will Smith", "Morgan Freeman", "Robert De Niro", "Omar Sy", "Jean Dujardin",
            "Louis de Funès", "Jean Reno", "Marion Cotillard", "Steve Jobs", "Bill Gates",
            "Elon Musk", "Walt Disney",
        ),
    )

    val ECOLE = WordPack(
        id = "ecole",
        name = "École",
        emoji = "🎒",
        words = listOf(
            "Cartable", "Trousse", "Stylo", "Crayon", "Gomme", "Règle", "Compas",
            "Calculatrice", "Cahier", "Classeur", "Manuel", "Tableau", "Craie", "Feutre",
            "Bureau", "Récréation", "Cantine", "Cour de récré", "Gymnase", "Bibliothèque",
            "Laboratoire", "Mathématiques", "Français", "Histoire", "Géographie",
            "Sciences", "Physique", "Chimie", "Biologie", "Anglais", "Espagnol", "Philosophie",
            "Arts plastiques", "Sport", "Contrôle", "Examen", "Bulletin", "Devoirs", "Note",
            "Diplôme", "Rentrée des classes", "Vacances scolaires", "Directeur", "Surveillant",
            "Professeur principal", "Sonnerie", "Sortie scolaire", "Carnet de correspondance",
        ),
    )

    val MAISON = WordPack(
        id = "maison",
        name = "Maison",
        emoji = "🏠",
        words = listOf(
            "Salon", "Cuisine", "Chambre", "Salle de bain", "Toilettes", "Garage", "Grenier",
            "Cave", "Jardin", "Balcon", "Terrasse", "Couloir", "Escalier", "Porte", "Fenêtre",
            "Toit", "Cheminée", "Canapé", "Fauteuil", "Table", "Lit", "Armoire", "Commode",
            "Étagère", "Tabouret", "Évier", "Baignoire", "Douche", "Lavabo", "Placard",
            "Tiroir", "Radiateur", "Climatisation", "Interrupteur", "Prise électrique",
            "Sonnette", "Boîte aux lettres", "Portail", "Clôture", "Piscine", "Barbecue",
            "Niche", "Cabane", "Parquet", "Moquette", "Papier peint", "Plafond", "Mur",
        ),
    )

    val TECHNOLOGIE = WordPack(
        id = "technologie",
        name = "Technologie",
        emoji = "💻",
        words = listOf(
            "Ordinateur", "Smartphone", "Tablette", "Montre connectée", "Écran", "Clavier",
            "Souris", "Imprimante", "Scanner", "Webcam", "Casque VR", "Drone", "Robot",
            "Satellite", "Wifi", "Bluetooth", "Clé USB", "Disque dur", "Carte SIM", "Batterie",
            "Chargeur", "Application", "Navigateur", "Moteur de recherche", "Réseau social",
            "Email", "Mot de passe", "Pirate informatique", "Virus", "Pare-feu", "Cloud",
            "Serveur", "Base de données", "Algorithme", "Intelligence artificielle",
            "Réalité virtuelle", "Blockchain", "Cryptomonnaie", "Streaming", "Podcast",
            "Visioconférence", "GPS", "Code-barres", "QR code", "Panneau solaire",
            "Voiture électrique", "Console de jeu", "Enceinte connectée", "Imprimante 3D",
        ),
    )

    val FETES = WordPack(
        id = "fetes",
        name = "Fêtes & Événements",
        emoji = "🎉",
        words = listOf(
            "Anniversaire", "Noël", "Nouvel An", "Pâques", "Halloween", "Carnaval", "Mariage",
            "Baptême", "Fête de la musique", "14 juillet", "Saint-Valentin", "Fête des mères",
            "Fête des pères", "Thanksgiving", "Ramadan", "Hanouka", "Diwali", "Réveillon",
            "Feu d'artifice", "Défilé", "Concert", "Festival", "Barbecue", "Pique-nique",
            "Soirée pyjama", "Enterrement de vie de garçon", "Remise de diplôme", "Crémaillère",
            "Bal masqué", "Kermesse", "Foire", "Fête foraine", "Cirque", "Brocante",
            "Marché de Noël", "Coupe du monde", "Jeux olympiques", "Eurovision", "Oscars",
            "Départ à la retraite", "Déménagement", "Vacances d'été", "Colonie de vacances",
            "Voyage scolaire", "Gâteau d'anniversaire", "Cadeau", "Confettis", "Ballons",
        ),
    )

    val CORPS = WordPack(
        id = "corps",
        name = "Corps & Santé",
        emoji = "🫀",
        words = listOf(
            "Tête", "Cheveux", "Front", "Sourcil", "Œil", "Nez", "Bouche", "Dent", "Langue",
            "Oreille", "Menton", "Joue", "Cou", "Épaule", "Bras", "Coude", "Poignet", "Main",
            "Doigt", "Ongle", "Pouce", "Poitrine", "Dos", "Ventre", "Nombril", "Hanche",
            "Jambe", "Genou", "Cheville", "Pied", "Orteil", "Talon", "Cœur", "Cerveau",
            "Poumon", "Estomac", "Foie", "Rein", "Muscle", "Os", "Squelette", "Sang", "Peau",
            "Fièvre", "Rhume", "Grippe", "Migraine", "Allergie", "Vaccin", "Pansement",
            "Plâtre", "Radiographie", "Ordonnance", "Stéthoscope", "Seringue",
        ),
    )

    val VETEMENTS = WordPack(
        id = "vetements",
        name = "Vêtements",
        emoji = "👗",
        words = listOf(
            "T-shirt", "Chemise", "Pull", "Sweat", "Veste", "Manteau", "Blouson", "Imperméable",
            "Gilet", "Pantalon", "Jean", "Short", "Jupe", "Robe", "Costume", "Cravate",
            "Nœud papillon", "Ceinture", "Chaussettes", "Collants", "Chaussures", "Baskets",
            "Bottes", "Sandales", "Tongs", "Talons hauts", "Pantoufles", "Casquette", "Chapeau",
            "Bonnet", "Écharpe", "Gants", "Moufles", "Lunettes de soleil", "Maillot de bain",
            "Pyjama", "Peignoir", "Tablier", "Uniforme", "Smoking", "Salopette", "Combinaison",
            "Poncho", "Kimono", "Collier", "Bracelet", "Bague", "Boucles d'oreilles",
            "Sac à main", "Foulard", "Bretelles",
        ),
    )

    val HISTOIRE = WordPack(
        id = "histoire",
        name = "Histoire & Mythologie",
        emoji = "🏛️",
        words = listOf(
            "Zeus", "Poséidon", "Hadès", "Athéna", "Apollon", "Artémis", "Arès", "Aphrodite",
            "Hermès", "Héra", "Hercule", "Achille", "Ulysse", "Icare", "Méduse", "Minotaure",
            "Cyclope", "Sirène", "Pégase", "Centaure", "Sphinx", "Cerbère", "Thor", "Odin",
            "Loki", "Valkyrie", "Viking", "Pharaon", "Toutânkhamon", "Momie", "Hiéroglyphe",
            "Gladiateur", "Empereur romain", "Légion romaine", "Chevalier", "Château fort",
            "Dragon", "Roi Arthur", "Excalibur", "Merlin", "Robin des Bois", "Samouraï",
            "Ninja", "Pirate", "Corsaire", "Cow-boy", "Révolution française", "Guillotine",
            "Mur de Berlin", "Renaissance", "Moyen Âge", "Préhistoire", "Dinosaure", "Mammouth",
        ),
    )

    val ACTIONS = WordPack(
        id = "actions",
        name = "Situations & Actions",
        emoji = "🎭",
        words = listOf(
            "Dormir", "Courir", "Nager", "Danser", "Chanter", "Rire", "Pleurer", "Éternuer",
            "Bâiller", "Cuisiner", "Manger", "Boire", "Conduire", "Téléphoner", "Lire",
            "Écrire", "Dessiner", "Bricoler", "Jardiner", "Ranger", "Nettoyer", "Repasser",
            "Se maquiller", "Se raser", "Se doucher", "Se brosser les dents",
            "Faire les courses", "Attendre le bus", "Faire la queue", "Rater son train",
            "Déménager", "Se perdre", "Tomber amoureux", "Se disputer", "Faire la sieste",
            "Regarder un film", "Jouer aux cartes", "Faire un selfie", "Prendre l'avion",
            "Passer un examen", "Faire un discours", "Souffler les bougies",
            "Ouvrir un cadeau", "Aller chez le dentiste", "Rater le réveil", "Perdre ses clés",
            "Faire du camping", "Monter une tente", "Faire un barbecue",
        ),
    )

    /** Every built-in pack, in the order they appear in the packs screen. */
    val BUILT_IN: List<WordPack> = listOf(
        ANIMAUX, LIEUX, METIERS, NOURRITURE, SPORTS, FILMS, MARQUES, TRANSPORTS,
        OBJETS, MUSIQUE, JEUX_VIDEO, CELEBRITES, ECOLE, MAISON, TECHNOLOGIE, FETES,
        CORPS, VETEMENTS, HISTOIRE, ACTIONS,
    )

    private val byId: Map<String, WordPack> = BUILT_IN.associateBy { it.id }

    fun findById(id: String): WordPack? = byId[id]
}
