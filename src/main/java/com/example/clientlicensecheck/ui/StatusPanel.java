package com.example.clientlicensecheck.ui;

import javax.swing.*;
import java.awt.*;

public class StatusPanel extends JPanel {
    private JLabel statusLabel;

    public StatusPanel() {
        statusLabel = new JLabel("Ładowanie...", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Arial", Font.BOLD, 24));
        setPreferredSize(new Dimension(200, 150));
        setLayout(new BorderLayout());
        add(statusLabel, BorderLayout.CENTER);
    }

    public void updateStatus(String message, Color color) {
        statusLabel.setText(message);
        setBackground(color);
    }
}
