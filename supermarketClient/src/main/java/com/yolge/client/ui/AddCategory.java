package com.yolge.client.ui;

import com.formdev.flatlaf.FlatClientProperties;
import com.yolge.client.dto.category.CategoryRequest;
import com.yolge.client.dto.category.CategoryResponse;
import com.yolge.client.exceptions.ApiException;
import com.yolge.client.exceptions.ApiValidationException;
import com.yolge.client.service.CategoryService;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.util.List;

public class AddCategory extends JPanel {

    private JTextField txtName;
    private JTextArea txtDescription;
    private JLabel lbTitle;

    private JLabel lbErrName;
    private JLabel lbErrDescription;

    private JButton btnSave;
    private JButton btnCancel;

    private Long editingCategoryId = null;

    private final Runnable onClose;
    private final CategoryService categoryService;

    public AddCategory(Runnable onClose) {
        this.onClose = onClose;
        this.categoryService = CategoryService.getInstance();
        init();
    }

    public AddCategory(Runnable onClose, CategoryResponse categoryToEdit) {
        this(onClose);
        this.editingCategoryId = categoryToEdit.getId();

        lbTitle.setText("Editar Categoría #" + categoryToEdit.getId());
        btnSave.setText("Actualizar Categoría");

        txtName.setText(categoryToEdit.getName());
        txtDescription.setText(categoryToEdit.getDescription());
    }

    private void init() {
        setLayout(new MigLayout("wrap, fillx, insets 25 35 25 35, hidemode 3", "[fill, 350]"));

        lbTitle = new JLabel("Nueva Categoría");
        lbTitle.putClientProperty(FlatClientProperties.STYLE, "font:bold +10");
        add(lbTitle, "align center, gapbottom 20");

        // Nombre
        add(new JLabel("Nombre:"));
        txtName = new JTextField();
        add(txtName);
        lbErrName = createErrorLabel();
        add(lbErrName);

        // Descripción
        add(new JLabel("Descripción:"));
        txtDescription = new JTextArea(3, 20); // 3 filas de alto
        txtDescription.setLineWrap(true);
        txtDescription.setWrapStyleWord(true);

        JScrollPane scrollDesc = new JScrollPane(txtDescription);
        scrollDesc.putClientProperty(FlatClientProperties.STYLE, "border:1,1,1,1,#cccccc");
        add(scrollDesc);
        
        lbErrDescription = createErrorLabel();
        add(lbErrDescription);

        addButtons();
    }

    private void addButtons() {
        add(new JSeparator(), "gaptop 15, gapbottom 15");

        btnCancel = new JButton("Cancelar");
        btnCancel.addActionListener(e -> onClose.run());

        btnSave = new JButton("Guardar");
        btnSave.putClientProperty(FlatClientProperties.STYLE, "background:@accentColor; foreground:#fff; font:bold");
        btnSave.addActionListener(e -> saveAction());

        JPanel buttonPanel = new JPanel(new MigLayout("insets 0", "push[][]"));
        buttonPanel.add(btnCancel);
        buttonPanel.add(btnSave);

        add(buttonPanel);
    }

    private void saveAction() {
        CategoryRequest request = new CategoryRequest();
        request.setName(txtName.getText().trim());
        request.setDescription(txtDescription.getText().trim());

        cleanErrors();
        loading(true);

        new Thread(() -> {
            try {
                if (editingCategoryId == null) {
                    categoryService.createCategory(request);
                } else {
                    categoryService.updateCategory(editingCategoryId, request);
                }

                SwingUtilities.invokeLater(() -> {
                    loading(false);
                    JOptionPane.showMessageDialog(this, editingCategoryId == null ? "Categoría creada" : "Categoría actualizada");
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
        txtDescription.setEnabled(!loading);
        btnSave.setText(loading ? "Guardando..." : (editingCategoryId == null ? "Guardar" : "Actualizar"));
    }

    private void cleanErrors() {
        lbErrName.setVisible(false);
        lbErrDescription.setVisible(false);
        txtName.putClientProperty(FlatClientProperties.OUTLINE, null);
        txtDescription.putClientProperty(FlatClientProperties.OUTLINE, null);
    }

    private void showValidationErrors(List<String> errors) {
        for (String errorRaw : errors) {
            String[] partes = errorRaw.split(":", 2);
            if (partes.length < 2) continue;
            String campo = partes[0].trim().toLowerCase();
            String mensaje = partes[1].trim();

            if (campo.equals("name")) showError(lbErrName, txtName, mensaje);
            if (campo.equals("description")) showError(lbErrDescription, txtDescription, mensaje);
        }
    }

    private void showError(JLabel label, JComponent component, String mensaje) {
        label.setText(mensaje);
        label.setVisible(true);
        if (component instanceof JTextArea) {
             component.putClientProperty(FlatClientProperties.OUTLINE, "error");
        } else {
             component.putClientProperty(FlatClientProperties.OUTLINE, "error");
        }
    }
}