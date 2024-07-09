import java.util.ArrayList;
import java.util.List;

public class Joueur {
    private int joueur_id;
    private String pseudo;
    private String motDePasse;
    private Integer noteReseaux;
    private Integer noteWeb;
    private Integer noteJava;
    private Avatar avatar;
    private List<Integer> questionsCorrectes;
    private List<Question> questions;

    public Joueur(int joueur_id, String pseudo, String motDePasse) {
        this.joueur_id = joueur_id;
        this.pseudo = pseudo;
        this.motDePasse = motDePasse;
        this.noteReseaux = null;
        this.noteWeb = null;
        this.noteJava = null;
        this.avatar = null;
        this.questionsCorrectes = new ArrayList<>();
        this.questions = new ArrayList<>();
    }

    public int getJoueur_id() {
        return joueur_id;
    }

    public void setJoueur_id(int joueur_id) {
        this.joueur_id = joueur_id;
    }

    public String getPseudo() {
        return pseudo;
    }

    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }

    public String getMotDePasse() {
        return motDePasse;
    }

    public void setMotDePasse(String motDePasse) {
        this.motDePasse = motDePasse;
    }

    public Integer getNoteReseaux() {
        return noteReseaux;
    }

    public void setNoteReseaux(Integer noteReseaux) {
        this.noteReseaux = noteReseaux;
    }

    public Integer getNoteWeb() {
        return noteWeb;
    }

    public void setNoteWeb(Integer noteWeb) {
        this.noteWeb = noteWeb;
    }

    public Integer getNoteJava() {
        return noteJava;
    }

    public void setNoteJava(Integer noteJava) {
        this.noteJava = noteJava;
    }

    public Avatar getAvatar() {
        return avatar;
    }

    public void setAvatar(Avatar avatar) {
        this.avatar = avatar;
    }

    public List<Integer> getQuestionsCorrectes() {
        return questionsCorrectes;
    }

    public void setQuestionsCorrectes(List<Integer> questionsCorrectes) {
        this.questionsCorrectes = questionsCorrectes;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    public void ajouterQuestionCorrecte(int questionId) {
        if (!questionsCorrectes.contains(questionId)) {
            questionsCorrectes.add(questionId);
        }
    }

    public NiveauQuestion determinerNiveau() {
        int moyenne = (noteReseaux + noteWeb + noteJava) / 3;
        if (moyenne < 5) {
            return NiveauQuestion.FACILE;
        } else if (moyenne < 10) {
            return NiveauQuestion.MOYEN;
        } else {
            return NiveauQuestion.DIFFICILE;
        }
    }

    
    public void determinerAvatar() {
        int moyenne = (noteReseaux + noteWeb + noteJava) / 3;

        if (moyenne < 5) {
            this.avatar = new Avatar("Alchimiste", 100);
        } else if (moyenne < 10) {
            this.avatar = new Avatar("Ingénieur", 100);
        } else {
            this.avatar = new Avatar("Céleste", 100);
        }
    }
}
