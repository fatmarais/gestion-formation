package com.example.bd_project;

import Models.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
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

public class ProfilFormateurController implements Initializable {

    @FXML
    private Button delete;

    @FXML
    private ComboBox<String> domaine;

    @FXML
    private TextField login;

    @FXML
    private Button logout;

    @FXML
    private TextField mail;

    @FXML
    private TextField nom;

    @FXML
    private TextField num_tel;

    @FXML
    private PasswordField password;

    @FXML
    private TextField prenom;
    @FXML
    AnchorPane root;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ConnectionDb connect = new ConnectionDb();
        Connection conn = connect.conDB();

        try {

            //affichage des domaines
            String QUERY = "Select Libelle from domaine";
            PreparedStatement preparedStatement = conn.prepareStatement(QUERY);
            ResultSet queryResult = preparedStatement.executeQuery();

            while (queryResult.next()) {
                String libelle = queryResult.getString("Libelle");
                domaine.getItems().add(libelle);
            }

        } catch (Exception e) {
            e.printStackTrace();
            e.getCause();
        }
        login.setText(User.login);
        password.setText(User.password);
        //afficher les données de l'utilisateur
        String QUERY = "select nom, prenom,Domaine,Email,Num_telephone from formateur where compte =?";
        try {
            PreparedStatement Statement = conn.prepareStatement(QUERY);
            Statement.setString(1, User.id.toString());
            ResultSet rs = Statement.executeQuery();
            if (rs.next()) {
                nom.setText(rs.getString("nom"));
                prenom.setText(rs.getString("prenom"));
                mail.setText(rs.getString("Email"));
                num_tel.setText(rs.getString("Num_telephone"));
                //pour affichage domaine
                String Selectdomaine = "Select Libelle from domaine where code_domaine= ?";
                PreparedStatement domaine_statement = conn.prepareStatement(Selectdomaine);
                domaine_statement.setString(1, rs.getString("domaine"));
                ResultSet Result = domaine_statement.executeQuery();

                if (Result.next()) {
                    String libelle = Result.getString("Libelle");
                    domaine.setValue(libelle);
                }

            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public void modifierButton(ActionEvent event) {
        ConnectionDb connect = new ConnectionDb();
        Connection conn = connect.conDB();
        try {
            //modifier les données
            if (!nom.getText().isBlank()) {
                String Nomquery = "update formateur set nom=? where compte =? ";
                PreparedStatement nomStatement = conn.prepareStatement(Nomquery);
                nomStatement.setString(1, nom.getText());
                nomStatement.setString(2, User.id.toString());
                nomStatement.execute();
            }
            if (!prenom.getText().isBlank()) {
                String prenomquery = "update formateur set prenom=? where compte =? ";
                PreparedStatement prenom_Statement = conn.prepareStatement(prenomquery);
                prenom_Statement.setString(1, prenom.getText());
                prenom_Statement.setString(2, User.id.toString());
                prenom_Statement.execute();
            }
             if (!mail.getText().isBlank()) {
               String prenomquery = "update formateur set Email=? where compte =? ";
               PreparedStatement prenom_Statement = conn.prepareStatement(prenomquery);
               prenom_Statement.setString(1, mail.getText());
               prenom_Statement.setString(2, User.id.toString());
               prenom_Statement.execute();
                        }
             //selection du code domaine
            String profil_querry = "select code_domaine from domaine where  libelle =?";
            PreparedStatement profil_statement = conn.prepareStatement(profil_querry);
            profil_statement.setString(1, domaine.getValue());
            ResultSet rs = profil_statement.executeQuery();
            if (rs.next()) {
                String query = "update formateur set domaine=? where compte =? ";
                PreparedStatement Statement = conn.prepareStatement(query);
                Statement.setString(1, rs.getString("code_domaine"));
                Statement.setString(2, User.id.toString());
                Statement.execute();
            }
            //vérification du nom d'utilisateur
            String query = "SELECT * FROM utilisateur WHERE Login = ? ";
            PreparedStatement login_Statement = conn.prepareStatement(query);
            login_Statement.setString(1, login.getText());
            ResultSet Res = login_Statement.executeQuery();

            if (Res.next()) {
                Alert alert = new Alert(ERROR, "Nom d'utilisateur existe déja");
                alert.show();
            } else {
                //update login
                String query1 = "update utilisateur set login =? where code_utilisateur=?";
                PreparedStatement statement1 = conn.prepareStatement(query1);
                statement1.setString(1, login.getText());
                statement1.setString(2, User.id.toString());
                statement1.execute();

            }
            //update password
            String query2 = "update utilisateur set  password= ? where code_utilisateur=?";
            PreparedStatement statement2 = conn.prepareStatement(query2);
            statement2.setString(1, password.getText());
            statement2.setString(2, User.id.toString());
            statement2.execute();



        } catch (Exception e) {
            e.printStackTrace();
            e.getCause();
        }
    }

    //supprimer compte
    public void detete(ActionEvent event) {
        ConnectionDb connect = new ConnectionDb();
        Connection conn = connect.conDB();

        try {
            //supprimer de la table formateur
            String query="delete from formateur where compte=?";
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1,User.id.toString());
            statement.execute();
            //supprimer le compte de l'utilisateur
            String querry2="delete from utilisateur where code_utilisateur=?";
            PreparedStatement statement2 = conn.prepareStatement(querry2);
            statement2.setString(1,User.id.toString());
            statement2.execute();

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

        } catch (Exception e) {
            e.printStackTrace();
            e.getCause();
        }
}
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
    public void ListButton(ActionEvent event) throws IOException {
        AnchorPane pane = FXMLLoader.load(getClass().getResource("Formateur_Dash.fxml"));
        root.getChildren().setAll(pane);
    }


}
