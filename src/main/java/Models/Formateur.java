package Models;

public class Formateur {

    private String nom;

    private String prenom;

    private String domaine;

    private String email;

    private String tel;


    public Formateur(String nom, String prenom, String domaine, String email, String tel) {
        this.nom = nom;
        this.prenom = prenom;
        this.domaine = domaine;
        this.email = email;
        this.tel = tel;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getDomaine() {
        return domaine;
    }

    public void setDomaine(String domaine) {
        this.domaine = domaine;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }
}
