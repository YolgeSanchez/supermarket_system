package com.yolge;

import com.yolge.client.core.RestClient;
import com.yolge.client.ui.MainPanel;
import com.yolge.client.ui.vwLogin;

import javax.swing.*;
import java.awt.*;

public class App extends JFrame {
    private CardLayout cardLayout;
    private JPanel cardContainer;

    public App() {
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        cardContainer = new JPanel(cardLayout);

        cardContainer.add("login", new vwLogin(this::onLogin));

        add(cardContainer);
        cardLayout.show(cardContainer, "login");
    }

    private void onLogin() {
        setExtendedState(MAXIMIZED_BOTH);
        cardContainer.add("main", new MainPanel(this::onLogout));
        cardLayout.show(cardContainer, "main");
    }

    private void onLogout() {
        setSize(800, 600);
        setLocationRelativeTo(null);
        RestClient.getInstance().logout();
        cardLayout.show(cardContainer, "login");
    }
}
