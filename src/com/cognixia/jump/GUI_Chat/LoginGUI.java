package com.cognixia.jump.GUI_Chat;

import java.io.IOException;
import java.net.Socket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.cognixia.jump.client.Client;

import com.cognixia.jump.connection.ConnectionManager;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class LoginGUI extends Application {

    private TextField usernameField;
    private TextField passwordField;
    private TextField IPField;

    private Label feedback;

    @Override
    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();

        VBox loginArea = new VBox();
        loginArea.setPadding(new Insets(10));
        loginArea.setSpacing(10);

        Label title = new Label("Welcome to JumpChat App!");
        title.setStyle("-fx-font: 24 arial;");
        VBox titleArea = new VBox();
        titleArea.getChildren().add(title);
        titleArea.setAlignment(Pos.CENTER);

        usernameField = new TextField();
        usernameField.setPromptText("Username");

        passwordField = new TextField();
        passwordField.setPromptText("Password");

        IPField = new TextField();
        IPField.setPromptText("IP Server");

        Button loginButton = new Button("Login");
        loginButton.setOnAction(event -> login(primaryStage));
        VBox buttonArea = new VBox();
        buttonArea.getChildren().add(loginButton);
        buttonArea.setAlignment(Pos.CENTER);
        BorderPane.setMargin(buttonArea, new Insets(0,0,350,0)); // Top margin to separate from loginArea
        root.setBottom(buttonArea);

        feedback = new Label();
        VBox feedbackArea = new VBox();
        feedbackArea.getChildren().add(feedback);
        feedbackArea.setAlignment(Pos.CENTER);

        loginArea.getChildren().addAll(titleArea, usernameField, passwordField,IPField, buttonArea, feedbackArea);
        root.setCenter(loginArea);

        Scene loginScene = new Scene(root, 600, 300);
        primaryStage.setScene(loginScene);
        primaryStage.setTitle("LOGIN");
        primaryStage.show();
    }

    private void login(Stage primaryStage) {

        try {

            // Handle login logic here
            String username = usernameField.getText();
            String password = passwordField.getText();
            String IPserver = IPField.getText();
            Connection connection = ConnectionManager.getConnection();

            // Perform authentication, socket connection, etc.
            String encryptedPass = encoder(password);

            if(authenticate(username, encryptedPass, connection)){

                try {
                    Socket socket = new Socket(IPserver, 1234);
                    Client client = new Client(socket, username);

                    JavaFx_GUI chatGUI = new JavaFx_GUI(socket, username, client);

                    chatGUI.start(new Stage());

                    primaryStage.hide();
                }
                catch (IOException e){
                    System.err.println("Error connection to the server");
                    feedback.setText("Invalid IP Address.");
                    feedback.setTextFill(Color.RED);
                }
            }
            else{
                // Need to provide feedback to user if username/password does not match records of DB
                feedback.setText("Invalid username or password.");
                feedback.setTextFill(Color.RED);
            }

        } catch (IOException e) {
            e.printStackTrace();
            // Handle connection error
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean authenticate(String username, String encryptedPassword, Connection connection){

        // Authenticate
        try {

            PreparedStatement allUsers = connection.prepareStatement("select * from users");
            ResultSet rs = allUsers.executeQuery();

            while(rs.next()) {

                String result_Username = rs.getString("username");
                String result_Password = rs.getString("password");

                if(result_Username.equals(username) && result_Password.equals(encryptedPassword)){

                    return true;
                }

            }

            rs.close();
            allUsers.close();
            return false;
        }
        catch (SQLException e) {
            System.err.println("Authentication Error");
            return false;
        }
    }

    public static String encoder(String password){

        char[] encoded = password.toCharArray();
        StringBuilder stringBuilder = new StringBuilder();


        for(char c: encoded){
            c += 3;
            stringBuilder.append(c);
        }

        return stringBuilder.toString();
    }
    public static void main(String[] args) {
        launch(args);
    }
}