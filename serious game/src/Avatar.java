public class Avatar {
    private String nom;
    private int pointsDeVie;

    public Avatar(String nom, int pointsDeVie) {
        this.nom = nom;
        this.pointsDeVie = pointsDeVie;
    }

    public String getNom() {
        return nom;
    }

    public int getPointsDeVie() {
        return pointsDeVie;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public void setPointsDeVie(int pointsDeVie) {
        this.pointsDeVie = pointsDeVie;
    }

    // Méthode pour obtenir le niveau de question associé à l'avatar
    public NiveauQuestion getNiveauQuestion() {
        return switch (nom.toLowerCase()) {
            case "alchimiste" -> NiveauQuestion.FACILE;
            case "ingenieur" -> NiveauQuestion.MOYEN;
            case "celeste" -> NiveauQuestion.DIFFICILE;
            default -> NiveauQuestion.FACILE;
        }; // Par défaut
    }

    @Override
    public String toString() {
        return nom; // Retourne le nom de l'avatar lors de l'affichage
    }
}
