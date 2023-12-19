package com.example.bd_project;


import Models.Formation;

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
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import javafx.util.converter.IntegerStringConverter;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AdminDashFormationController implements Initializable {
    @FXML
    private Button add;

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
        //connection à la bd
        ConnectionDb connect = new ConnectionDb();
        Connection conn = connect.conDB();
        //selection des données de la table formation
        String query = "SELECT code_formation,Intitule,Nombre_jours,Annee,Mois,Nombre_participants,Domaine, formateur from formation";

        try {
            PreparedStatement preparedStatement = conn.prepareStatement(query);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {


                //pour afficher la libelle du domaine
                String DomaineQUERY ="select libelle from domaine where code_domaine =? ";
                PreparedStatement Statement=conn.prepareStatement(DomaineQUERY);
                Statement.setString(1,rs.getString("Domaine"));
                ResultSet resultSet= Statement.executeQuery();
                //pour afficher le nom de formateur
                String FormateurQUERY ="select nom from formateur where code_formateur =? ";
                PreparedStatement Formateur_Statement=conn.prepareStatement(FormateurQUERY);
                Formateur_Statement.setString(1,rs.getString("formateur"));
                ResultSet result= Formateur_Statement.executeQuery();

                while(resultSet.next() && result.next()){
                    //remplir la liste à partir de la base
                    domaine=resultSet.getString("libelle");
                    String formateur=result.getString("nom");
                    oblist.add(new Formation(rs.getInt("code_formation"),rs.getString("Intitule"), rs.getInt("Nombre_jours"),
                            rs.getInt("Annee"), rs.getInt("Mois"), rs.getInt("Nombre_participants"),
                            domaine,formateur));
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
            e.getCause();
        }

        //ajouter les valeurs sélectionnées aux tableview
        col_intitule.setCellValueFactory(new PropertyValueFactory<>("Intitule"));
        col_jours.setCellValueFactory(new PropertyValueFactory<>("Nombre_jours"));
        col_anne.setCellValueFactory(new PropertyValueFactory<>("Annee"));
        col_mois.setCellValueFactory(new PropertyValueFactory<>("Mois"));
        col_nb_Participants.setCellValueFactory(new PropertyValueFactory<>("Nombre_participants"));
        col_domaine.setCellValueFactory(new PropertyValueFactory<>("Domaine"));
        col_formateur.setCellValueFactory(new PropertyValueFactory<>("formateur"));
        table.setItems(oblist);



        table.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        table.setEditable(true);
        col_intitule.setCellFactory(TextFieldTableCell.forTableColumn());
        col_jours.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        col_anne.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        col_mois.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        col_nb_Participants.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        col_domaine.setCellFactory(TextFieldTableCell.forTableColumn());




    }

    //supprimer formation
    public void deleteButtonOnAction(ActionEvent actionEvent) {
        //liste des formations dans tableview

        ObservableList<Formation> listformation;
        listformation = table.getItems();
        //get formation sélectionnée
        Formation selectedformation = table.getSelectionModel().getSelectedItem();
        String cellValue = selectedformation.getIntitule();

        //supprimer de la base
        ConnectionDb connect = new ConnectionDb();
        Connection conn = connect.conDB();
        try {

                String Delete_QUERY = "DELETE FROM formation WHERE code_formation=?";

                PreparedStatement preparedStatement = conn.prepareStatement(Delete_QUERY);
                preparedStatement.setString(1,selectedformation.getCode_formation().toString() );
                preparedStatement.execute();


            listformation.remove(selectedformation);


        } catch (Exception e) {
            e.printStackTrace();
            e.getCause();
        }


    }
    // load interface pour ajouter formation
    public void AddButtonOnAction(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Home.class.getResource("AjouterFormation.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 376, 462);
        Stage stage=(Stage)((Node)event.getSource()).getScene().getWindow();
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();



    }




    //mis à jour de la valeur de intitulé
    public void changeIntituleCellEvent(TableColumn.CellEditEvent edittedCell) {
        Formation personSelected = table.getSelectionModel().getSelectedItem();
        personSelected.setIntitule(edittedCell.getNewValue().toString());
        String newIntitule = edittedCell.getNewValue().toString();
        ConnectionDb connect = new ConnectionDb();
        Connection conn = connect.conDB();
        try {

               String Update_QUERY = "update formation set intitule=? where Code_formation=?";
                PreparedStatement preparedStatement = conn.prepareStatement(Update_QUERY);
                preparedStatement.setString(1, newIntitule);
                preparedStatement.setString(2,personSelected.getCode_formation().toString() );
                preparedStatement.execute();

        } catch (Exception e) {
            e.printStackTrace();
            e.getCause();
        }


    }
    //mis à jour de la valeur de nbjours
    public void changeNbJoursCellEvent(TableColumn.CellEditEvent edittedCell) {
        Formation personSelected = table.getSelectionModel().getSelectedItem();
        personSelected.setNombre_jours(Integer.parseInt(edittedCell.getNewValue().toString()));
        String newValue = edittedCell.getNewValue().toString();
        ConnectionDb connect = new ConnectionDb();
        Connection conn = connect.conDB();
        try {

                String Update_QUERY = "update formation set Nombre_jours=? where Code_formation=?";
                PreparedStatement preparedStatement = conn.prepareStatement(Update_QUERY);
                preparedStatement.setString(1, newValue);
            preparedStatement.setString(2,personSelected.getCode_formation().toString() );
                preparedStatement.execute();


        } catch (Exception e) {
            e.printStackTrace();
            e.getCause();
        }


    }

    //mis à jour de la valeur de année
    public void changeAnneeCellEvent(TableColumn.CellEditEvent edittedCell) {
        Formation personSelected = table.getSelectionModel().getSelectedItem();
        personSelected.setAnnee(Integer.parseInt(edittedCell.getNewValue().toString()));
        String newValue = edittedCell.getNewValue().toString();
        ConnectionDb connect = new ConnectionDb();
        Connection conn = connect.conDB();
        try {

            String Update_QUERY = "update formation set annee=? where Code_formation=?";
            PreparedStatement preparedStatement = conn.prepareStatement(Update_QUERY);
            preparedStatement.setString(1, newValue);
            preparedStatement.setString(2,personSelected.getCode_formation().toString() );
            preparedStatement.execute();


        } catch (Exception e) {
            e.printStackTrace();
            e.getCause();
        }


    }
    //mis à jour de la valeur de mois
    public void changeMoisCellEvent(TableColumn.CellEditEvent edittedCell) {
        Formation personSelected = table.getSelectionModel().getSelectedItem();
        personSelected.setMois(Integer.parseInt(edittedCell.getNewValue().toString()));
        String newValue = edittedCell.getNewValue().toString();
        ConnectionDb connect = new ConnectionDb();
        Connection conn = connect.conDB();
        try {

            String Update_QUERY = "update formation set mois=? where Code_formation=?";
            PreparedStatement preparedStatement = conn.prepareStatement(Update_QUERY);
            preparedStatement.setString(1, newValue);
            preparedStatement.setString(2,personSelected.getCode_formation().toString() );
            preparedStatement.execute();


        } catch (Exception e) {
            e.printStackTrace();
            e.getCause();
        }


    }
    //mis à jour de la valeur de nb participant
    public void changeNbParticipantCellEvent(TableColumn.CellEditEvent edittedCell) {
        Formation personSelected = table.getSelectionModel().getSelectedItem();
        personSelected.setNombre_participants(Integer.parseInt(edittedCell.getNewValue().toString()));
        String newValue = edittedCell.getNewValue().toString();
        ConnectionDb connect = new ConnectionDb();
        Connection conn = connect.conDB();
        try {

            String Update_QUERY = "update formation set Nombre_participants=? where Code_formation=?";
            PreparedStatement preparedStatement = conn.prepareStatement(Update_QUERY);
            preparedStatement.setString(1, newValue);
            preparedStatement.setString(2,personSelected.getCode_formation().toString() );
            preparedStatement.execute();


        } catch (Exception e) {
            e.printStackTrace();
            e.getCause();
        }


    }

    //redirection page
    public void ParticipantButton(ActionEvent event) throws IOException {
        AnchorPane pane = FXMLLoader.load(getClass().getResource("Admin_dash_formateur.fxml"));
        root.getChildren().setAll(pane);
    }
    public void FormateurButton(ActionEvent event) throws IOException {
        AnchorPane pane = FXMLLoader.load(getClass().getResource("Admin_dash_formateur.fxml"));
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






