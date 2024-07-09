import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GestionBaseDeDonnees {
    private static final String URL = "jdbc:mysql://localhost/seriousgame";
    private static final String USER = "root";
    private static final String PASSWORD = "";
    private Connection connection;
    private List<Joueur> joueurs;
   
    public GestionBaseDeDonnees() {
        joueurs = new ArrayList<>();
        try {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la connexion à la base de données", e);
        }
    }

    public void ajouterJoueur(Joueur joueur) {
        String sql = "INSERT INTO joueurs (nom, mot_de_passe, note_reseaux, note_web, note_java, avatar) VALUES (?, ?, ?, ?, ?, ?)";
    
        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, joueur.getPseudo());
            statement.setString(2, joueur.getMotDePasse());
            statement.setNull(3, Types.INTEGER);
            statement.setNull(4, Types.INTEGER);
            statement.setNull(5, Types.INTEGER);
            statement.setString(6, joueur.getAvatar() != null ? joueur.getAvatar().getNom() : null);
    
            statement.executeUpdate();
    
            ResultSet generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                int joueurId = generatedKeys.getInt(1);
                joueur.setJoueur_id(joueurId);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de l'ajout du joueur", e);
        }
    }
    
    public void mettreAJourJoueur(Joueur joueur) {
        String sql = "UPDATE joueurs SET note_reseaux = ?, note_web = ?, note_java = ?, avatar = ? WHERE id_joueur = ?";
    
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, joueur.getNoteReseaux() != null ? joueur.getNoteReseaux() : 0);
            statement.setInt(2, joueur.getNoteWeb() != null ? joueur.getNoteWeb() : 0);
            statement.setInt(3, joueur.getNoteJava() != null ? joueur.getNoteJava() : 0);
    
            if (joueur.getAvatar() != null) {
                statement.setString(4, joueur.getAvatar().getNom());
            } else {
                statement.setString(4, null); // ou une valeur par défaut appropriée pour l'avatar
            }
    
            statement.setInt(5, joueur.getJoueur_id());
    
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la mise à jour du joueur", e);
        }
    }
 
    public Question recupererQuestionParId(Integer id) {
        String sql = "SELECT * FROM questions WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                String texte = resultSet.getString("texte");
                String reponse = resultSet.getString("reponse");
                String option1 = resultSet.getString("option1");
                String option2 = resultSet.getString("option2");
                String option3 = resultSet.getString("option3");
                NiveauQuestion niveau = NiveauQuestion.valueOf(resultSet.getString("niveau").toUpperCase());
                return new Question(id, texte, option1, option2, option3, reponse, niveau);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération de la question par ID", e);
        }
        return null;
    }

    public Joueur recupererJoueurParNomEtMotDePasse(String nom, String motDePasse) {
        String sql = "SELECT * FROM joueurs WHERE nom = ? AND mot_de_passe = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, nom);
            statement.setString(2, motDePasse);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                int joueurId = resultSet.getInt("id_joueur");
                Integer noteReseaux = (Integer) resultSet.getObject("note_reseaux");
                Integer noteWeb = (Integer) resultSet.getObject("note_web");
                Integer noteJava = (Integer) resultSet.getObject("note_java");
                String avatarNom = resultSet.getString("avatar");
                Avatar avatar = avatarNom != null ? new Avatar(avatarNom, 100) : null;
                Joueur joueur = new Joueur(joueurId, nom, motDePasse);
                joueur.setNoteReseaux(noteReseaux);
                joueur.setNoteWeb(noteWeb);
                joueur.setNoteJava(noteJava);
                joueur.setAvatar(avatar);
                return joueur;
            } else {
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération du joueur", e);
        }
    }

    public List<Question> recupererQuestionsParNiveauEtMatiere(NiveauQuestion niveau, String matiere) {
        List<Question> questions = new ArrayList<>();
        String sql = "SELECT * FROM questions WHERE niveau = ? AND matiere = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, niveau.toString());
            statement.setString(2, matiere);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String texte = resultSet.getString("texte");
                String reponse = resultSet.getString("reponse");
                String option1 = resultSet.getString("option1");
                String option2 = resultSet.getString("option2");
                String option3 = resultSet.getString("option3");
                NiveauQuestion niveauQuestion = NiveauQuestion.valueOf(resultSet.getString("niveau").toUpperCase());
                Question question = new Question(id, texte, option1, option2, option3, reponse, niveauQuestion);
                questions.add(question);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération des questions par niveau et matière", e);
        }

        return questions;
    }

    public void enregistrerReponseCorrecte(Joueur joueur, Question question) {
        // Assurez-vous que joueur est non null et possède un id valide
        if (joueur != null && joueur.getJoueur_id() > 0) {
            // Insérez dans la table joueur_question en utilisant joueur.getJoueur_id() comme joueur_id
            String sql = "INSERT INTO joueur_question (joueur_id, question_id) VALUES (?, ?)";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setInt(1, joueur.getJoueur_id());
                stmt.setInt(2, question.getId());
                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected == 1) {
                    System.out.println("Réponse correctement enregistrée.");
                } else {
                    System.out.println("Erreur lors de l'enregistrement de la réponse.");
                }
            } catch (SQLException e) {
                throw new RuntimeException("Erreur lors de l'enregistrement de la réponse correcte", e);
            }
        } else {
            throw new IllegalArgumentException("Joueur invalide pour enregistrer la réponse.");
        }
    }

    public List<Question> recupererQuestionsCorrectesJoueur(int joueurId) {
        List<Question> questions = new ArrayList<>();
        String sql = "SELECT q.* FROM questions q JOIN joueur_question jq ON q.id = jq.question_id WHERE jq.joueur_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, joueurId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String texte = resultSet.getString("texte");
                String reponse = resultSet.getString("reponse");
                String option1 = resultSet.getString("option1");
                String option2 = resultSet.getString("option2");
                String option3 = resultSet.getString("option3");
                NiveauQuestion niveau = NiveauQuestion.valueOf(resultSet.getString("niveau").toUpperCase());
                questions.add(new Question(id, texte, option1, option2, option3, reponse, niveau));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération des questions correctes du joueur", e);
        }

        return questions;
    }

    public List<Joueur> recupererJoueursParAvatar(String avatarNom) {
        List<Joueur> filteredJoueurs = new ArrayList<>();
        String query = "SELECT * FROM joueurs WHERE avatar = ?";
    
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, avatarNom);
            ResultSet resultSet = statement.executeQuery();
    
            while (resultSet.next()) {
                int id = resultSet.getInt("id_joueur");
                String pseudo = resultSet.getString("nom");
                String motDePasse = resultSet.getString("mot_de_passe");
    
                // Créez un joueur en utilisant le constructeur existant
                Joueur joueur = new Joueur(id, pseudo, motDePasse);
                joueur.setNoteReseaux(resultSet.getInt("note_reseaux"));
                joueur.setNoteWeb(resultSet.getInt("note_web"));
                joueur.setNoteJava(resultSet.getInt("note_java"));
                
                // Vous pouvez également initialiser l'avatar ici si nécessaire
                String avatar = resultSet.getString("avatar");
                if (avatar != null) {
                    joueur.setAvatar(new Avatar(avatar, 100)); // Vérifiez le constructeur d'Avatar
                }
    
                filteredJoueurs.add(joueur);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération des joueurs par avatar", e);
        }
    
        return filteredJoueurs;
    }

    public void ajouterDefi(int idEmetteur, int idRecepteur, List<Question> questions, String matiere, String niveau) throws SQLException {
        PreparedStatement pstmtDefi = null;
        PreparedStatement pstmtDefiQuestions = null;
    
        try {
            // Insertion du défi dans la table defi
            String sqlAjoutDefi = "INSERT INTO defi (id_emetteur, id_recepteur, matiere, niveau) VALUES (?, ?, ?, ?)";
            pstmtDefi = connection.prepareStatement(sqlAjoutDefi, Statement.RETURN_GENERATED_KEYS);
            pstmtDefi.setInt(1, idEmetteur);
            pstmtDefi.setInt(2, idRecepteur);
            pstmtDefi.setString(3, matiere); // Utilisation de la matiere fournie
            pstmtDefi.setString(4, niveau); // Utilisation du niveau fourni
            pstmtDefi.executeUpdate();
    
            // Récupération de l'ID du défi inséré
            int idDefi;
            ResultSet generatedKeys = pstmtDefi.getGeneratedKeys();
            if (generatedKeys.next()) {
                idDefi = generatedKeys.getInt(1);
    
                // Insertion des questions du défi dans la table defi_questions
                String sqlAjoutDefiQuestion = "INSERT INTO defi_questions (id_defi, id_question) VALUES (?, ?)";
                pstmtDefiQuestions = connection.prepareStatement(sqlAjoutDefiQuestion);
                for (Question question : questions) {
                    pstmtDefiQuestions.setInt(1, idDefi);
                    pstmtDefiQuestions.setInt(2, question.getId());
                    pstmtDefiQuestions.executeUpdate();
                }
            } else {
                throw new SQLException("Échec de l'insertion du défi, aucun ID généré.");
            }
        } finally {
            // Fermeture des PreparedStatement
            if (pstmtDefiQuestions != null) {
                pstmtDefiQuestions.close();
            }
            if (pstmtDefi != null) {
                pstmtDefi.close();
            }
        }
    }
    
    public List<Defi> recupererDefisPourJoueur(int idRecepteur) {
        List<Defi> defis = new ArrayList<>();
        String sql = "SELECT d.id_defi, d.id_emetteur, d.id_recepteur, d.matiere, d.niveau, d.date_creation " +
                     "FROM defi d " +
                     "WHERE d.id_recepteur = ? AND d.accepte = 0 AND d.date_creation >= DATE_SUB(NOW(), INTERVAL 2 DAY)";
    
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, idRecepteur);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                int idDefi = resultSet.getInt("id_defi");
                int idEmetteur = resultSet.getInt("id_emetteur");
                String matiere = resultSet.getString("matiere");
                String niveau = resultSet.getString("niveau");
                Timestamp dateCreation = resultSet.getTimestamp("date_creation");
    
                // Récupérer le joueur émetteur
                Joueur emetteur = recupererJoueurParId(idEmetteur);
    
                Defi defi = new Defi(idDefi, emetteur, idRecepteur, matiere, niveau, false, dateCreation);
                defis.add(defi);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération des défis", e);
        }
    
        return defis;
    }
     
    public Joueur recupererJoueurParId(int id) {
        String sql = "SELECT * FROM joueurs WHERE id_joueur = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                int joueurId = resultSet.getInt("id_joueur");
                String pseudo = resultSet.getString("nom");
                String motDePasse = resultSet.getString("mot_de_passe");
                Integer noteReseaux = (Integer) resultSet.getObject("note_reseaux");
                Integer noteWeb = (Integer) resultSet.getObject("note_web");
                Integer noteJava = (Integer) resultSet.getObject("note_java");
                String avatarNom = resultSet.getString("avatar");
                Avatar avatar = avatarNom != null ? new Avatar(avatarNom, 100) : null;
                Joueur joueur = new Joueur(joueurId, pseudo, motDePasse);
                joueur.setNoteReseaux(noteReseaux);
                joueur.setNoteWeb(noteWeb);
                joueur.setNoteJava(noteJava);
                joueur.setAvatar(avatar);
                return joueur;
            } else {
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération du joueur par ID", e);
        }
    }

    public List<Question> recupererQuestionsPourDefirecepteur(int idDefi) {
        List<Question> questions = new ArrayList<>();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
    
        try {
            // Récupérer les IDs des questions associées au défi depuis la table defi_questions
            String sql = "SELECT q.* " +
                         "FROM defi_questions dq " +
                         "JOIN questions q ON dq.id_question = q.id " +
                         "WHERE dq.id_defi = ?";
            pstmt = connection.prepareStatement(sql);
            pstmt.setInt(1, idDefi);
            rs = pstmt.executeQuery();
    
            while (rs.next()) {
                int id = rs.getInt("id");
                String texte = rs.getString("texte");
                String reponse = rs.getString("reponse");
                String option1 = rs.getString("option1");
                String option2 = rs.getString("option2");
                String option3 = rs.getString("option3");
                NiveauQuestion niveauQuestion = NiveauQuestion.valueOf(rs.getString("niveau").toUpperCase());
                Question question = new Question(id, texte, option1, option2, option3, reponse, niveauQuestion);
                questions.add(question);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération des questions pour le défi", e);
        } finally {
            // Fermeture des ressources
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    
        return questions;
    }
    
    public List<Question> recupererQuestionsPourDefi(int idJoueur, String matiere, String niveau) {
        List<Question> questions = new ArrayList<>();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
    
        try {
            // Récupérer les IDs des questions que le joueur possède dans la matière et le niveau choisis
            String sql = "SELECT jq.question_id " +
                         "FROM joueur_question jq " +
                         "INNER JOIN questions q ON jq.question_id = q.id " +
                         "WHERE jq.joueur_id = ?";
    
            // Ajouter les conditions matiere et niveau si elles sont spécifiées
            if (matiere != null && niveau != null) {
                sql += " AND q.matiere = ? AND q.niveau = ?";
            }
    
            pstmt = connection.prepareStatement(sql);
            pstmt.setInt(1, idJoueur);
    
            // Set matiere and niveau if specified
            if (matiere != null && niveau != null) {
                pstmt.setString(2, matiere);
                pstmt.setString(3, niveau);
            }
    
            rs = pstmt.executeQuery();
    
            List<Integer> questionIds = new ArrayList<>();
            while (rs.next()) {
                int questionId = rs.getInt("question_id");
                questionIds.add(questionId);
            }
    
            // Vérifier s'il y a des questions associées au joueur
            if (questionIds.isEmpty()) {
                System.out.println("Aucune question disponible pour ce joueur dans ces critères.");
                return questions;
            }
    
            // Récupérer les détails des questions
            StringBuilder questionsSqlBuilder = new StringBuilder();
            questionsSqlBuilder.append("SELECT * FROM questions WHERE id IN (");
            for (int i = 0; i < questionIds.size(); i++) {
                questionsSqlBuilder.append("?");
                if (i < questionIds.size() - 1) {
                    questionsSqlBuilder.append(",");
                }
            }
            questionsSqlBuilder.append(")");
    
            // Ajouter les conditions matiere et niveau si elles sont spécifiées
            if (matiere != null && niveau != null) {
                questionsSqlBuilder.append(" AND matiere = ? AND niveau = ?");
            }
    
            pstmt = connection.prepareStatement(questionsSqlBuilder.toString());
    
            // Set question_ids
            for (int i = 0; i < questionIds.size(); i++) {
                pstmt.setInt(i + 1, questionIds.get(i));
            }
    
            // Set matiere and niveau if specified
            int paramIndex = questionIds.size() + 1;
            if (matiere != null && niveau != null) {
                pstmt.setString(paramIndex++, matiere);
                pstmt.setString(paramIndex++, niveau);
            }
    
            rs = pstmt.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                String texte = rs.getString("texte");
                String reponse = rs.getString("reponse");
                String option1 = rs.getString("option1");
                String option2 = rs.getString("option2");
                String option3 = rs.getString("option3");
                NiveauQuestion niveauQuestion = NiveauQuestion.valueOf(rs.getString("niveau").toUpperCase());
                Question question = new Question(id, texte, option1, option2, option3, reponse, niveauQuestion);
                questions.add(question);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération des questions pour le défi", e);
        } finally {
            // Fermeture des ressources
            try {
                if (rs != null) {
                    rs.close();
                }
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    
        return questions;
    }
    
    public void ajouterQuestionJoueur(int joueurId, int questionId) {
        PreparedStatement pstmt = null;
        
        try {
            String sql = "INSERT INTO joueur_question (joueur_id, question_id) VALUES (?, ?)";
            pstmt = connection.prepareStatement(sql);
            pstmt.setInt(1, joueurId);
            pstmt.setInt(2, questionId);
            
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de l'ajout de la question au joueur", e);
        } finally {
            // Fermeture des ressources
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
   
    public int recupererIdEmetteurPourDefi(int idDefi) {
    int idEmetteur = 0;
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    try {
        String sql = "SELECT id_emetteur FROM defi WHERE id_defi = ?";
        pstmt = connection.prepareStatement(sql);
        pstmt.setInt(1, idDefi);
        rs = pstmt.executeQuery();
        if (rs.next()) {
            idEmetteur = rs.getInt("id_emetteur");
        }
    } catch (SQLException e) {
        throw new RuntimeException("Erreur lors de la récupération de l'émetteur pour le défi", e);
    } finally {
        // Fermeture des ressources
        try {
            if (rs != null) {
                rs.close();
            }
            if (pstmt != null) {
                pstmt.close();
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Ou autre gestion de l'erreur
        }
    }
    return idEmetteur;
}
    
    public String recupererNiveauPourDefi(int idDefi) {
        String niveau = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            String sql = "SELECT niveau FROM defi WHERE id_defi = ?";
            pstmt = connection.prepareStatement(sql);
            pstmt.setInt(1, idDefi);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                niveau = rs.getString("niveau");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération du niveau pour le défi", e);
        } finally {
            // Fermeture des ressources
            try {
                if (rs != null) {
                    rs.close();
                }
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (SQLException e) {
                e.printStackTrace(); // Ou autre gestion de l'erreur
            }
        }
        return niveau;
    }
    
    public String recupererMatierePourDefi(int idDefi) {
        String matiere = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            String sql = "SELECT matiere FROM defi WHERE id_defi = ?";
            pstmt = connection.prepareStatement(sql);
            pstmt.setInt(1, idDefi);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                matiere = rs.getString("matiere");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération de la matière pour le défi", e);
        } finally {
            // Fermeture des ressources
            try {
                if (rs != null) {
                    rs.close();
                }
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (SQLException e) {
                e.printStackTrace(); // Ou autre gestion de l'erreur
            }
        }
        return matiere;
    }
    
    public void accepterDefi(int idDefi) {
        PreparedStatement pstmt = null;
        try {
            String sql = "UPDATE defi SET accepte = true WHERE id_defi = ?";
            pstmt = connection.prepareStatement(sql);
            pstmt.setInt(1, idDefi);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de l'acceptation du défi", e);
        } finally {
            // Fermeture des ressources
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (SQLException e) {
                e.printStackTrace(); // Ou autre gestion de l'erreur
            }
        }
    }
    
    public void refuserDefi(int idDefi) {
        PreparedStatement pstmt = null;
        try {
            String sql = "UPDATE defi SET accepte = false WHERE id_defi = ?";
            pstmt = connection.prepareStatement(sql);
            pstmt.setInt(1, idDefi);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors du refus du défi", e);
        } finally {
            // Fermeture des ressources
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (SQLException e) {
                e.printStackTrace(); // Ou autre gestion de l'erreur
            }
        }
    }
    
    public void mettreAJourNotesJoueur(int joueurId, int noteReseaux, int noteWeb, int noteJava) throws SQLException {
        String sql = "UPDATE joueurs SET note_reseaux = ?, note_web = ?, note_java = ? WHERE id_joueur = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, noteReseaux);
            statement.setInt(2, noteWeb);
            statement.setInt(3, noteJava);
            statement.setInt(4, joueurId);
            
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Les notes du joueur ont été mises à jour avec succès.");
                
                // Récupérer le joueur mis à jour pour recalculer l'avatar
                Joueur joueurMisAJour = recupererJoueurParId(joueurId);
                joueurMisAJour.determinerAvatar(); // Recalculer l'avatar en fonction des nouvelles notes
                
                // Mettre à jour l'avatar dans la base de données
               mettreAJourAvatarJoueur(joueurId, joueurMisAJour.getAvatar().getNom());
            } else {
                System.out.println("Aucune mise à jour effectuée. Vérifiez l'ID du joueur.");
            }
        }
    }

    public void mettreAJourAvatarJoueur(int joueurId, String nouvelAvatar) throws SQLException {
        String sql = "UPDATE joueurs SET avatar = ? WHERE id_joueur = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, nouvelAvatar);
            statement.setInt(2, joueurId);
            
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("L'avatar du joueur a été mis à jour avec succès.");
            } else {
                System.out.println("Aucune mise à jour effectuée. Vérifiez l'ID du joueur.");
            }
        }
    }
    
}
