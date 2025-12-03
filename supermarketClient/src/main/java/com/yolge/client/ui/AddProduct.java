package com.yolge.client.ui;

import com.formdev.flatlaf.FlatClientProperties;
import com.yolge.client.dto.PageResponse;
import com.yolge.client.dto.category.CategoryResponse;
import com.yolge.client.dto.product.ProductRequest;
import com.yolge.client.dto.product.ProductResponse;
import com.yolge.client.exceptions.ApiException;
import com.yolge.client.exceptions.ApiValidationException;
import com.yolge.client.service.CategoryService;
import com.yolge.client.service.ProductService;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Objects;

public class AddProduct extends JPanel {

    private JTextField txtName;
    private JTextField txtBrand;
    private JTextField txtBasePrice;
    private JTextField txtTax;
    private JComboBox<CategoryResponse> cbCategory;
    private JSpinner spStock;
    private JLabel lbTitle;

    private JLabel lbErrName;
    private JLabel lbErrBrand;
    private JLabel lbErrBasePrice;
    private JLabel lbErrTax;
    private JLabel lbErrCategory;
    private JLabel lbErrStock;

    private JButton btnSave;
    private JButton btnCancel;

    private Long editingProductId = null;

    private final Runnable onClose;
    private final ProductService productService;
    private final CategoryService categoryService;

    public AddProduct(Runnable onClose) {
        this.onClose = onClose;
        this.productService = ProductService.getInstance();
        this.categoryService = CategoryService.getInstance();
        init();
        loadCategories(null);
    }

    public AddProduct(Runnable onClose, ProductResponse productToEdit) {
        this(onClose);
        this.editingProductId = productToEdit.getId();

        lbTitle.setText("Editar Producto #" + productToEdit.getId());
        btnSave.setText("Actualizar Producto");

        txtName.setText(productToEdit.getName());
        txtBrand.setText(productToEdit.getBrand());
        txtBasePrice.setText(String.valueOf(productToEdit.getBasePrice()));
        txtTax.setText(String.valueOf(productToEdit.getTaxPercentage()));
        spStock.setValue(productToEdit.getStock());

        loadCategories(productToEdit.getCategory().getId());
    }

    private void init() {
        setLayout(new MigLayout("wrap 2, fillx, insets 25 35 25 35, hidemode 3", "[trail]15[fill, 300]"));

        lbTitle = new JLabel("Crear nuevo producto");
        lbTitle.putClientProperty(FlatClientProperties.STYLE, "font:bold +10");
        add(lbTitle, "span 2, align center, gapbottom 20");


        // Nombre
        add(new JLabel("Nombre:"));
        txtName = new JTextField();
        add(txtName);
        lbErrName = createErrorLabel();
        add(lbErrName, "skip 1, wrap");

        // Marca
        add(new JLabel("Marca:"));
        txtBrand = new JTextField();
        add(txtBrand);
        lbErrBrand = createErrorLabel();
        add(lbErrBrand, "skip 1, wrap");

        // Precio Base
        add(new JLabel("Precio Base:"));
        txtBasePrice = new JTextField();
        txtBasePrice.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "0.00");
        add(txtBasePrice);
        lbErrBasePrice = createErrorLabel();
        add(lbErrBasePrice, "skip 1, wrap");

        // Impuesto
        add(new JLabel("Impuesto (%):"));
        txtTax = new JTextField("16.0");
        add(txtTax);
        lbErrTax = createErrorLabel();
        add(lbErrTax, "skip 1, wrap");

