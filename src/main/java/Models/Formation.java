package Models;

public class Formation {
    private Integer Code_formation;
    private String Intitule;
    private Integer Nombre_jours;
    private Integer Annee;
    private Integer Mois;
    private Integer Nombre_participants;
    private String 	Domaine;
    private String 	formateur;



    public Formation(Integer code_formation, String intitule, Integer nombre_jours, Integer annee, Integer mois, Integer nombre_participants, String domaine, String formateur) {
        Code_formation = code_formation;
        Intitule = intitule;
        Nombre_jours = nombre_jours;
        Annee = annee;
        Mois = mois;
        Nombre_participants = nombre_participants;
        Domaine = domaine;
        this.formateur = formateur;
    }


    public Integer getCode_formation() {
        return Code_formation;
    }

    public void setCode_formation(Integer code_formation) {
        Code_formation = code_formation;
    }

    public String getIntitule() {
        return Intitule;
    }

    public void setIntitule(String intitule) {
        Intitule = intitule;
    }

    public Integer getNombre_jours() {
        return Nombre_jours;
    }

    public void setNombre_jours(Integer nombre_jours) {
        Nombre_jours = nombre_jours;
    }

    public Integer getAnnee() {
        return Annee;
    }

    public void setAnnee(Integer annee) {
        Annee = annee;
    }

    public Integer getMois() {
        return Mois;
    }

    public void setMois(Integer mois) {
        Mois = mois;
    }

    public Integer getNombre_participants() {
        return Nombre_participants;
    }

    public void setNombre_participants(Integer nombre_participants) {
        Nombre_participants = nombre_participants;
    }

    public String getDomaine() {
        return Domaine;
    }

    public void setDomaine(String domaine) {
        Domaine = domaine;
    }

    public String getFormateur() {
        return formateur;
    }

    public void setFormateur(String formateur) {
        this.formateur = formateur;
    }
}
