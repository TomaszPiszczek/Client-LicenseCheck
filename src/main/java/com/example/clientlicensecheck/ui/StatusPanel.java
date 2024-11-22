package com.example.clientlicensecheck.ui;

import javax.swing.*;
import java.awt.*;

public class StatusPanel extends JPanel {
    private JLabel statusLabel;

    public StatusPanel() {
        // Inicjalizacja etykiety i ustawienie początkowego tekstu
        statusLabel = new JLabel("Ładowanie...", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Arial", Font.BOLD, 100)); // Czcionka zajmująca dużą część ekranu

        // Preferowany rozmiar panelu, będzie się dostosowywał do ekranu
        setPreferredSize(new Dimension(200, 150));
        setLayout(new BorderLayout());
        add(statusLabel, BorderLayout.CENTER);
    }

    public void updateStatus(String message, Color color) {
        // Zmiana stylu wiadomości na HTML, żeby lepiej wyglądała na dużym ekranie
        String htmlMessage = "<html><div style='text-align: center;'>" + message.replace(" ", "<br>") + "</div></html>";
        statusLabel.setText(htmlMessage);
        statusLabel.setFont(new Font("Arial", Font.BOLD, 100)); // Ustawienie czcionki na bardzo dużą

        // Zmiana tła panelu
        setBackground(color);
    }
}
