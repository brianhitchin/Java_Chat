package com.cognixia.jump.client;
import com.cognixia.jump.connection.ConnectionManager;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.OutputStream;
import java.io.BufferedReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.*;
import java.util.Objects;
import java.util.Scanner;


public class Client  {


	private Socket socket;
	private BufferedReader bufferedReader;
	private BufferedWriter bufferedWriter;
	private String username;

	public static final String ANSI_RESET = "\u001B[0m";
	public static final String ANSI_RED = "\u001B[31m";
	public static final String ANSI_GREEN = "\u001B[32m";
	public static final String ANSI_YELLOW = "\u001B[33m";
	public static final String ANSI_BLUE = "\u001b[38;2;145;231;255m";

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

	public static String decoder(String password){

		char[] encoded = password.toCharArray();
		StringBuilder stringBuilder = new StringBuilder();


		for(char c: encoded){
			c -= 3;
			stringBuilder.append(c);
		}

		return stringBuilder.toString();
	}

	public static boolean createUser(String username, String encryptedPassword, Connection connection){

		try{
			Statement stmt = connection.createStatement();

			int userCreated = stmt.executeUpdate("insert into users(username, password) values(\"" + username +"\", \""+ encryptedPassword +"\")");

			stmt.close();
			return userCreated > 0;
		}
		catch (SQLException e) {
			System.err.println("Error inserting user into DB");
			return false;
		}

	}

