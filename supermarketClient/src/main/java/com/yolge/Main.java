package com.yolge;

import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.themes.FlatMacLightLaf;

import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        FlatLaf.registerCustomDefaultsSource("app.themes");
        FlatMacLightLaf.setup();
        UIManager.put("defaultFont", new Font("Roboto", Font.PLAIN, 13));
        EventQueue.invokeLater(() ->  new App().setVisible(true));
    }
}