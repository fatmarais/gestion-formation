package com.example.bd_project;

import Models.Participant;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;
import java.util.ResourceBundle;


public class AdminDashController implements Initializable {

    @FXML
    private TableView<Participant> table;
    @FXML
    private TableColumn<Participant, String> col_dateN;

    @FXML
    private TableColumn<Participant, String> col_nom;

    @FXML
    private TableColumn<Participant, String> col_prenom;

    @FXML
    private AnchorPane root;




   //liste de model participant
    ObservableList<Participant> oblist = FXCollections.observableArrayList();


    @Override
    //afficher la liste des participants dans tableView
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //connection db
        ConnectionDb connect = new ConnectionDb();
        Connection conn = connect.conDB();
        //selectionner nom prénom et Dn à partir de la table participant
        String query = "SELECT Nom, Prenom, Date_naissance from Participant";

        try {
            PreparedStatement preparedStatement = conn.prepareStatement(query);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {

                Date date = rs.getDate("Date_naissance");
                //remplir la liste avec les données de participants
                oblist.add(new Participant(rs.getString("Nom"), rs.getString("Prenom"), date.toString()));
            }

        } catch (Exception e) {
            e.printStackTrace();
            e.getCause();
        }

       //remplir les colones de tableview par les données de la base
        col_nom.setCellValueFactory(new PropertyValueFactory<>("Nom"));
        col_prenom.setCellValueFactory(new PropertyValueFactory<>("Prenom"));
        col_dateN.setCellValueFactory(new PropertyValueFactory<>("DateN"));
        table.setItems(oblist);

        //pour selectionner une seule ligne
        table.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

    }

    //Supprimer un participant
    public void deleteButtonOnAction(ActionEvent actionEvent) {

       //liste des participant dans tableview
        ObservableList<Participant> listParticipant;
        listParticipant = table.getItems();
        //get participant selectionné
        Participant selectedParticipant = table.getSelectionModel().getSelectedItem();
        String cellValue = selectedParticipant.getNom();

            listParticipant.remove(selectedParticipant);
            ConnectionDb connect = new ConnectionDb();
            Connection conn = connect.conDB();
            try {
                //select matricule participant pour supprimer compte
                String SelectQUERY= "Select Matricule_participant from participant where nom=?";
                PreparedStatement id_Statement = conn.prepareStatement(SelectQUERY);
                id_Statement.setString(1,cellValue);
                ResultSet queryResult = id_Statement.executeQuery();
                if (queryResult.next()) {
                    String matricule = queryResult.getString("Matricule_participant");

                    //selection du nunéro compte du participant
                    String SelectCompte= "Select compte from participant where nom=?";
                    PreparedStatement compte_Statement = conn.prepareStatement(SelectCompte);
                    compte_Statement.setString(1,cellValue);
                    ResultSet rs = compte_Statement.executeQuery();
                    if(rs.next()){
                    String compte=rs.getString("compte");
                    //supprimer de la table participant
                    String QUERY="DELETE FROM participant WHERE Matricule_participant=?";

                    PreparedStatement preparedStatement = conn.prepareStatement(QUERY);
                    preparedStatement.setString(1, matricule);
                    preparedStatement.execute();
                    //supprimer le compte du participant de la table utilisateur
                    String delete_QUERY="DELETE FROM utilisateur WHERE Code_utilisateur=?";

                    PreparedStatement Statement = conn.prepareStatement(delete_QUERY);
                    Statement.setString(1, compte);
                    Statement.execute();
                }}


            } catch (Exception e) {
                e.printStackTrace();
                e.getCause();
            }

    }
    public void FormateurButton(ActionEvent event) throws IOException {
        //redirection vers l'interface de gestion de formateur
        AnchorPane pane = FXMLLoader.load(getClass().getResource("Admin_dash_formateur.fxml"));
        root.getChildren().setAll(pane);
    }
    public void FormationButton(ActionEvent event) throws IOException {
        //redirection vers l'interface de gestion de formateur
        AnchorPane pane = FXMLLoader.load(getClass().getResource("Admin_Dash_formation.fxml"));
        root.getChildren().setAll(pane);
    }
    //déconnexion
    public void logout(ActionEvent event){
        //redirection vers la page de connexion
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