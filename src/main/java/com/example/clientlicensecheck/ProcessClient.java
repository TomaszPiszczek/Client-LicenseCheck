package com.example.clientlicensecheck;

import com.example.clientlicensecheck.ui.ButtonPanel;
import com.example.clientlicensecheck.ui.MainFrame;
import com.example.clientlicensecheck.ui.StatusPanel;

import javax.swing.*;
import java.awt.*;
import java.util.Scanner;
import java.util.logging.Logger;

public class ProcessClient {
    private static final Logger logger = Logger.getLogger(ProcessClient.class.getName());

    public static void main(String[] args) {
        CardPanelManager cardPanelManager = new CardPanelManager();
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter the first server IP address: ");
        String ipAddressA = scanner.nextLine();

        System.out.print("Enter the second server IP address: ");
        String ipAddressB = scanner.nextLine();

        ServerConnection serverConnectionA = new ServerConnection(ipAddressA, 1099);
        ServerConnection serverConnectionB = new ServerConnection(ipAddressB, 1099);

        ButtonPanel buttonPanel = new ButtonPanel(e -> {
        });
        ButtonPanel finalButtonPanel = buttonPanel;
        buttonPanel = new ButtonPanel(e -> {
            finalButtonPanel.setButtonVisible(false);
            cardPanelManager.showPanel("loading");

            SwingWorker<String, Void> worker = new SwingWorker<>() {
                @Override
                protected String doInBackground() {
                    // Check both servers
                    String responseA = serverConnectionA.connect();
                    String responseB = serverConnectionB.connect();

                    // Determine the final response
                    if ("true".equals(responseA) || "true".equals(responseB)) {
                        return "true";
                    } else {
                        return "false";
                    }
                }

                @Override
                protected void done() {
                    try {
                        String response = get();
                        StatusPanel statusPanel = new StatusPanel();

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
