package com.yolge.client.ui;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.yolge.client.core.RestClient;
import com.yolge.client.dto.PageResponse;
import com.yolge.client.dto.user.UserResponse;
import com.yolge.client.service.UserService;
import net.miginfocom.swing.MigLayout;
import raven.swingpack.JPagination;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;

public class vwUser extends JPanel {

    private JTable tbUser;
    private DefaultTableModel model;
    private JTextField tfSearch;
    private JButton btnAdd, btnEdit, btnDelete;
    private JPagination pagination;

    private final UserService userService;
    private int currentPage = 0;
    private int pageSize = 10;
    private String currentSearchTerm = "";

    public vwUser() {
        this.userService = UserService.getInstance();
        
        setLayout(new MigLayout("wrap, fill, insets 70", "[fill]", "push[grow 0][fill][]push"));
        
        addTable();
        addTopBar();
        addPagination();
        
        loadUsers();

        this.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentShown(java.awt.event.ComponentEvent e) {
                loadUsers();
            }
        });
    }

    private void addTable() {
        String[] columnNames = {"ID", "Nombre Completo", "Usuario", "Rol"};
        
        model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0) return Long.class;
                return String.class;
            }
        };

        tbUser = new JTable(model);
        styleTable();
        add(new JScrollPane(tbUser), "cell 0 1");
    }

    private void styleTable() {
        javax.swing.table.DefaultTableCellRenderer leftRenderer = new javax.swing.table.DefaultTableCellRenderer();
        leftRenderer.setHorizontalAlignment(javax.swing.JLabel.LEFT);

        for (int i = 0; i < tbUser.getColumnCount(); i++) {
            tbUser.getColumnModel().getColumn(i).setCellRenderer(leftRenderer);
            tbUser.getColumnModel().getColumn(i).setHeaderRenderer(leftRenderer);
        }

        tbUser.getTableHeader().setReorderingAllowed(false);
        tbUser.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tbUser.setRowHeight(34);
        tbUser.setShowHorizontalLines(true);
        tbUser.setIntercellSpacing(new Dimension(0, 1));

        tbUser.putClientProperty(FlatClientProperties.STYLE, "" +
                "selectionBackground:fade(@accentColor,35%);" +
                "selectionForeground:@foreground;" +
                "selectionInactiveBackground:darken(@background,2%);" +
                "rowHeight:34");

        JTableHeader tbhProduct = tbUser.getTableHeader();
        tbhProduct.putClientProperty(FlatClientProperties.STYLE, "" +
                "height:38;" +
                "font:semibold;" +
                "background:darken(@background, 5%);" +
                "foreground:lighten(@foreground,20%);" +
                "separatorColor:@background;" +
                "bottomSeparatorColor:darken(@background,5%)");

        tbUser.getColumnModel().getColumn(0).setPreferredWidth(50);
        tbUser.getColumnModel().getColumn(1).setPreferredWidth(250);
        tbUser.getColumnModel().getColumn(2).setPreferredWidth(250);
        tbUser.getColumnModel().getColumn(3).setPreferredWidth(100);

    }

    private void addTopBar() {
        JPanel pnlTopBar = new JPanel(new MigLayout("insets 0", "[350]push[][][]", "[grow]"));

        tfSearch = new JTextField();
        tfSearch.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Buscar por usuario...");
        tfSearch.putClientProperty(FlatClientProperties.TEXT_FIELD_LEADING_ICON, new FlatSVGIcon("util/search.svg"));
        tfSearch.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { searchUsers(); }
            public void removeUpdate(DocumentEvent e) { searchUsers(); }
            public void changedUpdate(DocumentEvent e) { searchUsers(); }
        });

        btnAdd = createStyledButton("Registrar", "util/add.svg", "#10B981");
        btnEdit = createStyledButton("Editar", "util/edit.svg", "#6B7280");
        btnDelete = createStyledButton("Eliminar", "util/delete.svg", "#EF4444");

        btnAdd.addActionListener(e -> openModal(null));
        btnEdit.addActionListener(e -> {
            if (tbUser.getSelectedRow() != -1) {
                Long id = (Long) tbUser.getValueAt(tbUser.getSelectedRow(), 0);
                editUser(id);
            }
        });
        btnDelete.addActionListener(e -> {
            if (tbUser.getSelectedRow() != -1) {
                Long id = (Long) tbUser.getValueAt(tbUser.getSelectedRow(), 0);
                String name = (String) tbUser.getValueAt(tbUser.getSelectedRow(), 2); // username
                deleteUser(id, name);
            }
        });

        btnEdit.setEnabled(false);
        btnDelete.setEnabled(false);
        tbUser.getSelectionModel().addListSelectionListener(e -> {
            boolean sel = tbUser.getSelectedRow() != -1;
            btnEdit.setEnabled(sel);
            btnDelete.setEnabled(sel);
        });

        pnlTopBar.add(tfSearch, "growx");
        pnlTopBar.add(btnAdd);
        pnlTopBar.add(btnEdit);
        pnlTopBar.add(btnDelete);

        add(pnlTopBar, "cell 0 0");
    }

    private void loadUsers() {
        setControlsEnabled(false);
        new SwingWorker<PageResponse<UserResponse>, Void>() {
            @Override
            protected PageResponse<UserResponse> doInBackground() throws Exception {
                if (currentSearchTerm.isEmpty()) {
                    return userService.getAll(currentPage, pageSize);
                } else {
                    return userService.searchByUsername(currentPage, pageSize, currentSearchTerm);
                }
            }
            @Override
            protected void done() {
                try {
                    displayData(get());
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(vwUser.this, "Error: " + e.getMessage());
                } finally {
                    setControlsEnabled(true);
                }
            }
        }.execute();
    }

    private void displayData(PageResponse<UserResponse> response) {
        model.setRowCount(0);
        if (response.getContent() != null) {
            for (UserResponse u : response.getContent()) {
                model.addRow(new Object[]{
                    u.getUserId(),
                    u.getFullName(),
                    u.getUsername(),
                    u.getRole()
                });
            }
        }
        int totalPages = response.getTotalPages() > 0 ? response.getTotalPages() : 1;
        pagination.setPageRange(currentPage + 1, totalPages);
    }

    private void editUser(Long id) {
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        new Thread(() -> {
            try {
                UserResponse userFull = userService.getById(id);
                SwingUtilities.invokeLater(() -> {
                    setCursor(Cursor.getDefaultCursor());
                    openModal(userFull);
                });
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    setCursor(Cursor.getDefaultCursor());
                    JOptionPane.showMessageDialog(this, "Error cargando usuario: " + e.getMessage());
                });
            }
        }).start();
    }

    private void deleteUser(Long id, String username) {
        if (JOptionPane.showConfirmDialog(this, "¿Eliminar usuario '" + username + "'?", "Confirmar", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            new Thread(() -> {
                try {
                    userService.deleteUser(id);
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(this, "Eliminado correctamente");
                        loadUsers();
                    });
                } catch (Exception e) {
                    SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this, "Error: " + e.getMessage()));
                }
            }).start();
        }
    }

    private void openModal(UserResponse userToEdit) {
        Window w = SwingUtilities.getWindowAncestor(this);
        String title = (userToEdit == null) ? "Registrar Usuario" : "Editar Usuario";
        JDialog dialog = new JDialog(w != null ? (Frame) w : null, title, true);
        
        AddUser panel = (userToEdit == null) 
            ? new AddUser(() -> dialog.dispose()) 
            : new AddUser(() -> dialog.dispose(), userToEdit);
            
        dialog.setContentPane(panel);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.pack();
        dialog.setLocationRelativeTo(w);
        dialog.setResizable(false);
        dialog.setVisible(true);
        loadUsers();
    }

    private void searchUsers() {
        String t = tfSearch.getText().trim();
        if(!t.equals(currentSearchTerm)) {
            currentSearchTerm = t;
            currentPage = 0;
            loadUsers();
        }
    }

    private void addPagination() {
        pagination = new JPagination();
        pagination.addChangeListener(e -> {
            currentPage = Math.max(0, pagination.getSelectedPage() - 1);
            loadUsers();
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