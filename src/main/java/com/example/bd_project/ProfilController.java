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

public class ProfilController  implements Initializable {

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
    AnchorPane root;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ConnectionDb connect = new ConnectionDb();
        Connection conn = connect.conDB();

        try {
            //afficher les valeurs des profils
            String QUERY = "Select Libelle from Profil";
            PreparedStatement preparedStatement = conn.prepareStatement(QUERY);
            ResultSet queryResult = preparedStatement.executeQuery();

            while (queryResult.next()) {
                String libelle = queryResult.getString("Libelle");
                profil.getItems().add(libelle);
            }

        } catch (Exception e) {
            e.printStackTrace();
            e.getCause();
        }
        //afficher les données de l'utilisateur
        login.setText(User.login);
        password.setText(User.password);
        String QUERY = "select nom, prenom,Date_naissance,profil from participant where compte =?";
        try {
            PreparedStatement Statement = conn.prepareStatement(QUERY);
            Statement.setString(1, User.id.toString());
            ResultSet rs = Statement.executeQuery();
            if (rs.next()) {
                nom.setText(rs.getString("nom"));
                prenom.setText(rs.getString("prenom"));
                DateN.setValue(rs.getDate("Date_naissance").toLocalDate());
                //pour afficher profil
                String SelectProfil = "Select Libelle from Profil where code_profil= ?";
                PreparedStatement Profil_statement = conn.prepareStatement(SelectProfil);
                Profil_statement.setString(1, rs.getString("profil"));
                ResultSet Result = Profil_statement.executeQuery();

                if (Result.next()) {
                    String libelle = Result.getString("Libelle");
                    profil.setValue(libelle);
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
                String Nomquery = "update participant set nom=? where compte =? ";
                PreparedStatement nomStatement = conn.prepareStatement(Nomquery);
                nomStatement.setString(1, nom.getText());
                nomStatement.setString(2, User.id.toString());
                nomStatement.execute();
            }
            if (!prenom.getText().isBlank()) {
                String prenomquery = "update participant set prenom=? where compte =? ";
                PreparedStatement prenom_Statement = conn.prepareStatement(prenomquery);
                prenom_Statement.setString(1, prenom.getText());
                prenom_Statement.setString(2, User.id.toString());
                prenom_Statement.execute();
            }
            if ((DateN.getValue() != null)) {
                String DNquery = "update participant set Date_naissance=? where compte =? ";
                PreparedStatement DN_Statement = conn.prepareStatement(DNquery);
                DN_Statement.setString(1, DateN.getValue().toString());
                DN_Statement.setString(2, User.id.toString());
                DN_Statement.execute();
            }
            String profil_querry = "select code_profil from profil where  libelle =?";
            PreparedStatement profil_statement = conn.prepareStatement(profil_querry);
            profil_statement.setString(1, profil.getValue());
            ResultSet rs = profil_statement.executeQuery();
            if (rs.next()) {
                String query = "update participant set profil=? where compte =? ";
                PreparedStatement Statement = conn.prepareStatement(query);
                Statement.setString(1, rs.getString("code_profil"));
                Statement.setString(2, User.id.toString());
                Statement.execute();
            }
            //vérifier si le login existe
            String query = "SELECT * FROM utilisateur WHERE Login = ? ";
            PreparedStatement login_Statement = conn.prepareStatement(query);
            login_Statement.setString(1, login.getText());
            ResultSet Res = login_Statement.executeQuery();

            if (Res.next()) {
                Alert alert = new Alert(ERROR, "Nom d'utilisateur existe déja");
                alert.show();
            } else {
                String query1 = "update utilisateur set login =? where code_utilisateur=?";
                PreparedStatement statement1 = conn.prepareStatement(query1);
                statement1.setString(1, login.getText());
                statement1.setString(2, User.id.toString());
                statement1.execute();

            }
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

    public void detete(ActionEvent event) {
        ConnectionDb connect = new ConnectionDb();
        Connection conn = connect.conDB();

        try {
            //supprimer de la table participant
            String query="delete from participant where compte=?";
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1,User.id.toString());
            statement.execute();
            //supprimer le compte d'utilisateur
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
        AnchorPane pane = FXMLLoader.load(getClass().getResource("Participant_Dash.fxml"));
        root.getChildren().setAll(pane);
    }
    public void MesFormationsButton(ActionEvent event) throws IOException {
        AnchorPane pane = FXMLLoader.load(getClass().getResource("MesFormation.fxml"));
        root.getChildren().setAll(pane);

    }
}