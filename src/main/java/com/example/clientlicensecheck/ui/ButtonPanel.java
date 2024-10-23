package com.example.clientlicensecheck.ui;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class ButtonPanel extends JPanel {
    private JButton button;

    public ButtonPanel(ActionListener listener) {
        button = new JButton("TEST");
        button.setPreferredSize(new Dimension(200, 150));
        button.setFont(new Font("Arial", Font.BOLD, 24));
        button.setFocusable(false);
        button.addActionListener(listener);
        setLayout(new BorderLayout());
        add(button, BorderLayout.CENTER);
    }

    public void setButtonVisible(boolean visible) {
        button.setVisible(visible);
    }
}
