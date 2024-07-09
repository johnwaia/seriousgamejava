public class Question {
    private int id;
    private String texte;
    private String option1;
    private String option2;
    private String option3;
    private String reponse;
    private NiveauQuestion niveau;

    public Question(int id, String texte, String option1, String option2, String option3, String reponse, NiveauQuestion niveau) {
        this.id = id;
        this.texte = texte;
        this.option1 = option1;
        this.option2 = option2;
        this.option3 = option3;
        this.reponse = reponse;
        this.niveau = niveau;
    }

    public int getId() {
        return id;
    }

    public String getTexte() {
        return texte;
    }

    public String getOption1() {
        return option1;
    }

    public String getOption2() {
        return option2;
    }

    public String getOption3() {
        return option3;
    }

    public String getReponse() {
        return reponse;
    }

    public NiveauQuestion getNiveau() {
        return niveau;
    }
    
    public boolean verifierReponse(String reponseUtilisateur) {
        System.out.println("Réponse correcte attendue : " + this.reponse);
        System.out.println("Réponse donnée par l'utilisateur : " + reponseUtilisateur.trim());
        String reponseAttendue = this.reponse.trim();
        String premiereLettreAttendue = reponseAttendue.substring(0, 1).toUpperCase();
        String premiereLettreUtilisateur = reponseUtilisateur.trim().toUpperCase().substring(0, 1);
        return premiereLettreUtilisateur.equals(premiereLettreAttendue);
    }
}
