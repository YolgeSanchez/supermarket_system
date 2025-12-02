package com.yolge.client.ui;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.yolge.client.core.RestClient;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

public class Sidebar extends JPanel {
    private Consumer<String> onNavigate;
    private Runnable onLogout;
    private String currentView = "productos";
    private JButton btnLogout;
    private JPanel navPanel;

    public Sidebar(Consumer<String> onNavigate, Runnable onLogout) {
        this.onNavigate = onNavigate;
        this.onLogout = onLogout;
        init();
    }

    private void init() {
        setPreferredSize(new Dimension(280, 0));
        setLayout(new MigLayout("wrap, fillx, insets 16", "[fill]", "[]16[]push[]push[]16[]"));
        putClientProperty(FlatClientProperties.STYLE, "" + "background:darken(@background,1%);");

        addUserInfo();
        addSeparator();

        addNavigationButtons();

        addSeparator();
        addLogoutButton();
    }

    private void addUserInfo() {
        JPanel userPanel = new JPanel(new MigLayout("fillx, insets 8", "[fill]"));
        userPanel.putClientProperty(FlatClientProperties.STYLE, "" + "background:darken(@background,1%);");

        String username = RestClient.getInstance().getUsername();
        String role = getRoleDisplay(RestClient.getInstance().getRole());

        JLabel lblUsername = new JLabel(username != null ? username : "Usuario", new FlatSVGIcon("login/user.svg").setColorFilter(new FlatSVGIcon.ColorFilter(color -> Color.BLACK)), JLabel.LEFT);
        lblUsername.putClientProperty(FlatClientProperties.STYLE, "" +
                "font:bold +3;" +
                "iconTextGap:8;" +
                "background:darken(@background,1%);");

        JLabel lblRole = new JLabel(role);
        lblRole.putClientProperty(FlatClientProperties.STYLE, "" +
                "font:italic -1;" +
                "foreground:lighten(@foreground,30%)");

        userPanel.add(lblUsername, "split 2");
        userPanel.add(lblRole);

        add(userPanel);
    }

    private void addSeparator() {
        JSeparator separator = new JSeparator();
        add(separator, "growx");
    }

    private void addNavigationButtons() {
        RestClient client = RestClient.getInstance();
        navPanel = new JPanel(new MigLayout("al left center, fillx, insets 0", "[fill]", "[]4[]4[]4[]4[]4[]4[]"));
        navPanel.putClientProperty(FlatClientProperties.STYLE, "" + "background:darken(@background,1%);");

        addNavButton("Productos", "productos", true);
        if (client.isAdmin()) {
            addNavButton("Categorías", "categorías", true);
            addNavButton("Promociones", "promociones", true);
            addNavButton("Usuarios", "usuarios", true);
            addNavButton("Clientes", "clientes", true);
            addNavButton("Ventas", "ventas", true);
        } else if (client.isCashier()) {
            addNavButton("Clientes", "clientes", true);
            addNavButton("Ventas", "ventas", true);
        } else if (client.isInventory()) {
            addNavButton("Categorías", "categorías", true);
            addNavButton("Promociones", "promociones", true);
        }

        add(navPanel);
    }

    private void addNavButton(String text, String viewName, boolean enabled) {
        JButton button = new JButton(text);
        button.setEnabled(enabled);
        button.setPreferredSize(new Dimension(0, 50));
        
        updateButtonStyle(button, viewName.equals(currentView));

        button.addActionListener(e -> {
            if (!viewName.equals(currentView)) {
                currentView = viewName;
                updateAllButtonStyles();
                
                if (onNavigate != null) {
                    onNavigate.accept(viewName);
                }
            }
        });

        navPanel.add(button, "wrap");
    }

    private void updateButtonStyle(JButton button, boolean isActive) {
        if (isActive) {
            button.putClientProperty(FlatClientProperties.STYLE, "" +
                    "background:lighten(@accentColor,15%);" +
                    "borderWidth:0;" +
                    "focusWidth:0;" +
                    "innerFocusWidth:0;" +
                    "arc:4");
        } else {
            button.putClientProperty(FlatClientProperties.STYLE, "" +
                    "background:darken(@background, 1%);" +
                    "borderWidth:0;" +
                    "focusWidth:0;" +
                    "innerFocusWidth:0;" +
                    "arc:4");
        }
    }

    private void updateAllButtonStyles() {
        for (Component comp : navPanel.getComponents()) {
            if (comp instanceof JButton) {
                JButton btn = (JButton) comp;
                if (btn == btnLogout) continue;
                String btnText = btn.getText().toLowerCase();
                updateButtonStyle(btn, btnText.equals(currentView));
            }
        }
    }

    private void addLogoutButton() {
        btnLogout = new JButton("Cerrar Sesión");
        btnLogout.setPreferredSize(new Dimension(0, 50));
        btnLogout.putClientProperty(FlatClientProperties.STYLE, "" +
                "foreground:#FF4545;" +
                "borderWidth:1;" +
                "borderColor:#FF4545;" +
                "background:null;" +
                "hoverBackground:#FF4545;" +
                "hoverForeground:#FFFFFF;" +
                "hoverBorderColor:#FF4545;" +
                "focusedBorderColor:#FF4545;" +
                "arc:8");

        btnLogout.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "¿Está seguro que desea cerrar sesión?",
                    "Cerrar Sesión",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
            );

            if (confirm == JOptionPane.YES_OPTION) {
                if (onLogout != null) {
                    onLogout.run();
                }
            }
        });

        add(btnLogout);
    }

    private String getRoleDisplay(String role) {
        if (role == null) return "Sin rol";
        
        switch (role) {
            case "ROLE_ADMIN":
            case "ADMIN":
                return "Administrador";
            case "ROLE_CASHIER":
            case "CASHIER":
                return "Cajero";
            case "ROLE_INVENTORY":
            case "INVENTORY":
                return "Inventario";
            default:
                return role;
        }
    }
}