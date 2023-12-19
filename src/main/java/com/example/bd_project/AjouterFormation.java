package com.example.bd_project;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.w3c.dom.events.MouseEvent;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

import static javafx.scene.control.Alert.AlertType.ERROR;

public class AjouterFormation implements Initializable {

    @FXML
    private Button AddButton;

    @FXML
    private TextField annee;



    @FXML
    private ComboBox<String> formateur;
    @FXML
    private ComboBox<String> domaine;

    @FXML
    private TextField intitule;

    @FXML
    private TextField mois;

    @FXML
    private TextField nbJours;
    @FXML
    private Label mois_msg;
    @FXML
    private Label annee_msg;
    @FXML
    private Label nbJours_msg;


    @FXML
    private AnchorPane root;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ConnectionDb connect =new ConnectionDb();
        Connection conn=connect.conDB();

        try {

            String QUERY ="Select Nom from formateur";
            PreparedStatement preparedStatement = conn.prepareStatement(QUERY);
            ResultSet queryResult = preparedStatement.executeQuery();

            while(queryResult.next()){
                String nom= queryResult.getString("Nom");
                formateur.getItems().add(nom);
            }
            String domaine_QUERY ="Select libelle from domaine";
            PreparedStatement Statement = conn.prepareStatement(domaine_QUERY);
            ResultSet rs = Statement.executeQuery();

            while(rs.next()){
                String libelle= rs.getString("libelle");
                domaine.getItems().add(libelle);
            }

        }catch(Exception e){
            e.printStackTrace();
            e.getCause();
        }

    }
    //vérification des champs
    public boolean isEmpty()
    {
        boolean empty =false;
        if ((intitule.getText().isBlank()) || (nbJours.getText().isBlank()) || (mois.getText().isBlank())
                || (annee.getText().isBlank()) || (domaine.getSelectionModel().isEmpty()) ||(domaine.getSelectionModel().isEmpty()))
        {
            empty=true;
        }
        return empty;
    }

    //controle de saisie sur mois
    public void Mois_Control(javafx.scene.input.KeyEvent Event) {
        String str=mois.getText();
        boolean isNumeric = str.chars().allMatch( Character::isDigit );
        mois_msg.setText("");

        if(isNumeric )
        { if (Integer.parseInt(str)>0 && Integer.parseInt(str)<13)
        {

            mois_msg.setText("");
        }
        else {
            mois_msg.setText("mois entre 1 et 12");
        }
    }
    }

    //controle de saisie sur année
    public void Annee_Control(javafx.scene.input.KeyEvent Event) {
        String str=annee.getText();
        boolean isNumeric = str.chars().allMatch( Character::isDigit );
        annee_msg.setText("");
        if(!isNumeric || str.length()!=4)
        {
            annee_msg.setText("Année incorrecte");

        }
        else {

            annee_msg.setText("");
        }

    }
    //controle de saisie sur nbjours
    public void Nbjours_Control(javafx.scene.input.KeyEvent Event) {
        String str=nbJours.getText();
        boolean isNumeric = str.chars().allMatch( Character::isDigit );
        nbJours_msg.setText("");
        if(!isNumeric)
        {
            nbJours_msg.setText("numeros incorrect");
            //Alert alert=new Alert(ERROR,"numeros incorrect");
            //alert.show();
        }
        else {
            System.out.println("numeros correct");
            nbJours_msg.setText("");
        }

    }


//insertion dans la base
    public void AddButtonOnAction(ActionEvent event) throws IOException {
        if (!isEmpty()) {
            //connection db
            ConnectionDb connect = new ConnectionDb();
            Connection conn = connect.conDB();
            try {
                //select code formateur du valeur choisi
                String formateur_value = "select Code_formateur FROM formateur where Nom = ? ";
                PreparedStatement Select_formateur = conn.prepareStatement(formateur_value);
                Select_formateur.setString(1, formateur.getValue());
                ResultSet id_formateur = Select_formateur.executeQuery();
                //select code domaine du valeur choisi
                String domaine_value = "select Code_domaine FROM domaine where libelle = ? ";
                PreparedStatement Select_domaine = conn.prepareStatement(domaine_value);
                Select_domaine.setString(1, domaine.getValue());
                ResultSet id_domaine = Select_domaine.executeQuery();
                //Insertion des valeur dans la base
                String INSERT_QUERY = "INSERT INTO Formation (Intitule, Nombre_jours,Annee,Mois,Formateur,Domaine) VALUES (?, ?, ?, ?,?,?)";
                PreparedStatement preparedStatement = conn.prepareStatement(INSERT_QUERY);
                preparedStatement.setString(1, intitule.getText());
                preparedStatement.setString(2, nbJours.getText());
                preparedStatement.setString(3, annee.getText());
                preparedStatement.setString(4, mois.getText());
                if (id_formateur.next()) {
                    preparedStatement.setString(5, id_formateur.getString("code_formateur"));
                }
                if (id_domaine.next()) {
                    preparedStatement.setString(6, id_domaine.getString("code_domaine"));
                }
                preparedStatement.execute();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            //load page gestion de formation
            FXMLLoader fxmlLoader = new FXMLLoader(AjouterFormation.class.getResource("Admin_Dash_formation.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 1000, 600);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            stage.setScene(scene);
            stage.show();

        } else {
            Alert alert=new Alert(ERROR,"Remplir tous les champs");
            alert.show();

        }

    }
}
