package com.example.bd_project;

import Models.Formation;
import Models.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ResourceBundle;

public class FormateurDashController implements Initializable {
    @FXML
    private TableColumn<Formation, Integer> col_anne;

    @FXML
    private TableColumn<Formation, String> col_domaine;

    @FXML
    private TableColumn<Formation, String> col_intitule;
    @FXML
    private TableColumn<Formation, String> col_formateur;
    @FXML
    private TableColumn<Formation, Integer> col_jours;

    @FXML
    private TableColumn<Formation, Integer> col_mois;

    @FXML
    private TableColumn<Formation, Integer> col_nb_Participants;

    @FXML
    private TableView<Formation> table;
    @FXML
    private AnchorPane root;
    ObservableList<Formation> oblist = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        String domaine="";
        //connection à la base
        ConnectionDb connect = new ConnectionDb();
        Connection conn = connect.conDB();
        //afficher les formation ou l'utilisateur est un formateur
        String query = "SELECT * FROM `formation` " +
                "WHERE code_formation In(SELECT formation from sessionformation" +
                "                        where formateur In (Select code_formateur from formateur where compte=?))";

        try {
            PreparedStatement preparedStatement = conn.prepareStatement(query);
            preparedStatement.setString(1, User.id.toString());
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {


                //selectionner la libelle pour afficher le domaine
                String DomaineQUERY ="select libelle from domaine where code_domaine =? ";
                PreparedStatement Statement=conn.prepareStatement(DomaineQUERY);
                Statement.setString(1,rs.getString("Domaine"));
                ResultSet resultSet= Statement.executeQuery();

                //selectionner la nom pour afficher le formateur
                String FormateurQUERY ="select nom from formateur where code_formateur =? ";
                PreparedStatement Formateur_Statement=conn.prepareStatement(FormateurQUERY);
                Formateur_Statement.setString(1,rs.getString("formateur"));
                ResultSet result= Formateur_Statement.executeQuery();

                while(resultSet.next() && result.next()){
                    domaine=resultSet.getString("libelle");
                    String formateur=result.getString("nom");
                    //ajouter les valeur à la liste
                    oblist.add(new Formation(rs.getInt("code_formation"),rs.getString("Intitule"), rs.getInt("Nombre_jours"),
                            rs.getInt("Annee"), rs.getInt("Mois"), rs.getInt("Nombre_participants"),
                            domaine,formateur));
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
            e.getCause();
        }

        //remplir le tableview
        col_intitule.setCellValueFactory(new PropertyValueFactory<>("Intitule"));
        col_jours.setCellValueFactory(new PropertyValueFactory<>("Nombre_jours"));
        col_anne.setCellValueFactory(new PropertyValueFactory<>("Annee"));
        col_mois.setCellValueFactory(new PropertyValueFactory<>("Mois"));
        col_nb_Participants.setCellValueFactory(new PropertyValueFactory<>("Nombre_participants"));
        col_domaine.setCellValueFactory(new PropertyValueFactory<>("Domaine"));
        col_formateur.setCellValueFactory(new PropertyValueFactory<>("formateur"));
        table.setItems(oblist);


        //pour selectionner une seule ligne
        table.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

    }

    //redirection au profil
    public void profilButton(ActionEvent event) throws IOException {
        AnchorPane pane = FXMLLoader.load(getClass().getResource("ProfilFormateur.fxml"));
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
