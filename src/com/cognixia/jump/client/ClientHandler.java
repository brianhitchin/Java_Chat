package com.cognixia.jump.client;

import com.cognixia.jump.server.Server;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class ClientHandler implements Runnable{

    // Keep track of each client
    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
    
    private static HashMap<String, ClientHandler>Privateusername = new HashMap<>();

    // connection between client and server
    private Socket socket;

    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;

    private String clientUsername;

    // Need to keep reference to server to communicate with the menu
    private Server server;

    public ClientHandler(Socket socket, Server server){

        try {
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.clientUsername = bufferedReader.readLine();
            this.server = server;
            clientHandlers.add(this);
            Privateusername.put(clientUsername,this);
            broadcastMessage("SERVER: " + clientUsername + " has entered the chat!");
            writeToLogs("SERVER: " + clientUsername + " has entered the chat!**");
        }
        catch (IOException e) {
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    public void sendPrivateMessage(String recipient, String msg) {

        ClientHandler privateHandler = Privateusername.get(recipient);
        if (privateHandler != null) {
        	
            try {
            	privateHandler.bufferedWriter.write(clientUsername + "(private): " + msg);
            	privateHandler.bufferedWriter.newLine();
            	privateHandler.bufferedWriter.flush();
            } catch (IOException e) {
                System.err.println("Error sending Private message");
            }
        } else {
            try {
                bufferedWriter.write("SERVER: Private message to " + recipient + " failed. User not found.");
                bufferedWriter.newLine();
                bufferedWriter.flush();
            } catch (IOException e) {
                System.err.println("Error sending Private message");
            }
        }
    }

    @Override
    public void run() {
        try {
       
            String messageFromClient;
            while (socket.isConnected()) {
                messageFromClient = bufferedReader.readLine();
                if (messageFromClient == null) {
                    break; // Connection closed by client
                }
                
                else if (messageFromClient.startsWith("@")) {
                    String[] parts = messageFromClient.split(":", 2);
                    String recipient = parts[0].substring(1).trim();
                    String message = parts[1].trim();
                    sendPrivateMessage(recipient, message);
   
                
                
                }
                
                else {
                broadcastMessage(messageFromClient);
                writeToLogs(messageFromClient);}
            }
        } catch (IOException e) {
            // Exception occurred, close everything
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
        finally {
            try {
                server.removeClientHandler(this);
                removeClientHandler();
            } catch (UnknownHostException e) {
                System.err.println("Error Removing client handler");
            }
        }
    }

    public void broadcastMessage(String msg){
        for(ClientHandler clientHandler : clientHandlers){
            try{
                if(!clientHandler.clientUsername.equals(clientUsername)){
                    clientHandler.bufferedWriter.write(msg);
                    clientHandler.bufferedWriter.newLine();
                    clientHandler.bufferedWriter.flush();
                }
            }
            catch (IOException e){
                closeEverything(socket, bufferedReader, bufferedWriter);
            }
        }
    }
    
    public void removeClientHandler() throws UnknownHostException {

        clientHandlers.remove(this);

        broadcastMessage("SERVER: " + clientUsername + " has left the chat!");
        writeToLogs("SERVER: " + clientUsername + " has left the chat!**");

    }

    public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {

        try {

            if(bufferedReader != null){
                bufferedReader.close();
            }
            if(bufferedWriter != null) {
                bufferedWriter.close();
            }
            if(socket != null){
                socket.close();
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    public static void writeToLogs(String msg) {

        File file = new File("JumpChatLogs.txt");

        try(FileWriter fileWriter = new FileWriter(file, true)){

            // If file doesn't exist create one
            if(!file.exists()){
                boolean created = file.createNewFile();

                if(created){
                    System.out.println("Log file created");
                }
            }

//             Encryption
            char[] encoded = msg.toCharArray();

            for(char c: encoded){
                c += 34;
                fileWriter.write(c);
            }



            fileWriter.write("\n");
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    public static void ReadFromLogs(int lastLines) {

        File file = new File("JumpChatLogs.txt");

        if(!file.exists()){

            // Log file does not exist
            return;
        }

        try{
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            String line;

            /*
            // TODO
                    Might look into RandomAccessFile later for better efficiency
            */

            int counter = 0;

            String[] logs = new String[lastLines];

            while((line = bufferedReader.readLine()) != null){

                // Reset counter to zero once it reaches the max size we want to read
                // This will reset the logs to only store the most recent messages
                if(counter == lastLines - 1){

                    counter = 0;
                }
                // store the logs first here
                logs[counter] = line;
                counter++;

            }

            // Need to sort the log file
            String[] sorted = new String[lastLines];

            if(counter != 0)
                counter -= 1;
            for(int i = 0; i < logs.length; i++){

                if(counter == lastLines - 1){
                    counter = 0;
                }

                sorted[i] = logs[counter];
                counter++;
            }

            // Decrypt Message from logs
            for (String message : sorted) {

                if(message == null){
                    break;
                }

                char[] encoded = message.toCharArray();
                StringBuilder stringBuilder = new StringBuilder();

                for (char c : encoded) {
                    c -= 34;
                    stringBuilder.append(c);
                }

                // Print sb
                System.out.println(stringBuilder);

                //clear sb
                stringBuilder.delete(0, stringBuilder.length());



            }


        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
