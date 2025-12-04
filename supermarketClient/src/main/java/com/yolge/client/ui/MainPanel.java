package com.yolge.client.ui;

import com.yolge.client.core.RestClient;

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

        RestClient client = RestClient.getInstance();

        contentArea.add(new vwProduct(), "productos");

        if (client.isAdmin()) {
            contentArea.add(new vwCategory(), "categorías");
            contentArea.add(new vwPromotion(), "promociones");
            contentArea.add(new vwUser(), "usuarios");
            contentArea.add(new vwClient(), "clientes");
            contentArea.add(new vwSale(), "ventas");
        } else if (client.isCashier()) {
            contentArea.add(new vwClient(), "clientes");
            contentArea.add(new vwSale(), "ventas");
        } else if (client.isInventory()) {
            contentArea.add(new vwCategory(), "categorías");
            contentArea.add(new vwPromotion(), "promociones");
        }

        add(contentArea, BorderLayout.CENTER);

        contentCardLayout.show(contentArea, "productos");
    }

    private void changeView(String viewName) {
        contentCardLayout.show(contentArea, viewName);
    }
}