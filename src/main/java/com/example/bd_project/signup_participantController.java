package com.example.bd_project;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

import static javafx.scene.control.Alert.AlertType.ERROR;

public class signup_participantController implements Initializable {





    @FXML
    private TextField nom;

    @FXML
    private TextField prenom;

    @FXML
    private PasswordField password;

    @FXML
    private TextField login;

    @FXML
    private DatePicker DateN;

    @FXML
    private ComboBox<String> profil;

    @FXML
    private AnchorPane root;
    @FXML
    private  Label error_msg;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
       //connection a la base
        ConnectionDb connect =new ConnectionDb();
        Connection conn=connect.conDB();

        try {

            String QUERY ="Select Libelle from Profil";
            PreparedStatement preparedStatement = conn.prepareStatement(QUERY);
            ResultSet queryResult = preparedStatement.executeQuery();

            while(queryResult.next()){
                String libelle= queryResult.getString("Libelle");
                profil.getItems().add(libelle);
            }

        }catch(Exception e){
            e.printStackTrace();
            e.getCause();
        }
    }

    public boolean isEmpty()
    { //vérification des champs
        boolean empty =false;
        if ((login.getText().isBlank()) || (password.getText().isBlank()) || (nom.getText().isBlank())
                || (prenom.getText().isBlank()) || (profil.getSelectionModel().isEmpty()) || (DateN.getValue()==null))
        {
            empty=true;
        }
        return empty;
    }
    public boolean ExistLogin()
    {
       //vérification existance de l'utlisateur
        ConnectionDb connect = new ConnectionDb();
        Connection conn = connect.conDB();
        boolean existe=false;
        try {
        String username = "select * FROM utilisateur where Login = ?";
        PreparedStatement Select_username = conn.prepareStatement(username);
        Select_username.setString(1, login.getText());
        ResultSet rs = Select_username.executeQuery();
        if(rs.next()) {
            error_msg.setText("Nom d'utilisateur existe déja");
            existe=true;

        }

    }  catch (Exception e) {
        e.printStackTrace();
        e.getCause();
    }
return  existe;
        }


        public void ValidateSignup(ActionEvent event) throws SQLException {
        if (!isEmpty()) {
            ConnectionDb connect = new ConnectionDb();
            Connection conn = connect.conDB();
            try {


                if(!ExistLogin()) {
                    //Insertion des données dans la table utilisateur
                    String INSERT_QUERY = "INSERT INTO utilisateur (Login, Password, Role) VALUES (?, ?, ?)";
                    PreparedStatement preparedStatement = conn.prepareStatement(INSERT_QUERY);
                    preparedStatement.setString(1, login.getText());
                    preparedStatement.setString(2, password.getText());
                    preparedStatement.setString(3, "participant");
                    preparedStatement.execute();
                    //code utilisateur pour le numéro de compte
                    String compte = "select Code_utilisateur FROM utilisateur where Login = ?";
                    PreparedStatement Select_compte = conn.prepareStatement(compte);
                    Select_compte.setString(1, login.getText());
                    ResultSet id_compte = Select_compte.executeQuery();
                    String profil_valeur = "select Code_profil FROM Profil where Libelle = ? ";
                    PreparedStatement Select_profil = conn.prepareStatement(profil_valeur);
                    Select_profil.setString(1, profil.getValue());
                    ResultSet id_profil = Select_profil.executeQuery();
                    //Insertion dans la table participant

                    String QUERY = "INSERT INTO participant(Nom,Prenom,Date_naissance,profil,compte) VALUES(?, ?, ?, ?, ?)";

                    PreparedStatement Statement = conn.prepareStatement(QUERY);

                    Statement.setString(1, nom.getText());
                    Statement.setString(2, prenom.getText());
                    Statement.setString(3, DateN.getValue().toString());
                    id_compte.next();
                    Statement.setString(5, id_compte.getString("Code_utilisateur"));
                    id_profil.next();
                    Statement.setString(4, id_profil.getString("Code_profil"));

                    Statement.execute();

                    //load page login
                    AnchorPane pane = FXMLLoader.load(getClass().getResource("login.fxml"));
                    root.getChildren().setAll(pane);

                }
            } catch (Exception e) {
                e.printStackTrace();
                e.getCause();
            }
        }else {
                Alert alert=new Alert(ERROR,"Remplir tous les champs");
                alert.show();}

    }
    //Redirection vers signup formateur
    public  void signUp_ButtonFormateur(ActionEvent event) throws IOException {


        AnchorPane pane = FXMLLoader.load(getClass().getResource("signup_formateur.fxml"));
        root.getChildren().setAll(pane);

    }
    //déconnexion
    public void logout(ActionEvent event){
        Node node = (Node) event.getSource();
        Stage stage = (Stage) node.getScene().getWindow();
        stage.close();
        try {
            // Step 4
            Parent root = FXMLLoader.load(ProfilController.class.getResource("Login.fxml"));

            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.err.println(String.format("Error: %s", e.getMessage()));
        }

    }




}


