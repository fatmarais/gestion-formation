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
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.converter.IntegerStringConverter;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import Models.Formation;

public class ParticipantDashController implements Initializable {

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
    private TableColumn<Formation, Integer> id;

    @FXML
    private TableColumn<Formation, String> editcol;

    @FXML
    private TableView<Formation> table;

    @FXML
    private Button inscri;
    @FXML
    private AnchorPane root;

    @FXML
    private Button listFormations;

    @FXML
    private Button profil;
    ObservableList<Formation> oblist = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        String domaine = "";
        ConnectionDb connect = new ConnectionDb();
        Connection conn = connect.conDB();
        //afficher toutes les formations
        String query = "SELECT code_formation,Intitule,Nombre_jours,Annee,Mois,Nombre_participants,Domaine, formateur from formation";

        try {
            PreparedStatement preparedStatement = conn.prepareStatement(query);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {

                //pour afficher le domaine
                String DomaineQUERY = "select libelle from domaine where code_domaine =? ";
                PreparedStatement Statement = conn.prepareStatement(DomaineQUERY);
                Statement.setString(1, rs.getString("Domaine"));
                ResultSet resultSet = Statement.executeQuery();

                //pour afficher le nom de formateur
                String FormateurQUERY = "select nom from formateur where code_formateur =? ";
                PreparedStatement Formateur_Statement = conn.prepareStatement(FormateurQUERY);
                Formateur_Statement.setString(1, rs.getString("formateur"));
                ResultSet result = Formateur_Statement.executeQuery();

                while (resultSet.next() && result.next()) {
                    domaine = resultSet.getString("libelle");
                    String formateur = result.getString("nom");
                    oblist.add(new Formation(rs.getInt("code_formation"), rs.getString("Intitule"), rs.getInt("Nombre_jours"),
                            rs.getInt("Annee"), rs.getInt("Mois"), rs.getInt("Nombre_participants"),
                            domaine, formateur));
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
            e.getCause();
        }

        //remplir le table view
        col_intitule.setCellValueFactory(new PropertyValueFactory<>("Intitule"));
        col_jours.setCellValueFactory(new PropertyValueFactory<>("Nombre_jours"));
        col_anne.setCellValueFactory(new PropertyValueFactory<>("Annee"));
        col_mois.setCellValueFactory(new PropertyValueFactory<>("Mois"));
        col_nb_Participants.setCellValueFactory(new PropertyValueFactory<>("Nombre_participants"));
        col_domaine.setCellValueFactory(new PropertyValueFactory<>("Domaine"));
        col_formateur.setCellValueFactory(new PropertyValueFactory<>("formateur"));
        table.setItems(oblist);


        table.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

    }

    //s'inscrire à une formation
    public void Inscription(ActionEvent event) throws SQLException {
        //connection à la base
        ConnectionDb connect = new ConnectionDb();
        Connection conn = connect.conDB();
        String code_formation = "";
        String formateur = "";
        //la formation sélectionnée
        Formation selectedformation = table.getSelectionModel().getSelectedItem();
        //nom de la formation
        String cellValue = selectedformation.getIntitule();

        String QUERY = "Select code_formation, formateur, Nombre_participants from formation where intitule=?";
        PreparedStatement Statement = conn.prepareStatement(QUERY);
        Statement.setString(1, cellValue);
        ResultSet rs = Statement.executeQuery();
        if (rs.next()) {
            code_formation = rs.getString("code_formation");
            formateur = rs.getString("formateur");

            //insertion dans la table d'association participant formation
            String Insert = "Insert Into sessionformation (formation,formateur,participant)" +
                    "values(?,?,?)";
            PreparedStatement preparedStatement = conn.prepareStatement(Insert);
            preparedStatement.setString(1, code_formation);
            preparedStatement.setString(2, formateur);
            System.out.println(User.id);
            preparedStatement.setString(3,User.id.toString());
            preparedStatement.execute();
            //mise à jour de nb participants
            String Update = "Update formation set Nombre_participants =? ";
            PreparedStatement statement = conn.prepareStatement(Update);
            Integer nb = rs.getInt("Nombre_participants") + 1;
            statement.setString(1, nb.toString());
            statement.execute();
        }
    }

    //redirection ver profil
    public void profilButton(ActionEvent event) throws IOException {
        AnchorPane pane = FXMLLoader.load(getClass().getResource("Profil.fxml"));
        root.getChildren().setAll(pane);

    }

    public void MesFormationsButton(ActionEvent event) throws IOException {
        AnchorPane pane = FXMLLoader.load(getClass().getResource("MesFormation.fxml"));
        root.getChildren().setAll(pane);

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
}
