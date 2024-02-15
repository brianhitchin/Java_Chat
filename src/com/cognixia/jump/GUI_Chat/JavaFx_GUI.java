package com.cognixia.jump.GUI_Chat;

import java.io.IOException;
import java.net.Socket;

import com.cognixia.jump.client.ClientHandler;
import javafx.application.Application;
import com.cognixia.jump.client.Client;


import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class JavaFx_GUI extends Application {

    private TextArea chatArea;
    private TextField messageField;
    private final Client client;
    private final Socket socket;
    private final String username;

    private final Stage loginStage;

    // Constructor
    public JavaFx_GUI(Socket socket, String username, Client client, Stage loginStage) {

        this.socket = socket;
        this.username = username;
        this.client = client;
        this.loginStage = loginStage;
    }

    @Override
    public void start(Stage primaryStage) throws IOException {

        BorderPane root = new BorderPane();

        // Chat area ----------------------------------------
        chatArea = new TextArea();
        chatArea.setStyle("-fx-font-size: 1.3em;");
        chatArea.setEditable(false);
        root.setCenter(chatArea);

        // Load History
        loadHistory(10);

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
        ExitButton.setOnAction(event -> exitButton(primaryStage));
        listenMessage();
        enterChat();

        messageInputArea.getChildren().addAll(messageField, sendButton,ExitButton);
        root.setBottom(messageInputArea);

        // Set up the scene and stage
        Scene scene = new Scene(root, 600, 400);
        primaryStage.setScene(scene);
        primaryStage.setTitle("JUMP CHAT");

        primaryStage.getScene().getWindow().addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, this::closeApp);
        primaryStage.show();
    }

    private void loadHistory(int loadLastLines){

        String[] history = ClientHandler.ReadFromLogs(loadLastLines);
        if(history != null){
            for(String message : history){
                chatArea.appendText(message + "\n");
            }
        }

    }

    private void exitButton(Stage primaryStage){
        primaryStage.close();
        client.closeEverything(socket, client.getBufferedReader(), client.getBufferedWriter());
        loginStage.show();
    }

    private void closeApp(WindowEvent event) {

        client.closeEverything(socket, client.getBufferedReader(), client.getBufferedWriter());

    }

    private void sendMessageGUI() {

        String message = messageField.getText();

        if (!message.isEmpty() && !message.isBlank()) {

            // Send message using the client
            sendMessage(message);

            // Optionally, append the message to the chat area
            chatArea.appendText("You: " + message + "\n");
            
            // Clear the message field after sending
            messageField.clear();
        }
    }

    public void sendMessage(String msg)  {

        try {
            if (socket.isConnected()) {

                if (msg.startsWith("@")) {

                    String[] parts = msg.split(":", 2);
                    String recipient = parts[0].substring(1).trim();
                    String pm = parts[1].trim();

                    // Encrypt here
                    String message = "@" + recipient + ":" + pm;
                    message = Client.encoder(message);

                    client.getBufferedWriter().write(message);
                }
                else {

                    // Encrypt here
                    String message = username + ": " + msg;
                    message = Client.encoder(message);

                    client.getBufferedWriter().write(message); // Normal message
                }
                client.getBufferedWriter().newLine();
                client.getBufferedWriter().flush();
            }
        }
        catch (IOException e){
            System.err.println("GUI: Error Sending Message");
        }
    }

    public void enterChat(){
        try {
                client.getBufferedWriter().write(username);
                client.getBufferedWriter().newLine();
                client.getBufferedWriter().flush();
        }
        catch (IOException e){
            System.err.println("GUI: Error entering the chat");
        }
    }

    public void listenMessage() {
        new Thread(new Runnable() {

            public void run() {

                String msgFromGroupChat;

                while(socket.isConnected() && !socket.isClosed()) {

                    try {

                        msgFromGroupChat = client.getBufferedReader().readLine();

                        // Decrypt here
                        msgFromGroupChat = Client.decoder(msgFromGroupChat);

                        chatArea.appendText(msgFromGroupChat + "\n");

                    }catch(IOException ignored){

                    }
                }
            }
        }).start();
    }

}
