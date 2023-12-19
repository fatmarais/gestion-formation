package com.example.bd_project;

import Models.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.event.ActionEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ResourceBundle;

import static javafx.scene.control.Alert.AlertType.ERROR;

public class loginController implements Initializable {
    @FXML
    private CheckBox admin;

    @FXML
    private Button connecter;

    @FXML
    private ImageView logo;

    @FXML
    private CheckBox participant;

    @FXML
    private PasswordField password;

    @FXML
    private AnchorPane sidebar;
    @FXML
    private AnchorPane root;

    @FXML
    private TextField username;

    @FXML
    private Label message;
    @FXML
    private Label role_msg;

    @FXML
    private CheckBox formateur;



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Image image=new Image(getClass().getResourceAsStream("preview.jpg"));
        logo.setImage(image);

    }
    public void loginButtonOnAction(ActionEvent event)
    { // controle des champs
        if(username.getText().isBlank() || password.getText().isBlank())
     {
         message.setText("Remplir tous les champs");

     } else message.setText("");
        if (!admin.isSelected()||!formateur.isSelected()||!participant.isSelected())
        {
           role_msg.setText("choisir un rôle");
        }else
        {role_msg.setText("");}
     if(!username.getText().isBlank() && !password.getText().isBlank()
             && admin.isSelected()|| formateur.isSelected()||participant.isSelected())
     {
         //connection à la base
         ConnectionDb connect =new ConnectionDb();
         Connection conn=connect.conDB();
         //vérifier l'existance du username & password
         String query="SELECT * FROM utilisateur WHERE Login = ? and Password = ?";

         try {

             PreparedStatement preparedStatement = conn.prepareStatement(query);
             preparedStatement.setString(1, username.getText());
             preparedStatement.setString(2, password.getText());
             ResultSet queryResult = preparedStatement.executeQuery();

             if (queryResult.next()) {
                 message.setText("succés");
                 Integer id = queryResult.getInt("Code_utilisateur");
                 String login = queryResult.getString("login");
                 String motDePasse = queryResult.getString("password");
                 User user = new User(id, login, motDePasse);
                 Node node = (Node) event.getSource();
                 Stage stage = (Stage) node.getScene().getWindow();
                 stage.close();
                 try {
                     //choisir l'interface selon le role

                     String destination="";
                     if (participant.isSelected()) {
                       destination="Participant_Dash.fxml" ;

                     } if (admin.isSelected()) {
                         destination ="Admin_Dash.fxml";
                     } if (formateur.isSelected()) {
                        destination="Formateur_Dash.fxml";
                     }
                      Parent  root = FXMLLoader.load(loginController.class.getResource(destination));
                         stage.setUserData(user);
                         // Step 6
                         Scene scene = new Scene(root);
                         stage.setScene(scene);
                         // Step 7
                         stage.show();
                     } catch(IOException e){
                         System.err.println(String.format("Error: %s", e.getMessage()));
                     }


                 }else{
                     message.setText("Nom d'utilisateur ou mot de passe incorrect");
                 }


             }catch(Exception e){
                 e.printStackTrace();
                 e.getCause();

             }
     }
    }
    //pour s'inscrire
    public  void signUpParticipant(MouseEvent event) throws IOException {

        AnchorPane pane = FXMLLoader.load(getClass().getResource("signup_participant.fxml"));
        root.getChildren().setAll(pane);
    }


}
