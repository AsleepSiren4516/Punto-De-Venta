
package com.interfaz;

import carpeta.constructor.factura;
import carpeta.constructor.producto;
import com.conexion.Conexion;
import static com.conexion.Conexion.contarFilas;
import static com.conexion.Conexion.conectar;
import static com.conexion.Conexion.obtenerUltimoID;
import com.conexion.productoCon;
import com.constructor.Apuntadores;
import com.constructor.Contadores;
import com.constructor.Reportes;
import com.constructor.proveedor;
import constructor.accionIcono;
import constructor.accionTabla;
import constructor.editarIcono;
import constructor.iconoTabla;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.regex.Pattern;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.RowFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

/**
 *
 * @author bobby
 */
public class Inicio extends javax.swing.JFrame {
    private accionTabla event;
    int xMouse;
    int yMouse;
    private JPanel panelSeleccionado = null;
    private int bandera;
    /**
     * Creates new form hola
     */
    public Inicio() {
        initComponents();
        tablaVenta.getColumnModel().getColumn(6).setPreferredWidth(30); // Ajusta el ancho según sea necesario
        tablaVenta.setRowHeight(30);
        setSize(1040, 647); 
        setLocationRelativeTo(null);
        
        
    event = new accionTabla(){
            @Override
            public void onDelete(int row) {// Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/OverriddenMethodBody
                DefaultTableModel modeloVenta = (DefaultTableModel) tablaVenta.getModel();
                DefaultTableModel modeloInventario = (DefaultTableModel) tablaInventario.getModel();

        // Recuperar datos de la fila seleccionada en tablaVenta
        int productoCodigo = (int) modeloVenta.getValueAt(row, 0);
        int productoCantidad = (int) modeloVenta.getValueAt(row, 4);

        // Eliminar la fila de tablaVenta
        modeloVenta.removeRow(row);

        // Buscar el producto en tablaInventario y actualizar la cantidad
        for (int i = 0; i < modeloInventario.getRowCount(); i++) {
            int codigoFila = (int) modeloInventario.getValueAt(i, 0);

            if (codigoFila == productoCodigo) {    
                int cantidadActual = (int) modeloInventario.getValueAt(i, 3);
                int nuevaCantidad = cantidadActual + productoCantidad;

                // Actualizar la cantidad en la tabla de inventario
                modeloInventario.setValueAt(nuevaCantidad, i, 3);

                // Actualizar la cantidad en la base de datos
                try {
                    Conexion cn = new Conexion();
                    Connection con = cn.conectar();
                    String updateQuery = "UPDATE productos SET cantidad = " + nuevaCantidad + " WHERE codigo = " + codigoFila;
                    Statement st = con.createStatement();
                    st.executeUpdate(updateQuery);
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Error al actualizar la cantidad en la base de datos: " + ex.getMessage());
                }
                break; // Salir del bucle una vez que se ha encontrado y actualizado el producto
            }
        }

        // Recalcular el total y la cantidad total de productos en tablaVenta
        double total = 0;
        int totalCantidad = 0;
        for (int i = 0; i < modeloVenta.getRowCount(); i++) {
            total += (double) modeloVenta.getValueAt(i, 5);
            totalCantidad += (int) modeloVenta.getValueAt(i, 4);
        }
        txtTotal.setText(String.valueOf(total));
        txtTotalproductos.setText(String.valueOf(totalCantidad));
    }
    };
    tablaVenta.getColumnModel().getColumn(6).setCellEditor(new editarIcono(event));
    tablaVenta.addMouseMotionListener(new MouseAdapter(){
        @Override
        public void mouseMoved(MouseEvent e) {
            int row = tablaVenta.rowAtPoint(e.getPoint());
            int column = tablaVenta.columnAtPoint(e.getPoint());

            if (column == 6) { // Asegúrate de que el número de columna coincida con el de la columna del botón
                Rectangle cellRect = tablaVenta.getCellRect(row, column, true);
            
            // Define el área del icono dentro de la celda (ajusta según el tamaño y posición del icono)
            Rectangle iconRect = new Rectangle(
                cellRect.x + 2, // Margen izquierdo
                cellRect.y + 2, // Margen superior
                26,             // Ancho del icono
                26              // Alto del icono
            );

            if (iconRect.contains(e.getPoint())) {
                tablaVenta.setCursor(new Cursor(Cursor.HAND_CURSOR));
            } else {
                tablaVenta.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        } else {
            tablaVenta.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        }
    }
    });
    

        MenuProducto.setVisible(false);
        MenuProveedor.setVisible(false);
        Venta.setVisible(false);
        //this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        
        
        productoCon Inve = new productoCon();
        Inve.mostrarTabla(tablaInventario, 0, "");
        
        Conexion Prove = new Conexion();
        Prove.mostrarTablaProveedor(tablaProveedores, "", "");

        /*productoCon Fact = new productoCon();
        Fact.mostrarTablaFactura(tablasemFacturas);*/
       
        productoCon com = new productoCon();
        com.llenarCombo(comboCategoria);
        
        LocalDate now = LocalDate.now();
        int year = now.getYear();
        int dia = now.getDayOfMonth();
        int month = now.getMonthValue();
        String[] meses = {"Enero","Febrero","Marzo","Abril","Mayo","Junio","Julio","Agosto"," ;Septiembre"
            ,"Octubre","Noviembre","Diciemrbre"};
        fecha.setText("Hoy es "+dia+" de "+meses[month - 1]+" de "+year);
        
}
    public String Forma(String texto) {
    return "<html><p style='width:100px;'>" + texto.replace("\n", "<br>") + "</p></html>";
}
    public void setBandera(int bandera) {
        this.bandera = bandera;
    }
    
    void actualizarInterfaz() {
        if (bandera==1) {
            MenuProducto.setVisible(true);
            btn_prin.setVisible(false);
            btn_lends.setVisible(false);
            btn_returns.setVisible(false);
            btn_users.setVisible(false);
            btn_books.setVisible(false);
            btn_reports.setVisible(false);
        } 
    }
    
    void actualizarInterfaz2() {
        if (bandera==2) {
            MenuProveedor.setVisible(true);
            btn_prin.setVisible(false);
            btn_lends.setVisible(false);
            btn_returns.setVisible(false);
            btn_users.setVisible(false);
            btn_books.setVisible(false);
            btn_reports.setVisible(false);
        } 
    }
    
        void eliminarFactura() {
        if (bandera==3) {
            DefaultTableModel modelo = (DefaultTableModel) tablaFactura.getModel();
            int fila = tablaFactura.getSelectedRow();
            int contador = 0;
            if (fila != -1) {
            modelo.removeRow(fila);
            contador++;
            contenidoFac.setText("");
            txtfactEli.setText(String.valueOf(contador));
        } else {
            JOptionPane.showMessageDialog(null, "Por favor, selecciona una fila para eliminar.");
        }
        } 
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLayeredPane1 = new javax.swing.JLayeredPane();
        jPanel17 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        Venta = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tablaVenta = new javax.swing.JTable();
        txtTotal = new javax.swing.JTextField();
        Title14 = new javax.swing.JLabel();
        comboPago = new javax.swing.JComboBox<>();
        Title16 = new javax.swing.JLabel();
        txtPago = new javax.swing.JTextField();
        Title = new javax.swing.JLabel();
        txtCambio = new javax.swing.JTextField();
        txtcantVenta = new javax.swing.JTextField();
        jSeparator6 = new javax.swing.JSeparator();
        Title2 = new javax.swing.JLabel();
        textcodVenta = new javax.swing.JTextField();
        jSeparator9 = new javax.swing.JSeparator();
        Title1 = new javax.swing.JLabel();
        textdesVenta = new javax.swing.JTextField();
        jSeparator10 = new javax.swing.JSeparator();
        Title17 = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        Title6 = new javax.swing.JLabel();
        txtTotalproductos = new javax.swing.JTextField();
        Title8 = new javax.swing.JLabel();
        exit1 = new javax.swing.JLabel();
        btnAgregar = new javax.swing.JLabel();
        btnAceptar = new javax.swing.JLabel();
        btnCancelar = new javax.swing.JLabel();
        Cabecera = new javax.swing.JPanel();
        fecha = new javax.swing.JLabel();
        exit = new javax.swing.JLabel();
        Pestañas = new javax.swing.JTabbedPane();
        jPanel4 = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jPanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tablaFactura = new javax.swing.JTable();
        botonEliminarventa = new javax.swing.JPanel();
        txtfactEli = new javax.swing.JTextField();
        Title4 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        botonCerrar = new javax.swing.JLabel();
        jPanel13 = new javax.swing.JPanel();
        Title10 = new javax.swing.JLabel();
        contenidoFac = new javax.swing.JLabel();
        Title15 = new javax.swing.JLabel();
        jPanel18 = new javax.swing.JPanel();
        jLabel18 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tablaInventario = new javax.swing.JTable();
        jSeparator3 = new javax.swing.JSeparator();
        txtbuscarDescripcion = new javax.swing.JTextField();
        Title11 = new javax.swing.JLabel();
        jSeparator4 = new javax.swing.JSeparator();
        txtbuscarCodigo = new javax.swing.JTextField();
        Title12 = new javax.swing.JLabel();
        comboCategoria = new javax.swing.JComboBox<>();
        Title13 = new javax.swing.JLabel();
        nuevo = new javax.swing.JPanel();
        Title3 = new javax.swing.JLabel();
        Editar = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        newReport = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jPanel12 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        tablatotalFacturas = new javax.swing.JTable();
        Title18 = new javax.swing.JLabel();
        txtGanancia = new javax.swing.JTextField();
        jPanel14 = new javax.swing.JPanel();
        jPanel15 = new javax.swing.JPanel();
        pane_semanal = new javax.swing.JPanel();
        btn_semanal = new javax.swing.JLabel();
        pane_mensual = new javax.swing.JPanel();
        btn_Mensual = new javax.swing.JLabel();
        pane_anual = new javax.swing.JPanel();
        btn_anual = new javax.swing.JLabel();
        pane_dias = new javax.swing.JPanel();
        btn_anual1 = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jPanel11 = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        tablaProveedores = new javax.swing.JTable();
        Title5 = new javax.swing.JLabel();
        Title19 = new javax.swing.JLabel();
        txtbuscarNombre = new javax.swing.JTextField();
        jSeparator11 = new javax.swing.JSeparator();
        Title20 = new javax.swing.JLabel();
        txtbuscarRFC = new javax.swing.JTextField();
        jSeparator12 = new javax.swing.JSeparator();
        jLabel3 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        editarPro = new javax.swing.JLabel();
        jPanel9 = new javax.swing.JPanel();
        jPanel10 = new javax.swing.JPanel();
        Title7 = new javax.swing.JLabel();
        Text3 = new javax.swing.JLabel();
        txtFechaInicio = new javax.swing.JTextField();
        jSeparator13 = new javax.swing.JSeparator();
        jSeparator14 = new javax.swing.JSeparator();
        btn_grafica = new javax.swing.JPanel();
        jLabel14 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        Text4 = new javax.swing.JLabel();
        txtFechaFin = new javax.swing.JTextField();
        jSeparator15 = new javax.swing.JSeparator();
        Menu = new javax.swing.JPanel();
        MenuProveedor = new javax.swing.JPanel();
        txtDom = new javax.swing.JTextField();
        jSeparator16 = new javax.swing.JSeparator();
        jLabel31 = new javax.swing.JLabel();
        jLabel32 = new javax.swing.JLabel();
        txtTel = new javax.swing.JTextField();
        txtRFC = new javax.swing.JTextField();
        jLabel33 = new javax.swing.JLabel();
        jLabel34 = new javax.swing.JLabel();
        txtApel = new javax.swing.JTextField();
        txtNom = new javax.swing.JTextField();
        jLabel35 = new javax.swing.JLabel();
        jSeparator17 = new javax.swing.JSeparator();
        app_name3 = new javax.swing.JLabel();
        eliminarPro = new javax.swing.JLabel();
        btn_AgregarPro = new javax.swing.JPanel();
        jLabel21 = new javax.swing.JLabel();
        btnCancelarPro = new javax.swing.JPanel();
        jLabel36 = new javax.swing.JLabel();
        jLabel37 = new javax.swing.JLabel();
        guardarPro = new javax.swing.JLabel();
        jLabel38 = new javax.swing.JLabel();
        txtPro = new javax.swing.JTextField();
        MenuProducto = new javax.swing.JPanel();
        txtPrecio = new javax.swing.JTextField();
        jSeparator7 = new javax.swing.JSeparator();
        jLabel24 = new javax.swing.JLabel();
        txtCantidadNueva = new javax.swing.JTextField();
        txtCategoria = new javax.swing.JTextField();
        jLabel26 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        txtDescripcion = new javax.swing.JTextField();
        txtCodigo = new javax.swing.JTextField();
        jLabel28 = new javax.swing.JLabel();
        jSeparator8 = new javax.swing.JSeparator();
        app_name2 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        btn_Agregar = new javax.swing.JPanel();
        jLabel20 = new javax.swing.JLabel();
        btn_cancelar = new javax.swing.JPanel();
        jLabel29 = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel39 = new javax.swing.JLabel();
        txtCantidad = new javax.swing.JTextField();
        btn_users = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        btn_books = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        btn_lends = new javax.swing.JPanel();
        jLabel16 = new javax.swing.JLabel();
        btn_reports = new javax.swing.JPanel();
        jLabel15 = new javax.swing.JLabel();
        jSeparator5 = new javax.swing.JSeparator();
        btn_prin = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        app_name1 = new javax.swing.JLabel();
        btn_returns = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();

        javax.swing.GroupLayout jLayeredPane1Layout = new javax.swing.GroupLayout(jLayeredPane1);
        jLayeredPane1.setLayout(jLayeredPane1Layout);
        jLayeredPane1Layout.setHorizontalGroup(
            jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        jLayeredPane1Layout.setVerticalGroup(
            jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel17Layout = new javax.swing.GroupLayout(jPanel17);
        jPanel17.setLayout(jPanel17Layout);
        jPanel17Layout.setHorizontalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        jPanel17Layout.setVerticalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);
        setResizable(false);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        Venta.setBackground(new java.awt.Color(255, 255, 255));
        Venta.setName(""); // NOI18N
        Venta.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        tablaVenta.setBackground(new java.awt.Color(204, 204, 255));
        tablaVenta.setFont(new java.awt.Font("Roboto", 0, 14)); // NOI18N
        tablaVenta.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Codigo", "Categoria", "Descripcion", "Precio con IVA", "Cantidad", "Subtotal", "Eliminar"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tablaVenta.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                tablaVentaMousePressed(evt);
            }
        });
        jScrollPane3.setViewportView(tablaVenta);

