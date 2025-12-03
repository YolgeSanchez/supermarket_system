package com.yolge.client.ui;

import javax.swing.*;
import java.awt.*;

public class MainPanel extends JPanel {
    private Sidebar sidebar;
    private JPanel contentArea;
    private CardLayout contentCardLayout;

    public MainPanel(Runnable onLogout) {
        setLayout(new BorderLayout());

        // Crear el sidebar pasándole ambos callbacks:
        // - changeView: para navegar entre vistas internas
        // - onLogout: para volver al login
        sidebar = new Sidebar(this::changeView, onLogout);
        add(sidebar, BorderLayout.WEST);

        // Crear el área de contenido con CardLayout
        contentCardLayout = new CardLayout();
        contentArea = new JPanel(contentCardLayout);

        // Aquí irán todas las vistas/pantallas del sistema
        contentArea.add(createPlaceholderPanel("Ventas"), "ventas");
        contentArea.add(new vwProduct(), "productos");
        contentArea.add(createPlaceholderPanel("Categorías"), "categorías");
        contentArea.add(createPlaceholderPanel("Promociones"), "promociones");
        contentArea.add(createPlaceholderPanel("Usuarios"), "usuarios");
        contentArea.add(createPlaceholderPanel("Clientes"), "clientes");

        add(contentArea, BorderLayout.CENTER);

        // Mostrar la primera vista por defecto
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