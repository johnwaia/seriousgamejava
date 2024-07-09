import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MenuPrincipal {
    private Scanner scanner;
    private GestionBaseDeDonnees gestionBaseDeDonnees;
    private Joueur joueurConnecte;

    public MenuPrincipal() {
        scanner = new Scanner(System.in);
        gestionBaseDeDonnees = new GestionBaseDeDonnees();
    }

    public void afficherMenu() {
        boolean continuer = true;
        while (continuer) {
            System.out.println("===== MENU PRINCIPAL =====");
            System.out.println("1. S'inscrire");
            System.out.println("2. Se connecter");
            System.out.println("3. Quitter");
            System.out.print("Choix : ");
            int choix = scanner.nextInt();
            scanner.nextLine(); // Clear the buffer
            switch (choix) {
                case 1:
                    inscrireJoueur();
                    break;
                case 2:
                    System.out.print("Entrez votre nom d'utilisateur : ");
                    String nom = scanner.nextLine();
                    System.out.print("Entrez votre mot de passe : ");
                    String motDePasse = scanner.nextLine();
                    connecterJoueur(nom, motDePasse);
                    break;
                case 3:
                    continuer = false;
                    break;
                default:
                    System.out.println("Choix invalide, veuillez réessayer.");
            }
        }
    }

    public void connecterJoueur(String nom, String motDePasse) {
        // Hasher le mot de passe entré par l'utilisateur
        String motDePasseHash = hashMotDePasseSHA256(motDePasse);

        joueurConnecte = gestionBaseDeDonnees.recupererJoueurParNomEtMotDePasse(nom, motDePasseHash);
        if (joueurConnecte != null) {
            System.out.println("Connexion réussie pour le joueur : " + joueurConnecte.getPseudo());
            afficherMenuConnecte();
        } else {
            System.out.println("Échec de la connexion. Veuillez vérifier vos identifiants.");
        }
    }

    private String hashMotDePasseSHA256(String motDePasse) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(motDePasse.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void inscrireJoueur() {
        System.out.println("===== INSCRIPTION =====");
        scanner.nextLine(); // Clear scanner buffer
        System.out.print("Entrez un pseudo : ");
        String pseudo = scanner.nextLine();
        System.out.print("Entrez un mot de passe : ");
        String motDePasse = scanner.nextLine();

        // Hasher le mot de passe avec SHA-256
        String motDePasseHash = hashMotDePasseSHA256(motDePasse);

        Joueur joueur = new Joueur(0, pseudo, motDePasseHash); // Création d'un joueur avec ID temporaire (0)
        gestionBaseDeDonnees.ajouterJoueur(joueur); // Ajout du joueur à la base de données

        System.out.println("Inscription réussie ! Maintenant, veuillez entrer vos notes pour déterminer le niveau des questions.");

        System.out.print("Entrez votre note en Réseaux : ");
        int noteReseaux = scanner.nextInt();
        joueur.setNoteReseaux(noteReseaux);

        System.out.print("Entrez votre note en Développement Web : ");
        int noteWeb = scanner.nextInt();
        joueur.setNoteWeb(noteWeb);

        System.out.print("Entrez votre note en Programmation orientée objet en Java : ");
        int noteJava = scanner.nextInt();
        joueur.setNoteJava(noteJava);

        // Déterminer et définir l'avatar en fonction des notes
        joueur.determinerAvatar();

        // Déterminer le niveau des questions en utilisant l'avatar associé
        NiveauQuestion niveauQuestion = joueur.determinerNiveau();

        List<Question> questionsReseaux = gestionBaseDeDonnees.recupererQuestionsParNiveauEtMatiere(niveauQuestion, "Réseaux");
        List<Question> questionsWeb = gestionBaseDeDonnees.recupererQuestionsParNiveauEtMatiere(niveauQuestion, "Développement Web");
        List<Question> questionsJava = gestionBaseDeDonnees.recupererQuestionsParNiveauEtMatiere(niveauQuestion, "Programmation orientée objet en Java");

        System.out.println("===== Questions de Réseaux =====");
        repondreQuestionsAvecOptions(questionsReseaux, joueur); // Méthode pour répondre aux questions de réseaux
        System.out.println("===== Questions de Développement Web =====");
        repondreQuestionsAvecOptions(questionsWeb, joueur); // Méthode pour répondre aux questions de développement web
        System.out.println("===== Questions de Programmation orientée objet en Java =====");
        repondreQuestionsAvecOptions(questionsJava, joueur); // Méthode pour répondre aux questions de Java

        gestionBaseDeDonnees.mettreAJourJoueur(joueur); // Mettre à jour les informations du joueur dans la base de données
        afficherRecapitulatifJoueur(joueur);
        System.out.println("Mise à jour des informations réussie ! Vous êtes maintenant inscrit.");
    }

    private void repondreQuestionsAvecOptions(List<Question> questions, Joueur joueur) {
        for (int i = 0; i < Math.min(5, questions.size()); i++) {
            Question question = questions.get(i);
            System.out.println(question.getTexte());
            System.out.println("Choisissez la bonne réponse : ");
            System.out.println("a. " + question.getOption1());
            System.out.println("b. " + question.getOption2());
            System.out.println("c. " + question.getOption3());
            System.out.print("Votre réponse : ");
            String reponse = scanner.next().toUpperCase(); // Convertir la réponse en majuscules
            scanner.nextLine(); // Clear the buffer
            switch (reponse) {
                case "A":
                    reponse = question.getOption1();
                    break;
                case "B":
                    reponse = question.getOption2();
                    break;
                case "C":
                    reponse = question.getOption3();
                    break;
                default:
                    System.out.println("Réponse invalide, la question sera ignorée.");
                    continue;
            }
            if (question.verifierReponse(reponse)) {
                gestionBaseDeDonnees.enregistrerReponseCorrecte(joueur, question);
                joueur.ajouterQuestionCorrecte(question.getId());
                System.out.println("Réponse correcte !");
            } else {
                System.out.println("Réponse incorrecte.");
            }
        }
    }
   
    public void afficherMenuConnecte() {
        boolean continuer = true;
        while (continuer) {
            System.out.println("===== MENU CONNECTÉ =====");
            System.out.println("1. Lancer un Défi");
            System.out.println("2. Voir les défis");
            System.out.println("3. Déconnexion");
            System.out.println("4. Mettre à jour mes notes");
            System.out.print("Choix : ");
            int choix = scanner.nextInt();
            scanner.nextLine(); // Clear the buffer
            switch (choix) {
                case 1:
                    lancerDefi();
                    break;
                case 2:
                    voirDefis();
                    break;
                case 3:
                    continuer = false;
                    deconnexion();
                    break;
                case 4:
                    // Demander au joueur de saisir ses nouvelles notes
                    System.out.println("===== METTRE À JOUR MES NOTES =====");
                    System.out.print("Entrez votre nouvelle note en Réseaux : ");
                    int nouvelleNoteReseaux = scanner.nextInt();
                    System.out.print("Entrez votre nouvelle note en Développement Web : ");
                    int nouvelleNoteWeb = scanner.nextInt();
                    System.out.print("Entrez votre nouvelle note en Programmation orientée objet en Java : ");
                    int nouvelleNoteJava = scanner.nextInt();
                    
                    try {
                        gestionBaseDeDonnees.mettreAJourNotesJoueur(
                            joueurConnecte.getJoueur_id(),
                            nouvelleNoteReseaux,
                            nouvelleNoteWeb,
                            nouvelleNoteJava
                        );
                        joueurConnecte = gestionBaseDeDonnees.recupererJoueurParId(joueurConnecte.getJoueur_id()); // Mettre à jour le joueur connecté après la mise à jour des notes
                        afficherRecapitulatifJoueur(joueurConnecte); // Afficher le récapitulatif mis à jour
                    } catch (SQLException e) {
                        System.out.println("Erreur lors de la mise à jour des notes : " + e.getMessage());
                    }
                    break;
                default:
                    System.out.println("Choix invalide, veuillez réessayer.");
            }
        }
    }
    
    public void lancerDefi() {
        if (joueurConnecte != null && joueurConnecte.getAvatar() != null) {
            String avatarNom = joueurConnecte.getAvatar().getNom();
            List<Joueur> joueursAvecMemeAvatar = gestionBaseDeDonnees.recupererJoueursParAvatar(avatarNom);
    
            if (joueursAvecMemeAvatar.isEmpty()) {
                System.out.println("Aucun joueur avec le même avatar disponible pour défier.");
                return;
            }
            System.out.println("Joueurs avec le même avatar (" + avatarNom + ") :");
            for (int i = 0; i < joueursAvecMemeAvatar.size(); i++) {
                Joueur joueur = joueursAvecMemeAvatar.get(i);
                System.out.println((i + 1) + ". Nom: " + joueur.getPseudo());
            }
            System.out.print("Choisissez le numéro du joueur à défier : ");
            int choixJoueur = scanner.nextInt();
            scanner.nextLine(); // Clear the buffer
            if (choixJoueur > 0 && choixJoueur <= joueursAvecMemeAvatar.size()) {
                Joueur joueurDefie = joueursAvecMemeAvatar.get(choixJoueur - 1);
    
                // Variables pour la sélection de matière et niveau
                String matiere = "";
                String niveau = "";
                List<Question> questions = new ArrayList<>();
                boolean questionsTrouvees = false;
                while (!questionsTrouvees) {
                    // Menu pour choisir la matière
                    String[] matieres = {"Programmation orientée objet en Java", "Développement web", "Réseaux"};
                    System.out.println("Choisissez la matière des questions :");
                    for (int i = 0; i < matieres.length; i++) {
                        System.out.println((i + 1) + ". " + matieres[i]);
                    }
                    System.out.print("Votre choix : ");
                    int choixMatiere = scanner.nextInt();
                    scanner.nextLine(); // Clear the buffer
                    if (choixMatiere > 0 && choixMatiere <= matieres.length) {
                        matiere = matieres[choixMatiere - 1];
                        // Menu pour choisir le niveau
                        String[] niveaux = {"Facile", "Moyen", "Difficile"};
                        System.out.println("Choisissez le niveau des questions :");
                        for (int i = 0; i < niveaux.length; i++) {
                            System.out.println((i + 1) + ". " + niveaux[i]);
                        }
                        System.out.print("Votre choix : ");
                        int choixNiveau = scanner.nextInt();
                        scanner.nextLine(); // Clear the buffer
                        if (choixNiveau > 0 && choixNiveau <= niveaux.length) {
                            niveau = niveaux[choixNiveau - 1];
                            // Récupération des questions disponibles pour le défi
                            questions = gestionBaseDeDonnees.recupererQuestionsPourDefi(joueurConnecte.getJoueur_id(), matiere, niveau);
                            if (questions.isEmpty()) {
                                System.out.println("Aucune question disponible pour ces critères. Veuillez choisir une autre matière ou un autre niveau.");
                            } else {
                                questionsTrouvees = true;
                            }
                        } else {
                            System.out.println("Choix invalide pour le niveau.");
                        }
                    } else {
                        System.out.println("Choix invalide pour la matière.");
                    }
                }
                // Envoi du défi à la base de données
                try {
                    gestionBaseDeDonnees.ajouterDefi(joueurConnecte.getJoueur_id(), joueurDefie.getJoueur_id(), questions, matiere, niveau);
                    System.out.println("Défi envoyé à " + joueurDefie.getPseudo());
                    // Afficher les questions envoyées
                    System.out.println("Questions envoyées :");
                    for (Question question : questions) {
                        System.out.println("- " + question.getTexte());
                    }
                } catch (SQLException e) {
                    System.out.println("Erreur lors de l'envoi du défi : " + e.getMessage());
                }
            } else {
                System.out.println("Choix invalide pour le joueur à défier.");
            }
        } else {
            System.out.println("Aucun joueur connecté ou avatar non défini.");
        }
    }
    
    private void voirDefis() {
        if (joueurConnecte != null) {
            List<Defi> defis = gestionBaseDeDonnees.recupererDefisPourJoueur(joueurConnecte.getJoueur_id());
            if (!defis.isEmpty()) {
                System.out.println("Défis reçus :");
                for (int i = 0; i < defis.size(); i++) {
                    Defi defi = defis.get(i);
                    Joueur emetteur = gestionBaseDeDonnees.recupererJoueurParId(defi.getEmetteur().getJoueur_id());
                    System.out.println((i + 1) + ". Défi de " + emetteur.getPseudo() + " - Matière: " + defi.getMatiere() + ", Niveau: " + defi.getNiveau());
    
                    // Vérifier si le défi est toujours valide (moins de 2 jours depuis la création)
                    long diffInMillis = System.currentTimeMillis() - defi.getDateCreation().getTime();
                    long diffInDays = diffInMillis / (1000 * 60 * 60 * 24);
    
                    if (diffInDays >= 2) {
                        System.out.println("Ce défi a expiré.");
                        continue;
                    }
    
                    // Proposer au joueur de choisir entre accepter ou refuser le défi
                    System.out.print("Voulez-vous accepter ce défi ? (Oui/Non) : ");
                    String choix = scanner.nextLine().trim().toLowerCase();
                    if (choix.equals("oui")) {
                        // Accepter le défi
                        gestionBaseDeDonnees.accepterDefi(defi.getIdDefi());
                        System.out.println("Défi accepté !");
    
                        // Récupérer les questions pour ce défi depuis la table defi_questions
                        List<Question> questions = gestionBaseDeDonnees.recupererQuestionsPourDefirecepteur(defi.getIdDefi());
                        if (!questions.isEmpty()) {
                            System.out.println("Questions du défi :");
                            for (Question question : questions) {
                                System.out.println("Question : " + question.getTexte());
                                System.out.println("Options : ");
                                System.out.println("a. " + question.getOption1());
                                System.out.println("b. " + question.getOption2());
                                System.out.println("c. " + question.getOption3());
                                System.out.print("Votre réponse : ");
                                String reponseUtilisateur = scanner.nextLine().trim().toUpperCase();
    
                                // Vérifier la réponse
                                if (question.verifierReponse(reponseUtilisateur)) {
                                    System.out.println("Bonne réponse !");
                                    gestionBaseDeDonnees.enregistrerReponseCorrecte(joueurConnecte, question);
                                } else {
                                    System.out.println("Mauvaise réponse !");
                                }
                            }
                        } else {
                            System.out.println("Aucune question disponible pour ce défi.");
                        }
                    } else {
                        // Refuser le défi
                        gestionBaseDeDonnees.refuserDefi(defi.getIdDefi());
                        System.out.println("Défi refusé.");
                    }
                }
            } else {
                System.out.println("Vous n'avez reçu aucun défi.");
            }
        } else {
            System.out.println("Aucun joueur connecté.");
        }
    }

    private void deconnexion() {
        joueurConnecte = null;
        System.out.println("Déconnexion réussie.");
    }

    private void afficherRecapitulatifJoueur(Joueur joueur) {
        System.out.println("===== RÉCAPITULATIF =====");
        System.out.println("Nom : " + joueur.getPseudo());
        System.out.println("Avatar : " + joueur.getAvatar().getNom());
        System.out.println("Points de vie : " + joueur.getAvatar().getPointsDeVie());
        System.out.println("Note Réseaux : " + joueur.getNoteReseaux());
        System.out.println("Note Développement Web : " + joueur.getNoteWeb());
        System.out.println("Note POO Java : " + joueur.getNoteJava());

        List<Question> questionsCorrectes = gestionBaseDeDonnees.recupererQuestionsCorrectesJoueur(joueur.getJoueur_id());
        if (!questionsCorrectes.isEmpty()) {
            System.out.println("Questions correctement répondues : ");
            for (Question question : questionsCorrectes) {
                System.out.println("- " + question.getTexte());
                System.out.println("   Réponse correcte : " + question.getReponse());
                System.out.println("   Niveau : " + question.getNiveau());
            }
        } else {
            System.out.println("Le joueur n'a pas encore répondu correctement à des questions.");
        }
    }
}
