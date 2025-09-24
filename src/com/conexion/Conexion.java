/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.conexion;

import com.constructor.Apuntadores;
import com.constructor.Contadores;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

/**
 *
 * @author bobby
 */
public class Conexion {
    private static Connection cn;
    private ChartFrame chartFrame;

    public static Connection conectar(){
        try{
            Class.forName("com.mysql.jdbc.Driver");
            cn = DriverManager.getConnection("jdbc:mysql://localhost:3306/abarrotes","root","");
            System.out.println( "CONECTADO");
        } catch(SQLException e){
            JOptionPane.showMessageDialog(null, "Error en la conexion local "+ e.toString());
        } catch(ClassNotFoundException e){
            JOptionPane.showMessageDialog(null, "Error en la carga del driver "+ e.toString());
        }
        return cn;
    }
    
    public static Apuntadores sacarTotal(String nombreTabla, int limite, int inicio){
    double totalDinero = 0.0;
    int ultimoId = 0;

    String query = "SELECT total, id FROM " + nombreTabla + " " +
                   "ORDER BY id LIMIT ? OFFSET ?";

    try (Connection con = conectar();
         PreparedStatement ps = con.prepareStatement(query)) {

        ps.setInt(1, limite); 
        ps.setInt(2, inicio); 

        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                totalDinero += rs.getDouble("total");
                ultimoId = rs.getInt("id");
            }
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "Error inesperado " + e.toString());
    }
    return  new Apuntadores(totalDinero, ultimoId);
    }

    public static boolean tablaVacia(String nombreTabla) {
        String query = "SELECT COUNT(*) FROM " + nombreTabla;
        boolean estaVacia = false;

        try (Connection con = Conexion.conectar(); // Método para conectar a la base de datos
             PreparedStatement ps = con.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                int numeroDeFilas = rs.getInt(1); 
                estaVacia = (numeroDeFilas == 0);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al verificar si la tabla está vacía: " + e.getMessage());
        }

        return estaVacia;
    }
    
    public static int obtenerUltimoID(String nombreTabla) {
    int ultimoID = -1; 
    String query = "SELECT id FROM " + nombreTabla + " ORDER BY id DESC LIMIT 1";

    try (Connection con = Conexion.conectar();
         PreparedStatement ps = con.prepareStatement(query);
         ResultSet rs = ps.executeQuery()) {

        if (rs.next()) {
            ultimoID = rs.getInt("id");
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "Error al obtener el último ID: " + e.getMessage());
    }

    return ultimoID;
}
    
    public void mostrarTablaProveedor(JTable tabla, String rfc, String nombre){
        DefaultTableModel modelo = new DefaultTableModel();
        modelo.addColumn("Nombre");
        modelo.addColumn("Apellidos");
        modelo.addColumn("RFC");
        modelo.addColumn("Telefono");
        modelo.addColumn("Direccion");
	modelo.addColumn("Producto");
        tabla.setModel(modelo);
        String consulta;
        if(rfc.isEmpty() && nombre.isEmpty()){
            consulta = "SELECT * FROM proveedores";
        } else if(nombre.isEmpty()){
            consulta = "SELECT * FROM proveedores WHERE RFC LIKE '%" + rfc + "%'";
        } else {
            consulta = "SELECT * FROM proveedores WHERE nombre LIKE '%" + nombre + "%'";
        }
        try{Connection con = conectar();
            Statement st=con.createStatement();
            ResultSet rs = st.executeQuery(consulta);
            while(rs.next()){
                Object [] lista = new Object[]{rs.getString(1),rs.getString(2),rs.getString(3),rs.getLong(4),rs.getString(5),rs.getString(6)};
                modelo.addRow(lista);
            }
        tabla.setModel(modelo);
        } catch (SQLException e){
            JOptionPane.showMessageDialog(null,"Error al ingresar los datos"+e);
        }
    }
    
    public static void mostrarTabla(JTable tabla, String nombreTabla) {
  	DefaultTableModel modelo = new DefaultTableModel();
	modelo.addColumn("No.");
        modelo.addColumn("Total");
        modelo.addColumn("Fecha"); 
        tabla.setModel(modelo);
    
    String consulta = "SELECT * FROM " + nombreTabla;

    try (Connection con = conectar();
        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery(consulta)) {
        while(rs.next()){
                Object [] lista = new Object[]{rs.getInt(1),rs.getDouble(2),rs.getString(3)};
                modelo.addRow(lista);
            }
        tabla.setModel(modelo);
        }catch (SQLException e){
            JOptionPane.showMessageDialog(null,"Error al ingresar los datos"+e);
        }
   }
    
    
    public static Contadores contarFilas(String nombreTabla, String id) {
    int totalFilas = 0;
    int filaInicio = 0;
    int filaFinal = 0;
    
    String contar = "SELECT COUNT(*) FROM " + nombreTabla;
    String ultima = "SELECT MAX("+id+") AS ultima_id FROM"+nombreTabla;
    try (Connection con = conectar(); 
         Statement st = con.createStatement(); 
         ResultSet rsContar = st.executeQuery(contar);
         PreparedStatement psUltima = con.prepareStatement(ultima);
         ResultSet rsUltima = psUltima.executeQuery()) {

        if (rsContar.next()) {
            totalFilas = rsContar.getInt(1);
        }
        if (rsUltima.next()) {
                filaFinal = rsUltima.getInt("ultima_id");
            }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "Error al contar las filas: " + e.getMessage());
    }

    return new Contadores(totalFilas, filaInicio, filaFinal);
}


    public void registrarProducto(String nombre, int precio) {
        try {
            cn = conectar();
            PreparedStatement ps = cn.prepareStatement("INSERT INTO productos (nombre, precio) VALUES (?, ?)");
            ps.setString(1, nombre);
            ps.setInt(2, precio);
            ps.executeUpdate();
            System.out.println("Producto registrado correctamente");
        } catch (SQLException e) {
            System.out.println("Error al registrar el producto: " + e.getMessage());
        } 
    }
    
    public void Graficar(String anioInicio, String anioFin) {
        Connection con;
        PreparedStatement ps;
        ResultSet rs;

        // Cierra la gráfica anterior si existe
        if (chartFrame != null) {
            chartFrame.dispose();
        }

        try {
            String fechaInicio = anioInicio + "-01-01";
            String fechaFin = anioFin + "-12-31";

            String sql = "SELECT fecha, SUM(total) as total FROM total_ventas WHERE fecha BETWEEN ? AND ? GROUP BY fecha";
            con = conectar(); 
            ps = con.prepareStatement(sql);
            ps.setString(1, fechaInicio);
            ps.setString(2, fechaFin);
            rs = ps.executeQuery();

            DefaultCategoryDataset dataset = new DefaultCategoryDataset();
            boolean hasData = false; 

            while (rs.next()) {
                String fecha = rs.getString("fecha");
                double total = rs.getDouble("total");
                dataset.addValue(total, "Ventas", fecha);
                hasData = true; 
            }

            if (!hasData) {
                System.out.println("No se encontraron datos para el rango de fechas especificado.");
                return; 
            }

            // Crear el gráfico de barras 3D
            JFreeChart chart = ChartFactory.createBarChart3D(
                "Total de Ventas por Fecha", // Título del gráfico
                "Fecha",                    // Eje X
                "Total",                    // Eje Y
                dataset,                    // Datos
                PlotOrientation.VERTICAL,   // Orientación
                true,                       // Mostrar leyenda
                true,                       // Mostrar herramientas
                false                       // Mostrar URLs
            );

            // Mostrar el gráfico
            chartFrame = new ChartFrame("Gráfico de Ventas", chart);
            chartFrame.setSize(800, 600);
            chartFrame.setLocationRelativeTo(null);
            chartFrame.setVisible(true);
        } catch (SQLException e) {
            System.out.println("Error en la consulta: " + e.toString());
        }
    }
}
