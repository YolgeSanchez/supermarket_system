package com.yolge;

import com.yolge.client.login.LoginForm;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;

public class App extends JFrame {
    public App() {
        setSize(1920, 1080);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setExtendedState(MAXIMIZED_BOTH);
        setLayout(new MigLayout("al center center"));
        add(new LoginForm());
    }
}
