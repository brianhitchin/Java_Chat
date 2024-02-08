package com.cognixia.jump.client;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.OutputStream;
import java.io.BufferedReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;


public class Client  {

	
	private Socket socket;
	private BufferedReader bufferedReader;
	private BufferedWriter bufferedWriter;
	private String username;
	

	public Client(Socket socket, String username) {
		
		try {
			this.socket=socket;
			this.bufferedWriter= new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			this.bufferedReader= new BufferedReader(new InputStreamReader(socket.getInputStream()));
			
			this.username=username;
			
		}catch(IOException e)
		{
			closeEverything(socket,bufferedReader,bufferedWriter);
		}}
		
		
	public void sendMessage() {	
		try {
			bufferedWriter.write(username);
			bufferedWriter.newLine();
			bufferedWriter.flush();
			
			Scanner  scanner =new Scanner(System.in);
			while(socket.isConnected()) {

			String messageTosend = scanner.nextLine();

			if(messageTosend.equals("/exit")){
				closeEverything(socket, bufferedReader, bufferedWriter);
				return;
			}

			bufferedWriter.write(username+": " + messageTosend);
			bufferedWriter.newLine();
			bufferedWriter.flush();
			}
			
			
			
			
			
		}catch(IOException e){
			closeEverything(socket,bufferedReader,bufferedWriter);
			
		}
			
		
		
	}
	
	
	
	
	public void listenMessage() {	
		new Thread(new Runnable() {
			
			public void run() {
			String msgFromGroupChat;
			
		
		
		
		while(socket.isConnected()) {
		try {
			msgFromGroupChat= bufferedReader.readLine();
			System.out.println(msgFromGroupChat);
		}catch(IOException e){
			
			closeEverything(socket,bufferedReader,bufferedWriter);
		}
		}
		}
		}).start();
	}
	
	
	

	public void closeEverything(Socket socket,BufferedReader bufferedReader, BufferedWriter bufferedWriter){	
	try {
		
		if(bufferedReader !=null) {
			bufferedReader.close();
			}
		if(bufferedWriter !=null) {
			bufferedWriter.close();
		}
		if(socket !=null) {
			socket.close();
		}
		
	}catch(IOException e) {
		
		e.printStackTrace();
	
	}
	}
	
	
	
    public static void main(String[] args) throws IOException{
    	Scanner scanner =new Scanner(System.in);
    	System.out.println("+ ======================================================== +");
		System.out.println("|                    Welcome to JumpChat!                  |");
		System.out.println("|                                                          |");
		System.out.println("|                    1. CONNECT TO SERVER                  |");
		System.out.println("|                          2. EXIT                         |");
		System.out.println("+ ======================================================== +");
		
		int resp = Integer.parseInt(scanner.nextLine());
		
		if (resp == 1) {
			System.out.println("+ ======================================================== +");
	    	System.out.println("|           Enter your username for the group chat:        |");
	    	System.out.println("+ ======================================================== +");
	    	String username=scanner.nextLine();
			System.out.println("+ ======================================================== +");
	    	System.out.println("|              Enter the IP address of the server:         |");
	    	System.out.println("+ ======================================================== +");
			String ip = scanner.nextLine();
	    	Socket socket = new Socket(ip, 1234);
	    	Client client= new Client(socket,username);
	
			// Entered Jump chat room
	    	System.out.println("+ ======================================================== +");
			System.out.println("|              You've entered the JumpChat room.           |");
			System.out.println("|       Press /exit to exit or start sending messages!     |");
			System.out.println("+ ======================================================== +");
	
	    	client.listenMessage();
	    	client.sendMessage();
		} else {
			System.out.println("Good bye!");
			System.exit(0);
		}

    }}