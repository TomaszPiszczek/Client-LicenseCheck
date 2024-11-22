package com.example.clientlicensecheck.ui;

import com.example.clientlicensecheck.CardPanelManager;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    public MainFrame(CardPanelManager cardPanelManager) {
        super("Klient");

        // Usunięcie dekoracji okna (brak obramowania, brak tytułu)
        setUndecorated(true);

        // Uzyskanie obiektu GraphicsDevice i ustawienie trybu pełnoekranowego
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();
        gd.setFullScreenWindow(this);  // Ustawienie okna na pełny ekran

        // Dodanie panelu kart do ramki
        add(cardPanelManager.getCardPanel());

        // Ustawienie domyślnego działania przy zamknięciu okna
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Ustawienie widoczności okna
        setVisible(true);
    }
}
