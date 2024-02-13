package com.cognixia.jump.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Scanner;

import com.cognixia.jump.client.ClientHandler;

public class Server {

	private ServerSocket serverSocket;
	
	public static final String ANSI_RESET = "\u001B[0m";
	public static final String ANSI_YELLOW = "\u001B[33m";
	public static final String ANSI_BLUE = "\u001B[34m";
			
	// Keep track of each clientHandler
	private ArrayList<ClientHandler> clientHandlers;

	public Server(ServerSocket serverSocket) {

		this.serverSocket = serverSocket;
		this.clientHandlers = new ArrayList<>();
	}

	public void startServer() {

		try {
			displayCurrentServerStatus("Running");
			while (!serverSocket.isClosed()) {

				Socket socket = serverSocket.accept();
				System.out.println("A new client has connected.");
				ClientHandler clientHandler = new ClientHandler(socket, this);
				clientHandlers.add(clientHandler);
				Thread thread = new Thread(clientHandler);
				thread.start();
				
			}

		} catch (IOException e) {

			e.printStackTrace();
			closeServerSocket();
		}

	}

	public void removeClientHandler(ClientHandler clientHandler) throws UnknownHostException {
		clientHandlers.remove(clientHandler);
		System.out.println("Client disconnected.");
		displayCurrentServerStatus("Running");
	}

	public void closeServerSocket() {

		try {

			if (serverSocket != null) {

				System.out.println("Shutting down Jump Chat Server...");
				serverSocket.close();

			}

		} catch (IOException e) {


			e.printStackTrace();

		}

	}

	public static void main(String[] args) throws IOException {

		ServerSocket serverSocket = new ServerSocket(1234);

		Server server = new Server(serverSocket);
		server.startServer();

	}

	public void displayCurrentServerStatus(String status) throws UnknownHostException {

		System.out.println(ANSI_YELLOW + "+ ======================================================== +" + ANSI_RESET);
		System.out.println(ANSI_BLUE + "|                  Jump Chat Server: " + status + "               |" + ANSI_RESET);
		System.out.println(ANSI_BLUE + "|                  IP Address: " + InetAddress.getLocalHost().getHostAddress() + "               |" + ANSI_RESET);
		System.out.println(ANSI_BLUE + "|                          Port: " + 1234 + "                      |" + ANSI_RESET);
		System.out.println(ANSI_BLUE + "|                      Number of users: " + this.clientHandlers.size() + "                  |" + ANSI_RESET);
		System.out.println(ANSI_YELLOW + "+ ======================================================== +" + ANSI_RESET);

	}


}
