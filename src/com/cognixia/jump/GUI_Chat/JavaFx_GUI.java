package com.cognixia.jump.GUI_Chat;

import java.io.IOException;
import java.net.Socket;
import javafx.application.Application;
import com.cognixia.jump.client.Client;


import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.FontSmoothingType;
import javafx.stage.Stage;

public class JavaFx_GUI extends Application {

    private TextArea chatArea;
    private TextArea Writeusername;
    private TextField messageField;
    private TextField  usernameField;
    private TextField  passwordField;
    private Client client;

    
    
    private Socket socket;
    private String username;
    private String ip;
    
    
    
    
    public JavaFx_GUI(Socket socket, String username,String ip) {
        this.socket = socket;
        this.username = username;
        this.ip =ip;
    }
    
    
    @Override
    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();
        try {
            Socket socket = new Socket(ip, 1234); 
            client = new Client(socket, username); 
        } catch (IOException e) {
                e.printStackTrace();
                // Handle connection error
            }
        
///********************CHAt Area**********************************************************
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
       // messageField.setOnAction(event -> sendMessageGUI());

        Button sendButton = new Button("Send");
        Button ExitButton = new Button("End Chat");
       sendButton.setOnAction(event -> sendMessageGUI());
      // ExitButton.setOnAction(event -> client.closeEverything());
        
        
        messageInputArea.getChildren().addAll(messageField, sendButton,ExitButton);
        root.setBottom(messageInputArea);
        
        

        // Set up the scene and stage
        Scene scene = new Scene(root, 600, 400);
        primaryStage.setScene(scene);
        primaryStage.setTitle("JUMP CHAT");
       
        primaryStage.show();
    }

    private void sendMessageGUI() {
        String message = messageField.getText();
        if (!message.isEmpty()) {
        	
        
            // Send message to the client
        	 client.sendMessage();
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
