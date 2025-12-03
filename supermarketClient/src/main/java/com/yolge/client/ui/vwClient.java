package com.yolge.client.ui;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.yolge.client.core.RestClient;
import com.yolge.client.dto.PageResponse;
import com.yolge.client.dto.client.ClientResponse;
import com.yolge.client.service.ClientService;
import net.miginfocom.swing.MigLayout;
import raven.swingpack.JPagination;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.ArrayList;

public class vwClient extends JPanel {

    private JTable tbClient;
    private DefaultTableModel model;
    private JTextField tfSearch;
    private JButton btnAdd, btnEdit, btnDelete;
    private JPagination pagination;

    private final ClientService clientService;
    private final RestClient client;
    private int currentPage = 0;
    private int pageSize = 10;
    private String currentSearchTerm = "";

    public vwClient() {
        this.clientService = ClientService.getInstance();
        this.client = RestClient.getInstance();

        setLayout(new MigLayout("wrap, fill, insets 70", "[fill]", "push[grow 0][fill][]push"));

        addTable();
        addTopBar();
        addPagination();

        loadClients();

        this.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentShown(java.awt.event.ComponentEvent e) {
                loadClients();
            }
        });
    }

    private void addTable() {
        String[] columnNames = {"ID", "DNI / Cédula", "Nombre", "Email"};

        model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0) return Long.class;
                return String.class;
            }
        };

        tbClient = new JTable(model);
        styleTable();
        add(new JScrollPane(tbClient), "cell 0 1");
    }

    private void styleTable() {
        javax.swing.table.DefaultTableCellRenderer leftRenderer = new javax.swing.table.DefaultTableCellRenderer();
        leftRenderer.setHorizontalAlignment(javax.swing.JLabel.LEFT);

        for (int i = 0; i < tbClient.getColumnCount(); i++) {
            tbClient.getColumnModel().getColumn(i).setCellRenderer(leftRenderer);
            tbClient.getColumnModel().getColumn(i).setHeaderRenderer(leftRenderer);
        }

        tbClient.getTableHeader().setReorderingAllowed(false);
        tbClient.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tbClient.setRowHeight(34);
        tbClient.setShowHorizontalLines(true);
        tbClient.setIntercellSpacing(new Dimension(0, 1));

        tbClient.putClientProperty(FlatClientProperties.STYLE, "" +
                "selectionBackground:fade(@accentColor,35%);" +
                "selectionForeground:@foreground;" +
                "selectionInactiveBackground:darken(@background,2%);" +
                "rowHeight:34");

        JTableHeader tbhProduct = tbClient.getTableHeader();
        tbhProduct.putClientProperty(FlatClientProperties.STYLE, "" +
                "height:38;" +
                "font:semibold;" +
                "background:darken(@background, 5%);" +
                "foreground:lighten(@foreground,20%);" +
                "separatorColor:@background;" +
                "bottomSeparatorColor:darken(@background,5%)");
        
        tbClient.getColumnModel().getColumn(0).setPreferredWidth(50);
        tbClient.getColumnModel().getColumn(1).setPreferredWidth(100);
        tbClient.getColumnModel().getColumn(2).setPreferredWidth(250);
        tbClient.getColumnModel().getColumn(3).setPreferredWidth(200);
    }

    private void addTopBar() {
        JPanel pnlTopBar = new JPanel(new MigLayout("insets 0", "[350]push[][][]", "[grow]"));

        tfSearch = new JTextField();
        tfSearch.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Buscar por nombre...");
        tfSearch.putClientProperty(FlatClientProperties.TEXT_FIELD_LEADING_ICON, new FlatSVGIcon("util/search.svg"));
        tfSearch.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { searchClients(); }
            public void removeUpdate(DocumentEvent e) { searchClients(); }
            public void changedUpdate(DocumentEvent e) { searchClients(); }
        });

        btnAdd = createStyledButton("Registrar", "util/add.svg", "#10B981");
        btnEdit = createStyledButton("Editar", "util/edit.svg", "#6B7280");
        btnDelete = createStyledButton("Eliminar", "util/delete.svg", "#EF4444");

        btnAdd.addActionListener(e -> openModal(null));
        btnEdit.addActionListener(e -> {
            if (tbClient.getSelectedRow() != -1) {
                Long id = (Long) tbClient.getValueAt(tbClient.getSelectedRow(), 0);
                editClient(id);
            }
        });

        btnDelete.addActionListener(e -> {
            if (tbClient.getSelectedRow() != -1) {
                Long id = (Long) tbClient.getValueAt(tbClient.getSelectedRow(), 0);
                String name = (String) tbClient.getValueAt(tbClient.getSelectedRow(), 2);
                deleteClient(id, name);
            }
        });

        btnEdit.setEnabled(false);
        btnDelete.setEnabled(false);
        tbClient.getSelectionModel().addListSelectionListener(e -> {
            boolean sel = tbClient.getSelectedRow() != -1;
            btnEdit.setEnabled(sel);
            if (client.isAdmin()) {
                btnDelete.setEnabled(sel);
            }
        });

        pnlTopBar.add(tfSearch, "growx");
        
        if (client.isAdmin()) {
            pnlTopBar.add(btnAdd);
            pnlTopBar.add(btnEdit);
            pnlTopBar.add(btnDelete);
        } else if (client.isCashier()) {
            pnlTopBar.add(btnAdd);
            pnlTopBar.add(btnEdit);
        }

        add(pnlTopBar, "cell 0 0");
    }

    private void loadClients() {
        setControlsEnabled(false);
        new SwingWorker<PageResponse<ClientResponse>, Void>() {
            @Override
            protected PageResponse<ClientResponse> doInBackground() throws Exception {
                if (currentSearchTerm.isEmpty()) {
                    return clientService.getAll(currentPage, pageSize);
                } else {
                    PageResponse<ClientResponse> result = clientService.searchByName(currentPage, pageSize, currentSearchTerm);
                    if (result.getTotalElements() > 0) {
                        return result;
                    } else {
                        return clientService.searchByEmail(currentPage, pageSize, currentSearchTerm);
                    }
                }
            }
            @Override
            protected void done() {
                try {
                    displayData(get());
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(vwClient.this, "Error: " + e.getMessage());
                } finally {
                    setControlsEnabled(true);
                }
            }
        }.execute();
    }

    private void displayData(PageResponse<ClientResponse> response) {
        model.setRowCount(0);
        if (response.getContent() != null) {
            for (ClientResponse c : response.getContent()) {
                model.addRow(new Object[]{
                    c.getId(),
                    c.getDni(),
                    c.getName(),
                    c.getEmail()
                });
            }
        }
        int totalPages = response.getTotalPages() > 0 ? response.getTotalPages() : 1;
        pagination.setPageRange(currentPage + 1, totalPages);
    }

    private void editClient(Long id) {
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        new Thread(() -> {
            try {
                ClientResponse c = clientService.getById(id);
                SwingUtilities.invokeLater(() -> {
                    setCursor(Cursor.getDefaultCursor());
                    openModal(c);
                });
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    setCursor(Cursor.getDefaultCursor());
                    JOptionPane.showMessageDialog(this, "Error cargando datos: " + e.getMessage());
                });
            }
        }).start();
    }

    private void deleteClient(Long id, String name) {
        if (JOptionPane.showConfirmDialog(this, "¿Eliminar cliente '" + name + "'?", "Confirmar", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            new Thread(() -> {
                try {
                    clientService.deleteClient(id);
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(this, "Cliente eliminado");
                        loadClients();
                    });
                } catch (Exception e) {
                    SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this, "Error: " + e.getMessage()));
                }
            }).start();
        }
    }

    private void openModal(ClientResponse clientToEdit) {
        Window w = SwingUtilities.getWindowAncestor(this);
        String title = (clientToEdit == null) ? "Registrar Cliente" : "Editar Cliente";
        JDialog dialog = new JDialog(w != null ? (Frame) w : null, title, true);
        
        AddClient panel = (clientToEdit == null) 
            ? new AddClient(() -> dialog.dispose()) 
            : new AddClient(() -> dialog.dispose(), clientToEdit);
            
        dialog.setContentPane(panel);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.pack();
        dialog.setLocationRelativeTo(w);
        dialog.setResizable(false);
        dialog.setVisible(true);
        loadClients();
    }

    private void searchClients() {
        String t = tfSearch.getText().trim();
        if(!t.equals(currentSearchTerm)) {
            currentSearchTerm = t;
            currentPage = 0;
            loadClients();
        }
    }

    private void addPagination() {
        pagination = new JPagination();
        pagination.addChangeListener(e -> {
            currentPage = Math.max(0, pagination.getSelectedPage() - 1);
            loadClients();
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