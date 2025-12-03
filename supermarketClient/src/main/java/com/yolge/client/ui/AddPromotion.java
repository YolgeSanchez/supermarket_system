package com.yolge.client.ui;

import com.formdev.flatlaf.FlatClientProperties;
import com.yolge.client.dto.PageResponse;
import com.yolge.client.dto.product.ProductResponse;
import com.yolge.client.dto.promotion.PromotionRequest;
import com.yolge.client.exceptions.ApiValidationException;
import com.yolge.client.service.ProductService;
import com.yolge.client.service.PromotionService;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class AddPromotion extends JPanel {

    private JTextField txtName;
    private JTextField txtDiscount;
    private JTextField txtStartDate;
    private JTextField txtEndDate;
    
    private JList<ProductResponse> listProducts;
    private DefaultListModel<ProductResponse> productModel;

    private JLabel lbErrName, lbErrDiscount, lbErrStartDate, lbErrEndDate, lbErrProducts;
    private JButton btnSave, btnCancel;

    private final Runnable onClose;
    private final PromotionService promotionService;
    private final ProductService productService;

    public AddPromotion(Runnable onClose) {
        this.onClose = onClose;
        this.promotionService = PromotionService.getInstance();
        this.productService = ProductService.getInstance();
        init();
        loadProducts();
    }

    private void init() {
        setLayout(new MigLayout("wrap 2, fillx, insets 25 35 25 35, hidemode 3", "[trail]15[fill, 300]"));

        JLabel lbTitle = new JLabel("Nueva Promoción");
        lbTitle.putClientProperty(FlatClientProperties.STYLE, "font:bold +10");
        add(lbTitle, "span 2, align center, gapbottom 20");

        // Nombre
        add(new JLabel("Nombre Promoción:"));
        txtName = new JTextField();
        add(txtName);
        lbErrName = createErrorLabel();
        add(lbErrName, "skip 1, wrap");

        // Descuento
        add(new JLabel("Descuento (%):"));
        txtDiscount = new JTextField();
        txtDiscount.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Ej: 15.0");
        add(txtDiscount);
        lbErrDiscount = createErrorLabel();
        add(lbErrDiscount, "skip 1, wrap");

        // Fechas (Start - End)
        add(new JLabel("Fecha Inicio:"));
        txtStartDate = new JTextField();
        txtStartDate.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "YYYY-MM-DD");
        add(txtStartDate);
        lbErrStartDate = createErrorLabel();
        add(lbErrStartDate, "skip 1, wrap");

        add(new JLabel("Fecha Fin:"));
        txtEndDate = new JTextField();
        txtEndDate.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "YYYY-MM-DD");
        add(txtEndDate);
        lbErrEndDate = createErrorLabel();
        add(lbErrEndDate, "skip 1, wrap");

        // Selección de Productos (Multi-select List)
        add(new JLabel("Productos:"), "top");
        
        productModel = new DefaultListModel<>();
        listProducts = new JList<>(productModel);
        listProducts.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        listProducts.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof ProductResponse) {
                    setText(((ProductResponse) value).getName());
                }
                return this;
            }
        });

        JScrollPane scrollProducts = new JScrollPane(listProducts);
        scrollProducts.putClientProperty(FlatClientProperties.STYLE, "border:1,1,1,1,#cccccc");
        add(scrollProducts, "height 150!");
        
        lbErrProducts = createErrorLabel();
        add(lbErrProducts, "skip 1, wrap");
        
        JLabel help = new JLabel("Mantenga presionado Ctrl para seleccionar varios productos.");
        help.putClientProperty(FlatClientProperties.STYLE, "font: -2; foreground:#cccccc;");
        add(help, "skip 1, wrap");

        addButtons();
    }

    private void addButtons() {
        add(new JSeparator(), "span 2, growx, gaptop 15, gapbottom 15");

        btnCancel = new JButton("Cancelar");
        btnCancel.addActionListener(e -> onClose.run());

        btnSave = new JButton("Guardar Promoción");
        btnSave.putClientProperty(FlatClientProperties.STYLE, "background:@accentColor; foreground:#fff; font:bold");
        btnSave.addActionListener(e -> saveAction());

        JPanel buttonPanel = new JPanel(new MigLayout("insets 0", "push[][]"));
        buttonPanel.add(btnCancel);
        buttonPanel.add(btnSave);
        add(buttonPanel, "span 2, growx");
    }

    private void loadProducts() {
        new Thread(() -> {
            try {
                PageResponse<ProductResponse> response = productService.getProducts(0, 1000);
                SwingUtilities.invokeLater(() -> {
                    if (response.getContent() != null) {
                        for (ProductResponse p : response.getContent()) {
                            productModel.addElement(p);
                        }
                    }
                });
            } catch (Exception e) { /* Ignorar errores de carga silenciosamente o loggear */ }
        }).start();
    }

    private void saveAction() {
        try {
            PromotionRequest request = new PromotionRequest();
            request.setName(txtName.getText().trim());
            
            String discountStr = txtDiscount.getText().trim();
            if (!discountStr.isEmpty()) {
                request.setDiscountPercentage(Double.parseDouble(discountStr));
            }
            
            request.setStartDate(txtStartDate.getText().trim());
            request.setEndDate(txtEndDate.getText().trim());

            List<ProductResponse> selected = listProducts.getSelectedValuesList();
            if (selected.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Debe seleccionar al menos un producto.");
                return;
            }
            
            Long[] ids = selected.stream()
                    .map(ProductResponse::getId)
                    .toArray(Long[]::new);
            request.setProductIds(ids);

            cleanErrors();
            loading(true);

            new Thread(() -> {
                try {
                    promotionService.createPromotion(request);
                    SwingUtilities.invokeLater(() -> {
                        loading(false);
                        JOptionPane.showMessageDialog(this, "Promoción creada exitosamente");
                        onClose.run();
                    });
                } catch (ApiValidationException ve) {
                    SwingUtilities.invokeLater(() -> { loading(false); showValidationErrors(ve.getErrors()); });
                } catch (Exception e) {
                    SwingUtilities.invokeLater(() -> { loading(false); JOptionPane.showMessageDialog(this, "Error: " + e.getMessage()); });
                }
            }).start();

        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this, "El descuento debe ser un número válido.");
        }
    }

    private void loading(boolean l) {
        btnSave.setEnabled(!l);
        txtName.setEnabled(!l);
        listProducts.setEnabled(!l);
        btnSave.setText(l ? "Guardando..." : "Guardar Promoción");
    }
    
    private void cleanErrors() {
        lbErrName.setVisible(false); lbErrDiscount.setVisible(false);
        lbErrStartDate.setVisible(false); lbErrEndDate.setVisible(false); lbErrProducts.setVisible(false);
        txtName.putClientProperty(FlatClientProperties.OUTLINE, null);
    }

    private JLabel createErrorLabel() {
        JLabel label = new JLabel();
        label.setVisible(false);
        label.putClientProperty(FlatClientProperties.STYLE, "foreground:#FF4545; font: -1");
        return label;
    }

    private void showValidationErrors(List<String> errors) {
        for (String errorRaw : errors) {
            String[] parts = errorRaw.split(":", 2);
            if (parts.length < 2) continue;
            String field = parts[0].trim().toLowerCase();
            String msg = parts[1].trim();
            
            switch (field) {
                case "name": showError(lbErrName, txtName, msg); break;
                case "discountpercentage": showError(lbErrDiscount, txtDiscount, msg); break;
                case "startdate": showError(lbErrStartDate, txtStartDate, msg); break;
                case "enddate": showError(lbErrEndDate, txtEndDate, msg); break;
                case "productids": 
                    lbErrProducts.setText(msg); 
                    lbErrProducts.setVisible(true); 
                    break;
            }
        }
    }

    private void showError(JLabel l, JComponent c, String m) {
        l.setText(m); l.setVisible(true); c.putClientProperty(FlatClientProperties.OUTLINE, "error");
    }
}