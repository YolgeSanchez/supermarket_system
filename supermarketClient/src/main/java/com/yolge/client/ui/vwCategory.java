package com.yolge.client.ui;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.yolge.client.core.RestClient;
import com.yolge.client.dto.PageResponse;
import com.yolge.client.dto.category.CategoryResponse;
import com.yolge.client.exceptions.ApiException;
import com.yolge.client.service.CategoryService;
import net.miginfocom.swing.MigLayout;
import raven.swingpack.JPagination;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;

public class vwCategory extends JPanel {

    private JTable tbCategory;
    private DefaultTableModel model;
    private JTextField tfSearch;
    private JButton btnAdd, btnEdit, btnDelete;
    private JPagination pagination;

    private final CategoryService categoryService;
    private final RestClient client;
    private int currentPage = 0;
    private int pageSize = 10;
    private String currentSearchTerm = "";

    public vwCategory() {
        this.categoryService = CategoryService.getInstance();
        this.client = RestClient.getInstance();
        
        setLayout(new MigLayout("wrap, fill, insets 70", "[fill]", "push[grow 0][fill][]push"));
        
        addTable();
        addTopBar();
        addPagination();
        loadCategories();

        this.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentShown(java.awt.event.ComponentEvent e) {
                loadCategories();
            }
        });
    }

    private void addTable() {
        String[] columnNames = {"ID", "Nombre", "Descripción", "Productos"};

        model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0) return Long.class;
                if (columnIndex == 3) return Integer.class;
                return String.class;
            }
        };

        tbCategory = new JTable(model);
        styleTable();
        add(new JScrollPane(tbCategory), "cell 0 1");
    }

    private void styleTable() {
        javax.swing.table.DefaultTableCellRenderer leftRenderer = new javax.swing.table.DefaultTableCellRenderer();
        leftRenderer.setHorizontalAlignment(javax.swing.JLabel.LEFT);

        for (int i = 0; i < tbCategory.getColumnCount(); i++) {
            tbCategory.getColumnModel().getColumn(i).setCellRenderer(leftRenderer);
            tbCategory.getColumnModel().getColumn(i).setHeaderRenderer(leftRenderer);
        }

        tbCategory.getTableHeader().setReorderingAllowed(false);
        tbCategory.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tbCategory.setRowHeight(34);
        tbCategory.setShowHorizontalLines(true);
        tbCategory.setIntercellSpacing(new Dimension(0, 1));

        tbCategory.putClientProperty(FlatClientProperties.STYLE, "" +
                "selectionBackground:fade(@accentColor,35%);" +
                "selectionForeground:@foreground;" +
                "selectionInactiveBackground:darken(@background,2%);" +
                "rowHeight:34");

        JTableHeader tbhProduct = tbCategory.getTableHeader();
        tbhProduct.putClientProperty(FlatClientProperties.STYLE, "" +
                "height:38;" +
                "font:semibold;" +
                "background:darken(@background, 5%);" +
                "foreground:lighten(@foreground,20%);" +
                "separatorColor:@background;" +
                "bottomSeparatorColor:darken(@background,5%)");

        tbCategory.getColumnModel().getColumn(0).setPreferredWidth(50);
        tbCategory.getColumnModel().getColumn(1).setPreferredWidth(200);
        tbCategory.getColumnModel().getColumn(2).setPreferredWidth(400);
        tbCategory.getColumnModel().getColumn(3).setPreferredWidth(100);

    }

    private void addTopBar() {
        var pnlTopBar = new JPanel(new MigLayout("insets 0", "[350]push[][][]", "[grow]"));

        tfSearch = new JTextField();
        tfSearch.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Buscar categoría...");
        tfSearch.putClientProperty(FlatClientProperties.TEXT_FIELD_LEADING_ICON, new FlatSVGIcon("util/search.svg"));
        tfSearch.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { searchCategories(); }
            public void removeUpdate(DocumentEvent e) { searchCategories(); }
            public void changedUpdate(DocumentEvent e) { searchCategories(); }
        });

        btnAdd = createStyledButton("Agregar", "util/add.svg", "#10B981");
        btnEdit = createStyledButton("Editar", "util/edit.svg", "#6B7280");
        btnDelete = createStyledButton("Eliminar", "util/delete.svg", "#EF4444");

        btnAdd.addActionListener(e -> openModal(null));

        btnEdit.addActionListener(e -> {
            if (tbCategory.getSelectedRow() != -1) {
                Long id = (Long) tbCategory.getValueAt(tbCategory.getSelectedRow(), 0);
                editCategory(id);
            }
        });

        btnDelete.addActionListener(e -> {
            if (tbCategory.getSelectedRow() != -1) {
                Long id = (Long) tbCategory.getValueAt(tbCategory.getSelectedRow(), 0);
                String name = (String) tbCategory.getValueAt(tbCategory.getSelectedRow(), 1);
                deleteCategory(id, name);
            }
        });

        btnEdit.setEnabled(false);
        btnDelete.setEnabled(false);
        tbCategory.getSelectionModel().addListSelectionListener(e -> {
            boolean sel = tbCategory.getSelectedRow() != -1;
            btnEdit.setEnabled(sel);
            btnDelete.setEnabled(sel);
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

    private void loadCategories() {
        setControlsEnabled(false);
        new SwingWorker<PageResponse<CategoryResponse>, Void>() {
            @Override
            protected PageResponse<CategoryResponse> doInBackground() throws Exception {
                if (currentSearchTerm.isEmpty()) {
                    return categoryService.getAll(currentPage, pageSize);
                } else {
                    return categoryService.searchByName(currentPage, pageSize, currentSearchTerm);
                }
            }
            @Override
            protected void done() {
                try {
                    displayData(get());
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(vwCategory.this, "Error: " + e.getMessage());
                } finally {
                    setControlsEnabled(true);
                }
            }
        }.execute();
    }

    private void displayData(PageResponse<CategoryResponse> response) {
        model.setRowCount(0);
        if (response.getContent() != null) {
            for (CategoryResponse cat : response.getContent()) {
                int productCount = (cat.getProducts() != null) ? cat.getProducts().size() : 0;
                
                model.addRow(new Object[]{
                    cat.getId(),
                    cat.getName(),
                    cat.getDescription(),
                    productCount
                });
            }
        }
        int totalPages = response.getTotalPages() > 0 ? response.getTotalPages() : 1;
        pagination.setPageRange(currentPage + 1, totalPages);
    }
    
    private void editCategory(Long id) {
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        new Thread(() -> {
            try {
                CategoryResponse catFull = categoryService.getById(id);
                SwingUtilities.invokeLater(() -> {
                    setCursor(Cursor.getDefaultCursor());
                    openModal(catFull);
                });
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    setCursor(Cursor.getDefaultCursor());
                    JOptionPane.showMessageDialog(this, "Error cargando datos: " + e.getMessage());
                });
            }
        }).start();
    }

    private void deleteCategory(Long id, String name) {
        int confirm = JOptionPane.showConfirmDialog(this, 
            "¿Eliminar categoría '" + name + "'?\nEsto podría afectar productos asociados.",
            "Confirmar", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            
        if (confirm == JOptionPane.YES_OPTION) {
            new Thread(() -> {
                try {
                    categoryService.deleteCategory(id);
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(this, "Eliminado correctamente");
                        loadCategories();
                    });
                } catch (Exception e) {
                    SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this, "Error: " + e.getMessage()));
                }
            }).start();
        }
    }

    private void openModal(CategoryResponse categoryToEdit) {
        Window window = SwingUtilities.getWindowAncestor(this);
        String title = (categoryToEdit == null) ? "Nueva Categoría" : "Editar Categoría";
        JDialog dialog = new JDialog(window != null ? (Frame) window : null, title, true);
        
        AddCategory panel;
        if (categoryToEdit == null) {
            panel = new AddCategory(() -> dialog.dispose());
        } else {
            panel = new AddCategory(() -> dialog.dispose(), categoryToEdit);
        }
        
        dialog.setContentPane(panel);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.pack();
        dialog.setLocationRelativeTo(window);
        dialog.setResizable(false);
        dialog.setVisible(true);
        loadCategories();
    }

    private void searchCategories() {
        String term = tfSearch.getText().trim();
        if (!term.equals(currentSearchTerm)) {
            currentSearchTerm = term;
            currentPage = 0;
            loadCategories();
        }
    }

    private void addPagination() {
        pagination = new JPagination();
        pagination.addChangeListener(e -> {
            currentPage = Math.max(0, pagination.getSelectedPage() - 1);
            loadCategories();
        });
        add(pagination);
    }
    
    private void setControlsEnabled(boolean b) {
        if (btnAdd != null) btnAdd.setEnabled(b);
        if (pagination != null) pagination.setEnabled(b);
    }

    private JButton createStyledButton(String text, String icon, String color) {
        JButton btn = new JButton(text, new FlatSVGIcon(icon));
        btn.putClientProperty(FlatClientProperties.STYLE, 
            "arc:8; borderWidth:0; focusWidth:0; background:" + color + "; foreground:#fff; " +
            "hoverBackground:lighten(" + color + ",8%); iconTextGap:8; minimumHeight:30");
        btn.setPreferredSize(new Dimension(120, 40));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }
}