        Venta.add(jScrollPane3, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 147, 740, 500));

        txtTotal.setEditable(false);
        txtTotal.setBackground(new java.awt.Color(204, 204, 255));
        txtTotal.setFont(new java.awt.Font("Roboto", 0, 12)); // NOI18N
        txtTotal.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 153, 255)));
        txtTotal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtTotalActionPerformed(evt);
            }
        });
        Venta.add(txtTotal, new org.netbeans.lib.awtextra.AbsoluteConstraints(760, 230, 260, 40));

        Title14.setFont(new java.awt.Font("Roboto Medium", 0, 24)); // NOI18N
        Title14.setText("Total a Cobrar:");
        Title14.setToolTipText("");
        Venta.add(Title14, new org.netbeans.lib.awtextra.AbsoluteConstraints(760, 190, -1, -1));

        comboPago.setBackground(new java.awt.Color(204, 204, 255));
        comboPago.setFont(new java.awt.Font("Roboto", 0, 18)); // NOI18N
        comboPago.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Efectivo", "Tajeta" }));
        comboPago.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 153, 255)));
        Venta.add(comboPago, new org.netbeans.lib.awtextra.AbsoluteConstraints(910, 310, 110, -1));

        Title16.setFont(new java.awt.Font("Roboto Medium", 0, 24)); // NOI18N
        Title16.setText("Pago:");
        Title16.setToolTipText("");
        Venta.add(Title16, new org.netbeans.lib.awtextra.AbsoluteConstraints(760, 310, -1, 30));

        txtPago.setFont(new java.awt.Font("Roboto", 0, 12)); // NOI18N
        txtPago.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 153, 255)));
        txtPago.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtPagoKeyReleased(evt);
            }
        });
        Venta.add(txtPago, new org.netbeans.lib.awtextra.AbsoluteConstraints(760, 350, 260, 40));

        Title.setFont(new java.awt.Font("Roboto Medium", 0, 24)); // NOI18N
        Title.setText("Cambio:");
        Title.setToolTipText("");
        Venta.add(Title, new org.netbeans.lib.awtextra.AbsoluteConstraints(760, 400, -1, -1));

        txtCambio.setEditable(false);
        txtCambio.setBackground(new java.awt.Color(204, 204, 255));
        txtCambio.setFont(new java.awt.Font("Roboto", 0, 12)); // NOI18N
        txtCambio.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 153, 255)));
        Venta.add(txtCambio, new org.netbeans.lib.awtextra.AbsoluteConstraints(760, 440, 260, 40));

        txtcantVenta.setFont(new java.awt.Font("Roboto", 0, 14)); // NOI18N
        txtcantVenta.setBorder(null);
        txtcantVenta.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        txtcantVenta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtcantVentaActionPerformed(evt);
            }
        });
        Venta.add(txtcantVenta, new org.netbeans.lib.awtextra.AbsoluteConstraints(630, 100, 150, 20));

        jSeparator6.setForeground(new java.awt.Color(0, 153, 255));
        jSeparator6.setPreferredSize(new java.awt.Dimension(250, 10));
        Venta.add(jSeparator6, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 120, 160, 20));

        Title2.setFont(new java.awt.Font("Roboto", 0, 18)); // NOI18N
        Title2.setText("Cantidad:");
        Title2.setToolTipText("");
        Venta.add(Title2, new org.netbeans.lib.awtextra.AbsoluteConstraints(540, 100, -1, 20));

        textcodVenta.setFont(new java.awt.Font("Roboto", 0, 14)); // NOI18N
        textcodVenta.setBorder(null);
        textcodVenta.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        textcodVenta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                textcodVentaActionPerformed(evt);
            }
        });
        Venta.add(textcodVenta, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 100, 150, -1));

        jSeparator9.setForeground(new java.awt.Color(0, 153, 255));
        jSeparator9.setPreferredSize(new java.awt.Dimension(250, 10));
        Venta.add(jSeparator9, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 120, 160, 20));

        Title1.setFont(new java.awt.Font("Roboto", 0, 18)); // NOI18N
        Title1.setText("Codigo:");
        Title1.setToolTipText("");
        Venta.add(Title1, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 100, -1, -1));

        textdesVenta.setFont(new java.awt.Font("Roboto", 0, 14)); // NOI18N
        textdesVenta.setBorder(null);
        textdesVenta.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        textdesVenta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                textdesVentaActionPerformed(evt);
            }
        });
        Venta.add(textdesVenta, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 100, 150, -1));

        jSeparator10.setForeground(new java.awt.Color(0, 153, 255));
        jSeparator10.setPreferredSize(new java.awt.Dimension(250, 10));
        Venta.add(jSeparator10, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 120, 160, 20));

        Title17.setFont(new java.awt.Font("Roboto", 0, 18)); // NOI18N
        Title17.setText("Descripción:");
        Title17.setToolTipText("");
        Venta.add(Title17, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 100, -1, -1));

        jPanel7.setBackground(new java.awt.Color(25, 118, 210));

        Title6.setFont(new java.awt.Font("Roboto Medium", 0, 36)); // NOI18N
        Title6.setForeground(new java.awt.Color(255, 255, 255));
        Title6.setText("Venta de productos");

        txtTotalproductos.setEditable(false);
        txtTotalproductos.setBackground(new java.awt.Color(204, 204, 255));
        txtTotalproductos.setFont(new java.awt.Font("Roboto", 0, 14)); // NOI18N

        Title8.setFont(new java.awt.Font("Roboto", 0, 14)); // NOI18N
        Title8.setForeground(new java.awt.Color(255, 255, 255));
        Title8.setText("Productos en la venta actual:");
        Title8.setToolTipText("");

        exit1.setBackground(new java.awt.Color(255, 255, 255));
        exit1.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        exit1.setForeground(new java.awt.Color(255, 255, 255));
        exit1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        exit1.setText("X");
        exit1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        exit1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                exit1MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                exit1MouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                exit1MousePressed(evt);
            }
        });

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGap(32, 32, 32)
                .addComponent(Title6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 379, Short.MAX_VALUE)
                .addComponent(Title8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtTotalproductos, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(42, 42, 42)
                .addComponent(exit1, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(Title6)
                .addContainerGap(9, Short.MAX_VALUE))
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGap(31, 31, 31)
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(Title8, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtTotalproductos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(exit1, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, Short.MAX_VALUE))
        );

        Venta.add(jPanel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1040, 70));

        btnAgregar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/images/agregar (1).png"))); // NOI18N
        btnAgregar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnAgregar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                btnAgregarMousePressed(evt);
            }
        });
        Venta.add(btnAgregar, new org.netbeans.lib.awtextra.AbsoluteConstraints(800, 90, -1, -1));

        btnAceptar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/images/aceptarlisto.png"))); // NOI18N
        btnAceptar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnAceptar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                btnAceptarMousePressed(evt);
            }
        });
        Venta.add(btnAceptar, new org.netbeans.lib.awtextra.AbsoluteConstraints(800, 570, -1, -1));

        btnCancelar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/images/cancelarlisto.png"))); // NOI18N
        btnCancelar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnCancelar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                btnCancelarMousePressed(evt);
            }
        });
        Venta.add(btnCancelar, new org.netbeans.lib.awtextra.AbsoluteConstraints(940, 570, -1, -1));

        jPanel1.add(Venta, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, -3, -1, 650));

        Cabecera.setBackground(new java.awt.Color(25, 118, 210));

        fecha.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        fecha.setForeground(new java.awt.Color(255, 255, 255));
        fecha.setText("Hoy es Sábado 28 de Abril de 2018");

        exit.setBackground(new java.awt.Color(255, 255, 255));
        exit.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        exit.setForeground(new java.awt.Color(255, 255, 255));
        exit.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        exit.setText("X");
        exit.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        exit.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                exitMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                exitMouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                exitMousePressed(evt);
            }
        });

        javax.swing.GroupLayout CabeceraLayout = new javax.swing.GroupLayout(Cabecera);
        Cabecera.setLayout(CabeceraLayout);
        CabeceraLayout.setHorizontalGroup(
            CabeceraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(CabeceraLayout.createSequentialGroup()
                .addGap(65, 65, 65)
                .addComponent(fecha)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 294, Short.MAX_VALUE)
                .addComponent(exit, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        CabeceraLayout.setVerticalGroup(
            CabeceraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(CabeceraLayout.createSequentialGroup()
                .addGroup(CabeceraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(CabeceraLayout.createSequentialGroup()
                        .addGap(27, 27, 27)
                        .addComponent(fecha, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(CabeceraLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(exit)))
                .addGap(43, 43, 43))
        );

        jPanel1.add(Cabecera, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 0, 770, 100));

        jPanel8.setBackground(new java.awt.Color(255, 255, 255));

        jLabel5.setFont(new java.awt.Font("Roboto Black", 1, 36)); // NOI18N
        jLabel5.setText("¡Bienvenido!");

        jLabel8.setFont(new java.awt.Font("Roboto", 2, 14)); // NOI18N
        jLabel8.setText("\"Nuestra misión es producir ropa de calidad a precios que permitan a nuestros clientes");

        jLabel10.setFont(new java.awt.Font("Roboto", 2, 14)); // NOI18N
        jLabel10.setText("disfrutar del estilo sin salirse de su presupuesto. Creemos en la importancia de la accesibilidad");

        jLabel12.setFont(new java.awt.Font("Roboto", 2, 14)); // NOI18N
        jLabel12.setText(" y nos esforzamos por democratizar la moda para que nuestra ropa sea accesible para todos.\"");

        jLabel6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/images/fondochico.jpg"))); // NOI18N

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGap(270, 270, 270)
                .addComponent(jLabel5))
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGap(130, 130, 130)
                .addComponent(jLabel8))
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGap(110, 110, 110)
                .addComponent(jLabel12))
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGap(110, 110, 110)
                .addComponent(jLabel10))
            .addComponent(jLabel6)
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(3, 3, 3)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addGap(7, 7, 7)
                        .addComponent(jLabel8))
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addGap(67, 67, 67)
                        .addComponent(jLabel12))
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addGap(37, 37, 37)
                        .addComponent(jLabel10))
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 440, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        Pestañas.addTab("Inicio", jPanel4);

        jPanel.setBackground(new java.awt.Color(255, 255, 255));
        jPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        tablaFactura.setBackground(new java.awt.Color(204, 204, 255));
        tablaFactura.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No.", "Cant.Productos", "Total", "Forma de pago", "Pago", "Cambio"
            }
        ));
        tablaFactura.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tablaFacturaMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tablaFactura);

        jPanel.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 74, 539, 520));

        botonEliminarventa.setBackground(new java.awt.Color(18, 90, 173));
        botonEliminarventa.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        botonEliminarventa.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                botonEliminarventaMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                botonEliminarventaMouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                botonEliminarventaMousePressed(evt);
            }
        });
        botonEliminarventa.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        jPanel.add(botonEliminarventa, new org.netbeans.lib.awtextra.AbsoluteConstraints(294, 82, 132, -1));

        txtfactEli.setEditable(false);
        txtfactEli.setBackground(new java.awt.Color(204, 204, 255));
        txtfactEli.setFont(new java.awt.Font("Roboto", 0, 14)); // NOI18N
        txtfactEli.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtfactEli.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtfactEliActionPerformed(evt);
            }
        });
        jPanel.add(txtfactEli, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 30, 32, -1));

        Title4.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jPanel.add(Title4, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 6, 335, -1));

        jPanel2.setBackground(new java.awt.Color(0, 134, 190));

        botonCerrar.setFont(new java.awt.Font("Roboto Medium", 0, 14)); // NOI18N
        botonCerrar.setForeground(new java.awt.Color(255, 255, 255));
        botonCerrar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/images/file-chart.png"))); // NOI18N
        botonCerrar.setText("Cerrar Venta");
        botonCerrar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        botonCerrar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                botonCerrarMousePressed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(botonCerrar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(botonCerrar, javax.swing.GroupLayout.DEFAULT_SIZE, 36, Short.MAX_VALUE)
        );

        jPanel.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(629, 20, -1, -1));

        Title10.setFont(new java.awt.Font("Roboto", 0, 14)); // NOI18N
        Title10.setText("Contenido de factura:");
        Title10.setToolTipText("");

        contenidoFac.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(contenidoFac, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel13Layout.createSequentialGroup()
                        .addComponent(Title10)
                        .addGap(0, 64, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel13Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(Title10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(contenidoFac, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel.add(jPanel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(545, 74, -1, 520));

        Title15.setFont(new java.awt.Font("Roboto", 0, 14)); // NOI18N
        Title15.setText("Facturas eliminadas:");
        Title15.setToolTipText("");
        jPanel.add(Title15, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 30, -1, -1));

        jPanel18.setBackground(new java.awt.Color(0, 134, 190));
        jPanel18.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jPanel18.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jPanel18MousePressed(evt);
            }
        });

        jLabel18.setFont(new java.awt.Font("Roboto Medium", 0, 18)); // NOI18N
        jLabel18.setForeground(new java.awt.Color(255, 255, 255));
        jLabel18.setText("Eliminar");

        javax.swing.GroupLayout jPanel18Layout = new javax.swing.GroupLayout(jPanel18);
        jPanel18.setLayout(jPanel18Layout);
        jPanel18Layout.setHorizontalGroup(
            jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel18Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel18)
                .addContainerGap(7, Short.MAX_VALUE))
        );
        jPanel18Layout.setVerticalGroup(
            jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel18Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel18)
                .addContainerGap())
        );

        jPanel.add(jPanel18, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 20, 80, -1));

        Pestañas.addTab("Facturas", jPanel);

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setPreferredSize(new java.awt.Dimension(770, 600));

        tablaInventario.setBackground(new java.awt.Color(204, 204, 255));
        tablaInventario.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(51, 51, 51)));
        tablaInventario.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Codigo", "Categoria", "Descripcion", "Stock", "Precio Unitario"
            }
        ));
        tablaInventario.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tablaInventarioMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(tablaInventario);

        jSeparator3.setForeground(new java.awt.Color(0, 153, 255));
        jSeparator3.setPreferredSize(new java.awt.Dimension(250, 10));

        txtbuscarDescripcion.setFont(new java.awt.Font("Roboto", 0, 14)); // NOI18N
        txtbuscarDescripcion.setBorder(null);
        txtbuscarDescripcion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtbuscarDescripcionActionPerformed(evt);
            }
        });
        txtbuscarDescripcion.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtbuscarDescripcionKeyReleased(evt);
            }
        });

        Title11.setFont(new java.awt.Font("Roboto", 0, 14)); // NOI18N
        Title11.setText("Descripción:");
        Title11.setToolTipText("");

        jSeparator4.setForeground(new java.awt.Color(0, 153, 255));
        jSeparator4.setPreferredSize(new java.awt.Dimension(250, 10));

        txtbuscarCodigo.setFont(new java.awt.Font("Roboto", 0, 14)); // NOI18N
        txtbuscarCodigo.setBorder(null);
        txtbuscarCodigo.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtbuscarCodigoKeyReleased(evt);
            }
        });

        Title12.setFont(new java.awt.Font("Roboto", 0, 14)); // NOI18N
        Title12.setText("Codigo:");
        Title12.setToolTipText("");

        comboCategoria.setBackground(new java.awt.Color(204, 204, 204));
        comboCategoria.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                comboCategoriaItemStateChanged(evt);
            }
        });

        Title13.setFont(new java.awt.Font("Roboto", 0, 14)); // NOI18N
        Title13.setText("Categoria");
        Title13.setToolTipText("");

        nuevo.setBackground(new java.awt.Color(18, 90, 173));
        nuevo.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        nuevo.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                nuevoMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                nuevoMouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                nuevoMousePressed(evt);
            }
        });
        nuevo.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        Title3.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        Title3.setText("Filtrar busqueda");

        Editar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/images/iconoHerramienta.png"))); // NOI18N
        Editar.setText("Ajustes");
        Editar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        Editar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                EditarMousePressed(evt);
            }
        });

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/images/limp.png"))); // NOI18N
        jLabel2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jLabel2MousePressed(evt);
            }
        });

        newReport.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/images/nuevo-producto.png"))); // NOI18N
        newReport.setText("Nuevo Reporte");
        newReport.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        newReport.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                newReportMousePressed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 883, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(Title11)
                                .addGap(3, 3, 3)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                                    .addComponent(txtbuscarDescripcion, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(31, 31, 31)
                                .addComponent(Title12)
                                .addGap(3, 3, 3)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jSeparator4, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                                    .addComponent(txtbuscarCodigo, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(Title13)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(comboCategoria, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(6, 6, 6))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(Title3, javax.swing.GroupLayout.PREFERRED_SIZE, 335, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                                        .addComponent(nuevo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(84, 84, 84))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                                        .addComponent(newReport)
                                        .addGap(18, 18, 18)
                                        .addComponent(Editar)
                                        .addGap(15, 15, 15)))))))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(Title3)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(Editar)
                        .addComponent(newReport)))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addGap(7, 7, 7)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(Title11)
                                    .addComponent(txtbuscarDescripcion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(Title12)
                                        .addComponent(txtbuscarCodigo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(3, 3, 3)
                                .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jSeparator4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jLabel2)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(comboCategoria, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(Title13)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(nuevo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 410, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(176, 176, 176))
        );

        Pestañas.addTab("Inventario", jPanel3);

        jPanel12.setBackground(new java.awt.Color(255, 255, 255));
        jPanel12.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        tablatotalFacturas.setBackground(new java.awt.Color(204, 204, 255));
        tablatotalFacturas.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No.", "Total", "Fecha"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, true, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane4.setViewportView(tablatotalFacturas);

        jPanel12.add(jScrollPane4, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 80, 760, 470));

        Title18.setFont(new java.awt.Font("Roboto", 0, 14)); // NOI18N
        Title18.setText("GananciaTotal:");
        Title18.setToolTipText("");
        jPanel12.add(Title18, new org.netbeans.lib.awtextra.AbsoluteConstraints(570, 40, 99, -1));

        txtGanancia.setEditable(false);
        txtGanancia.setBackground(new java.awt.Color(204, 204, 255));
        txtGanancia.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(51, 51, 51)));
        jPanel12.add(txtGanancia, new org.netbeans.lib.awtextra.AbsoluteConstraints(674, 40, 70, 20));

        javax.swing.GroupLayout jPanel14Layout = new javax.swing.GroupLayout(jPanel14);
        jPanel14.setLayout(jPanel14Layout);
        jPanel14Layout.setHorizontalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel14Layout.setVerticalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        jPanel12.add(jPanel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 60, -1, -1));

        javax.swing.GroupLayout jPanel15Layout = new javax.swing.GroupLayout(jPanel15);
        jPanel15.setLayout(jPanel15Layout);
        jPanel15Layout.setHorizontalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel15Layout.setVerticalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        jPanel12.add(jPanel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(530, 130, -1, -1));

        pane_semanal.setBackground(new java.awt.Color(18, 90, 173));
        pane_semanal.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                pane_semanalMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                pane_semanalMouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                pane_semanalMousePressed(evt);
            }
        });

        btn_semanal.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        btn_semanal.setForeground(new java.awt.Color(255, 255, 255));
        btn_semanal.setText("Semanal");
        btn_semanal.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        javax.swing.GroupLayout pane_semanalLayout = new javax.swing.GroupLayout(pane_semanal);
        pane_semanal.setLayout(pane_semanalLayout);
        pane_semanalLayout.setHorizontalGroup(
            pane_semanalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pane_semanalLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btn_semanal)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pane_semanalLayout.setVerticalGroup(
            pane_semanalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pane_semanalLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btn_semanal)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel12.add(pane_semanal, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 40, 80, 40));

        pane_mensual.setBackground(new java.awt.Color(18, 90, 173));
        pane_mensual.setPreferredSize(new java.awt.Dimension(80, 40));
        pane_mensual.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                pane_mensualMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                pane_mensualMouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                pane_mensualMousePressed(evt);
            }
        });

        btn_Mensual.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        btn_Mensual.setForeground(new java.awt.Color(255, 255, 255));
        btn_Mensual.setText("Mensual");
        btn_Mensual.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        javax.swing.GroupLayout pane_mensualLayout = new javax.swing.GroupLayout(pane_mensual);
        pane_mensual.setLayout(pane_mensualLayout);
        pane_mensualLayout.setHorizontalGroup(
            pane_mensualLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pane_mensualLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btn_Mensual)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pane_mensualLayout.setVerticalGroup(
            pane_mensualLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pane_mensualLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btn_Mensual)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel12.add(pane_mensual, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 40, -1, 40));

        pane_anual.setBackground(new java.awt.Color(18, 90, 173));
        pane_anual.setDoubleBuffered(false);
        pane_anual.setPreferredSize(new java.awt.Dimension(80, 40));
        pane_anual.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                pane_anualMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                pane_anualMouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                pane_anualMousePressed(evt);
            }
        });

        btn_anual.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        btn_anual.setForeground(new java.awt.Color(255, 255, 255));
        btn_anual.setText("Anual");
        btn_anual.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        javax.swing.GroupLayout pane_anualLayout = new javax.swing.GroupLayout(pane_anual);
        pane_anual.setLayout(pane_anualLayout);
        pane_anualLayout.setHorizontalGroup(
            pane_anualLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pane_anualLayout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addComponent(btn_anual)
                .addContainerGap(18, Short.MAX_VALUE))
        );
        pane_anualLayout.setVerticalGroup(
            pane_anualLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pane_anualLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btn_anual)
                .addContainerGap(9, Short.MAX_VALUE))
        );

        jPanel12.add(pane_anual, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 40, -1, -1));

        pane_dias.setBackground(new java.awt.Color(18, 90, 173));
        pane_dias.setPreferredSize(new java.awt.Dimension(80, 40));
        pane_dias.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                pane_diasMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                pane_diasMouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                pane_diasMousePressed(evt);
            }
        });

        btn_anual1.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        btn_anual1.setForeground(new java.awt.Color(255, 255, 255));
        btn_anual1.setText("Dias");
        btn_anual1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        javax.swing.GroupLayout pane_diasLayout = new javax.swing.GroupLayout(pane_dias);
        pane_dias.setLayout(pane_diasLayout);
        pane_diasLayout.setHorizontalGroup(
            pane_diasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pane_diasLayout.createSequentialGroup()
                .addContainerGap(25, Short.MAX_VALUE)
                .addComponent(btn_anual1)
                .addGap(21, 21, 21))
        );
        pane_diasLayout.setVerticalGroup(
            pane_diasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pane_diasLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btn_anual1)
                .addContainerGap(9, Short.MAX_VALUE))
        );

        jPanel12.add(pane_dias, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 40, -1, 40));

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        Pestañas.addTab("Historial", jPanel5);

        jPanel11.setBackground(new java.awt.Color(255, 255, 255));

        tablaProveedores.setBackground(new java.awt.Color(204, 204, 255));
        tablaProveedores.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Nombres", "Apellidos", "RFC", "Telefono", "Domicilio", "Producto"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, true, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tablaProveedores.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tablaProveedoresMouseClicked(evt);
            }
        });
        jScrollPane5.setViewportView(tablaProveedores);

        Title5.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        Title5.setText("Filtrar busqueda");

        Title19.setFont(new java.awt.Font("Roboto", 0, 14)); // NOI18N
        Title19.setText("Nombre:");
        Title19.setToolTipText("");

        txtbuscarNombre.setBorder(null);
        txtbuscarNombre.setMinimumSize(new java.awt.Dimension(64, 17));
        txtbuscarNombre.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtbuscarNombreKeyReleased(evt);
            }
        });

        jSeparator11.setForeground(new java.awt.Color(0, 153, 255));
        jSeparator11.setPreferredSize(new java.awt.Dimension(250, 10));

        Title20.setFont(new java.awt.Font("Roboto", 0, 14)); // NOI18N
        Title20.setText("RFC:");
        Title20.setToolTipText("");

        txtbuscarRFC.setBorder(null);
        txtbuscarRFC.setMinimumSize(new java.awt.Dimension(64, 17));
        txtbuscarRFC.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtbuscarRFCKeyReleased(evt);
            }
        });

        jSeparator12.setForeground(new java.awt.Color(0, 153, 255));
        jSeparator12.setPreferredSize(new java.awt.Dimension(250, 10));

        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/images/limp.png"))); // NOI18N
        jLabel3.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        jLabel19.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/images/nuevo-producto.png"))); // NOI18N
        jLabel19.setText("Nuevo Reporte");
        jLabel19.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jLabel19.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jLabel19MousePressed(evt);
            }
        });

        editarPro.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/images/iconoHerramienta.png"))); // NOI18N
        editarPro.setText("Ajustes");
        editarPro.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                editarProMousePressed(evt);
            }
        });

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addComponent(Title19)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jSeparator11, javax.swing.GroupLayout.PREFERRED_SIZE, 198, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtbuscarNombre, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(30, 30, 30)
                        .addComponent(Title20)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jSeparator12, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                            .addComponent(txtbuscarRFC, javax.swing.GroupLayout.PREFERRED_SIZE, 198, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addComponent(jLabel3)
                        .addContainerGap(319, Short.MAX_VALUE))
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addComponent(Title5, javax.swing.GroupLayout.PREFERRED_SIZE, 335, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel19)
                        .addGap(18, 18, 18)
                        .addComponent(editarPro)
                        .addGap(23, 23, 23))))
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane5)
                .addContainerGap())
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(Title5)
                    .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel19)
                        .addComponent(editarPro)))
                .addGap(17, 17, 17)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(Title19)
                            .addComponent(txtbuscarNombre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSeparator11, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(Title20)
                        .addComponent(jLabel3))
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addComponent(txtbuscarRFC, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(7, 7, 7)
                        .addComponent(jSeparator12, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(14, 14, 14)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 389, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(201, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        Pestañas.addTab("Proveedores", jPanel6);

        jPanel10.setBackground(new java.awt.Color(255, 255, 255));

        Title7.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        Title7.setText("Graficar");

        Text3.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        Text3.setText("Fecha de inicio");

        txtFechaInicio.setForeground(new java.awt.Color(102, 102, 102));
        txtFechaInicio.setText("Ingrese el año (yyyy)");
        txtFechaInicio.setBorder(null);
        txtFechaInicio.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                txtFechaInicioMousePressed(evt);
            }
        });

        jSeparator13.setForeground(new java.awt.Color(0, 153, 255));
        jSeparator13.setPreferredSize(new java.awt.Dimension(200, 10));

        jSeparator14.setForeground(new java.awt.Color(204, 204, 204));
        jSeparator14.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jSeparator14.setPreferredSize(new java.awt.Dimension(200, 10));

        btn_grafica.setBackground(new java.awt.Color(18, 90, 173));
        btn_grafica.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btn_grafica.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn_graficaMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn_graficaMouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                btn_graficaMousePressed(evt);
            }
        });
        btn_grafica.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel14.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(255, 255, 255));
        jLabel14.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel14.setText("Crear");
        btn_grafica.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 10, -1, -1));

        jLabel17.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/images/fondochico (1).jpg"))); // NOI18N

        Text4.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        Text4.setText("Fecha final ");

        txtFechaFin.setForeground(new java.awt.Color(102, 102, 102));
        txtFechaFin.setText("Ingrese el año (yyyy)");
        txtFechaFin.setBorder(null);
        txtFechaFin.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                txtFechaFinMousePressed(evt);
            }
        });
        txtFechaFin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtFechaFinActionPerformed(evt);
            }
        });
        txtFechaFin.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtFechaFinKeyReleased(evt);
            }
        });

        jSeparator15.setForeground(new java.awt.Color(0, 153, 255));
        jSeparator15.setPreferredSize(new java.awt.Dimension(200, 10));

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addGap(40, 40, 40)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btn_grafica, javax.swing.GroupLayout.PREFERRED_SIZE, 260, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Title7)
                    .addComponent(txtFechaInicio, javax.swing.GroupLayout.PREFERRED_SIZE, 260, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jSeparator13, javax.swing.GroupLayout.PREFERRED_SIZE, 260, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtFechaFin, javax.swing.GroupLayout.PREFERRED_SIZE, 260, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jSeparator15, javax.swing.GroupLayout.PREFERRED_SIZE, 260, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Text3)
                    .addComponent(Text4))
                .addGap(55, 55, 55)
                .addComponent(jSeparator14, javax.swing.GroupLayout.PREFERRED_SIZE, 4, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 530, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addGap(93, 93, 93)
                        .addComponent(jSeparator14, javax.swing.GroupLayout.PREFERRED_SIZE, 350, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addGap(182, 182, 182)
                        .addComponent(jLabel17))
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addGap(68, 68, 68)
                        .addComponent(Title7)
                        .addGap(34, 34, 34)
                        .addComponent(Text3)
                        .addGap(10, 10, 10)
                        .addComponent(txtFechaInicio, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(jSeparator13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(26, 26, 26)
                        .addComponent(Text4)
                        .addGap(10, 10, 10)
                        .addComponent(txtFechaFin, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(jSeparator15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(59, 59, 59)
                        .addComponent(btn_grafica, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(239, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, 895, Short.MAX_VALUE)
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        Pestañas.addTab("Estadisticas", jPanel9);

        jPanel1.add(Pestañas, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 60, 770, 580));

        Menu.setBackground(new java.awt.Color(13, 71, 161));
        Menu.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        MenuProveedor.setBackground(new java.awt.Color(13, 71, 161));
        MenuProveedor.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        txtDom.setFont(new java.awt.Font("Roboto", 0, 14)); // NOI18N
        txtDom.setBorder(null);
        txtDom.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        txtDom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtDomActionPerformed(evt);
            }
        });
        MenuProveedor.add(txtDom, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 280, 140, 20));

        jSeparator16.setPreferredSize(new java.awt.Dimension(50, 5));
        MenuProveedor.add(jSeparator16, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 440, 190, 20));

        jLabel31.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel31.setForeground(new java.awt.Color(255, 255, 255));
        jLabel31.setText("Direccion:");
        MenuProveedor.add(jLabel31, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 280, -1, 20));

        jLabel32.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel32.setForeground(new java.awt.Color(255, 255, 255));
        jLabel32.setText("Telefono:");
        MenuProveedor.add(jLabel32, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 240, -1, 20));

        txtTel.setFont(new java.awt.Font("Roboto", 0, 14)); // NOI18N
        txtTel.setBorder(null);
        txtTel.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        txtTel.setMinimumSize(new java.awt.Dimension(20, 20));
        txtTel.setPreferredSize(new java.awt.Dimension(64, 10));
        MenuProveedor.add(txtTel, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 240, 140, 20));

        txtRFC.setFont(new java.awt.Font("Roboto", 0, 14)); // NOI18N
        txtRFC.setBorder(null);
        txtRFC.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        txtRFC.setMinimumSize(new java.awt.Dimension(64, 30));
        txtRFC.setPreferredSize(new java.awt.Dimension(64, 30));
        txtRFC.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtRFCActionPerformed(evt);
            }
        });
        MenuProveedor.add(txtRFC, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 200, 140, 20));

        jLabel33.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel33.setForeground(new java.awt.Color(255, 255, 255));
        jLabel33.setText("RFC:");
        MenuProveedor.add(jLabel33, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 200, -1, 20));

        jLabel34.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel34.setForeground(new java.awt.Color(255, 255, 255));
        jLabel34.setText("Apellidos:");
        MenuProveedor.add(jLabel34, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 160, -1, 20));

        txtApel.setFont(new java.awt.Font("Roboto", 0, 14)); // NOI18N
        txtApel.setBorder(null);
        txtApel.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        txtApel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtApelActionPerformed(evt);
            }
        });
        MenuProveedor.add(txtApel, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 160, 140, 20));

        txtNom.setFont(new java.awt.Font("Roboto", 0, 14)); // NOI18N
        txtNom.setBorder(null);
        txtNom.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        MenuProveedor.add(txtNom, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 120, 140, 20));

        jLabel35.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel35.setForeground(new java.awt.Color(255, 255, 255));
        jLabel35.setText("Nombres:");
        MenuProveedor.add(jLabel35, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 110, -1, 30));

        jSeparator17.setPreferredSize(new java.awt.Dimension(50, 5));
        MenuProveedor.add(jSeparator17, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 100, 190, 20));

        app_name3.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        app_name3.setForeground(new java.awt.Color(255, 255, 255));
        app_name3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        app_name3.setText("Modificar proveedor");
        MenuProveedor.add(app_name3, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 50, 250, -1));

        eliminarPro.setFont(new java.awt.Font("Roboto", 1, 12)); // NOI18N
        eliminarPro.setForeground(new java.awt.Color(255, 255, 255));
        eliminarPro.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        eliminarPro.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/images/basura1.png"))); // NOI18N
        eliminarPro.setText("ELIMINAR");
        eliminarPro.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        eliminarPro.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                eliminarProMousePressed(evt);
            }
        });
        MenuProveedor.add(eliminarPro, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 380, 100, -1));

        btn_AgregarPro.setBackground(new java.awt.Color(21, 101, 192));
        btn_AgregarPro.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btn_AgregarPro.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn_AgregarProMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn_AgregarProMouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                btn_AgregarProMousePressed(evt);
            }
        });
        btn_AgregarPro.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel21.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel21.setForeground(new java.awt.Color(255, 255, 255));
        jLabel21.setText("Agregar nuevo proveedor");
        btn_AgregarPro.add(jLabel21, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 10, 190, 30));

        MenuProveedor.add(btn_AgregarPro, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 470, 270, 50));

        btnCancelarPro.setBackground(new java.awt.Color(21, 101, 192));
        btnCancelarPro.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnCancelarPro.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnCancelarProMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnCancelarProMouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                btnCancelarProMousePressed(evt);
            }
        });
        btnCancelarPro.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel36.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel36.setForeground(new java.awt.Color(255, 255, 255));
        btnCancelarPro.add(jLabel36, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 10, -1, 30));

        jLabel37.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel37.setForeground(new java.awt.Color(255, 255, 255));
        jLabel37.setText("Salir");
        btnCancelarPro.add(jLabel37, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 10, 170, 30));

        MenuProveedor.add(btnCancelarPro, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 521, 270, 50));

        guardarPro.setFont(new java.awt.Font("Roboto", 1, 12)); // NOI18N
        guardarPro.setForeground(new java.awt.Color(255, 255, 255));
        guardarPro.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/images/guardar.png"))); // NOI18N
        guardarPro.setText("GUARDAR");
        guardarPro.setToolTipText("");
        guardarPro.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        guardarPro.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                guardarProMousePressed(evt);
            }
        });
        MenuProveedor.add(guardarPro, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 380, -1, -1));

        jLabel38.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel38.setForeground(new java.awt.Color(255, 255, 255));
        jLabel38.setText("Producto:");
        MenuProveedor.add(jLabel38, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 320, -1, 20));

        txtPro.setFont(new java.awt.Font("Roboto", 0, 14)); // NOI18N
        txtPro.setBorder(null);
        txtPro.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        MenuProveedor.add(txtPro, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 320, 140, 20));

        Menu.add(MenuProveedor, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 570, 270, 70));

        MenuProducto.setBackground(new java.awt.Color(13, 71, 161));
        MenuProducto.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        txtPrecio.setFont(new java.awt.Font("Roboto", 0, 14)); // NOI18N
        txtPrecio.setBorder(null);
        txtPrecio.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        txtPrecio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPrecioActionPerformed(evt);
            }
        });
        MenuProducto.add(txtPrecio, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 340, 140, 20));

        jSeparator7.setPreferredSize(new java.awt.Dimension(50, 5));
        MenuProducto.add(jSeparator7, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 440, 190, 20));

        jLabel24.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel24.setForeground(new java.awt.Color(255, 255, 255));
        jLabel24.setText("Precio:");
        MenuProducto.add(jLabel24, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 340, -1, 20));

        txtCantidadNueva.setFont(new java.awt.Font("Roboto", 0, 14)); // NOI18N
        txtCantidadNueva.setBorder(null);
        txtCantidadNueva.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        txtCantidadNueva.setMinimumSize(new java.awt.Dimension(20, 20));
        txtCantidadNueva.setPreferredSize(new java.awt.Dimension(64, 10));
        MenuProducto.add(txtCantidadNueva, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 300, 60, 20));

        txtCategoria.setFont(new java.awt.Font("Roboto", 0, 14)); // NOI18N
        txtCategoria.setBorder(null);
        txtCategoria.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        txtCategoria.setMinimumSize(new java.awt.Dimension(64, 30));
        txtCategoria.setPreferredSize(new java.awt.Dimension(64, 30));
        MenuProducto.add(txtCategoria, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 260, 140, 20));

        jLabel26.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel26.setForeground(new java.awt.Color(255, 255, 255));
        jLabel26.setText("Categoria:");
        MenuProducto.add(jLabel26, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 260, -1, 20));

        jLabel27.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel27.setForeground(new java.awt.Color(255, 255, 255));
        jLabel27.setText("Descripción:");
        MenuProducto.add(jLabel27, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 220, -1, 20));

        txtDescripcion.setFont(new java.awt.Font("Roboto", 0, 14)); // NOI18N
        txtDescripcion.setBorder(null);
        txtDescripcion.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        txtDescripcion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtDescripcionActionPerformed(evt);
            }
        });
        MenuProducto.add(txtDescripcion, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 220, 140, 20));

        txtCodigo.setEditable(false);
        txtCodigo.setBackground(new java.awt.Color(204, 204, 204));
        txtCodigo.setFont(new java.awt.Font("Roboto", 0, 14)); // NOI18N
        txtCodigo.setBorder(null);
        MenuProducto.add(txtCodigo, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 180, 140, 20));

        jLabel28.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel28.setForeground(new java.awt.Color(255, 255, 255));
        jLabel28.setText("Codigo:");
        MenuProducto.add(jLabel28, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 170, -1, 30));

        jSeparator8.setPreferredSize(new java.awt.Dimension(50, 5));
        MenuProducto.add(jSeparator8, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 162, 190, 20));

        app_name2.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        app_name2.setForeground(new java.awt.Color(255, 255, 255));
        app_name2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        app_name2.setText("Modificar producto");
        MenuProducto.add(app_name2, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 110, 230, -1));

        jLabel22.setFont(new java.awt.Font("Roboto", 1, 12)); // NOI18N
        jLabel22.setForeground(new java.awt.Color(255, 255, 255));
        jLabel22.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel22.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/images/basura1.png"))); // NOI18N
        jLabel22.setText("ELIMINAR");
        jLabel22.setToolTipText("");
        jLabel22.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jLabel22.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jLabel22MousePressed(evt);
            }
        });
        MenuProducto.add(jLabel22, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 390, 100, -1));

        btn_Agregar.setBackground(new java.awt.Color(21, 101, 192));
        btn_Agregar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btn_Agregar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn_AgregarMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn_AgregarMouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                btn_AgregarMousePressed(evt);
            }
        });
        btn_Agregar.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel20.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel20.setForeground(new java.awt.Color(255, 255, 255));
        jLabel20.setText("Agregar nuevo producto");
        btn_Agregar.add(jLabel20, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 10, 170, 30));

        MenuProducto.add(btn_Agregar, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 470, 270, 50));

        btn_cancelar.setBackground(new java.awt.Color(21, 101, 192));
        btn_cancelar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btn_cancelar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn_cancelarMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn_cancelarMouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                btn_cancelarMousePressed(evt);
            }
        });
        btn_cancelar.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel29.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel29.setForeground(new java.awt.Color(255, 255, 255));
        btn_cancelar.add(jLabel29, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 10, -1, 30));

        jLabel30.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel30.setForeground(new java.awt.Color(255, 255, 255));
        jLabel30.setText("Cancelar ");
        btn_cancelar.add(jLabel30, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 10, 170, 30));

        MenuProducto.add(btn_cancelar, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 521, 270, 50));

        jLabel1.setFont(new java.awt.Font("Roboto", 1, 12)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/images/guardar.png"))); // NOI18N
        jLabel1.setText("GUARDAR");
        jLabel1.setToolTipText("");
        jLabel1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jLabel1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jLabel1MousePressed(evt);
            }
        });
        MenuProducto.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 390, -1, -1));

        jLabel39.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel39.setForeground(new java.awt.Color(255, 255, 255));
        jLabel39.setText("Stock:");
        MenuProducto.add(jLabel39, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 300, -1, 20));

        txtCantidad.setEditable(false);
        txtCantidad.setBackground(new java.awt.Color(204, 204, 204));
        txtCantidad.setFont(new java.awt.Font("Roboto", 0, 14)); // NOI18N
        txtCantidad.setBorder(null);
        txtCantidad.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        txtCantidad.setMinimumSize(new java.awt.Dimension(20, 20));
        txtCantidad.setPreferredSize(new java.awt.Dimension(64, 10));
        MenuProducto.add(txtCantidad, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 300, 60, 20));

        Menu.add(MenuProducto, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 20, 270, 620));

        btn_users.setBackground(new java.awt.Color(18, 90, 173));
        btn_users.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        btn_users.setPreferredSize(new java.awt.Dimension(270, 51));
        btn_users.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn_usersMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn_usersMouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                btn_usersMousePressed(evt);
            }
        });
        btn_users.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel11.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(255, 255, 255));
        jLabel11.setText("Historial");
        btn_users.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 10, -1, 30));

        Menu.add(btn_users, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 370, -1, -1));

        btn_books.setBackground(new java.awt.Color(18, 90, 173));
        btn_books.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        btn_books.setPreferredSize(new java.awt.Dimension(270, 51));
        btn_books.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn_booksMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn_booksMouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                btn_booksMousePressed(evt);
            }
        });
        btn_books.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel13.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(255, 255, 255));
        jLabel13.setText("Proveedores");
        btn_books.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 10, -1, 30));

        Menu.add(btn_books, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 420, -1, -1));

        btn_lends.setBackground(new java.awt.Color(18, 90, 173));
        btn_lends.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        btn_lends.setPreferredSize(new java.awt.Dimension(270, 51));
        btn_lends.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn_lendsMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn_lendsMouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                btn_lendsMousePressed(evt);
            }
        });
        btn_lends.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel16.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel16.setForeground(new java.awt.Color(255, 255, 255));
        jLabel16.setText("Facturas");
        btn_lends.add(jLabel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 10, -1, 30));

        Menu.add(btn_lends, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 270, -1, -1));

        btn_reports.setBackground(new java.awt.Color(18, 90, 173));
        btn_reports.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        btn_reports.setPreferredSize(new java.awt.Dimension(270, 51));
        btn_reports.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn_reportsMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn_reportsMouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                btn_reportsMousePressed(evt);
            }
        });
        btn_reports.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel15.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(255, 255, 255));
        jLabel15.setText("Estadisticas");
        btn_reports.add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 10, -1, 30));

        Menu.add(btn_reports, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 470, -1, -1));

        jSeparator5.setPreferredSize(new java.awt.Dimension(50, 5));
        Menu.add(jSeparator5, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 180, 190, 20));

        btn_prin.setBackground(new java.awt.Color(18, 90, 173));
        btn_prin.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        btn_prin.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn_prinMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn_prinMouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                btn_prinMousePressed(evt);
            }
        });
        btn_prin.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setText("Venta");
        btn_prin.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 10, -1, 30));

        Menu.add(btn_prin, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 220, 270, 50));

        app_name1.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        app_name1.setForeground(new java.awt.Color(255, 255, 255));
        app_name1.setText("Inicio");
        Menu.add(app_name1, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 140, -1, -1));

        btn_returns.setBackground(new java.awt.Color(18, 90, 173));
        btn_returns.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        btn_returns.setPreferredSize(new java.awt.Dimension(270, 51));
        btn_returns.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn_returnsMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn_returnsMouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                btn_returnsMousePressed(evt);
            }
        });

        jLabel9.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(255, 255, 255));
        jLabel9.setText("Inventario");

        javax.swing.GroupLayout btn_returnsLayout = new javax.swing.GroupLayout(btn_returns);
        btn_returns.setLayout(btn_returnsLayout);
        btn_returnsLayout.setHorizontalGroup(
            btn_returnsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(btn_returnsLayout.createSequentialGroup()
                .addGap(50, 50, 50)
                .addComponent(jLabel9))
        );
        btn_returnsLayout.setVerticalGroup(
            btn_returnsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(btn_returnsLayout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        Menu.add(btn_returns, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 320, -1, -1));

        jPanel1.add(Menu, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 270, 640));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btn_lendsMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn_lendsMousePressed
        resetColor(btn_prin);
        setColor(btn_lends);
        resetColor(btn_returns);
        resetColor(btn_users);
        resetColor(btn_books);
        resetColor(btn_reports);
        
        Pestañas.setSelectedIndex(1);
    }//GEN-LAST:event_btn_lendsMousePressed

    private void btn_lendsMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn_lendsMouseExited
        if (btn_lends.getBackground().getRGB() != new Color(21,101,192).getRGB()) {
        resetColor(btn_lends);
    }
        if (btn_prin.getBackground().getRGB() == new Color(18,90,173).getRGB() && 
        btn_returns.getBackground().getRGB() == new Color(18,90,173).getRGB() &&
        btn_users.getBackground().getRGB() == new Color(18,90,173).getRGB() && 
        btn_books.getBackground().getRGB() == new Color(18,90,173).getRGB() && 
        btn_reports.getBackground().getRGB() == new Color(18,90,173).getRGB()) {
        
        resetColor(btn_lends);
    }
    }//GEN-LAST:event_btn_lendsMouseExited

    private void btn_lendsMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn_lendsMouseEntered
        if(btn_lends.getBackground().getRGB() == -15574355)
            setColor(btn_lends);
    }//GEN-LAST:event_btn_lendsMouseEntered

    private void btn_reportsMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn_reportsMousePressed
        resetColor(btn_prin);
        resetColor(btn_lends);
        resetColor(btn_returns);
        resetColor(btn_users);
        resetColor(btn_books);
        setColor(btn_reports);
        
        Pestañas.setSelectedIndex(5);
    }//GEN-LAST:event_btn_reportsMousePressed

    private void btn_reportsMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn_reportsMouseExited
        if(btn_lends.getBackground().getRGB() != -15574355 || btn_prin.getBackground().getRGB() != -15574355
        || btn_returns.getBackground().getRGB() != -15574355 || btn_users.getBackground().getRGB() != -15574355 
        || btn_books.getBackground().getRGB() != -15574355) {
        resetColor(btn_reports);
    }
    }//GEN-LAST:event_btn_reportsMouseExited

    private void btn_reportsMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn_reportsMouseEntered
        if(btn_reports.getBackground().getRGB() == -15574355)
            setColor(btn_reports);
    }//GEN-LAST:event_btn_reportsMouseEntered

    private void btn_booksMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn_booksMousePressed
        resetColor(btn_prin);
        resetColor(btn_lends);
        resetColor(btn_returns);
        resetColor(btn_users);
        setColor(btn_books);
        resetColor(btn_reports);
        
        Pestañas.setSelectedIndex(4);
    }//GEN-LAST:event_btn_booksMousePressed

    private void btn_booksMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn_booksMouseExited
        if(btn_lends.getBackground().getRGB() != -15574355 || btn_prin.getBackground().getRGB() != -15574355
            || btn_returns.getBackground().getRGB() != -15574355 || btn_users.getBackground().getRGB() != -15574355 || btn_reports.getBackground().getRGB() != -15574355)
            resetColor(btn_books);
    }//GEN-LAST:event_btn_booksMouseExited

    private void btn_booksMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn_booksMouseEntered
        if(btn_books.getBackground().getRGB() == -15574355)
            setColor(btn_books);
    }//GEN-LAST:event_btn_booksMouseEntered

    private void btn_usersMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn_usersMousePressed
        resetColor(btn_prin);
    resetColor(btn_lends);
    resetColor(btn_returns);
    setColor(btn_users);
    resetColor(btn_books);
    resetColor(btn_reports);
    
    Pestañas.setSelectedIndex(3);
    String nombreTabla = "total_semanal";
    Conexion Fact = new Conexion();
    Fact.mostrarTabla(tablatotalFacturas, nombreTabla);
    txtGanancia.setText("");
    
    double total = 0;
    for (int i = 0; i < tablatotalFacturas.getRowCount(); i++) {
        total += (double) tablatotalFacturas.getValueAt(i, 1);
    }
    txtGanancia.setText(String.valueOf(total));
    }//GEN-LAST:event_btn_usersMousePressed

    private void btn_usersMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn_usersMouseExited
        if(btn_lends.getBackground().getRGB() != -15574355 || btn_prin.getBackground().getRGB() != -15574355
            || btn_returns.getBackground().getRGB() != -15574355 || btn_books.getBackground().getRGB() != -15574355 || btn_reports.getBackground().getRGB() != -15574355)
            resetColor(btn_users);
    }//GEN-LAST:event_btn_usersMouseExited

    private void btn_usersMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn_usersMouseEntered
        if(btn_users.getBackground().getRGB() == -15574355)
            setColor(btn_users);
    }//GEN-LAST:event_btn_usersMouseEntered

    private void btn_returnsMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn_returnsMousePressed
        resetColor(btn_prin);
        resetColor(btn_lends);
        setColor(btn_returns);
        resetColor(btn_users);
        resetColor(btn_books);
        resetColor(btn_reports);
        
        Pestañas.setSelectedIndex(2);
    }//GEN-LAST:event_btn_returnsMousePressed

    private void btn_returnsMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn_returnsMouseExited
        if (btn_returns.getBackground().getRGB() != new Color(21,101,192).getRGB()) {
        resetColor(btn_returns);
    }
        if(btn_lends.getBackground().getRGB() != -15574355 || btn_prin.getBackground().getRGB() != -15574355
            || btn_users.getBackground().getRGB() != -15574355 || btn_books.getBackground().getRGB() != -15574355 || btn_reports.getBackground().getRGB() != -15574355)
            resetColor(btn_returns);
    }//GEN-LAST:event_btn_returnsMouseExited

    private void btn_returnsMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn_returnsMouseEntered
        if(btn_returns.getBackground().getRGB() == -15574355)
            setColor(btn_returns);
    }//GEN-LAST:event_btn_returnsMouseEntered

    private void btn_prinMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn_prinMousePressed
        setColor(btn_prin);
        resetColor(btn_lends);
        resetColor(btn_returns);
        resetColor(btn_users);
        resetColor(btn_books);
        resetColor(btn_reports);
        
        Venta.setVisible(true);
        Menu.setVisible(false);
        Cabecera.setVisible(false);
        Pestañas.setVisible(false);
    }//GEN-LAST:event_btn_prinMousePressed

    private void btn_prinMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn_prinMouseExited
        if (btn_prin.getBackground().getRGB() == new Color(21,101,192).getRGB()) {
        resetColor(btn_prin);
    }
        if(btn_lends.getBackground().getRGB() != -15574355 || btn_returns.getBackground().getRGB() != -15574355
            || btn_users.getBackground().getRGB() != -15574355 || btn_books.getBackground().getRGB() != -15574355 || btn_reports.getBackground().getRGB() != -15574355)
            resetColor(btn_prin);
    }//GEN-LAST:event_btn_prinMouseExited

    private void btn_prinMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn_prinMouseEntered
        if(btn_prin.getBackground().getRGB() == -15574355)
            setColor(btn_prin);
    }//GEN-LAST:event_btn_prinMouseEntered

    private void exitMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_exitMousePressed
        System.exit(0);
    }//GEN-LAST:event_exitMousePressed

    private void exitMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_exitMouseExited
        exit.setForeground(new Color(255,255,255));
    }//GEN-LAST:event_exitMouseExited

    private void exitMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_exitMouseEntered
        exit.setForeground(Color.red);
    }//GEN-LAST:event_exitMouseEntered

    private void jLabel22MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel22MousePressed
        try {
        int codigo = Integer.parseInt(txtCodigo.getText().trim());
        productoCon eliminar = new productoCon();
        eliminar.eliminarProducto(codigo);
        txtCodigo.setText("");
        txtDescripcion.setText("");
        txtCantidadNueva.setText("");
        txtCategoria.setText("");
        txtPrecio.setText("");
        productoCon controlProducto = new productoCon();
        controlProducto.mostrarTabla(tablaInventario, 0, "");
        productoCon com = new productoCon();
        com.llenarCombo(comboCategoria);
        JOptionPane.showMessageDialog(null, "Producto Eliminado");
    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(null, "Error al convertir el código");
    }
    }//GEN-LAST:event_jLabel22MousePressed

    private void txtDescripcionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtDescripcionActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtDescripcionActionPerformed

    private void tablaVentaMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablaVentaMousePressed
        // TODO add your handling code here:
    }//GEN-LAST:event_tablaVentaMousePressed

    private void txtTotalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTotalActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtTotalActionPerformed

    private void txtcantVentaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtcantVentaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtcantVentaActionPerformed

    private void textcodVentaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_textcodVentaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_textcodVentaActionPerformed

    private void textdesVentaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_textdesVentaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_textdesVentaActionPerformed

    private void btn_AgregarMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn_AgregarMouseEntered
        if (panelSeleccionado != btn_Agregar) {
        setColor(btn_Agregar);
    }
    }//GEN-LAST:event_btn_AgregarMouseEntered

    private void btn_AgregarMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn_AgregarMouseExited
        if (panelSeleccionado != btn_Agregar) {
        resetColor(btn_Agregar);
    }
    }//GEN-LAST:event_btn_AgregarMouseExited

    private void btn_AgregarMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn_AgregarMousePressed
        resetColor(panelSeleccionado);
        panelSeleccionado = btn_Agregar;
        setColor(btn_Agregar);
        
        agregarProducto1 ventanaAgrega = new agregarProducto1();
        ventanaAgrega.setVisible(true);
    }//GEN-LAST:event_btn_AgregarMousePressed

    private void btn_cancelarMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn_cancelarMouseEntered
        if (panelSeleccionado != btn_cancelar) {
        setColor(btn_cancelar);
    }
    }//GEN-LAST:event_btn_cancelarMouseEntered

    private void btn_cancelarMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn_cancelarMouseExited
        if (panelSeleccionado != btn_cancelar) {
        resetColor(btn_cancelar);
    }
    }//GEN-LAST:event_btn_cancelarMouseExited

    private void btn_cancelarMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn_cancelarMousePressed
        MenuProducto.setVisible(false);
        btn_prin.setVisible(true);
        btn_lends.setVisible(true);
        btn_returns.setVisible(true);
        btn_users.setVisible(true);
        btn_books.setVisible(true);
        btn_reports.setVisible(true);
    }//GEN-LAST:event_btn_cancelarMousePressed

    private void jLabel1MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel1MousePressed
        if (txtDescripcion.getText().trim().equals("") || txtCantidadNueva.getText().trim().equals("") || txtCategoria.getText().trim().equals("") || txtPrecio.getText().trim().equals("")) {
        JOptionPane.showMessageDialog(null, "Los campos están vacíos");
    } else {
        try {
            int codigo = Integer.parseInt(txtCodigo.getText().trim());
            String descripcion = txtDescripcion.getText().trim();
            String categoria = txtCategoria.getText().trim();
            int cantidadAc = Integer.parseInt(txtCantidad.getText().trim());
            int cantidadNu = Integer.parseInt(txtCantidadNueva.getText().trim());
            int cantidadNueva = cantidadAc + cantidadNu;
            double precio = Double.parseDouble(txtPrecio.getText().trim());
            producto Pro = new producto(descripcion, categoria, cantidadNueva, precio);
            productoCon modificar = new productoCon();
            modificar.modificarProducto(Pro, codigo);
            txtCodigo.setText("");
            txtDescripcion.setText("");
            txtCantidad.setText("");
            txtCantidadNueva.setText("");
            txtCategoria.setText("");
            txtPrecio.setText("");
           productoCon controlProducto = new productoCon();
           controlProducto.mostrarTabla(tablaInventario, 0,"");
           productoCon com = new productoCon();
           com.llenarCombo(comboCategoria);
            JOptionPane.showMessageDialog(null, "Producto Actualizado");
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Error al convertir los valores");
        }
    }
    }//GEN-LAST:event_jLabel1MousePressed

    private void btnAgregarMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnAgregarMousePressed
    DefaultTableModel modeloVenta = (DefaultTableModel) tablaVenta.getModel();
    DefaultTableModel modeloInventario = (DefaultTableModel) tablaInventario.getModel();
    int cantidadVenta = Integer.parseInt(txtcantVenta.getText().trim());
    String descripcion = textdesVenta.getText().trim();
    
    if (cantidadVenta <= 0) {
        JOptionPane.showMessageDialog(null, "Debe ingresar una cantidad válida");
        txtcantVenta.setText("");
        return;
    }
    
    int codigo = 0;
    if (!textcodVenta.getText().trim().isEmpty()) {
        codigo = Integer.parseInt(textcodVenta.getText().trim());
    }
    
    for (int i = 0; i < modeloInventario.getRowCount(); i++) {
        int codigoFila = (int) modeloInventario.getValueAt(i, 0);
        String descripcionFila = modeloInventario.getValueAt(i, 1).toString();
        
        if (codigoFila == codigo || descripcionFila.equalsIgnoreCase(descripcion)) {
            int cantidadActual = (int) modeloInventario.getValueAt(i, 3);
            int nuevaCantidad = cantidadActual - cantidadVenta;
            
            if (nuevaCantidad < 0) {
                JOptionPane.showMessageDialog(null, "Producto agotado");
                nuevaCantidad = 0;
            } else {
                String categoria = modeloInventario.getValueAt(i, 2).toString();
                double precioU = (double) modeloInventario.getValueAt(i, 4);
                double iva = precioU * .16;
                double precioT = precioU + iva;
                double subtotal = precioT * cantidadVenta;
                
                // Actualizar la tabla de ventas
                Object[] filaVenta = {codigoFila, descripcionFila, categoria, precioT, cantidadVenta, subtotal, new iconoTabla()};
                modeloVenta.addRow(filaVenta);
                tablaVenta.getColumnModel().getColumn(6).setCellRenderer(new accionIcono());
                tablaVenta.getColumnModel().getColumn(6).setCellEditor(new editarIcono(event));
                
                // Actualizar la tabla de inventario
                modeloInventario.setValueAt(nuevaCantidad, i, 3);
                
                // Actualizar la base de datos
                try {
                    Conexion cn = new Conexion();
                    Connection con = cn.conectar();
                    String updateQuery = "UPDATE productos SET cantidad = " + nuevaCantidad + " WHERE codigo = " + codigoFila;
                    Statement st = con.createStatement();
                    st.executeUpdate(updateQuery);
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Error al actualizar la cantidad en la base de datos: " + ex.getMessage());
                }
            }
            break;
        }
    }

    textdesVenta.setText("");
    textcodVenta.setText("");
    txtcantVenta.setText("");

    // Actualizar totales
    double total = 0;
    int totalCantidad = 0;
    for (int i = 0; i < modeloVenta.getRowCount(); i++) {
        total += (double) modeloVenta.getValueAt(i, 5);
        totalCantidad += (int) modeloVenta.getValueAt(i, 4);
    }
    txtTotal.setText(String.valueOf(total));
    txtTotalproductos.setText(String.valueOf(totalCantidad));

    modeloVenta.fireTableDataChanged();
    }//GEN-LAST:event_btnAgregarMousePressed

    private void btnAceptarMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnAceptarMousePressed
    DefaultTableModel modeloVenta = (DefaultTableModel) tablaVenta.getModel();
    DefaultTableModel modeloFactura = (DefaultTableModel) tablaFactura.getModel();
    
    String productoVenta;
    double subtotalVenta = 0;
    double precioUVenta = 0;
    int codigoVenta = 0;
    int totalCantidad = 0;
    int ultimaFila = 0; 

    try {
        Conexion cn = new Conexion();
        String insertar = "INSERT INTO productos_venta (contenido) VALUES (?)";
        Connection con = cn.conectar();
        PreparedStatement psInsertar = con.prepareStatement(insertar, Statement.RETURN_GENERATED_KEYS);
        psInsertar.setString(1, ""); 
        psInsertar.executeUpdate();
    
        ResultSet generatedKeys = psInsertar.getGeneratedKeys();
        if (generatedKeys.next()) {
            ultimaFila = generatedKeys.getInt(1);
    }
    
        psInsertar.close();
        con.close();    
    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(null, "Error al insertar en la base de datos: " + ex.getMessage());
    }
    StringBuilder contenidoVenta = new StringBuilder();
    for (int i = 0; i < modeloVenta.getRowCount(); i++) {
        codigoVenta = (int) modeloVenta.getValueAt(i, 0);
        precioUVenta = (double) modeloVenta.getValueAt(i, 3);
        totalCantidad = (int) modeloVenta.getValueAt(i, 4);
        subtotalVenta = (double) modeloVenta.getValueAt(i, 5);
    
        productoVenta = "Codigo: " + codigoVenta + " Precio con IVA: " + precioUVenta + " Cantidad: " + totalCantidad + " Subtotal: " + subtotalVenta + "| ";
        contenidoVenta.append(productoVenta);
    }
    try {
        Conexion cn = new Conexion();
        String actualizar = "UPDATE productos_venta SET contenido = CONCAT(contenido, ?) WHERE id_factura = ?";
        Connection con = cn.conectar();
        PreparedStatement agregar = con.prepareStatement(actualizar);
        agregar.setString(1, contenidoVenta.toString());
        agregar.setInt(2, ultimaFila);
        agregar.executeUpdate();
        agregar.close();
        con.close(); 
    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(null, "Error al actualizar la cantidad en la base de datos: " + ex.getMessage());
    }
    
        String formaPago = comboPago.getSelectedItem().toString();

        double total = Double.parseDouble(txtTotal.getText().trim());
        double pago = Double.parseDouble(txtPago.getText().trim());
        double cambio = Double.parseDouble(txtCambio.getText().trim());


        Object[] filaFactura = {ultimaFila, totalCantidad, total, formaPago, pago, cambio};
        modeloFactura.addRow(filaFactura);

        // Limpiar tablaVenta y campos de texto
        modeloVenta.setRowCount(0);
        txtTotal.setText("");
        txtPago.setText("");
        txtCambio.setText("");
        txtTotalproductos.setText("");
    }//GEN-LAST:event_btnAceptarMousePressed

    private void btnCancelarMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnCancelarMousePressed
        DefaultTableModel modeloVenta = (DefaultTableModel) tablaVenta.getModel();
    DefaultTableModel modeloInventario = (DefaultTableModel) tablaInventario.getModel();

    for (int i = 0; i < modeloVenta.getRowCount(); i++) {
        int productoCodigo = (int) modeloVenta.getValueAt(i, 0);
        int productoCantidad = (int) modeloVenta.getValueAt(i, 4);

        for (int j = 0; j < modeloInventario.getRowCount(); j++) {
            int codigoFila = (int) modeloInventario.getValueAt(j, 0);

            if (codigoFila == productoCodigo) {    
                int cantidadActual = (int) modeloInventario.getValueAt(j, 3);
                int nuevaCantidad = cantidadActual + productoCantidad;

                modeloInventario.setValueAt(nuevaCantidad, j, 3);

                try {
                    Conexion cn = new Conexion();
                    Connection con = cn.conectar();
                    String updateQuery = "UPDATE productos SET cantidad = " + nuevaCantidad + " WHERE codigo = " + codigoFila;
                    Statement st = con.createStatement();
                    st.executeUpdate(updateQuery);
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Error al actualizar la cantidad en la base de datos: " + ex.getMessage());
                }
                break; 
            }
        }
    }

    modeloVenta.setRowCount(0);

    textdesVenta.setText("");
    textcodVenta.setText("");
    txtcantVenta.setText("");
    txtTotal.setText("");
    txtTotalproductos.setText("");

    // Notificar cambios
    modeloVenta.fireTableDataChanged();
    modeloInventario.fireTableDataChanged();
    }//GEN-LAST:event_btnCancelarMousePressed

    private void txtPagoKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPagoKeyReleased
        double pago = Double.parseDouble(txtPago.getText().trim());
        double total = Double.parseDouble(txtTotal.getText().trim());
        double cambio = pago - total;

        DecimalFormat df = new DecimalFormat("#.##");
        txtCambio.setText(df.format(cambio));
    }//GEN-LAST:event_txtPagoKeyReleased

    private void jLabel2MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel2MousePressed
        txtbuscarNombre.setText("");
        txtbuscarRFC.setText("");
        Conexion control = new Conexion();
        control.mostrarTablaProveedor(tablaProveedores, "", "");
    }//GEN-LAST:event_jLabel2MousePressed

    private void EditarMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_EditarMousePressed
    setBandera(1);
    Autorizacion auto = new Autorizacion(bandera,this);
    auto.setVisible(true);
    }//GEN-LAST:event_EditarMousePressed

    private void nuevoMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_nuevoMousePressed
        // Abrir sección
    }//GEN-LAST:event_nuevoMousePressed

    private void nuevoMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_nuevoMouseExited
        // TODO add your handling code here:
    }//GEN-LAST:event_nuevoMouseExited

    private void nuevoMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_nuevoMouseEntered
        // TODO add your handling code here:
    }//GEN-LAST:event_nuevoMouseEntered

    private void comboCategoriaItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_comboCategoriaItemStateChanged
        DefaultTableModel modelo =(DefaultTableModel) tablaInventario.getModel();
        String query = comboCategoria.getSelectedItem().toString();
        TableRowSorter<DefaultTableModel> tr = new TableRowSorter<>(modelo);
        tablaInventario.setRowSorter(tr);

        if (!query.equals("Seleccione categoría")) {
            tr.setRowFilter(RowFilter.regexFilter(Pattern.quote(query)));
        } else{
            tablaInventario.setRowSorter(null);
        }
    }//GEN-LAST:event_comboCategoriaItemStateChanged

    private void txtbuscarCodigoKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtbuscarCodigoKeyReleased
        int codigo = Integer.parseInt(txtbuscarCodigo.getText());
        String descripcion = "";
        productoCon controlProducto = new productoCon();
        controlProducto.mostrarTabla(tablaInventario, codigo, descripcion);
    }//GEN-LAST:event_txtbuscarCodigoKeyReleased

    private void txtbuscarDescripcionKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtbuscarDescripcionKeyReleased
        int codigo = 0;
        String descripcion = txtbuscarDescripcion.getText().trim();
        productoCon controlProducto = new productoCon();
        controlProducto.mostrarTabla(tablaInventario, codigo, descripcion);
    }//GEN-LAST:event_txtbuscarDescripcionKeyReleased

    private void txtbuscarDescripcionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtbuscarDescripcionActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtbuscarDescripcionActionPerformed

    private void tablaInventarioMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablaInventarioMouseClicked
        int fila=tablaInventario.getSelectedRow();
        if(fila>=0){
            txtCodigo.setText(tablaInventario.getValueAt(fila,0).toString());
            txtDescripcion.setText(tablaInventario.getValueAt(fila,1).toString());
            txtCategoria.setText(tablaInventario.getValueAt(fila,2).toString());
            txtCantidad.setText(tablaInventario.getValueAt(fila,3).toString());
            txtPrecio.setText(tablaInventario.getValueAt(fila,4).toString());
        }else{
            JOptionPane.showMessageDialog(null,"Seleccione fila");
        }
    }//GEN-LAST:event_tablaInventarioMouseClicked

    private void txtfactEliActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtfactEliActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtfactEliActionPerformed

    private void botonEliminarventaMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_botonEliminarventaMousePressed
        DefaultTableModel modelo = (DefaultTableModel) tablaFactura.getModel();
        int fila = tablaFactura.getSelectedRow();
        int contador = 0;
        if (fila != -1) {
            modelo.removeRow(fila);
            contador++;
            txtfactEli.setText(String.valueOf(contador));
        } else {
            JOptionPane.showMessageDialog(null, "Por favor, selecciona una fila para eliminar.");
        }
    }//GEN-LAST:event_botonEliminarventaMousePressed

    private void botonEliminarventaMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_botonEliminarventaMouseExited
        // TODO add your handling code here:
    }//GEN-LAST:event_botonEliminarventaMouseExited

    private void botonEliminarventaMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_botonEliminarventaMouseEntered
        // TODO add your handling code here:
    }//GEN-LAST:event_botonEliminarventaMouseEntered

    private void pane_semanalMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pane_semanalMouseExited
        if (panelSeleccionado != pane_semanal) {
        resetColor(pane_semanal);
    }
    }//GEN-LAST:event_pane_semanalMouseExited

    private void pane_semanalMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pane_semanalMouseEntered
        if (panelSeleccionado != pane_semanal) {
        setColor(pane_semanal);
    }
    }//GEN-LAST:event_pane_semanalMouseEntered

    private void pane_mensualMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pane_mensualMouseExited
        if (panelSeleccionado != pane_mensual) {
        resetColor(pane_mensual);
    }
    }//GEN-LAST:event_pane_mensualMouseExited

    private void pane_mensualMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pane_mensualMouseEntered
        if (panelSeleccionado != pane_mensual) {
        setColor(pane_mensual);
    }
    }//GEN-LAST:event_pane_mensualMouseEntered

    private void pane_anualMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pane_anualMouseExited
        if (panelSeleccionado != pane_anual) {
        resetColor(pane_anual);
    }
    }//GEN-LAST:event_pane_anualMouseExited

    private void pane_anualMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pane_anualMouseEntered
        if (panelSeleccionado != pane_anual) {
        setColor(pane_anual);
    }
    }//GEN-LAST:event_pane_anualMouseEntered

    private void pane_semanalMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pane_semanalMousePressed
        resetColor(panelSeleccionado);
        panelSeleccionado = pane_semanal;
        setColor(pane_semanal);
               
        txtGanancia.setText("");

        String nombreTabla = "total_semanal";
        Conexion Fact = new Conexion();
        Fact.mostrarTabla(tablatotalFacturas, nombreTabla);
        txtGanancia.setText("");

        double total = 0;
        for (int i = 0; i < tablatotalFacturas.getRowCount(); i++) {
            total += (double) tablatotalFacturas.getValueAt(i, 1);
        }
        txtGanancia.setText(String.valueOf(total));
    }//GEN-LAST:event_pane_semanalMousePressed

    private void pane_mensualMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pane_mensualMousePressed
        resetColor(panelSeleccionado);
        panelSeleccionado = pane_mensual;
        setColor(pane_mensual);
        
        txtGanancia.setText("");
        String nombreTabla = "total_mensual";
        Conexion Fact = new Conexion();
        Fact.mostrarTabla(tablatotalFacturas, nombreTabla);
        
        double total = 0;
        for (int i = 0; i < tablatotalFacturas.getRowCount(); i++) {
        total += (double) tablatotalFacturas.getValueAt(i, 1);
        }
        txtGanancia.setText(String.valueOf(total));
    }//GEN-LAST:event_pane_mensualMousePressed

    private void pane_anualMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pane_anualMousePressed
        resetColor(panelSeleccionado);
        panelSeleccionado = pane_anual;
        setColor(pane_anual);
        
        txtGanancia.setText("");
        String nombreTabla = "total_anual";
        Conexion Fact = new Conexion();
        Fact.mostrarTabla(tablatotalFacturas, nombreTabla);
        txtGanancia.setText("");
        
        double total = 0;
        for (int i = 0; i < tablatotalFacturas.getRowCount(); i++) {
        total += (double) tablatotalFacturas.getValueAt(i, 1);
        }
        txtGanancia.setText(String.valueOf(total));
    }//GEN-LAST:event_pane_anualMousePressed

    private void exit1MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_exit1MousePressed
        Venta.setVisible(false);
        Menu.setVisible(true);
        Cabecera.setVisible(true);
        Pestañas.setVisible(true);
        Pestañas.setSelectedIndex(0);
    }//GEN-LAST:event_exit1MousePressed

    private void exit1MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_exit1MouseExited
        exit.setForeground(new Color(255,255,255));
    }//GEN-LAST:event_exit1MouseExited

    private void exit1MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_exit1MouseEntered
        exit.setForeground(Color.red);
    }//GEN-LAST:event_exit1MouseEntered

    private void txtbuscarNombreKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtbuscarNombreKeyReleased
        String nombre = txtbuscarNombre.getText().trim();
        Conexion controlProve = new Conexion();
        controlProve.mostrarTablaProveedor(tablaProveedores, "", nombre);
    }//GEN-LAST:event_txtbuscarNombreKeyReleased

    private void txtbuscarRFCKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtbuscarRFCKeyReleased
        String rfc = txtbuscarRFC.getText().trim();
        Conexion controlProve = new Conexion();
        controlProve.mostrarTablaProveedor(tablaProveedores, rfc, "");
    }//GEN-LAST:event_txtbuscarRFCKeyReleased

    private void txtFechaInicioMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtFechaInicioMousePressed
        if(txtFechaInicio.getText().equals("Ingrese el año (yyyy)"))
        txtFechaInicio.setText("");
        if(txtFechaFin.getText().equals("") || txtFechaFin.getText() == null || txtFechaFin.getText().equals(" "))
        txtFechaFin.setText("Ingrese el año (yyyy)");
    }//GEN-LAST:event_txtFechaInicioMousePressed

    private void btn_graficaMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn_graficaMouseEntered
        setColor(btn_grafica);
    }//GEN-LAST:event_btn_graficaMouseEntered

    private void btn_graficaMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn_graficaMouseExited
        resetColor(btn_grafica);
    }//GEN-LAST:event_btn_graficaMouseExited

    private void btn_graficaMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn_graficaMousePressed
        String fechaInicio = txtFechaInicio.getText();
        String fechaFin = txtFechaFin.getText();
        
        
        Conexion grafica = new Conexion();
        grafica.Graficar(fechaInicio, fechaFin);
    }//GEN-LAST:event_btn_graficaMousePressed

    private void newReportMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_newReportMousePressed
        Reportes reportes = new Reportes();
        reportes.ReportesProductos();
    }//GEN-LAST:event_newReportMousePressed

    private void txtFechaFinMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtFechaFinMousePressed
        if(txtFechaInicio.getText().equals("") || txtFechaInicio.getText() == null || txtFechaInicio.getText().equals(" "))
        txtFechaInicio.setText("Ingrese el año (yyyy)");
        if(txtFechaFin.getText().equals("Ingrese el año (yyyy)"))
        txtFechaFin.setText("");
        
    }//GEN-LAST:event_txtFechaFinMousePressed

    private void txtFechaFinActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtFechaFinActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtFechaFinActionPerformed

    private void txtFechaFinKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtFechaFinKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_txtFechaFinKeyReleased

    private void jLabel19MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel19MousePressed
        Reportes reportes = new Reportes();
        reportes.ReportesProveedores();
    }//GEN-LAST:event_jLabel19MousePressed

    private void txtDomActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtDomActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtDomActionPerformed

    private void txtRFCActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtRFCActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtRFCActionPerformed

    private void txtApelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtApelActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtApelActionPerformed

    private void eliminarProMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_eliminarProMousePressed
        String rfc = txtRFC.getText();
        productoCon eliPro = new productoCon();
        eliPro.eliminarProveedor(rfc);
        Conexion Prove = new Conexion();
        Prove.mostrarTablaProveedor(tablaProveedores, "", "");
        txtNom.setText("");
        txtApel.setText("");
        txtTel.setText("");
        txtRFC.setText("");
        txtDom.setText("");
        txtPro.setText("");
    }//GEN-LAST:event_eliminarProMousePressed

    private void btn_AgregarProMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn_AgregarProMouseEntered

    }//GEN-LAST:event_btn_AgregarProMouseEntered

    private void btn_AgregarProMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn_AgregarProMouseExited

    }//GEN-LAST:event_btn_AgregarProMouseExited

    private void btn_AgregarProMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn_AgregarProMousePressed
        agregarProveedor ventanaAgrega = new agregarProveedor();
        ventanaAgrega.setVisible(true);
    }//GEN-LAST:event_btn_AgregarProMousePressed

    private void btnCancelarProMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnCancelarProMouseEntered

    }//GEN-LAST:event_btnCancelarProMouseEntered

    private void btnCancelarProMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnCancelarProMouseExited

    }//GEN-LAST:event_btnCancelarProMouseExited

    private void btnCancelarProMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnCancelarProMousePressed
        MenuProveedor.setVisible(false);
        btn_prin.setVisible(true);
        btn_lends.setVisible(true);
        btn_returns.setVisible(true);
        btn_users.setVisible(true);
        btn_books.setVisible(true);
        btn_reports.setVisible(true);
    }//GEN-LAST:event_btnCancelarProMousePressed

    private void guardarProMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_guardarProMousePressed
        if (txtApel.getText().trim().equals("") || txtTel.getText().trim().equals("") || txtRFC.getText().trim().equals("") || txtDom.getText().trim().equals("")|| txtNom.getText().trim().equals("")|| txtPro.getText().trim().equals("")) {
            JOptionPane.showMessageDialog(null, "Los campos están vacíos");
        } else {
            try {
                String nombre = txtNom.getText().trim();
                String apellido = txtApel.getText().trim();
                String rfc = txtRFC.getText().trim();
                Long telefono = Long.parseLong(txtTel.getText().trim());
                String domicilio = txtDom.getText().trim();
                String producto = txtPro.getText().trim();
                proveedor Pro = new proveedor(nombre, apellido, rfc, telefono, domicilio, producto);
                productoCon modificar = new productoCon();
                modificar.modificarProveedor(Pro, rfc);
                txtNom.setText("");
                txtApel.setText("");
                txtTel.setText("");
                txtRFC.setText("");
                txtDom.setText("");
                txtPro.setText("");
                Conexion Prove = new Conexion();
                Prove.mostrarTablaProveedor(tablaProveedores, "", "");
                JOptionPane.showMessageDialog(null, "Proveedor Actualizado");

            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Error al convertir los valores");
            }
        }
    }//GEN-LAST:event_guardarProMousePressed

    private void editarProMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_editarProMousePressed
        setBandera(2);
        Autorizacion auto = new Autorizacion(bandera, this);
        auto.setVisible(true);
    }//GEN-LAST:event_editarProMousePressed

    private void tablaProveedoresMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablaProveedoresMouseClicked
        int fila=tablaProveedores.getSelectedRow();
        if(fila>=0){
            txtNom.setText(tablaProveedores.getValueAt(fila,0).toString());
            txtApel.setText(tablaProveedores.getValueAt(fila,1).toString());
            txtRFC.setText(tablaProveedores.getValueAt(fila,2).toString());
            txtTel.setText(tablaProveedores.getValueAt(fila,3).toString());
            txtDom.setText(tablaProveedores.getValueAt(fila,4).toString());
            txtPro.setText(tablaProveedores.getValueAt(fila,5).toString());
        }else{
            JOptionPane.showMessageDialog(null,"Seleccione fila");
        }
    }//GEN-LAST:event_tablaProveedoresMouseClicked

    private void txtPrecioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPrecioActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtPrecioActionPerformed

    private void pane_diasMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pane_diasMouseEntered
        if (panelSeleccionado != pane_dias) {
            setColor(pane_dias);
        }
    }//GEN-LAST:event_pane_diasMouseEntered

    private void pane_diasMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pane_diasMouseExited
        if (panelSeleccionado != pane_dias) {
            resetColor(pane_dias);
        }
    }//GEN-LAST:event_pane_diasMouseExited

    private void pane_diasMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pane_diasMousePressed
        resetColor(panelSeleccionado);
        panelSeleccionado = pane_dias;
        setColor(pane_dias);

        txtGanancia.setText("");

        String nombreTabla = "total_ventas";
        Conexion Fact = new Conexion();
        Fact.mostrarTabla(tablatotalFacturas, nombreTabla);
        txtGanancia.setText("");

        double total = 0;
        for (int i = 0; i < tablatotalFacturas.getRowCount(); i++) {
            total += (double) tablatotalFacturas.getValueAt(i, 1);
        }
        txtGanancia.setText(String.valueOf(total));
    }//GEN-LAST:event_pane_diasMousePressed

    private void botonCerrarMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_botonCerrarMousePressed
        contenidoFac.setText("");
        try (Connection con = conectar(); // Conectar a la base de datos
            Statement stmt = con.createStatement()) {
    
            String sql = "DELETE FROM productos_venta";
            stmt.executeUpdate(sql);
    
        } catch (SQLException e) {
            e.printStackTrace(); // Manejo de errores
        }
    
        DefaultTableModel modeloFactura = (DefaultTableModel) tablaFactura.getModel();
        
        double totalEfectivo = 0;
        double totalTarjeta = 0;
        double fondo = 0;

        try {
            fondo = Double.parseDouble(JOptionPane.showInputDialog("Ingrese el fondo: "));
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Debe ingresar un valor numérico válido para el fondo.");
            return;
        }

        for (int i = 0; i < modeloFactura.getRowCount(); i++) {
            String aux = (String) modeloFactura.getValueAt(i, 3);
            if (aux.equals("Efectivo")) {
                totalEfectivo += (double) modeloFactura.getValueAt(i, 2);
            } else if (aux.equals("Tarjeta")) {
                totalTarjeta += (double) modeloFactura.getValueAt(i, 2);
            }
        }

        double totalFinal = totalEfectivo + fondo;
        JOptionPane.showMessageDialog(null, "Fondo: " + fondo + "\nTotal de ventas con efectivo: " + totalEfectivo +
            "\nTotal de ventas con tarjeta: " + totalTarjeta + "\nTotal final: " + totalFinal);

        double totalVentas = 0;
        for (int i = 0; i < modeloFactura.getRowCount(); i++) {
            totalVentas += (double) modeloFactura.getValueAt(i, 2);
        }

        LocalDate now = LocalDate.now();
        Date sqlDate = Date.valueOf(now);

        factura fact = new factura(0, totalVentas, sqlDate);

        try (Connection con = conectar();
            PreparedStatement ps = con.prepareStatement("INSERT INTO total_ventas (total, fecha) VALUES (?, ?)")) {

            ps.setDouble(1, fact.getTotal());
            ps.setDate(2, fact.getFecha());
            ps.executeUpdate();

            JOptionPane.showMessageDialog(null, "Datos guardados correctamente");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Algo salió mal: " + e.getMessage());
        }

        modeloFactura.setRowCount(0);

        // Verificar y registrar las ventas en total_semanal
        /*int totalFilas = contarFilas("total_ventas").getTotalFilas();
        int ultimoID = obtenerUltimoID("total_semanal");

        if (totalFilas >= 7) {
            // Procesar grupos de 7 en 7
            for (int i = 0; i < totalFilas / 7; i++) {
                int offset = ultimoID + 1 + (i * 7);
                Apuntadores apuntador = Conexion.sacarTotal("total_ventas", 7, offset);
                double totalSema = apuntador.gettotalDinero();
                int ultimoId = apuntador.getultimoId();

                factura facSem = new factura(ultimoId, totalSema, sqlDate);

                try (Connection con = conectar();
                    PreparedStatement ps = con.prepareStatement("INSERT INTO total_semanal (id, total, fecha) VALUES (?, ?, ?)")) {

                    ps.setInt(1, facSem.getId());
                    ps.setDouble(2, facSem.getTotal());
                    ps.setDate(3, facSem.getFecha());
                    ps.executeUpdate();

                    JOptionPane.showMessageDialog(null, "Datos guardados correctamente en total_semanal.");
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null, "Algo salió mal al registrar en total_semanal: " + e.getMessage());
                }
            }
        } else {
            JOptionPane.showMessageDialog(null, "No hay suficientes registros para realizar un nuevo registro semanal.");
        }*/
    }//GEN-LAST:event_botonCerrarMousePressed

    private void tablaFacturaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablaFacturaMouseClicked
        int fila = tablaFactura.getSelectedRow();
    if (fila >= 0) {
        int No = (int) tablaFactura.getValueAt(fila, 0);
        String consulta = "SELECT contenido FROM productos_venta WHERE id_factura = ?";
    
    try (Connection con = conectar();
         PreparedStatement ps = con.prepareStatement(consulta)) {
        
        ps.setInt(1, No);
        ResultSet rs = ps.executeQuery();
        
        if (rs.next()) {
            String contenido = rs.getString("contenido"); 
            contenidoFac.setText(Forma(contenido)); 
        } else {
            JOptionPane.showMessageDialog(null, "No se encontró el registro con el código: " + No);
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "Algo salió mal al consultar en productos_venta: " + e.getMessage());
    }
} else {
    JOptionPane.showMessageDialog(null, "Seleccione una fila");
}
    }//GEN-LAST:event_tablaFacturaMouseClicked

    private void jPanel18MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel18MousePressed
    setBandera(3);
    Autorizacion auto = new Autorizacion(bandera,this);
    auto.setVisible(true);
    }//GEN-LAST:event_jPanel18MousePressed
    void setColor(JPanel panel){
        panel.setBackground(new Color(21,101,192));
    }
    void resetColor(JPanel panel){
        if (panel != null) {
        panel.setBackground(new java.awt.Color(18,90,173));
    }
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Inicio.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Inicio.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Inicio.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Inicio.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Inicio().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel Cabecera;
    private javax.swing.JLabel Editar;
    private javax.swing.JPanel Menu;
    private javax.swing.JPanel MenuProducto;
    private javax.swing.JPanel MenuProveedor;
    private javax.swing.JTabbedPane Pestañas;
    private javax.swing.JLabel Text3;
    private javax.swing.JLabel Text4;
    private javax.swing.JLabel Title;
    private javax.swing.JLabel Title1;
    private javax.swing.JLabel Title10;
    private javax.swing.JLabel Title11;
    private javax.swing.JLabel Title12;
    private javax.swing.JLabel Title13;
    private javax.swing.JLabel Title14;
    private javax.swing.JLabel Title15;
    private javax.swing.JLabel Title16;
    private javax.swing.JLabel Title17;
    private javax.swing.JLabel Title18;
    private javax.swing.JLabel Title19;
    private javax.swing.JLabel Title2;
    private javax.swing.JLabel Title20;
    private javax.swing.JLabel Title3;
    private javax.swing.JLabel Title4;
    private javax.swing.JLabel Title5;
    private javax.swing.JLabel Title6;
    private javax.swing.JLabel Title7;
    private javax.swing.JLabel Title8;
    private javax.swing.JPanel Venta;
    private javax.swing.JLabel app_name1;
    private javax.swing.JLabel app_name2;
    private javax.swing.JLabel app_name3;
    private javax.swing.JLabel botonCerrar;
    private javax.swing.JPanel botonEliminarventa;
    private javax.swing.JLabel btnAceptar;
    private javax.swing.JLabel btnAgregar;
    private javax.swing.JLabel btnCancelar;
    private javax.swing.JPanel btnCancelarPro;
    private javax.swing.JPanel btn_Agregar;
    private javax.swing.JPanel btn_AgregarPro;
    private javax.swing.JLabel btn_Mensual;
    private javax.swing.JLabel btn_anual;
    private javax.swing.JLabel btn_anual1;
    private javax.swing.JPanel btn_books;
    private javax.swing.JPanel btn_cancelar;
    private javax.swing.JPanel btn_grafica;
    private javax.swing.JPanel btn_lends;
    private javax.swing.JPanel btn_prin;
    private javax.swing.JPanel btn_reports;
    private javax.swing.JPanel btn_returns;
    private javax.swing.JLabel btn_semanal;
    private javax.swing.JPanel btn_users;
    private javax.swing.JComboBox<String> comboCategoria;
    private javax.swing.JComboBox<String> comboPago;
    private javax.swing.JLabel contenidoFac;
    private javax.swing.JLabel editarPro;
    private javax.swing.JLabel eliminarPro;
    private javax.swing.JLabel exit;
    private javax.swing.JLabel exit1;
    private javax.swing.JLabel fecha;
    private javax.swing.JLabel guardarPro;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLayeredPane jLayeredPane1;
    private javax.swing.JPanel jPanel;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel17;
    private javax.swing.JPanel jPanel18;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JSeparator jSeparator10;
    private javax.swing.JSeparator jSeparator11;
    private javax.swing.JSeparator jSeparator12;
    private javax.swing.JSeparator jSeparator13;
    private javax.swing.JSeparator jSeparator14;
    private javax.swing.JSeparator jSeparator15;
    private javax.swing.JSeparator jSeparator16;
    private javax.swing.JSeparator jSeparator17;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JSeparator jSeparator5;
    private javax.swing.JSeparator jSeparator6;
    private javax.swing.JSeparator jSeparator7;
    private javax.swing.JSeparator jSeparator8;
    private javax.swing.JSeparator jSeparator9;
    private javax.swing.JLabel newReport;
    private javax.swing.JPanel nuevo;
    private javax.swing.JPanel pane_anual;
    private javax.swing.JPanel pane_dias;
    private javax.swing.JPanel pane_mensual;
    private javax.swing.JPanel pane_semanal;
    private javax.swing.JTable tablaFactura;
    private javax.swing.JTable tablaInventario;
    private javax.swing.JTable tablaProveedores;
    private javax.swing.JTable tablaVenta;
    private javax.swing.JTable tablatotalFacturas;
    private javax.swing.JTextField textcodVenta;
    private javax.swing.JTextField textdesVenta;
    private javax.swing.JTextField txtApel;
    private javax.swing.JTextField txtCambio;
    private javax.swing.JTextField txtCantidad;
    private javax.swing.JTextField txtCantidadNueva;
    private javax.swing.JTextField txtCategoria;
    private javax.swing.JTextField txtCodigo;
    private javax.swing.JTextField txtDescripcion;
    private javax.swing.JTextField txtDom;
    private javax.swing.JTextField txtFechaFin;
    private javax.swing.JTextField txtFechaInicio;
    private javax.swing.JTextField txtGanancia;
    private javax.swing.JTextField txtNom;
    private javax.swing.JTextField txtPago;
    private javax.swing.JTextField txtPrecio;
    private javax.swing.JTextField txtPro;
    private javax.swing.JTextField txtRFC;
    private javax.swing.JTextField txtTel;
    private javax.swing.JTextField txtTotal;
    private javax.swing.JTextField txtTotalproductos;
    private javax.swing.JTextField txtbuscarCodigo;
    private javax.swing.JTextField txtbuscarDescripcion;
    private javax.swing.JTextField txtbuscarNombre;
    private javax.swing.JTextField txtbuscarRFC;
    private javax.swing.JTextField txtcantVenta;
    private javax.swing.JTextField txtfactEli;
    // End of variables declaration//GEN-END:variables
}
