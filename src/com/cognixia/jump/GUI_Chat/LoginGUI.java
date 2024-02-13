package com.cognixia.jump.GUI_Chat;

import java.io.IOException;
import java.net.Socket;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LoginGUI extends Application {

    private TextField usernameField;
    private TextField passwordField;

    @Override
    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();

        VBox loginArea = new VBox();
        loginArea.setPadding(new Insets(10));
        loginArea.setSpacing(10);

        usernameField = new TextField();
        usernameField.setPromptText("Username");

        passwordField = new TextField();
        passwordField.setPromptText("Password");

        loginArea.getChildren().addAll(usernameField, passwordField);
        root.setCenter(loginArea);

        Button loginButton = new Button("Login");
        loginButton.setOnAction(event -> login());
        VBox buttonArea = new VBox();
        buttonArea.getChildren().add(loginButton);
        buttonArea.setAlignment(Pos.CENTER);
        BorderPane.setMargin(buttonArea, new Insets(0,0,350,0)); // Top margin to separate from loginArea
        root.setBottom(buttonArea);
   

        Scene loginScene = new Scene(root, 400, 150);
        primaryStage.setScene(loginScene);
        primaryStage.setTitle("LOGIN");
        primaryStage.show();
    }

    private void login() {
        // Handle login logic here
        String username = usernameField.getText();
        String password = passwordField.getText();
        // Perform authentication, socket connection, etc.
    }

    public static void main(String[] args) {
        launch(args);
    }
}




//********************

