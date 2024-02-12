package com.cognixia.jump.GUI_Chat;

import com.cognixia.jump.client.Client;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class JavaFx_GUI extends Application {

    private TextArea chatArea;
    private TextField messageField;
    private Client client;

    @Override
    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();

        // Create an instance of the Client class


        // Chat area
        chatArea = new TextArea();
        chatArea.setEditable(false);
        root.setCenter(chatArea);

        // Message input area
        VBox messageInputArea = new VBox();
        messageInputArea.setPadding(new Insets(10));
        messageInputArea.setSpacing(10);

        messageField = new TextField();
        messageField.setPromptText("Type your message here...");
        messageField.setOnAction(event -> sendMessage());

        Button sendButton = new Button("Send");
        sendButton.setOnAction(event -> sendMessage());

        messageInputArea.getChildren().addAll(messageField, sendButton);
        root.setBottom(messageInputArea);

        // Set up the scene and stage
        Scene scene = new Scene(root, 600, 400);
        primaryStage.setScene(scene);
        primaryStage.setTitle("JUMP CHAT");
        primaryStage.show();
    }

    private void sendMessage() {
        String message = messageField.getText();
        if (!message.isEmpty()) {
            // Send message to the client
          
            // Optionally, append the message to the chat area
            appendMessage("You: " + message);
            
            
            // Clear the message field after sending
            messageField.clear();
        }
    }

    private void appendMessage(String message) {
        chatArea.appendText(message + "\n");
    }

    public static void main(String[] args) {
        launch(args);
    }
}
