package com.yolge.client.login;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.yolge.client.core.RestClient;
import com.yolge.client.exceptions.ApiException;
import com.yolge.client.exceptions.ApiValidationException;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.util.List;

public class LoginForm extends JPanel {
    private JTextField tfUsername;
    private JPasswordField tfPassword;
    private JLabel lbErrUsername;
    private JLabel lbErrPassword;
    private JButton cmdLogin;

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
        add(subtitle, "gapy 3 12");
    }

    private void addUsernameField() {
        var lbUsername = new JLabel("Usuario");
        lbUsername.putClientProperty(FlatClientProperties.STYLE, "" + "font:bold;");
        add(lbUsername, "gapy 5 6");

        lbErrUsername = createLabelError();
        add(lbErrUsername, "gapy 0 6, hidemode 3");

        tfUsername = new JTextField();
        tfUsername.putClientProperty(FlatClientProperties.STYLE, "" + "iconTextGap:10;");
        tfUsername.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Nate Higgerson");
        tfUsername.putClientProperty(FlatClientProperties.TEXT_FIELD_LEADING_ICON, new FlatSVGIcon("login/user.svg"));
        add(tfUsername, "gapy 0 10");
    }

    private void addPasswordField() {
        var lbPassword = new JLabel("Contraseña");
        lbPassword.putClientProperty(FlatClientProperties.STYLE, "" + "font:bold;");
        add(lbPassword, "gapy 5 6");

        lbErrPassword = createLabelError();
        add(lbErrPassword, "gapy 0 6, hidemode 3");

        tfPassword = new JPasswordField();
        tfPassword.putClientProperty(FlatClientProperties.STYLE, "" + "iconTextGap:10;" + "showRevealButton:true;");
        tfPassword.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "••••••");
        tfPassword.putClientProperty(FlatClientProperties.TEXT_FIELD_LEADING_ICON, new FlatSVGIcon("login/lock.svg"));
        add(tfPassword, "gapy 0 5");
    }

    private void addLoginButton() {
        cmdLogin = new JButton("Iniciar sesión", new FlatSVGIcon("login/login.svg")) {
            @Override
            public boolean isDefaultButton() {
                return true;
            }
        };
        cmdLogin.setHorizontalTextPosition(JButton.LEADING);
        cmdLogin.putClientProperty(FlatClientProperties.STYLE, "" + "foreground:#FFFFFF;" + "IconTextGap:10;");
        cmdLogin.addActionListener(e -> login());
        add(cmdLogin, "gapy 10 10");
    }

    private void login() {
        String usuario = tfUsername.getText().trim();
        String pass = new String(tfPassword.getPassword());

        cleanErrors();
        loading(true);

        new Thread(() -> {
            try {
                RestClient.getInstance().login(usuario, pass);

                SwingUtilities.invokeLater(() -> {
                    loading(false);
                    JOptionPane.showMessageDialog(this, "¡Login Correcto!");
                    // AQUÍ VA EL CAMBIO DE PANTALLA
                });

            } catch (ApiValidationException ve) {
                SwingUtilities.invokeLater(() -> {
                    loading(false);
                    showValidationErrors(ve.getErrors());
                });

            } catch (ApiException ae) {
                SwingUtilities.invokeLater(() -> {
                    loading(false);
                    showError(ae.getMessage());
                });

            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    loading(false);
                    JOptionPane.showMessageDialog(this, "Error crítico: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                });
            }
        }).start();
    }

    private JLabel createLabelError() {
        JLabel label = new JLabel();
        label.setVisible(false);
        label.putClientProperty(FlatClientProperties.STYLE, "foreground:#FF4545; font: -1");
        return label;
    }

    private void loading(boolean activo) {
        cmdLogin.setEnabled(!activo);
        tfUsername.setEnabled(!activo);
        tfPassword.setEnabled(!activo);
        if(activo) cmdLogin.setText("Cargando...");
        else cmdLogin.setText("Iniciar sesión");
    }

    private void cleanErrors() {
        lbErrUsername.setVisible(false);
        lbErrPassword.setVisible(false);

        tfUsername.putClientProperty(FlatClientProperties.OUTLINE, null);
        tfPassword.putClientProperty(FlatClientProperties.OUTLINE, null);
    }

    private void showValidationErrors(List<String> errors) {
        for (String errorRaw : errors) {
            String[] partes = errorRaw.split(":", 2);

            if (partes.length < 2) continue;

            String campo = partes[0].trim().toLowerCase();
            String mensaje = partes[1].trim();

            switch (campo) {
                case "username":
                    lbErrUsername.setText(mensaje);
                    lbErrUsername.setVisible(true);
                    tfUsername.putClientProperty(FlatClientProperties.OUTLINE, "error");
                    break;
                case "password":
                    lbErrPassword.setText(mensaje);
                    lbErrPassword.setVisible(true);
                    tfPassword.putClientProperty(FlatClientProperties.OUTLINE, "error");
                    break;
            }
        }

        revalidate();
        repaint();
    }

    private void showError(String mensaje) {
        tfUsername.putClientProperty(FlatClientProperties.OUTLINE, "error");
        tfPassword.putClientProperty(FlatClientProperties.OUTLINE, "error");

        lbErrUsername.setText(mensaje);
        lbErrUsername.setVisible(true);
    }
}
