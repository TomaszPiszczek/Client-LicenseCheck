package com.example.clientlicensecheck.ui;


import com.example.clientlicensecheck.CardPanelManager;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    public MainFrame(CardPanelManager cardPanelManager) {
        super("Klient");
        setAlwaysOnTop(true);
        setUndecorated(true);

        // Dodanie panelu kart do ramki
        add(cardPanelManager.getCardPanel());

        // Konfiguracja ramki
        setSize(200, 150);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }
}
