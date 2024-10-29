package com.example.clientlicensecheck;

import com.example.clientlicensecheck.ui.ButtonPanel;
import com.example.clientlicensecheck.ui.MainFrame;
import com.example.clientlicensecheck.ui.StatusPanel;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.concurrent.*;
import java.util.logging.Logger;

public class ProcessClient {
    private static final Logger logger = Logger.getLogger(ProcessClient.class.getName());

    public static void main(String[] args) {
        CardPanelManager cardPanelManager = new CardPanelManager();
        String ipAddressA;
        String ipAddressB;

        if (args.length >= 2) {
            ipAddressA = args[0];
            ipAddressB = args[1];
        } else {
            ipAddressA = "192.168.1.114";
            ipAddressB = "192.168.1.114";
        }

        ServerConnection serverConnectionA = new ServerConnection(ipAddressA, 1099);
        ServerConnection serverConnectionB = new ServerConnection(ipAddressB, 1099);

        ExecutorService executorService = Executors.newFixedThreadPool(2);

        ButtonPanel buttonPanel = new ButtonPanel(e -> {
        });
        ButtonPanel finalButtonPanel = buttonPanel;

        buttonPanel = new ButtonPanel(e -> {
            finalButtonPanel.setButtonVisible(false);
            cardPanelManager.showPanel("loading");

            String responseA = serverConnectionA.sendCommand("CHECK");
            String responseB = serverConnectionB.sendCommand("CHECK");

            SwingWorker<String, Void> worker = new SwingWorker<>() {
                @Override
                protected String doInBackground() {
                    try {
                        // Check if both servers returned "error"
                        if ("error".equalsIgnoreCase(responseA) && "error".equalsIgnoreCase(responseB)) {
                            return "error";
                        }

                        // If both servers returned "true"
                        if ("true".equals(responseA) && "true".equals(responseB)) {
                            // Create two tasks for server connections
                            Callable<String> taskA = () -> serverConnectionA.sendCommand("SHUTDOWN");
                            Callable<String> taskB = () -> serverConnectionB.sendCommand("SHUTDOWN");

                            // Submit tasks individually
                            Future<String> futureA = executorService.submit(taskA);
                            Future<String> futureB = executorService.submit(taskB);

                            try {
                                // Wait for taskA to complete and process its result
                                String responseA = futureA.get();
                                if ("true".equals(responseA)) {
                                    System.out.println("SENDED CLOSE B" );
                                    serverConnectionB.sendCommand("CLOSE");
                                    return "true";
                                }

                                // Wait for taskB to complete and process its result
                                String responseB = futureB.get();
                                if ("true".equals(responseB)) {
                                    System.out.println("SENDED CLOSE A ");

                                    serverConnectionA.sendCommand("CLOSE");
                                    return "true";
                                }

                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                            } catch (ExecutionException e) {
                                logger.severe("Error during server communication: " + e.getMessage());
                                return "error";
                            }
                        } else {
                            return "true";
                        }

                        return "I WANT TO RETURN IT ONLY IF THERE IS ERROR";
                    } catch (Exception ex) {
                        logger.severe("Error in concurrent server connection: " + ex.getMessage());
                        return "error";
                    }
                }


                @Override
                protected void done() {
                    try {
                        System.out.println("GOT RESPONSE " + get());
                        String response = get();
                        StatusPanel statusPanel = new StatusPanel();
                        if("error".equalsIgnoreCase(response)){
                            statusPanel.updateStatus("Błąd połączenia",Color.ORANGE);
                        }
                        if ("true".equals(response)) {
                            statusPanel.updateStatus("OK", Color.GREEN);
                        } else {
                            statusPanel.updateStatus("ZACZEKAJ", Color.RED);
                        }

                        cardPanelManager.addPanel(statusPanel, "status");
                        cardPanelManager.showPanel("status");

                        // Timer for 3 seconds
                        Timer timer = new Timer(3000, e1 -> {
                            cardPanelManager.showPanel("button");
                            finalButtonPanel.setButtonVisible(true);
                        });
                        timer.setRepeats(false);
                        timer.start();
                    } catch (Exception ex) {
                        logger.severe("Error in SwingWorker: " + ex.getMessage());
                    }
                }
            };
            worker.execute();
        });

        StatusPanel loadingPanel = new StatusPanel();
        loadingPanel.updateStatus("Ładowanie...", Color.LIGHT_GRAY);

        cardPanelManager.addPanel(buttonPanel, "button");
        cardPanelManager.addPanel(loadingPanel, "loading");

        // Initialize main window
        new MainFrame(cardPanelManager);
    }
}
