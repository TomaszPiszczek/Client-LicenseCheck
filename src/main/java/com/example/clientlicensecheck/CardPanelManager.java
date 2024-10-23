package com.example.clientlicensecheck;

import javax.swing.*;
import java.awt.*;

public class CardPanelManager {
    private JPanel cardPanel;
    private CardLayout cardLayout;

    public CardPanelManager() {
        cardPanel = new JPanel(new CardLayout());
        cardLayout = (CardLayout) cardPanel.getLayout();
    }

    public JPanel getCardPanel() {
        return cardPanel;
    }

    public void addPanel(JPanel panel, String name) {
        cardPanel.add(panel, name);
    }

    public void showPanel(String name) {
        cardLayout.show(cardPanel, name);
    }
}
