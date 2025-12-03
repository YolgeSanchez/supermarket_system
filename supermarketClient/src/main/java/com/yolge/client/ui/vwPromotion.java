package com.yolge.client.ui;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.yolge.client.core.RestClient;
import com.yolge.client.dto.PageResponse;
import com.yolge.client.dto.promotion.PromotionResponse;
import com.yolge.client.service.PromotionService;
import net.miginfocom.swing.MigLayout;
import raven.swingpack.JPagination;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;

public class vwPromotion extends JPanel {

    private JTable tbPromotion;
    private DefaultTableModel model;
    private JTextField tfSearch;
    private JButton btnAdd, btnDelete;
    private JPagination pagination;

    private final PromotionService promotionService;
    private final RestClient client;
    private int currentPage = 0;
    private int pageSize = 10;
    private String currentSearchTerm = "";

    public vwPromotion() {
        this.promotionService = PromotionService.getInstance();
        this.client = RestClient.getInstance();

        setLayout(new MigLayout("wrap, fill, insets 70", "[fill]", "push[grow 0][fill][]push"));

        addTable();
        addTopBar();
        addPagination();
        
        loadPromotions();

        this.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentShown(java.awt.event.ComponentEvent e) {
                loadPromotions();
            }
        });
    }

    private void addTable() {
        String[] columnNames = {"ID", "Nombre", "Descuento %", "Inicio", "Fin"};

        model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0) return Long.class;
                if (columnIndex == 2) return Double.class;
                return String.class;
            }
        };

        tbPromotion = new JTable(model);
        styleTable();
        add(new JScrollPane(tbPromotion), "cell 0 1");
    }

    private void styleTable() {
        javax.swing.table.DefaultTableCellRenderer leftRenderer = new javax.swing.table.DefaultTableCellRenderer();
        leftRenderer.setHorizontalAlignment(javax.swing.JLabel.LEFT);

        for (int i = 0; i < tbPromotion.getColumnCount(); i++) {
            tbPromotion.getColumnModel().getColumn(i).setCellRenderer(leftRenderer);
            tbPromotion.getColumnModel().getColumn(i).setHeaderRenderer(leftRenderer);
        }

        tbPromotion.getTableHeader().setReorderingAllowed(false);
        tbPromotion.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tbPromotion.setRowHeight(34);
        tbPromotion.setShowHorizontalLines(true);
        tbPromotion.setIntercellSpacing(new Dimension(0, 1));

        tbPromotion.putClientProperty(FlatClientProperties.STYLE, "" +
                "selectionBackground:fade(@accentColor,35%);" +
                "selectionForeground:@foreground;" +
                "selectionInactiveBackground:darken(@background,2%);" +
                "rowHeight:34");

        JTableHeader tbhProduct = tbPromotion.getTableHeader();
        tbhProduct.putClientProperty(FlatClientProperties.STYLE, "" +
                "height:38;" +
                "font:semibold;" +
                "background:darken(@background, 5%);" +
                "foreground:lighten(@foreground,20%);" +
                "separatorColor:@background;" +
                "bottomSeparatorColor:darken(@background,5%)");
        
        tbPromotion.getColumnModel().getColumn(0).setPreferredWidth(40);
        tbPromotion.getColumnModel().getColumn(1).setPreferredWidth(200);
        tbPromotion.getColumnModel().getColumn(2).setPreferredWidth(80);
        tbPromotion.getColumnModel().getColumn(3).setPreferredWidth(100);
        tbPromotion.getColumnModel().getColumn(4).setPreferredWidth(100);
    }

    private void addTopBar() {
        JPanel pnlTopBar = new JPanel(new MigLayout("insets 0", "[350]push[][][]", "[grow]"));

        tfSearch = new JTextField();
        tfSearch.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Buscar promoción...");
        tfSearch.putClientProperty(FlatClientProperties.TEXT_FIELD_LEADING_ICON, new FlatSVGIcon("util/search.svg"));
        tfSearch.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { searchPromotions(); }
            public void removeUpdate(DocumentEvent e) { searchPromotions(); }
            public void changedUpdate(DocumentEvent e) { searchPromotions(); }
        });

        btnAdd = createStyledButton("Agregar", "util/add.svg", "#10B981");
        btnDelete = createStyledButton("Eliminar", "util/delete.svg", "#EF4444");

        btnAdd.addActionListener(e -> openModal());

        btnDelete.addActionListener(e -> {
            if (tbPromotion.getSelectedRow() != -1) {
                Long id = (Long) tbPromotion.getValueAt(tbPromotion.getSelectedRow(), 0);
                String name = (String) tbPromotion.getValueAt(tbPromotion.getSelectedRow(), 1);
                deletePromotion(id, name);
            }
        });

        btnDelete.setEnabled(false);
        tbPromotion.getSelectionModel().addListSelectionListener(e -> {
            boolean sel = tbPromotion.getSelectedRow() != -1;
            // Solo admin puede borrar
            if (client.isAdmin()) {
                btnDelete.setEnabled(sel);
            }
        });

        pnlTopBar.add(tfSearch, "growx");

        if (client.isAdmin()) {
            pnlTopBar.add(btnAdd);
            pnlTopBar.add(btnDelete);
        } else if (client.isInventory()) {
            pnlTopBar.add(btnAdd);
        }

        add(pnlTopBar, "cell 0 0");
    }

    private void loadPromotions() {
        setControlsEnabled(false);
        new SwingWorker<PageResponse<PromotionResponse>, Void>() {
            @Override
            protected PageResponse<PromotionResponse> doInBackground() throws Exception {
                if (currentSearchTerm.isEmpty()) {
                    return promotionService.getAll(currentPage, pageSize);
                } else {
                    return promotionService.searchByName(currentPage, pageSize, currentSearchTerm);
                }
            }
            @Override
            protected void done() {
                try {
                    displayData(get());
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(vwPromotion.this, "Error: " + e.getMessage());
                } finally {
                    setControlsEnabled(true);
                }
            }
        }.execute();
    }

    private void displayData(PageResponse<PromotionResponse> response) {
        model.setRowCount(0);
        if (response.getContent() != null) {
            for (PromotionResponse p : response.getContent()) {

                model.addRow(new Object[]{
                    p.getId(),
                    p.getName(),
                    p.getDiscountPercentage(),
                    p.getStartDate(),
                    p.getEndDate(),
                });
            }
        }
        int totalPages = response.getTotalPages() > 0 ? response.getTotalPages() : 1;
        pagination.setPageRange(currentPage + 1, totalPages);
    }

    private void deletePromotion(Long id, String name) {
        if (JOptionPane.showConfirmDialog(this, "¿Eliminar promoción '" + name + "'?", "Confirmar", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            new Thread(() -> {
                try {
                    promotionService.deleteById(id);
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(this, "Eliminado correctamente");
                        loadPromotions();
                    });
                } catch (Exception e) {
                    SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this, "Error: " + e.getMessage()));
                }
            }).start();
        }
    }

    private void openModal() {
        Window w = SwingUtilities.getWindowAncestor(this);
        JDialog dialog = new JDialog(w != null ? (Frame) w : null, "Nueva Promoción", true);
        AddPromotion panel = new AddPromotion(() -> dialog.dispose());
        dialog.setContentPane(panel);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.pack();
        dialog.setLocationRelativeTo(w);
        dialog.setResizable(false);
        dialog.setVisible(true);
        loadPromotions();
    }

    private void searchPromotions() {
        String t = tfSearch.getText().trim();
        if(!t.equals(currentSearchTerm)) {
            currentSearchTerm = t;
            currentPage = 0;
            loadPromotions();
        }
    }

    private void addPagination() {
        pagination = new JPagination();
        pagination.addChangeListener(e -> {
            currentPage = Math.max(0, pagination.getSelectedPage() - 1);
            loadPromotions();
        });
        add(pagination);
    }

    private void setControlsEnabled(boolean b) {
        if(btnAdd != null) btnAdd.setEnabled(b);
        if(pagination != null) pagination.setEnabled(b);
    }

    private JButton createStyledButton(String text, String icon, String color) {
        JButton btn = new JButton(text, new FlatSVGIcon(icon));
        btn.putClientProperty(FlatClientProperties.STYLE, "arc:8; borderWidth:0; focusWidth:0; background:" + color + "; foreground:#fff; hoverBackground:lighten(" + color + ",8%); iconTextGap:8; minimumHeight:30");
        btn.setPreferredSize(new Dimension(120, 40));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }
}