package com.yolge.client.ui;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.yolge.client.core.RestClient;
import com.yolge.client.dto.PageResponse;
import com.yolge.client.dto.product.ProductResponse;
import com.yolge.client.exceptions.ApiException;
import com.yolge.client.service.ProductService;
import net.miginfocom.swing.MigLayout;
import raven.swingpack.JPagination;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;

public class vwProduct extends JPanel {
    private JTable tbProduct;
    private DefaultTableModel products;
    private JTextField tfSearch;
    private JButton btnAdd, btnEdit, btnDelete;
    private JPagination pagination;

    private final ProductService productService;
    private final RestClient client;
    private int currentPage = 0;
    private int pageSize = 11;
    private String currentSearchTerm = "";

    public vwProduct() {
        this.productService = ProductService.getInstance();
        this.client = RestClient.getInstance();
        setLayout(new MigLayout("wrap, fill, insets 70", "[fill]", "push[grow 0][fill][]push"));
        addTable();
        addTopBar();
        addPagination();
        loadProducts();
    }

    private void addTable() {
        String[] columnNames = {"ID", "Nombre", "Marca", "Categoría", "Stock", "Precio Final"};

        products = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                switch (columnIndex) {
                    case 0: return Long.class;
                    case 4: return Integer.class;
                    case 1, 2, 3: return String.class;
                    case 5: return Double.class;
                    default: return Object.class;
                }
            }
        };

        tbProduct = new JTable(products);
        styleTable();
        JScrollPane scrollPane = new JScrollPane(tbProduct);
        add(scrollPane, "cell 0 1");
    }

    private void styleTable() {
        javax.swing.table.DefaultTableCellRenderer leftRenderer = new javax.swing.table.DefaultTableCellRenderer();
        leftRenderer.setHorizontalAlignment(javax.swing.JLabel.LEFT);

        for (int i = 0; i < tbProduct.getColumnCount(); i++) {
            tbProduct.getColumnModel().getColumn(i).setCellRenderer(leftRenderer);
            tbProduct.getColumnModel().getColumn(i).setHeaderRenderer(leftRenderer);
        }

        tbProduct.getTableHeader().setReorderingAllowed(false);
        tbProduct.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tbProduct.setRowHeight(34);
        tbProduct.setShowHorizontalLines(true);
        tbProduct.setIntercellSpacing(new Dimension(0, 1));

        tbProduct.putClientProperty(FlatClientProperties.STYLE, "" +
                "selectionBackground:fade(@accentColor,35%);" +
                "selectionForeground:@foreground;" +
                "selectionInactiveBackground:darken(@background,2%);" +
                "rowHeight:34");

        JTableHeader tbhProduct = tbProduct.getTableHeader();
        tbhProduct.putClientProperty(FlatClientProperties.STYLE, "" +
                "height:38;" +
                "font:semibold;" +
                "background:darken(@background, 5%);" +
                "foreground:lighten(@foreground,20%);" +
                "separatorColor:@background;" +
                "bottomSeparatorColor:darken(@background,5%)");

        tbProduct.getColumnModel().getColumn(0).setPreferredWidth(30);
        tbProduct.getColumnModel().getColumn(1).setPreferredWidth(250);
        tbProduct.getColumnModel().getColumn(2).setPreferredWidth(150);
        tbProduct.getColumnModel().getColumn(3).setPreferredWidth(200);
        tbProduct.getColumnModel().getColumn(4).setPreferredWidth(50);
        tbProduct.getColumnModel().getColumn(5).setPreferredWidth(125);

    }


    private JButton createStyledButton(String text, String iconPath, String accentColor) {
        JButton button = new JButton(text, new FlatSVGIcon(iconPath));

        button.putClientProperty(FlatClientProperties.STYLE, "" +
                "arc:8;" +
                "borderWidth:0;" +
                "focusWidth:0;" +
                "innerFocusWidth:0;" +
                "background:" + accentColor + ";" +
                "foreground:#FFFFFF;" +
                "hoverBackground:lighten(" + accentColor + ",8%);" +
                "pressedBackground:darken(" + accentColor + ",5%);" +
                "disabledBackground:lighten(@background,5%);" +
                "iconTextGap:8;" +
                "minimumHeight:30");

        button.setPreferredSize(new Dimension(120, 40));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return button;
    }


    private void addTopBar() {
        var pnlTopBar = new JPanel(new MigLayout("insets 0", "[350]push[][][]", "[grow]"));
        tfSearch = new JTextField();
        tfSearch.putClientProperty(FlatClientProperties.STYLE, "" + "iconTextGap:10;");
        tfSearch.putClientProperty(FlatClientProperties.TEXT_FIELD_LEADING_ICON, new FlatSVGIcon("util/search.svg"));
        tfSearch.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Buscar producto...");

        tfSearch.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) {
                searchProducts();
            }
            @Override public void removeUpdate(DocumentEvent e) {
                searchProducts();
            }
            @Override public void changedUpdate(DocumentEvent e) {
                searchProducts();
            }
        });

        btnAdd = createStyledButton("Agregar", "util/add.svg", "#10B981");
        btnAdd.addActionListener(e -> openAddProductModal());

        btnEdit = createStyledButton("Editar", "util/edit.svg", "#6B7280");
        btnEdit.addActionListener(e -> {
            int selectedRow = tbProduct.getSelectedRow();
            if (selectedRow != -1) {
                // Obtenemos el ID de la columna 0
                Long id = (Long) tbProduct.getValueAt(selectedRow, 0);
                editProduct(id.longValue());
            }
        });

        btnDelete = createStyledButton("Eliminar", "util/delete.svg", "#EF4444");
        btnDelete.addActionListener(e -> {
            int selectedRow = tbProduct.getSelectedRow();
            if (selectedRow != -1) {
                Long id = (Long) tbProduct.getValueAt(selectedRow, 0);
                String name = (String) tbProduct.getValueAt(selectedRow, 1);
                deleteProduct(id.longValue(), name);
            }
        });

        btnEdit.setEnabled(false);
        btnDelete.setEnabled(false);

        tbProduct.getSelectionModel().addListSelectionListener(e -> {
            boolean isSelected = tbProduct.getSelectedRow() != -1;
            btnEdit.setEnabled(isSelected);
            btnDelete.setEnabled(isSelected);
        });

        pnlTopBar.add(tfSearch, "growx");

        if (client.isAdmin()) {
            pnlTopBar.add(btnAdd);
            pnlTopBar.add(btnEdit);
            pnlTopBar.add(btnDelete);
        } else if (client.isInventory()) {
            pnlTopBar.add(btnAdd);
            pnlTopBar.add(btnEdit);
        }

        add(pnlTopBar, "cell 0 0");
    }

    private void addPagination() {
        pagination = new JPagination();

        pagination.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                currentPage = Math.max(0, pagination.getSelectedPage() - 1);
                loadProducts();
            }
        });

        add(pagination);
    }

    private void loadProducts() {
       setControlsEnabled(false);

       SwingWorker<PageResponse<ProductResponse>, Void> worker = new SwingWorker<>() {
           @Override
           protected PageResponse<ProductResponse> doInBackground() throws Exception {
               if (currentSearchTerm.isEmpty()) {
                   return productService.getProducts(currentPage, pageSize);
               } else {
                   return productService.searchByName(currentPage, pageSize, currentSearchTerm);
               }
           }

           @Override
           protected void done() {
               try {
                   PageResponse<ProductResponse> response = get();
                   displayProducts(response);
               } catch (Exception ex) {
                   handleError(ex);
               } finally {
                   setControlsEnabled(true);
               }
           }
       };

       worker.execute();
    }

    private void displayProducts(PageResponse<ProductResponse> response) {
        products.setRowCount(0);

        if (response.getContent() != null) {
            for (ProductResponse product : response.getContent()) {
                Object[] rowData = {
                        product.getId(),
                        product.getName(),
                        product.getBrand() != null ? product.getBrand() : "Sin marca",
                        product.getCategory().getName(),
                        product.getStock(),
                        product.getFinalPrice().doubleValue()
                };
                products.addRow(rowData);
            }
        }

        int totalPages = response.getTotalPages() > 0 ? response.getTotalPages() : 1;
        pagination.setPageRange(currentPage + 1, totalPages);
    }

    private void searchProducts() {
        String searchTerm = tfSearch.getText().trim();

        if (!searchTerm.equals(currentSearchTerm)) {
            currentSearchTerm = searchTerm;
            currentPage = 0;
            loadProducts();
        }
    }

    private void handleError(Exception ex) {
        String message = "Error al cargar productos";

        if (ex.getCause() instanceof ApiException) {
            ApiException apiEx = (ApiException) ex.getCause();
            message = apiEx.getMessage();
        } else if (ex instanceof ApiException) {
            message = ex.getMessage();
        }

        JOptionPane.showMessageDialog(
                this,
                message,
                "Error",
                JOptionPane.ERROR_MESSAGE
        );

        products.setRowCount(0);
        pagination.setPageRange(1, 1);
    }

    private void editProduct(Long id) {
        // 1. Mostrar un pequeño "Cargando..." o cursor de espera
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        new Thread(() -> {
            try {
                ProductResponse productFull = productService.getProductById(id);

                SwingUtilities.invokeLater(() -> {
                    setCursor(Cursor.getDefaultCursor());
                    openProductModal(productFull);
                });
            } catch (Exception ex) {
                SwingUtilities.invokeLater(() -> {
                    setCursor(Cursor.getDefaultCursor());
                    JOptionPane.showMessageDialog(this, "Error al cargar datos: " + ex.getMessage());
                });
            }
        }).start();
    }

    private void deleteProduct(Long id, String name) {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "¿Estás seguro de eliminar el producto: " + name + "?\nEsta acción no se puede deshacer.",
                "Confirmar Eliminación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            new Thread(() -> {
                try {
                    productService.deleteProduct(id);
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(this, "Producto eliminado correctamente.");
                        loadProducts();
                    });
                } catch (Exception ex) {
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(this, "Error al eliminar: " + ex.getMessage());
                    });
                }
            }).start();
        }
    }

    private void openAddProductModal() {
        openProductModal(null);
    }

    private void openProductModal(ProductResponse productToEdit) {
        Window window = SwingUtilities.getWindowAncestor(this);
        String title = (productToEdit == null) ? "Nuevo Producto" : "Editar Producto";

        final JDialog dialog = new JDialog(window != null ? (Frame) window : null, title, true);

        AddProduct formPanel;
        if (productToEdit == null) {
            formPanel = new AddProduct(() -> dialog.dispose());
        } else {
            formPanel = new AddProduct(() -> dialog.dispose(), productToEdit);
        }

        dialog.setContentPane(formPanel);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.pack();
        dialog.setLocationRelativeTo(window);
        dialog.setResizable(false);
        dialog.setVisible(true);

        loadProducts();
    }

    private void setControlsEnabled(boolean enabled) {
        btnAdd.setEnabled(enabled);
        pagination.setEnabled(enabled);
    }

}
