package com.cognixia.jump.client;

import com.cognixia.jump.server.Server;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;

public class ClientHandler implements Runnable{

    // Keep track of each client
    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();

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

            broadcastMessage("SERVER: " + clientUsername + " has entered the chat!");
            writeToLogs("SERVER: " + clientUsername + " has entered the chat!**");
        }
        catch (IOException e) {
            closeEverything(socket, bufferedReader, bufferedWriter);
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
                broadcastMessage(messageFromClient);
                writeToLogs(messageFromClient);
            }
        } catch (IOException e) {
            // Exception occurred, close everything
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
        finally {
            try {
                server.removeClientHandler(this);
            } catch (UnknownHostException e) {
                e.printStackTrace();
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
            removeClientHandler();

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

    public void writeToLogs(String msg) {

        File file = new File("JumpChatLogs.txt");

        try(FileWriter fileWriter = new FileWriter(file, true)){

            // If file doesn't exist create one
            if(!file.exists()){
                boolean created = file.createNewFile();

                if(created){
                    System.out.println("Log file created");
                }
            }

            // This part is for testing purposes, Delete after -------
//            fileWriter.write(msg + "\n");
            // -------------------------------------------------------

            // Encryption would go here
//            byte[] c = msg.getBytes(StandardCharsets.UTF_16);
//            fileWriter.write(Arrays.toString(c));
//            fileWriter.write("\n");
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

    public String ReadFromLogs() {

        File file = new File("JumpChatLogs.txt");

        try(FileReader fileReader = new FileReader(file)){

            // If file doesn't exist create one
            if(!file.exists()){

                System.err.println("Log file does not exist.");
                return null;
            }

            // Decryption would go here
//            String line = null;
//            for()



            // We can either print all logs here using loop or
            // We can return an array of Strings with all the log lines
            return "One";

        }
        catch (IOException e){
            e.printStackTrace();
        }


        return null;
    }
}
