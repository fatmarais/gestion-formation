package Models;

public class Participant {
    private String  Nom;
    private String Prenom;
    private String DateN;

   public Participant(String Nom, String Prenom, String DateN)
    {

        this.Nom=Nom;
        this.Prenom=Prenom;
        this.DateN=DateN;



    }

    public String getNom()
    {
        return  Nom;
    }

    public void setNom(String Nom)
    {
       this.Nom=Nom;
    }

    public String getPrenom()
    {
        return Prenom;
    }



    public void setPrenom(String prenom) {
        this.Prenom = prenom;
    }

    public String getDateN()
    {
        return DateN;
    }

    public void setDateN(String dateN) {
        this.DateN = dateN;
    }


}
