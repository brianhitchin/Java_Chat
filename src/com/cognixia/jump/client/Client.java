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
			System.out.println("+ ======================================================== +");
			System.out.println("|                    Welcome to JumpChat!                  |");
			System.out.println("|                                                          |");
			System.out.println("|                    1. CREATE AN ACCOUNT                  |");
			System.out.println("|                    2. CONNECT TO SERVER                  |");
			System.out.println("|                          3. EXIT                         |");
			System.out.println("+ ======================================================== +");

			String resp = scanner.nextLine();

			if(Objects.equals(resp, "1")){

				String username;
				boolean exit = false;

				System.out.println("+ ======================================================== +");
				System.out.println("|          Enter your username for JUMP Chat App:          |");
				System.out.println("+ ======================================================== +");
				username = scanner.nextLine();

				// need to add username restrictions

				// Check if username already exists
				while(doesUsernameExist(username, connection) || username.startsWith("/")) {
					System.out.println("+ ======================================================== +");
					System.out.println("|         This username already exists or is invalid       |");
					System.out.println("|          Enter another username for JUMP Chat App        |");
					System.out.println("|          		     Enter \"/exit\" to cancel               |");
					System.out.println("+ ======================================================== +");
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
							System.out.println("+ ======================================================== +");
							System.out.println("|          Passwords do not match. Please try again        |");
							System.out.println("|          		     Enter \"/exit\" to cancel               |");
							System.out.println("+ ======================================================== +");
							System.out.println();
						}

						System.out.println("+ ======================================================== +");
						System.out.println("|           Enter your password for JUMP Chat Login:       |");
						System.out.println("+ ======================================================== +");
						password = scanner.nextLine();

						if(password.equals("/exit")){
							exit = true;
							break;
						}

                        System.out.println("+ ======================================================== +");
                        System.out.println("|         Confirm your password for JUMP Chat Login:       |");
                        System.out.println("+ ======================================================== +");
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
							System.out.println("+ ======================================================== +");
							System.out.println("|                  User successfully created               |");
							System.out.println("+ ======================================================== +");
						}
						else{
							System.out.println("+ ======================================================== +");
							System.out.println("|                     Error creating user                  |");
							System.out.println("+ ======================================================== +");
						}
					}
				}
			}
			else if (Objects.equals(resp, "2")) {

				System.out.println("+ ======================================================== +");
				System.out.println("|          Enter your username for JUMP Chat Login:        |");
				System.out.println("+ ======================================================== +");
				String username = scanner.nextLine();

				System.out.println("+ ======================================================== +");
				System.out.println("|           Enter your password for JUMP Chat Login:       |");
				System.out.println("+ ======================================================== +");
				String password = scanner.nextLine();

				// Encrypt password
				String encryptedPass = encoder(password);

				// Authenticate
				authenticated = authenticate(username, encryptedPass, connection);

				if(authenticated) {

					while(true) {
						System.out.println("+ ======================================================== +");
						System.out.println("|                User Successfully Authenticated.          |");
						System.out.println("|              Enter the IP address of the server:         |");
						System.out.println("+ ======================================================== +");
						String ip = scanner.nextLine();

						try {
							Socket socket = new Socket(ip, 1234);
							Client client = new Client(socket, username);

							// Entered Jump chat room
							System.out.println("+ ======================================================== +");
							System.out.println("|              You've entered the JumpChat room.           |");
							System.out.println("|       Press /exit to exit or start sending messages!     |");
							System.out.println("+ ======================================================== +");

							client.listenMessage();
							client.sendMessage();

						} catch (IOException e) {

							System.out.println("+ ======================================================== +");
							System.out.println("|          			  Invalid IP address.                  |");
							System.out.println("+ ======================================================== +");

						}
					}
				}else{
					System.out.println("+ ======================================================== +");
					System.out.println("|          Authentication Failed. Please try again.        |");
					System.out.println("+ ======================================================== +");
				}
			} else if(Objects.equals(resp, "3")) {
				System.out.println("+ ======================================================== +");
				System.out.println("|          	            ~ Goodbye! ~                       |");
				System.out.println("+ ======================================================== +");
				connection.close();
				System.exit(0);
			}
		}
	}}