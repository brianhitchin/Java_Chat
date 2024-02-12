package com.cognixia.jump.connection;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConnectionManager {

    private static Connection connection;

    private static void makeConnection() throws FileNotFoundException, IOException, ClassNotFoundException, SQLException {

        Properties props = new Properties();

        props.load( new FileInputStream("resources/config.properties") );

        String url = props.getProperty("url");
        String username = props.getProperty("username");
        String password = props.getProperty("password");

        Class.forName("com.mysql.cj.jdbc.Driver"); // load in the driver
        connection = DriverManager.getConnection(url, username, password);
    }

    // singleton pattern
    public static Connection getConnection() throws FileNotFoundException, IOException, ClassNotFoundException, SQLException {

        if(connection == null) {
            makeConnection();
        }

        return connection;
    }

    public static void main(String[] args) {

        try {
            Connection connection = ConnectionManager.getConnection();
            connection.close();

        } catch (IOException e) {
            System.err.println("Detail connection error.");
        } catch (ClassNotFoundException e) {
            System.err.println("Driver connection error.");
        } catch (SQLException e) {
            System.err.println("Database connection error.");
        }
    }

}