	public static void main(String[] args) throws IOException, SQLException, ClassNotFoundException {

		Scanner scanner =new Scanner(System.in);
		boolean authenticated;
		Connection connection = ConnectionManager.getConnection();

		// Menu
		while(true) {
			System.out.println(ANSI_YELLOW + "+ ======================================================== +" + ANSI_RESET);
			System.out.println(ANSI_BLUE + "|                    Welcome to JumpChat!                  |" + ANSI_RESET);
			System.out.println(ANSI_BLUE + "|                                                          |" + ANSI_RESET);
			System.out.println(ANSI_BLUE + "|                    1. CREATE AN ACCOUNT                  |" + ANSI_RESET);
			System.out.println(ANSI_BLUE + "|                    2. CONNECT TO SERVER                  |" + ANSI_RESET);
			System.out.println(ANSI_BLUE + "|                          3. EXIT                         |" + ANSI_RESET);
			System.out.println(ANSI_YELLOW + "+ ======================================================== +" + ANSI_RESET);

			String resp = scanner.nextLine();

			if(Objects.equals(resp, "1")){

				String username;
				boolean exit = false;

				System.out.println(ANSI_YELLOW + "+ ======================================================== +" + ANSI_RESET);
				System.out.println(ANSI_BLUE + "|          Enter your username for JUMP Chat App:          |" + ANSI_RESET);
				System.out.println(ANSI_YELLOW + "+ ======================================================== +" + ANSI_RESET);
				username = scanner.nextLine();

				// need to add username restrictions

				// Check if username already exists
				while(doesUsernameExist(username, connection) || username.startsWith("/")) {
					System.out.println(ANSI_YELLOW + "+ ======================================================== +" + ANSI_RESET);
					System.out.println(ANSI_RED + "|         This username already exists or is invalid       |" + ANSI_RESET);
					System.out.println(ANSI_RED + "|          Enter another username for JUMP Chat App        |" + ANSI_RESET);
					System.out.println(ANSI_RED + "|          		     Enter \"/exit\" to cancel               |" + ANSI_RESET);
					System.out.println(ANSI_YELLOW + "+ ======================================================== +" + ANSI_RESET);
					username = scanner.nextLine();

					if(username.equals("/exit")){
						exit = true;
						break;
					}

				}

				if(!exit){
					String password = "";
					String passwordConfirm = "";

					do {

						if(!passwordConfirm.equals(password)){
							System.out.println(ANSI_YELLOW + "+ ======================================================== +" + ANSI_RESET);
							System.out.println(ANSI_RED + "|          Passwords do not match. Please try again        |" + ANSI_RESET);
							System.out.println(ANSI_RED + "|          		     Enter \"/exit\" to cancel               |" + ANSI_RESET);
							System.out.println(ANSI_YELLOW + "+ ======================================================== +" + ANSI_RESET);
							System.out.println();
						}

						System.out.println(ANSI_YELLOW + "+ ======================================================== +" + ANSI_RESET);
						System.out.println(ANSI_BLUE + "|           Enter your password for JUMP Chat Login:       |" + ANSI_RESET);
						System.out.println(ANSI_YELLOW + "+ ======================================================== +" + ANSI_RESET);
						password = scanner.nextLine();

						if(password.equals("/exit")){
							exit = true;
							break;
						}

						System.out.println(ANSI_YELLOW + "+ ======================================================== +" + ANSI_RESET);
						System.out.println(ANSI_BLUE + "|         Confirm your password for JUMP Chat Login:       |" + ANSI_RESET);
						System.out.println(ANSI_YELLOW + "+ ======================================================== +" + ANSI_RESET);
						passwordConfirm = scanner.nextLine();

						if(passwordConfirm.equals("/exit")){
							exit = true;
							break;
						}


					}while(!passwordConfirm.equals(password));

					// create user
					if(!exit){

						String encryptedPassword = encoder(password);
						boolean created = createUser(username, encryptedPassword, connection);

						if(created){
							System.out.println(ANSI_YELLOW + "+ ======================================================== +" + ANSI_RESET);
							System.out.println(ANSI_GREEN + "|                  User successfully created               |" + ANSI_RESET);
							System.out.println(ANSI_YELLOW + "+ ======================================================== +" + ANSI_RESET);
						}
						else{
							System.out.println(ANSI_YELLOW + "+ ======================================================== +" + ANSI_RESET);
							System.out.println(ANSI_RED + "|                     Error creating user                  |" + ANSI_RESET);
							System.out.println(ANSI_YELLOW + "+ ======================================================== +" + ANSI_RESET);
						}
					}
				}
			}
			else if (Objects.equals(resp, "2")) {

				System.out.println(ANSI_YELLOW + "+ ======================================================== +" + ANSI_RESET);
				System.out.println(ANSI_BLUE + "|          Enter your username for JUMP Chat Login:        |" + ANSI_RESET);
				System.out.println(ANSI_YELLOW + "+ ======================================================== +" + ANSI_RESET);
				String username = scanner.nextLine();

				System.out.println(ANSI_YELLOW + "+ ======================================================== +" + ANSI_RESET);
				System.out.println(ANSI_BLUE + "|           Enter your password for JUMP Chat Login:       |" + ANSI_RESET);
				System.out.println(ANSI_YELLOW + "+ ======================================================== +" + ANSI_RESET);
				String password = scanner.nextLine();

				// Encrypt password
				String encryptedPass = encoder(password);

				// Authenticate
				authenticated = authenticate(username, encryptedPass, connection);

				if(authenticated) {

					while(true) {
						System.out.println(ANSI_YELLOW + "+ ======================================================== +" + ANSI_RESET);
						System.out.println(ANSI_GREEN + "|                User Successfully Authenticated.          |" + ANSI_RESET);
						System.out.println(ANSI_GREEN + "|              Enter the IP address of the server:         |" + ANSI_RESET);
						System.out.println(ANSI_YELLOW + "+ ======================================================== +" + ANSI_RESET);
						String ip = scanner.nextLine();

						try {
							Socket socket = new Socket(ip, 1234);
							Client client = new Client(socket, username);

							// Entered Jump chat room
							System.out.println(ANSI_YELLOW + "+ ======================================================== +" + ANSI_RESET);
							System.out.println(ANSI_BLUE + "|              You've entered the JumpChat room.           |" + ANSI_RESET);
							System.out.println(ANSI_BLUE + "|       Press /exit to exit or start sending messages!     |" + ANSI_RESET);
							System.out.println(ANSI_YELLOW + "+ ======================================================== +" + ANSI_RESET);

							client.listenMessage();
							client.sendMessage();

						} catch (IOException e) {

							System.out.println(ANSI_YELLOW + "+ ======================================================== +" + ANSI_RESET);
							System.out.println(ANSI_RED + "|          			  Invalid IP address.                  |" + ANSI_RESET);
							System.out.println(ANSI_YELLOW + "+ ======================================================== +" + ANSI_RESET);

						}
					}
				}else{
					System.out.println(ANSI_YELLOW + "+ ======================================================== +" + ANSI_RESET);
					System.out.println(ANSI_RED + "|          Authentication Failed. Please try again.        |" + ANSI_RESET);
					System.out.println(ANSI_YELLOW + "+ ======================================================== +" + ANSI_RESET);
				}
			} else if(Objects.equals(resp, "3")) {
				System.out.println(ANSI_YELLOW + "+ ======================================================== +" + ANSI_RESET);
				System.out.println(ANSI_BLUE + "|          	            ~ Goodbye! ~                       |" + ANSI_RESET);
				System.out.println(ANSI_YELLOW + "+ ======================================================== +" + ANSI_RESET);
				connection.close();
				System.exit(0);
			}
		}
	}}
