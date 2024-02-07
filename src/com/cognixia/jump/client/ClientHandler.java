package com.cognixia.jump.client;

import java.io.*;
import java.net.Socket;
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

    public ClientHandler(Socket socket){

        try {
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.clientUsername = bufferedReader.readLine();
            clientHandlers.add(this);

            broadcastMessage("SERVER: " + clientUsername + " has entered the chat!");
            writeToLogs("**SERVER: " + clientUsername + " has entered the chat!**");
        }
        catch (IOException e) {
            closeEverything(socket, bufferedReader, bufferedWriter);
        }

    }

    @Override
    public void run() {

        String messageFromClient;

        while (socket.isConnected()){
            try {
                messageFromClient = bufferedReader.readLine();
                broadcastMessage(messageFromClient);
                writeToLogs(messageFromClient);

            }
            catch (IOException e){
                closeEverything(socket, bufferedReader, bufferedWriter);
                break;
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

    public void removeClientHandler() {

        clientHandlers.remove(this);
        broadcastMessage("SERVER: " + clientUsername + " has left the chat!");
        writeToLogs("**SERVER: " + clientUsername + " has left the chat!**");

    }

    public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter){
        removeClientHandler();
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

    public void displayCurrentServerStatus(){
        System.out.println("#=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=#");
        System.out.println("# Jump Chat Server: RUNNING");
        System.out.println("# IP Address: + ");

        System.out.println("#=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=#");
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
