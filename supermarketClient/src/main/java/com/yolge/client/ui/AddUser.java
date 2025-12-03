package com.yolge.client.ui;

import com.formdev.flatlaf.FlatClientProperties;
import com.yolge.client.dto.user.RegisterRequest;
import com.yolge.client.dto.user.UpdateUserRequest;
import com.yolge.client.dto.user.UserResponse;
import com.yolge.client.exceptions.ApiValidationException;
import com.yolge.client.service.UserService;
import com.yolge.client.enums.Role;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.util.List;

public class AddUser extends JPanel {

    private JTextField txtFullName;
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JComboBox<Role> cbRole;
    private JLabel lbTitle;

    private JLabel lbErrFullName;
    private JLabel lbErrUsername;
    private JLabel lbErrPassword;
    private JLabel lbErrRole;

    private JButton btnSave;
    private JButton btnCancel;

    private Long editingUserId = null;
    private final Runnable onClose;
    private final UserService userService;

    public AddUser(Runnable onClose) {
        this.onClose = onClose;
        this.userService = UserService.getInstance();
        init();
    }

    public AddUser(Runnable onClose, UserResponse userToEdit) {
        this(onClose);
        this.editingUserId = userToEdit.getUserId();

        lbTitle.setText("Editar Usuario #" + userToEdit.getUserId());
        btnSave.setText("Actualizar Usuario");

        txtFullName.setText(userToEdit.getFullName());
        txtUsername.setText(userToEdit.getUsername());
        
        try {
            cbRole.setSelectedItem(Role.valueOf(userToEdit.getRole()));
        } catch (Exception e) {
        }

        txtUsername.setEnabled(false);
        txtPassword.setEnabled(false);
        txtPassword.setToolTipText("No se puede cambiar la contraseña al editar");
        cbRole.setEnabled(false);
    }

    private void init() {
        setLayout(new MigLayout("wrap 2, fillx, insets 25 35 25 35, hidemode 3", "[trail]15[fill, 300]"));

        lbTitle = new JLabel("Registrar Nuevo Usuario");
        lbTitle.putClientProperty(FlatClientProperties.STYLE, "font:bold +10");
        add(lbTitle, "span 2, align center, gapbottom 20");

        // Nombre Completo
        add(new JLabel("Nombre Completo:"));
        txtFullName = new JTextField();
        add(txtFullName);
        lbErrFullName = createErrorLabel();
        add(lbErrFullName, "skip 1, wrap");

        // Username
        add(new JLabel("Usuario:"));
        txtUsername = new JTextField();
        add(txtUsername);
        lbErrUsername = createErrorLabel();
        add(lbErrUsername, "skip 1, wrap");

        // Password
        add(new JLabel("Contraseña:"));
        txtPassword = new JPasswordField();
        txtPassword.putClientProperty(FlatClientProperties.STYLE, "showRevealButton:true");
        add(txtPassword);
        lbErrPassword = createErrorLabel();
        add(lbErrPassword, "skip 1, wrap");

        // Rol
        add(new JLabel("Rol:"));
        cbRole = new JComboBox<>(Role.values());
        add(cbRole);
        lbErrRole = createErrorLabel();
        add(lbErrRole, "skip 1, wrap");

        addButtons();
    }

    private void addButtons() {
        add(new JSeparator(), "span 2, growx, gaptop 15, gapbottom 15");

        btnCancel = new JButton("Cancelar");
        btnCancel.addActionListener(e -> onClose.run());

        btnSave = new JButton("Guardar");
        btnSave.putClientProperty(FlatClientProperties.STYLE, "background:@accentColor; foreground:#fff; font:bold");
        btnSave.addActionListener(e -> saveAction());

        JPanel buttonPanel = new JPanel(new MigLayout("insets 0", "push[][]"));
        buttonPanel.add(btnCancel);
        buttonPanel.add(btnSave);
        add(buttonPanel, "span 2, growx");
    }

    private void saveAction() {
        String fullName = txtFullName.getText().trim();
        
        cleanErrors();
        loading(true);

        new Thread(() -> {
            try {
                if (editingUserId == null) {
                    String username = txtUsername.getText().trim();
                    String password = new String(txtPassword.getPassword());
                    Role role = (Role) cbRole.getSelectedItem();
                    
                    if (role == null) throw new RuntimeException("Rol es requerido");

                    RegisterRequest req = new RegisterRequest(fullName, username, password, role.name());
                    userService.createUser(req);

                } else {
                    UpdateUserRequest req = new UpdateUserRequest();
                    req.setFullName(fullName);
                    userService.updateUser(editingUserId, req);
                }

                SwingUtilities.invokeLater(() -> {
                    loading(false);
                    JOptionPane.showMessageDialog(this, editingUserId == null ? "Usuario registrado" : "Usuario actualizado");
                    onClose.run();
                });

            } catch (ApiValidationException ve) {
                SwingUtilities.invokeLater(() -> { loading(false); showValidationErrors(ve.getErrors()); });
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> { loading(false); JOptionPane.showMessageDialog(this, "Error: " + e.getMessage()); });
            }
        }).start();
    }

    private JLabel createErrorLabel() {
        JLabel label = new JLabel();
        label.setVisible(false);
        label.putClientProperty(FlatClientProperties.STYLE, "foreground:#FF4545; font: -1");
        return label;
    }

    private void loading(boolean loading) {
        btnSave.setEnabled(!loading);
        txtFullName.setEnabled(!loading);
        if (editingUserId == null) {
            txtUsername.setEnabled(!loading);
            txtPassword.setEnabled(!loading);
            cbRole.setEnabled(!loading);
        }
        btnSave.setText(loading ? "Guardando..." : "Guardar");
    }

    private void cleanErrors() {
        lbErrFullName.setVisible(false);
        lbErrUsername.setVisible(false);
        lbErrPassword.setVisible(false);
        lbErrRole.setVisible(false);
        txtFullName.putClientProperty(FlatClientProperties.OUTLINE, null);
        txtUsername.putClientProperty(FlatClientProperties.OUTLINE, null);
        txtPassword.putClientProperty(FlatClientProperties.OUTLINE, null);
        cbRole.putClientProperty(FlatClientProperties.OUTLINE, null);
    }

    private void showValidationErrors(List<String> errors) {
        for (String errorRaw : errors) {
            String[] partes = errorRaw.split(":", 2);
            if (partes.length < 2) continue;
            String campo = partes[0].trim().toLowerCase();
            String mensaje = partes[1].trim();

            switch (campo) {
                case "fullname": showError(lbErrFullName, txtFullName, mensaje); break;
                case "username": showError(lbErrUsername, txtUsername, mensaje); break;
                case "password": showError(lbErrPassword, txtPassword, mensaje); break;
                case "role": showError(lbErrRole, cbRole, mensaje); break;
            }
        }
    }

    private void showError(JLabel label, JComponent component, String mensaje) {
        label.setText(mensaje);
        label.setVisible(true);
        component.putClientProperty(FlatClientProperties.OUTLINE, "error");
    }
}