package com.example.clientlicensecheck.ui;

import com.example.clientlicensecheck.CardPanelManager;
import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    public MainFrame(CardPanelManager cardPanelManager) {
        super("Klient");

        // Dodanie panelu kart do ramki
        add(cardPanelManager.getCardPanel());

        // Konfiguracja trybu pełnoekranowegox
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Maksymalizacja na cały ekran
        setUndecorated(true); // Usunięcie dekoracji ramki

        // Ustawienie lokalizacji ramki i widoczności
        setVisible(true);
    }
}
