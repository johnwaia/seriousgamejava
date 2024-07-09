

public class Defi {
    private int idDefi;
    private Joueur emetteur;
    private int idRecepteur;
    private String matiere;
    private String niveau;
    private boolean accepte;
    private java.sql.Timestamp dateCreation;
  

    public Defi(int idDefi, Joueur emetteur, int idRecepteur, String matiere, String niveau, boolean accepte, java.sql.Timestamp dateCreation2) {
        this.idDefi = idDefi;
        this.emetteur = emetteur;
        this.idRecepteur = idRecepteur;
        this.matiere = matiere;
        this.niveau = niveau;
        this.accepte = accepte;
        this.dateCreation = dateCreation2;
    }

    // Getters et setters
    public int getIdDefi() {
        return idDefi;
    }

    public Joueur getEmetteur() {
        return emetteur;
    }

    public int getIdRecepteur() {
        return idRecepteur;
    }

    public String getMatiere() {
        return matiere;
    }

    public String getNiveau() {
        return niveau;
    }

    public boolean isAccepte() {
        return accepte;
    }

    public java.sql.Timestamp getDateCreation() {
        return dateCreation;
    }

    public void setAccepte(boolean accepte) {
        this.accepte = accepte;
    }
}
