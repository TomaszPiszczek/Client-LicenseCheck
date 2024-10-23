package com.example.clientlicensecheck;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.logging.Logger;

public class ServerConnection {
    private static final Logger logger = Logger.getLogger(ServerConnection.class.getName());
    private final String serverAddress;
    private final int serverPort;

    public ServerConnection(String serverAddress, int serverPort) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
    }

    public String connect() {
        try (Socket socket = new Socket(serverAddress, serverPort);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            logger.info("Connected to server at " + socket.getInetAddress() + ":" + socket.getPort());

            String response = in.readLine();
            logger.info("Received response from server: " + response);
            return response;

        } catch (IOException ex) {
            logger.severe("Error during communication with server: " + ex.getMessage());

            return null;
        } finally {
            logger.info("Client connection closed.");
        }
    }
}