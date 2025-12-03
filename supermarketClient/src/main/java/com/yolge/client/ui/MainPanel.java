package com.yolge.client.ui;

import javax.swing.*;
import java.awt.*;

public class MainPanel extends JPanel {
    private Sidebar sidebar;
    private JPanel contentArea;
    private CardLayout contentCardLayout;

    public MainPanel(Runnable onLogout) {
        setLayout(new BorderLayout());

        sidebar = new Sidebar(this::changeView, onLogout);
        add(sidebar, BorderLayout.WEST);

        contentCardLayout = new CardLayout();
        contentArea = new JPanel(contentCardLayout);

        contentArea.add(createPlaceholderPanel("Ventas"), "ventas");
        contentArea.add(new vwProduct(), "productos");
        contentArea.add(new vwCategory(), "categorías");
        contentArea.add(createPlaceholderPanel("Promociones"), "promociones");
        contentArea.add(new vwUser(), "usuarios");
        contentArea.add(new vwClient(), "clientes");

        add(contentArea, BorderLayout.CENTER);

        contentCardLayout.show(contentArea, "productos");
    }

    private void changeView(String viewName) {
        contentCardLayout.show(contentArea, viewName);
    }

    private JPanel createPlaceholderPanel(String titulo) {
        JPanel panel = new JPanel(new GridBagLayout());
        JLabel label = new JLabel("Vista de " + titulo);
        label.setFont(new Font("Arial", Font.BOLD, 24));
        panel.add(label);
        return panel;
    }
}