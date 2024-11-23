package com.example.clientlicensecheck;

import com.example.clientlicensecheck.ui.ButtonPanel;
import com.example.clientlicensecheck.ui.MainFrame;
import com.example.clientlicensecheck.ui.StatusPanel;

import javax.swing.*;
import java.awt.*;
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
            ipAddressA = "192.168.1.46";
            ipAddressB = "192.168.1.28";
        }

        ServerConnection serverConnectionA = new ServerConnection(ipAddressA, 1099);
        ServerConnection serverConnectionB = new ServerConnection(ipAddressB, 1099);

        ButtonPanel buttonPanel = new ButtonPanel(e -> {});

        ButtonPanel finalButtonPanel = buttonPanel;

        buttonPanel = new ButtonPanel(e -> {
            finalButtonPanel.setButtonVisible(false);
            cardPanelManager.showPanel("loading");
            System.out.println("SENDED CHECK A: " + ipAddressA);
            System.out.println("SENDED CHECK B: " + ipAddressB);

            // Wysyłanie żądań "CHECK" równocześnie
            CompletableFuture<String> responseA = CompletableFuture
                    .supplyAsync(() -> serverConnectionA.sendCommand("CHECK"))
                    .orTimeout(2, TimeUnit.SECONDS) // Ustawienie timeoutu na 5 sekund
                    .exceptionally(ex -> {
                        System.out.println("ERROR");

                        return "error";
                    });

            CompletableFuture<String> responseB = CompletableFuture
                    .supplyAsync(() -> serverConnectionB.sendCommand("CHECK"))
                    .orTimeout(2, TimeUnit.SECONDS) // Ustawienie timeoutu na 5 sekund
                    .exceptionally(ex -> {
                        System.out.println("ERROR");
                        return "error";
                    });

            // Przetwarzanie wyników "CHECK"
            CompletableFuture.allOf(responseA, responseB).thenRun(() -> {
                try {
                    String resultA = responseA.get();
                    String resultB = responseB.get();
                    System.out.println(resultA + ipAddressA + "\n" + resultB + ipAddressB);
                    if(resultA.equals("error") || resultB.equals("error")) {
                        updateUI("Błąd<br>połączenia", Color.ORANGE, cardPanelManager, finalButtonPanel);
                        return;
                    }
                    if ("true".equals(resultA) && "true".equals(resultB)) {
                        // Jeśli oba zwróciły "true", wysyłamy "SHUTDOWN" równocześnie z limitem czasu
                        CompletableFuture<String> shutdownA = CompletableFuture.supplyAsync(() -> serverConnectionA.sendCommand("SHUTDOWN"))
                                .orTimeout(20, TimeUnit.SECONDS)
                                .exceptionally(ex -> {
                                    if (ex instanceof TimeoutException) {
                                        logger.warning("Shutdown A command timed out.");
                                        updateUI("BRAK ODPOWIEDZI", Color.ORANGE, cardPanelManager, finalButtonPanel);
                                        return null;
                                    }
                                    return "false";
                                });

                        CompletableFuture<String> shutdownB = CompletableFuture.supplyAsync(() -> serverConnectionB.sendCommand("SHUTDOWN"))
                                .orTimeout(20, TimeUnit.SECONDS)
                                .exceptionally(ex -> {
                                    if (ex instanceof TimeoutException) {
                                        logger.warning("Shutdown B command timed out.");
                                        updateUI("BRAK ODPOWIEDZI", Color.ORANGE, cardPanelManager, finalButtonPanel);
                                        return null;
                                    }
                                    return "false"; // Zwracamy wartość domyślną, gdy wystąpi TimeoutException
                                });

                        CompletableFuture.anyOf(shutdownA, shutdownB).thenAccept(response -> {
                            // Jeśli pierwszy z serwerów zwróci "true" na "SHUTDOWN", kończymy natychmiast
                            try {
                                if ("true".equals(response)) {
                                    if ("true".equals(shutdownA.getNow("false"))) {
                                        System.out.println("SENDED CLOSE B");
                                        serverConnectionB.sendCommand("DIALOG");
                                        serverConnectionB.sendCommand("CLOSE");
                                    } else {
                                        System.out.println("SENDED CLOSE A");
                                        serverConnectionA.sendCommand("DIALOG");
                                        serverConnectionA.sendCommand("CLOSE");
                                    }
                                    updateUI("OK", Color.GREEN, cardPanelManager, finalButtonPanel);
                                } else {
                                    // Jeśli pierwszy zwróci "false", czekamy na drugi
                                    CompletableFuture.allOf(shutdownA, shutdownB).thenRun(() -> {
                                        if ("false".equals(shutdownA.join()) && "false".equals(shutdownB.join())) {
                                            updateUI("ZACZEKAJ", Color.RED, cardPanelManager, finalButtonPanel);
                                        }
                                        if ("true".equals(shutdownA.join()) || "true".equals(shutdownB.join())) {
                                            updateUI("OK", Color.GREEN, cardPanelManager, finalButtonPanel);
                                        }
                                        serverConnectionA.sendCommand("DIALOG");
                                        serverConnectionA.sendCommand("CLOSE");
                                        serverConnectionB.sendCommand("DIALOG");
                                        serverConnectionB.sendCommand("CLOSE");
                                    });
                                }
                            } catch (Exception ex) {
                                logger.severe("Error in server communication: " + ex.getMessage());
                                updateUI("Błąd<br>połączenia", Color.ORANGE, cardPanelManager, finalButtonPanel);
                            }
                        });
                    } else {
                        updateUI("OK", Color.GREEN, cardPanelManager, finalButtonPanel);
                    }
                } catch (Exception ex) {
                    logger.severe("Error in concurrent server connection: " + ex.getMessage());
                    updateUI("Błąd<br>połączenia", Color.ORANGE, cardPanelManager, finalButtonPanel);
                }
            });
        });

        StatusPanel loadingPanel = new StatusPanel();
        loadingPanel.updateStatus("Ładowanie...", Color.LIGHT_GRAY);

        cardPanelManager.addPanel(buttonPanel, "button");
        cardPanelManager.addPanel(loadingPanel, "loading");

        new MainFrame(cardPanelManager);
    }

    private static void updateUI(String status, Color color, CardPanelManager cardPanelManager, ButtonPanel finalButtonPanel) {
        SwingUtilities.invokeLater(() -> {
            StatusPanel statusPanel = new StatusPanel();
            statusPanel.updateStatus(status, color);
            cardPanelManager.addPanel(statusPanel, "status");
            cardPanelManager.showPanel("status");

            Timer timer = new Timer(3000, e -> {
                cardPanelManager.showPanel("button");
                finalButtonPanel.setButtonVisible(true);
            });
            timer.setRepeats(false);
            timer.start();
        });
    }
}
