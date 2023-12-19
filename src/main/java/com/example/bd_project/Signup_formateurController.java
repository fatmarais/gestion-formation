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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static javafx.scene.control.Alert.AlertType.ERROR;


public class Signup_formateurController implements Initializable {
    @FXML
    private Button button;

    @FXML
    private Hyperlink link;

    @FXML
    private TextField login;

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
    private ComboBox<String> domaine;
    @FXML
    private Label NumTel_msg;

    @FXML
    private Label Email_msg;

    @FXML
    AnchorPane root;
    @FXML
    private  Label error_msg;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ConnectionDb connect =new ConnectionDb();
        Connection conn=connect.conDB();

        try {

            String QUERY ="Select Libelle from Domaine";
            PreparedStatement preparedStatement = conn.prepareStatement(QUERY);
            ResultSet queryResult = preparedStatement.executeQuery();

            while(queryResult.next()){
                String libelle= queryResult.getString("Libelle");
                domaine.getItems().add(libelle);
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
                || (prenom.getText().isBlank()) || (domaine.getSelectionModel().isEmpty()) ||(num_tel.getText().isBlank()) || (mail.getText().isBlank()))
        {
            empty=true;
        }
        return empty;
    }

    public boolean ExistLogin()
    { // est ce que login existe
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
                    preparedStatement.setString(3, "formateur");
                    preparedStatement.execute();
                    //code utilisateur pour le numéro de compte
                    String compte = "select Code_utilisateur FROM utilisateur where Login = ?";
                    PreparedStatement Select_compte = conn.prepareStatement(compte);
                    Select_compte.setString(1, login.getText());
                    ResultSet id_compte = Select_compte.executeQuery();
                    String domaine_valeur = "select Code_domaine FROM Domaine where Libelle = ? ";
                    PreparedStatement Select_domaine = conn.prepareStatement(domaine_valeur);
                    Select_domaine.setString(1, domaine.getValue());
                    ResultSet id_domaine = Select_domaine.executeQuery();
                    //Insertion dans la table formateur
                    String QUERY = "INSERT INTO formateur(Nom,Prenom,Domaine,Email,Num_telephone,compte) VALUES(?, ?, ?, ?, ?, ?)";

                    PreparedStatement Statement = conn.prepareStatement(QUERY);

                    Statement.setString(1, nom.getText());
                    Statement.setString(2, prenom.getText());
                    id_domaine.next();
                    Statement.setString(3, id_domaine.getString("Code_domaine"));
                    Statement.setString(4, mail.getText());
                    Statement.setString(5, num_tel.getText());
                    id_compte.next();
                    Statement.setString(6, id_compte.getString("Code_utilisateur"));
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



    //contole de saisie num tel
    public void NumTel_Control(javafx.scene.input.KeyEvent Event) {
        String str=num_tel.getText();
        boolean isNumeric = str.chars().allMatch( Character::isDigit );
        NumTel_msg.setText("");
        if(!isNumeric || str.length()!=8)
        {
            NumTel_msg.setText("numeros incorrect");
            //Alert alert=new Alert(ERROR,"numeros incorrect");
            //alert.show();
        }
        else {
            System.out.println("numeros correct");
            NumTel_msg.setText("");
        }

    }
   //controle de saisie e-mail
    public void Email_Control(javafx.scene.input.KeyEvent Event) {
        Pattern pattern = Pattern.compile("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}");
        //vérifier que le string correspend au pattern
        Matcher mat = pattern.matcher(mail.getText());
        if (!mat.matches())
        {
            Email_msg.setText("mail incorrect");

        /*Alert alert=new Alert(ERROR,"mail incorrect");
        alert.show();*/}
        else {
            Email_msg.setText("");
        }
    }
    //Redirection vers le signup participant
public  void signUp_ButtonParticipant(ActionEvent event) throws IOException {

    AnchorPane pane = FXMLLoader.load(getClass().getResource("signup_participant.fxml"));
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
