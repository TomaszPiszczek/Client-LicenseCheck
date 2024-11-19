package com.example.clientlicensecheck;

import com.example.clientlicensecheck.CardPanelManager;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class MainFrame extends JFrame {
    public MainFrame(CardPanelManager cardPanelManager) {
        super("Klient");
        setAlwaysOnTop(true);
        setUndecorated(true);

        // Dodanie panelu kart do ramki
        add(cardPanelManager.getCardPanel());

        // Konfiguracja ramki
        setSize(120, 120);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Ustawienie lokalizacji ramki w prawym dolnym rogu ekranu
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int x = screenSize.width - getWidth();  // x = szerokość ekranu - szerokość okna
        int y = screenSize.height - getHeight(); // y = wysokość ekranu - wysokość okna
        setLocation(x, y);

        setVisible(true);

        // Dodanie WindowFocusListener, aby monitorować utratę fokusu
        addWindowFocusListener(new WindowAdapter() {
            @Override
            public void windowDeactivated(WindowEvent e) {
                SwingUtilities.invokeLater(() -> {
                    System.out.println("RESET FOCUS (Deactivated)");
                    setAlwaysOnTop(false);
                    setAlwaysOnTop(true);
                    toFront();
                    requestFocusInWindow();
                });
            }
        });


    }
}