package com.cognixia.jump.GUI_Chat;
import java.io.IOException;
import java.net.Socket;
import javafx.application.Application;
import com.cognixia.jump.client.Client;


import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;

import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import javafx.stage.Stage;


public class LoginGUI extends Application {
	
    private TextField messageField;
    private TextField  usernameField;
    private TextField  passwordField;
    private Client client;

    @Override
    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();
        
      ///********************LOGIN IN********************************************************** 
      
 
        try {
            Socket socket = new Socket("0.0.0.0", 1234); 
            client = new Client(socket, "username"); 
        } catch (IOException e) {
            e.printStackTrace();
        }
        VBox LoginArea= new VBox();
        LoginArea.setPadding(new Insets(10));
        LoginArea.setSpacing(10);

        usernameField = new TextField();
        usernameField.setPromptText("Username");
        
        passwordField = new TextField();
        passwordField.setPromptText("Password");
    
        
        LoginArea.getChildren().addAll(usernameField, passwordField);
        root.setCenter(LoginArea);
       
        Button loginButton= new Button("Login");
       // loginButton.setOnAction(event -> login());
        root.setBottom(loginButton);
    
        
      
        
        
        Scene Loginscene = new Scene(root, 600, 400);
        primaryStage.setScene(Loginscene);
        primaryStage.setTitle("LOGIN ");
       
        primaryStage.show();
        }
 

    public static void main(String[] args) {
       launch(args);
    }


	
}





//********************

