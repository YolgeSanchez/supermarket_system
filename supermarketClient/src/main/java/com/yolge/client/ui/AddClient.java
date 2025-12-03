package com.yolge.client.ui;

import com.formdev.flatlaf.FlatClientProperties;
import com.yolge.client.dto.client.ClientRequest;
import com.yolge.client.dto.client.ClientResponse;
import com.yolge.client.exceptions.ApiValidationException;
import com.yolge.client.service.ClientService;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.util.List;

public class AddClient extends JPanel {

    private JTextField txtName;
    private JTextField txtDni;
    private JTextField txtEmail;
    private JLabel lbTitle;

    private JLabel lbErrName;
    private JLabel lbErrDni;
    private JLabel lbErrEmail;

    private JButton btnSave;
    private JButton btnCancel;

    private Long editingClientId = null;
    private final Runnable onClose;
    private final ClientService clientService;

    public AddClient(Runnable onClose) {
        this.onClose = onClose;
        this.clientService = ClientService.getInstance();
        init();
    }

    public AddClient(Runnable onClose, ClientResponse clientToEdit) {
        this(onClose);
        this.editingClientId = clientToEdit.getId();

        lbTitle.setText("Editar Cliente #" + clientToEdit.getId());
        btnSave.setText("Actualizar Cliente");

        txtName.setText(clientToEdit.getName());
        txtDni.setText(clientToEdit.getDni());
        txtEmail.setText(clientToEdit.getEmail());
    }

    private void init() {
        setLayout(new MigLayout("wrap 2, fillx, insets 25 35 25 35, hidemode 3", "[trail]15[fill, 300]"));

        lbTitle = new JLabel("Registrar Nuevo Cliente");
        lbTitle.putClientProperty(FlatClientProperties.STYLE, "font:bold +10");
        add(lbTitle, "span 2, align center, gapbottom 20");

        // Nombre
        add(new JLabel("Nombre Completo:"));
        txtName = new JTextField();
        add(txtName);
        lbErrName = createErrorLabel();
        add(lbErrName, "skip 1, wrap");

        // DNI
        add(new JLabel("DNI / Cédula:"));
        txtDni = new JTextField();
        add(txtDni);
        lbErrDni = createErrorLabel();
        add(lbErrDni, "skip 1, wrap");

        // Email
        add(new JLabel("Correo Electrónico:"));
        txtEmail = new JTextField();
        add(txtEmail);
        lbErrEmail = createErrorLabel();
        add(lbErrEmail, "skip 1, wrap");

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
        ClientRequest request = new ClientRequest();
        request.setName(txtName.getText().trim());
        request.setDni(txtDni.getText().trim());
        request.setEmail(txtEmail.getText().trim());

        cleanErrors();
        loading(true);

        new Thread(() -> {
            try {
                if (editingClientId == null) {
                    clientService.createClient(request);
                } else {
                    clientService.updateClient(editingClientId, request);
                }

                SwingUtilities.invokeLater(() -> {
                    loading(false);
                    JOptionPane.showMessageDialog(this, editingClientId == null ? "Cliente registrado" : "Cliente actualizado");
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
        txtName.setEnabled(!loading);
        txtDni.setEnabled(!loading);
        txtEmail.setEnabled(!loading);
        btnSave.setText(loading ? "Guardando..." : (editingClientId == null ? "Guardar" : "Actualizar"));
    }

    private void cleanErrors() {
        lbErrName.setVisible(false);
        lbErrDni.setVisible(false);
        lbErrEmail.setVisible(false);
        txtName.putClientProperty(FlatClientProperties.OUTLINE, null);
        txtDni.putClientProperty(FlatClientProperties.OUTLINE, null);
        txtEmail.putClientProperty(FlatClientProperties.OUTLINE, null);
    }

    private void showValidationErrors(List<String> errors) {
        for (String errorRaw : errors) {
            String[] partes = errorRaw.split(":", 2);
            if (partes.length < 2) continue;
            String field = partes[0].trim().toLowerCase();
            String message = partes[1].trim();

            switch (field) {
                case "name": showError(lbErrName, txtName, message); break;
                case "dni": showError(lbErrDni, txtDni, message); break;
                case "email": showError(lbErrEmail, txtEmail, message); break;
            }
        }
    }

    private void showError(JLabel label, JComponent component, String mensaje) {
        label.setText(mensaje);
        label.setVisible(true);
        component.putClientProperty(FlatClientProperties.OUTLINE, "error");
    }
}