        // Categoría
        add(new JLabel("Categoría:"));
        cbCategory = new JComboBox<>();
        cbCategory.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof CategoryResponse) {
                    setText(((CategoryResponse) value).getName());
                }
                return this;
            }
        });
        add(cbCategory);
        lbErrCategory = createErrorLabel();
        add(lbErrCategory, "skip 1, wrap");

        // Stock
        add(new JLabel("Stock Inicial:"));
        spStock = new JSpinner(new SpinnerNumberModel(0, 0, 99999, 1));
        add(spStock);
        lbErrStock = createErrorLabel();
        add(lbErrStock, "skip 1, wrap");

        addButtons();
    }

    private void addButtons() {
        add(new JSeparator(), "span 2, growx, gaptop 15, gapbottom 15");

        btnCancel = new JButton("Cancelar");
        btnCancel.addActionListener(e -> onClose.run());

        btnSave = new JButton("Guardar Producto");
        btnSave.putClientProperty(FlatClientProperties.STYLE, "background:@accentColor; foreground:#fff; font:bold");
        btnSave.addActionListener(e -> saveAction());

        JPanel buttonPanel = new JPanel(new MigLayout("insets 0", "push[][]"));
        buttonPanel.add(btnCancel);
        buttonPanel.add(btnSave);

        add(buttonPanel, "span 2, growx");
    }

    private void loadCategories(Long selectedCatId) {
        cbCategory.setEnabled(false);
        new Thread(() -> {
            try {
                PageResponse<CategoryResponse> response = categoryService.getAllCategories();
                SwingUtilities.invokeLater(() -> {
                    cbCategory.removeAllItems();
                    if (response.getContent() != null) {
                        for (CategoryResponse cat : response.getContent()) {
                            cbCategory.addItem(cat);
                            if (selectedCatId != null && Objects.equals(cat.getId(), selectedCatId)) {
                                cbCategory.setSelectedItem(cat);
                            }
                        }
                    }
                    cbCategory.setEnabled(true);
                    if (selectedCatId == null) cbCategory.setSelectedIndex(-1);
                });
            } catch (Exception e) { /* ... */ }
        }).start();
    }

    private void saveAction() {
        ProductRequest request;
        try {
            String name = txtName.getText().trim();
            String brand = txtBrand.getText().trim();
            Double price = Double.parseDouble(txtBasePrice.getText().trim());
            Double tax = Double.parseDouble(txtTax.getText().trim());
            Integer stock = (Integer) spStock.getValue();
            CategoryResponse selectedCat = (CategoryResponse) cbCategory.getSelectedItem();

            if (selectedCat == null) { JOptionPane.showMessageDialog(this, "Seleccione categoría"); return; }

            request = new ProductRequest(name, brand, price, tax, selectedCat.getId(), stock);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Revise los números");
            return;
        }

        cleanErrors();
        loading(true);

        new Thread(() -> {
            try {
                if (editingProductId == null) {
                    productService.createProduct(request);
                } else {
                    productService.updateProduct(editingProductId, request);
                }

                SwingUtilities.invokeLater(() -> {
                    loading(false);
                    JOptionPane.showMessageDialog(this, editingProductId == null ? "Creado con éxito" : "Actualizado con éxito");
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
        btnCancel.setEnabled(!loading);
        txtName.setEnabled(!loading);
        txtBasePrice.setEnabled(!loading);
        btnSave.setText(loading ? "Guardando..." : "Guardar Producto");
    }

    private void cleanErrors() {
        lbErrName.setVisible(false);
        lbErrBrand.setVisible(false);
        lbErrBasePrice.setVisible(false);
        lbErrTax.setVisible(false);
        lbErrCategory.setVisible(false);
        lbErrStock.setVisible(false);

        txtName.putClientProperty(FlatClientProperties.OUTLINE, null);
        txtBrand.putClientProperty(FlatClientProperties.OUTLINE, null);
        txtBasePrice.putClientProperty(FlatClientProperties.OUTLINE, null);
        txtTax.putClientProperty(FlatClientProperties.OUTLINE, null);
        cbCategory.putClientProperty(FlatClientProperties.OUTLINE, null);
        spStock.putClientProperty(FlatClientProperties.OUTLINE, null);
    }

    private void showValidationErrors(List<String> errors) {
        for (String errorRaw : errors) {
            String[] partes = errorRaw.split(":", 2);
            if (partes.length < 2) continue;

            String campo = partes[0].trim().toLowerCase();
            String mensaje = partes[1].trim();

            switch (campo) {
                case "name":
                    showError(lbErrName, txtName, mensaje);
                    break;
                case "brand":
                    showError(lbErrBrand, txtBrand, mensaje);
                    break;
                case "baseprice":
                    showError(lbErrBasePrice, txtBasePrice, mensaje);
                    break;
                case "taxpercentage":
                    showError(lbErrTax, txtTax, mensaje);
                    break;
                case "categoryId":
                case "category":
                    showError(lbErrCategory, cbCategory, mensaje);
                    break;
                case "stock":
                    showError(lbErrStock, spStock, mensaje);
                    break;
            }
        }
        revalidate();
        repaint();
    }

    private void showError(JLabel label, JComponent component, String mensaje) {
        label.setText(mensaje);
        label.setVisible(true);
        component.putClientProperty(FlatClientProperties.OUTLINE, "error");
    }
}