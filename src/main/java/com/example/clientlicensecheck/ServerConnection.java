package com.example.clientlicensecheck;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Logger;

public class ServerConnection {
    private static final Logger logger = Logger.getLogger(ServerConnection.class.getName());
    private final String serverAddress;
    private final int serverPort;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    public ServerConnection(String serverAddress, int serverPort) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
    }

    public String sendCommand(String command) {
        try {
            connect();
            out.println(command);
            logger.info("Command sent to server: " + command);
            String response = in.readLine();
            logger.info("Received response from server: " + response);
            System.out.println("GOT RESPONSE FROM " + serverAddress + " " + response);
            return response;
        } catch (IOException ex) {
            logger.severe("Error during communication with server: " + ex.getMessage());
            return "false";
        } finally {
            disconnect();
        }
    }

    private void connect() throws IOException {
        socket = new Socket(serverAddress, serverPort);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
        logger.info("Connected to server at " + socket.getInetAddress() + ":" + socket.getPort());
    }

    public void disconnect() {

    }

    private void closeConnection() {
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null && !socket.isClosed()) socket.close();
            logger.info("Connection closed.");
        } catch (IOException ex) {
            logger.severe("Error closing connection: " + ex.getMessage());
        }
    }
}
