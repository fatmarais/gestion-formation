package com.example.bd_project;

import Models.Formateur;
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

public class AdminDashFormateurController implements Initializable {

    @FXML
    private TableView<Formateur> table;

    @FXML
    private TableColumn<Formateur, String> col_domaine;

    @FXML
    private TableColumn<Formateur, String> col_email;

    @FXML
    private TableColumn<Formateur, String> col_nom;

    @FXML
    private TableColumn<Formateur, String> col_prenom;


    @FXML
    private TableColumn<Formateur, String> col_tel;

    @FXML
     private AnchorPane root;

    //liste des modèles formateurs
    ObservableList<Formateur> oblist = FXCollections.observableArrayList();


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ConnectionDb connect =new ConnectionDb();
        Connection conn=connect.conDB();
        //afficher la liste des formateur dans tableView
        String query="SELECT Nom, Prenom, Domaine, Email, Num_telephone from Formateur";

        try {
            PreparedStatement preparedStatement = conn.prepareStatement(query);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next())
            { //pour afficher la libelle du domaine
                String DomaineQUERY ="select libelle from domaine where code_domaine =? ";
                PreparedStatement Statement=conn.prepareStatement(DomaineQUERY);
                Statement.setString(1,rs.getString("Domaine"));
                ResultSet resultSet= Statement.executeQuery();

                //remplir la liste à partir de la base
                oblist.add(new Formateur(rs.getString("Nom"),rs.getString("Prenom"),rs.getString("Domaine"),
                        rs.getString("Email"),rs.getString("Num_telephone")));
            }

        }catch(Exception e){
            e.printStackTrace();
            e.getCause();
        }
        //ajouter les valeurs sélectionnées aux table
        col_nom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        col_prenom.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        col_domaine.setCellValueFactory(new PropertyValueFactory<>("domaine"));
        col_email.setCellValueFactory(new PropertyValueFactory<>("email"));
        col_tel.setCellValueFactory(new PropertyValueFactory<>("tel"));
        
        table.setItems(oblist);

        //pour sélectionner une seule ligne
        table.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    }
    //Supprimer un Formateur
    public void deleteButtonOnAction(ActionEvent actionEvent) {
        //liste des formateur dans tableview
        ObservableList<Formateur> listFormateur;
        listFormateur=table.getItems();

        //get formateur sélectionné
        Formateur selectedFormateur = table.getSelectionModel().getSelectedItem();
        String cellValue = selectedFormateur.getNom();

            //supprimer du tableview
            listFormateur.remove(selectedFormateur);

            //connection à la base
        ConnectionDb connect = new ConnectionDb();
        Connection conn = connect.conDB();
        try {

           //select code formateur pour supprimer son compte
            String SelectQUERY= "Select code_formateur from formateur where nom=?";
            PreparedStatement id_Statement = conn.prepareStatement(SelectQUERY);
            id_Statement.setString(1,cellValue);
            ResultSet queryResult = id_Statement.executeQuery();
            if (queryResult.next()) {
                String code_formateur = queryResult.getString("code_formateur");
                //selection de numéro de compte de formateur
                String SelectCompte= "Select compte from formateur where nom=?";
                PreparedStatement compte_Statement = conn.prepareStatement(SelectCompte);
                compte_Statement.setString(1,cellValue);
                ResultSet rs = compte_Statement.executeQuery();
                if(rs.next()){
                    String compte=rs.getString("compte");
                    //supprimer de la table formateur
                    String QUERY="DELETE FROM formateur WHERE code_formateur=?";

                    PreparedStatement preparedStatement = conn.prepareStatement(QUERY);
                    preparedStatement.setString(1, code_formateur);
                    preparedStatement.execute();
                    //supprimer le compte de formateur de la table utilisateur
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
    public void ParticipantButton(ActionEvent event) throws IOException {
        //redirection vers l'interface de gestion de participants
        AnchorPane pane = FXMLLoader.load(getClass().getResource("Admin_dash_formateur.fxml"));
        root.getChildren().setAll(pane);
    }  public void FormationButton(ActionEvent event) throws IOException {
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
