/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.conexion;
import carpeta.constructor.producto;
import com.constructor.proveedor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author bobby
 */
public class productoCon {
    Conexion cn = new Conexion();
    Connection con = cn.conectar();
    
    public void RegistrarProductos(producto pro){
        try {
            PreparedStatement ps = con.prepareStatement("INSERT INTO productos (descripcion, categoria, cantidad, precio) VALUES (?,?,?,?)");
            ps.setString(1, pro.getDescripcion());
            ps.setString(2, pro.getCategoria());
            ps.setInt(3, pro.getCantidad());
            ps.setDouble(4, pro.getPrecio());
            ps.executeUpdate();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,"Error al registrar el producto"+e);
        }
    }
    
    public void modificarProveedor(proveedor pro, String rfc) {
    try {
        PreparedStatement pasar = con.prepareStatement("UPDATE proveedores SET nombre=?, apellido=?, telefono=?, direccion=?, producto=? WHERE rfc=?");
        pasar.setString(1, pro.getNombre());
        pasar.setString(2, pro.getApellidos());
        pasar.setLong(3, pro.getTelefono());
        pasar.setString(4, pro.getDomicilio());
        pasar.setString(5, pro.getProducto());
        pasar.setString(6, rfc);
        pasar.executeUpdate();
    } catch (SQLException e) {
        System.out.println("Error al cerrar la conexión: " + e);
    }
}
    
    public void modificarProducto(producto pro, int codigo) {
    try {
        PreparedStatement pasar = con.prepareStatement("UPDATE productos SET descripcion=?, categoria=?, cantidad=?, precio=? WHERE codigo=?");
        pasar.setString(1, pro.getDescripcion());
        pasar.setString(2, pro.getCategoria());
        pasar.setInt(3, pro.getCantidad());
        pasar.setDouble(4, pro.getPrecio());
        pasar.setInt(5, codigo);
        pasar.executeUpdate();
    } catch (SQLException e) {
        System.out.println("Error al cerrar la conexión: " + e);
    }
}
    
    public void mostrarTabla(JTable tabla, int codigo, String descripcion){
        DefaultTableModel modelo = new DefaultTableModel();
        modelo.addColumn("Codigo");
        modelo.addColumn("Descripcion");
        modelo.addColumn("Categoria");
        modelo.addColumn("Cantidad");
        modelo.addColumn("Precio");
        tabla.setModel(modelo);
        String consulta;
        if(codigo == 0 && descripcion.isEmpty()){
            consulta = "SELECT * FROM productos";
        } else if(descripcion.isEmpty()){
            consulta = "SELECT * FROM productos WHERE codigo LIKE '%" + codigo + "%'";
        } else {
            consulta = "SELECT * FROM productos WHERE descripcion LIKE '%" + descripcion + "%'";
        }
        try{
            Statement st=con.createStatement();
            ResultSet rs = st.executeQuery(consulta);
            while(rs.next()){
                Object [] lista = new Object[]{rs.getInt(1),rs.getString(2),rs.getString(3),rs.getInt(4),rs.getDouble(5)};
                modelo.addRow(lista);
            }
        tabla.setModel(modelo);
        } catch (SQLException e){
            JOptionPane.showMessageDialog(null,"Error al ingresar los datos"+e);
        }
    }

    public void eliminarProducto(int codigo) {
    try {
        PreparedStatement pasar = con.prepareStatement("DELETE FROM productos WHERE codigo=?");
        pasar.setInt(1, codigo);
        pasar.executeUpdate();
    } catch (SQLException e) {
        System.out.println("Error al eliminar el producto: " + e);
    }
    }
    
    public void eliminarProveedor(String rfc) {
    try {
        PreparedStatement pasar = con.prepareStatement("DELETE FROM proveedores WHERE rfc=?");
        pasar.setString(1, rfc);
        pasar.executeUpdate();
    } catch (SQLException e) {
        System.out.println("Error al eliminar el proveedor: " + e);
    }
    }
    
    public void mostrarTablaFactura(JTable tabla){
        DefaultTableModel modelo = new DefaultTableModel();
        modelo.addColumn("No.");
        modelo.addColumn("Total");
        modelo.addColumn("Fecha");
        tabla.setModel(modelo);
        String consulta;
        consulta = "SELECT * FROM total_ventas";
        try{
            Statement st=con.createStatement();
            ResultSet rs = st.executeQuery(consulta);
            while(rs.next()){
                Object [] lista = new Object[]{rs.getInt(1),rs.getDouble(2),rs.getDate(3)};
                modelo.addRow(lista);
            }
        tabla.setModel(modelo);
        } catch (SQLException e){
            JOptionPane.showMessageDialog(null,"Error al ingresar los datos"+e);
        }
    }
    
    public void llenarCombo(JComboBox<String> combo){
    Set<String> categorias = new HashSet<>();
    try {
        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT DISTINCT categoria FROM productos");
        while (rs.next()) {
            categorias.add(rs.getString("categoria"));
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "Error al obtener las categorías: " + e.getMessage());
    }
    combo.setModel(new DefaultComboBoxModel<>());
    //combo.removeAllItems();
    combo.addItem("Seleccione categoría");
    for (String categoria : categorias) {
        combo.addItem(categoria);
    }
    }
    
    public void vaciarTablaBD() {
        try {
        Statement st = con.createStatement();
        st.executeUpdate("DELETE FROM total_ventas");
        JOptionPane.showMessageDialog(null, "Se han eliminado todos los datos de la base de datos.");
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "Error al vaciar la base de datos: " + e);
    }
    }
}
