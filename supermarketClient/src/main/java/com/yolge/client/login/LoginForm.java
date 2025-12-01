package com.yolge.client.login;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;

public class LoginForm extends JPanel {
    public LoginForm() {
        init();
    }

    private void init() {
        setSize(1920, 1080);
        setLayout(new MigLayout("wrap, gapy 3", "[fill, 300]"));

        addTitle();
        addSubtitle();
        addUsernameField();
        addPasswordField();
        addLoginButton();
    }

    private void addTitle() {
        var title = new JLabel("Bienvenido", JLabel.CENTER);
        title.putClientProperty(FlatClientProperties.STYLE, "" + "font:bold +15;");
        add(title, "gapy 8 8");
    }

    private void addSubtitle() {
        var subtitle = new JLabel("Por favor, inicie sesión para continuar", JLabel.CENTER);
        subtitle.putClientProperty(FlatClientProperties.STYLE, "" + "font:italic -1;");
        add(subtitle, "gapy 0 15");
    }

    private void addUsernameField() {
        var lbUsername = new JLabel("Usuario");
        lbUsername.putClientProperty(FlatClientProperties.STYLE, "" + "font:bold;");
        add(lbUsername, "gapy 10 5");

        var tfUsername = new JTextField();
        tfUsername.putClientProperty(FlatClientProperties.STYLE, "" + "iconTextGap:10;");
        tfUsername.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Nate Higgerson");
        tfUsername.putClientProperty(FlatClientProperties.TEXT_FIELD_LEADING_ICON, new FlatSVGIcon("login/user.svg"));
        add(tfUsername);
    }

    private void addPasswordField() {
        var lbPassword = new JLabel("Contraseña");
        lbPassword.putClientProperty(FlatClientProperties.STYLE, "" + "font:bold;");
        add(lbPassword, "gapy 10 5");

        var tfPassword = new JPasswordField();
        tfPassword.putClientProperty(FlatClientProperties.STYLE, "" + "iconTextGap:10;" + "showRevealButton:true;");
        tfPassword.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "••••••");
        tfPassword.putClientProperty(FlatClientProperties.TEXT_FIELD_LEADING_ICON, new FlatSVGIcon("login/lock.svg"));
        add(tfPassword);
    }

    private void addLoginButton() {
        var cmdLogin = new JButton("Iniciar sesión", new FlatSVGIcon("login/login.svg")) {
            @Override
            public boolean isDefaultButton() {
                return true;
            }
        };
        cmdLogin.setHorizontalTextPosition(JButton.LEADING);
        cmdLogin.putClientProperty(FlatClientProperties.STYLE, "" +
                "foreground:#FFFFFF;" +
                "IconTextGap:10;");
        add(cmdLogin, "gapy 10 5");
    }
}
