package com.yolge.client.ui;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.yolge.client.dto.PageResponse;
import com.yolge.client.dto.client.ClientResponse;
import com.yolge.client.dto.product.ProductResponse;
import com.yolge.client.dto.sale.SaleResponse;
import com.yolge.client.dto.sale.SaleDetailResponse;
import com.yolge.client.service.ClientService;
import com.yolge.client.service.ProductService;
import com.yolge.client.service.SaleService;
import net.miginfocom.swing.MigLayout;
import raven.swingpack.JPagination;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class vwSale extends JPanel {

    // --- Servicios ---
    private final SaleService saleService;
    private final ProductService productService;
    private final ClientService clientService; // Nuevo servicio

    // --- Componentes Principales ---
    private JPanel mainContainer;
    private CardLayout cardLayout;

    // --- Pantalla 1: Inicio ---
    private JPanel pnlStart;
    private JComboBox<ClientResponse> cbClients; // Nuevo combo
    private JButton btnCreateSale;

    // --- Pantalla 2: POS (Split) ---
    private JPanel pnlPOS;
    private Long currentSaleId;

    // >>> LADO IZQUIERDO (CARRITO)
    private JLabel lbSaleInfo;
    private JLabel lbTotal;
    private JTable tbCart;
    private DefaultTableModel modelCart;
    private JButton btnRemoveItem;
    private JButton btnFinalize;
    private JButton btnCancelSale;

    // >>> LADO DERECHO (CATÁLOGO)
    private JTextField tfSearchProduct;
    private JTable tbCatalog;
    private DefaultTableModel modelCatalog;
    private JSpinner spQuantity;
    private JButton btnAddToCart;
    private JPagination pagination;
    
    private int catPage = 0;
    private String catSearch = "";

    public vwSale() {
        this.saleService = SaleService.getInstance();
        this.productService = ProductService.getInstance();
        this.clientService = ClientService.getInstance(); // Inicializar

        init();
    }

    private void init() {
        setLayout(new BorderLayout());
        cardLayout = new CardLayout();
        mainContainer = new JPanel(cardLayout);

        initStartPanel();
        initPOSPanel();

        add(mainContainer, BorderLayout.CENTER);
        cardLayout.show(mainContainer, "START");
        
        // Cargar clientes al iniciar
        loadClientsForCombo();
    }

    // ==========================================
    //            PANTALLA 1: INICIO
    // ==========================================
    private void initStartPanel() {
        pnlStart = new JPanel(new MigLayout("fill, wrap", "[center]", "push[]20[]20[]push"));
        
        JLabel lbWelcome = new JLabel("Punto de Venta");
        lbWelcome.putClientProperty(FlatClientProperties.STYLE, "font:bold +20");
        
        // --- SELECCIÓN DE CLIENTE ---
        JPanel pnlClientSelect = new JPanel(new MigLayout("insets 0", "[]10[]"));
        pnlClientSelect.add(new JLabel("Cliente:"));
        
        cbClients = new JComboBox<>();
        cbClients.setPreferredSize(new Dimension(250, 35));
        // Renderer para ver Nombre y DNI
        cbClients.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof ClientResponse) {
                    ClientResponse c = (ClientResponse) value;
                    setText(c.getName() + " (" + c.getDni() + ")");
                } else {
                    setText("Cliente Genérico / Anónimo");
                }
                return this;
            }
        });
        pnlClientSelect.add(cbClients);
        
        // Botón Crear
        btnCreateSale = new JButton("Iniciar Venta", new FlatSVGIcon("util/add.svg"));
        btnCreateSale.putClientProperty(FlatClientProperties.STYLE, 
                "background:#10B981; foreground:#fff; font:bold +5; arc:20; margin:10,20,10,20");
        btnCreateSale.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCreateSale.addActionListener(e -> createNewSale());

        pnlStart.add(lbWelcome);
        pnlStart.add(pnlClientSelect);
        pnlStart.add(btnCreateSale);
        
        mainContainer.add(pnlStart, "START");
    }

    // ==========================================
    //            PANTALLA 2: POS
    // ==========================================
    private void initPOSPanel() {
        pnlPOS = new JPanel(new MigLayout("fill, insets 10", "[45%][55%]", "[grow]"));

        // --- IZQUIERDA: CARRITO ---
        JPanel leftPanel = new JPanel(new MigLayout("wrap, fill", "[grow]", "[][grow][]"));
        leftPanel.putClientProperty(FlatClientProperties.STYLE, "background:darken(@background, 3%); arc:10");
        
        lbSaleInfo = new JLabel("Venta # ---");
        lbSaleInfo.putClientProperty(FlatClientProperties.STYLE, "font:bold +5");
        leftPanel.add(lbSaleInfo, "growx");

        initCartTable();
        JScrollPane scrollCart = new JScrollPane(tbCart);
        // SOLUCIÓN ERROR CLASE '0': Usar setBorder(null) en lugar de putClientProperty
        scrollCart.setBorder(null); 
        leftPanel.add(scrollCart, "grow, h 100%");

        // Footer Carrito
        JPanel footerCart = new JPanel(new MigLayout("fillx, insets 10", "[grow][]", "[][][]"));
        footerCart.setOpaque(false);
        
        lbTotal = new JLabel("Total: $0.00");
        lbTotal.putClientProperty(FlatClientProperties.STYLE, "font:bold +20; foreground:@accentColor");
        
        btnRemoveItem = createStyledButton("Quitar", "#EF4444");
        btnRemoveItem.setEnabled(false);
        btnRemoveItem.addActionListener(e -> removeSelectedItem());

        btnCancelSale = createStyledButton("Cancelar", "#6B7280");
        btnCancelSale.addActionListener(e -> cancelCurrentSale());

        btnFinalize = createStyledButton("COBRAR", "#10B981");
        btnFinalize.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnFinalize.addActionListener(e -> finalizeCurrentSale());

        footerCart.add(btnRemoveItem, "wrap");
        footerCart.add(new JSeparator(), "span, growx, gaptop 10, gapbottom 10");
        footerCart.add(lbTotal, "span, align right, wrap");
        footerCart.add(btnCancelSale, "growx, width 50%, h 40!");
        footerCart.add(btnFinalize, "growx, width 50%, h 40!");
        
        leftPanel.add(footerCart, "growx");

        // --- DERECHA: CATÁLOGO ---
        JPanel rightPanel = new JPanel(new MigLayout("wrap, fill", "[grow]", "[][grow][]"));
        
        JPanel headerCatalog = new JPanel(new MigLayout("insets 0, fillx", "[grow][][][]", "[]"));
        
        tfSearchProduct = new JTextField();
        tfSearchProduct.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Buscar producto...");
        tfSearchProduct.putClientProperty(FlatClientProperties.TEXT_FIELD_LEADING_ICON, new FlatSVGIcon("util/search.svg"));
        tfSearchProduct.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { searchCatalog(); }
            public void removeUpdate(DocumentEvent e) { searchCatalog(); }
            public void changedUpdate(DocumentEvent e) { searchCatalog(); }
        });

        spQuantity = new JSpinner(new SpinnerNumberModel(1, 1, 999, 1));
        
        btnAddToCart = createStyledButton("Agregar", "#3B82F6");
        btnAddToCart.addActionListener(e -> addProductToCart());
        
        headerCatalog.add(tfSearchProduct, "growx");
        headerCatalog.add(new JLabel("Cant:"));
        headerCatalog.add(spQuantity, "w 100!");
        headerCatalog.add(btnAddToCart);
        
        rightPanel.add(headerCatalog, "growx");

        initCatalogTable();
        rightPanel.add(new JScrollPane(tbCatalog), "grow");

        pagination = new JPagination();
        pagination.addChangeListener(e -> {
            catPage = Math.max(0, pagination.getSelectedPage() - 1);
            loadCatalog();
        });
        rightPanel.add(pagination, "align center");

        pnlPOS.add(leftPanel, "grow");
        pnlPOS.add(rightPanel, "grow");
        
        mainContainer.add(pnlPOS, "POS");
    }

    // clientes
    private void loadClientsForCombo() {
        new Thread(() -> {
            try {
                PageResponse<ClientResponse> response = clientService.getAll(0, 100);
                SwingUtilities.invokeLater(() -> {
                    cbClients.removeAllItems();
                    cbClients.addItem(null);
                    if (response.getContent() != null) {
                        for (ClientResponse c : response.getContent()) {
                            cbClients.addItem(c);
                        }
                    }
                });
            } catch (Exception e) { /* ... */ }
        }).start();
    }

    // logica venta
    private void createNewSale() {
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        
        ClientResponse selectedClient = (ClientResponse) cbClients.getSelectedItem();
        Long clientId = (selectedClient != null) ? selectedClient.getId() : null;

        new Thread(() -> {
            try {
                SaleResponse sale = saleService.createSale(clientId);
                currentSaleId = sale.getId();

                SwingUtilities.invokeLater(() -> {
                    setCursor(Cursor.getDefaultCursor());
                    String clientName = (sale.getClient() != null) ? sale.getClient().getName() : "Genérico";
                    lbSaleInfo.setText("<html>Venta #" + sale.getId() + "<br><span style='font-weight:normal; font-size:10px'>Cliente: " + clientName + "</span></html>");
                    
                    updateCartTable(sale);
                    loadCatalog();
                    cardLayout.show(mainContainer, "POS");
                });
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    setCursor(Cursor.getDefaultCursor());
                    JOptionPane.showMessageDialog(this, "Error al crear venta: " + e.getMessage());
                });
            }
        }).start();
    }
    
    private void loadCatalog() {
        new Thread(() -> {
            try {
                PageResponse<ProductResponse> response;
                if(catSearch.isEmpty()) response = productService.getProducts(catPage, 19); // 14 items caben bien
                else response = productService.searchByName(catPage, 19, catSearch);
                
                SwingUtilities.invokeLater(() -> {
                    modelCatalog.setRowCount(0);
                    if(response.getContent() != null) {
                        for(ProductResponse p : response.getContent()) {
                            modelCatalog.addRow(new Object[]{ p.getId(), p.getName(), p.getBrand(), p.getFinalPrice(), p.getStock() });
                        }
                    }
                    int total = response.getTotalPages() > 0 ? response.getTotalPages() : 1;
                    pagination.setPageRange(catPage + 1, total);
                });
            } catch (Exception e) { /* ... */ }
        }).start();
    }

    private void addProductToCart() {
        int selectedRow = tbCatalog.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un producto.");
            return;
        }
        Long prodId = (Long) tbCatalog.getValueAt(selectedRow, 0);
        Integer qty = (Integer) spQuantity.getValue();

        new Thread(() -> {
            try {
                SaleResponse updatedSale = saleService.addDetail(currentSaleId, prodId, qty);
                SwingUtilities.invokeLater(() -> {
                    updateCartTable(updatedSale);
                    loadCatalog();
                    spQuantity.setValue(1);
                });
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this, "Error: " + e.getMessage()));
            }
        }).start();
    }

    private void removeSelectedItem() {
        int selectedRow = tbCart.getSelectedRow();
        if (selectedRow == -1) return;
        Long detailId = (Long) tbCart.getValueAt(selectedRow, 0);

        new Thread(() -> {
            try {
                SaleResponse updatedSale = saleService.removeDetail(detailId);
                SwingUtilities.invokeLater(() -> {
                    updateCartTable(updatedSale);
                    loadCatalog();
                });
            } catch (Exception e) { /*...*/ }
        }).start();
    }

    private void updateCartTable(SaleResponse sale) {
        modelCart.setRowCount(0);
        if (sale.getDetails() != null) {
            for (SaleDetailResponse det : sale.getDetails()) {
                modelCart.addRow(new Object[]{
                    det.getId(),
                    det.getProduct().getName(),
                    det.getQuantity(),
                    det.getUnitPrice(),
                    det.getSubTotal()
                });
            }
        }
        lbTotal.setText(String.format("Total: $%.2f", sale.getTotalPrice()));
        btnFinalize.setEnabled(modelCart.getRowCount() > 0);
    }
    
    private void finalizeCurrentSale() {
        if (JOptionPane.showConfirmDialog(this, "¿Finalizar la venta?", "Confirmar", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            new Thread(() -> {
                try {
                    saleService.finalizeSale(currentSaleId);
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(this, "¡Venta registrada!");
                        cardLayout.show(mainContainer, "START");
                        loadCatalog(); // Resetear catálogo
                    });
                } catch (Exception e) {
                    SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this, "Error: " + e.getMessage()));
                }
            }).start();
        }
    }

    private void cancelCurrentSale() {
        if (JOptionPane.showConfirmDialog(this, "¿Cancelar?", "Confirmar", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            new Thread(() -> {
                try {
                    saleService.cancelSale(currentSaleId);
                    SwingUtilities.invokeLater(() -> cardLayout.show(mainContainer, "START"));
                } catch (Exception e) { /*...*/ }
            }).start();
        }
    }

    private void searchCatalog() {
        String t = tfSearchProduct.getText().trim();
        if (!t.equals(catSearch)) {
            catSearch = t;
            catPage = 0;
            loadCatalog();
        }
    }

    private void initCartTable() {
        String[] cols = {"ID", "Producto", "Cant", "Precio", "Subtotal"};
        modelCart = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
            @Override public Class<?> getColumnClass(int col) {
                if(col==0) return Long.class;
                if(col==2) return Integer.class;
                if(col==3 || col==4) return Double.class;
                return String.class;
            }
        };
        tbCart = new JTable(modelCart);
        styleTable(tbCart);
        // Ocultar ID
        tbCart.getColumnModel().getColumn(0).setMinWidth(0);
        tbCart.getColumnModel().getColumn(0).setMaxWidth(0);
        tbCart.getColumnModel().getColumn(0).setPreferredWidth(0);
        
        tbCart.getColumnModel().getColumn(1).setPreferredWidth(150);
        tbCart.getColumnModel().getColumn(2).setPreferredWidth(40);
        
        tbCart.getSelectionModel().addListSelectionListener(e -> btnRemoveItem.setEnabled(tbCart.getSelectedRow() != -1));
    }

    private void initCatalogTable() {
        String[] cols = {"ID", "Nombre", "Marca", "Precio", "Stock"};
        modelCatalog = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
            @Override public Class<?> getColumnClass(int col) {
                if(col==0) return Long.class;
                if(col==3) return Double.class;
                if(col==4) return Integer.class;
                return String.class;
            }
        };
        tbCatalog = new JTable(modelCatalog);
        styleTable(tbCatalog);
        tbCatalog.getColumnModel().getColumn(0).setPreferredWidth(30);
        tbCatalog.getColumnModel().getColumn(1).setPreferredWidth(150);
        tbCatalog.getColumnModel().getColumn(4).setPreferredWidth(40);
    }

    private void styleTable(JTable table) {
        javax.swing.table.DefaultTableCellRenderer leftRenderer = new javax.swing.table.DefaultTableCellRenderer();
        leftRenderer.setHorizontalAlignment(JLabel.LEFT);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(leftRenderer);
            table.getColumnModel().getColumn(i).setHeaderRenderer(leftRenderer);
        }
        table.getTableHeader().setReorderingAllowed(false);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(30);
        table.setShowHorizontalLines(true);
        table.putClientProperty(FlatClientProperties.STYLE, "selectionBackground:fade(@accentColor,35%); selectionForeground:@foreground; rowHeight:30");
        table.getTableHeader().putClientProperty(FlatClientProperties.STYLE, "height:34; font:semibold; background:darken(@background, 5%)");
    }

    private JButton createStyledButton(String text, String color) {
        JButton btn = new JButton(text);
        btn.putClientProperty(FlatClientProperties.STYLE, 
            "arc:8; borderWidth:0; background:" + color + "; foreground:#fff; hoverBackground:lighten(" + color + ",10%)");
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }
}