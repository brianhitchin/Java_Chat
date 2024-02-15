package com.cognixia.jump.GUI_Chat;

import com.cognixia.jump.client.Client;
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

import java.sql.*;

public class SignUpGUI extends Application {

    private TextField usernameField;

    private TextField passwordField;

    private TextField confirmPasswordField;

    private Label feedback;

    private final Connection connection;

    private final Stage previosStage;

    SignUpGUI(Connection connection, Stage previousStage){
        this.connection = connection;
        this.previosStage = previousStage;
    }

    @Override
    public void start(Stage stage) {
        BorderPane root = new BorderPane();

        VBox signUpArea = new VBox();
        signUpArea.setPadding(new Insets(10));
        signUpArea.setSpacing(10);

        Label title = new Label("Sign Up");
        title.setStyle("-fx-font: 24 arial;");
        VBox titleArea = new VBox();
        titleArea.getChildren().add(title);
        titleArea.setAlignment(Pos.CENTER);

        usernameField = new TextField();
        usernameField.setPromptText("Username");

        passwordField = new TextField();
        passwordField.setPromptText("Password");

        confirmPasswordField = new TextField();
        confirmPasswordField.setPromptText("Password");

        Button signupButton = new Button("Sign Up");
        signupButton.setOnAction(event -> signUp(connection, stage));
        VBox buttonArea = new VBox();
        buttonArea.getChildren().add(signupButton);
        buttonArea.setAlignment(Pos.CENTER);

        BorderPane.setMargin(buttonArea, new Insets(0,0,350,0));
        root.setBottom(buttonArea);

        feedback = new Label();
        VBox feedbackArea = new VBox();
        feedbackArea.getChildren().add(feedback);
        feedbackArea.setAlignment(Pos.CENTER);

        signUpArea.getChildren().addAll(titleArea, usernameField, passwordField, confirmPasswordField, buttonArea, feedbackArea);
        root.setCenter(signUpArea);

        Scene signUpScene = new Scene(root, 600, 300);
        stage.setScene(signUpScene);
        stage.setTitle("Sign Up");
        stage.show();
    }

    public void signUp(Connection connection, Stage stage){

        // Get username and passwords from fields
        String username = usernameField.getText();
        String password = passwordField.getText();
        String confirmPasswordFieldText = confirmPasswordField.getText();

        // Check if username already exists in the DB
        if(doesUsernameExist(username, connection)){
            System.err.println("This username already exists");
            feedback.setText("This username already exists");
            feedback.setTextFill(Color.RED);
            return;
        }

        // Check if passwords match
        if(!password.equals(confirmPasswordFieldText)){
            System.err.println("Passwords do not match");
            feedback.setText("Passwords do not match");
            feedback.setTextFill(Color.RED);
            return;
        }

        // Encrypt password
        String encryptedPassword = Client.encoder(password);

        try{
            Statement stmt = connection.createStatement();

            stmt.executeUpdate("insert into users(username, password) values(\"" + username +"\", \""+ encryptedPassword +"\")");

            stmt.close();

            previosStage.show();
            stage.close();

        }
        catch (SQLException e) {
            System.err.println("Error inserting user into DB");
        }

    }

    public static boolean doesUsernameExist(String username, Connection connection){

        try {

            PreparedStatement allUsers = connection.prepareStatement("select * from users");
            ResultSet rs = allUsers.executeQuery();

            while(rs.next()) {

                String result_Username = rs.getString("username");

                if(result_Username.equals(username)){

                    return true;
                }

            }

            rs.close();
            allUsers.close();
            return false;
        }
        catch (SQLException e) {
            e.printStackTrace();
            return true;
        }
    }

}
