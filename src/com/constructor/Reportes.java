/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.constructor;

import com.conexion.Conexion;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;

/**
 *
 * @author bobby
 */
public class Reportes {
    public void ReportesClientes() {
        Document documento = new Document();
        try {
            String ruta = System.getProperty("user.home");
            PdfWriter.getInstance(documento, new FileOutputStream(ruta + "/Desktop/Reporte_Clientes.pdf"));
            Image header = Image.getInstance("src/img/header1.jpg");
            header.scaleToFit(650, 1000);
            header.setAlignment(Chunk.ALIGN_CENTER);
            //formato al texto
            Paragraph parrafo = new Paragraph();
            parrafo.setAlignment(Paragraph.ALIGN_CENTER);
            parrafo.add("Reporte creado por \nEstilo Urbano © Corporation\n\n");
            parrafo.setFont(FontFactory.getFont("Tahoma", 18, Font.BOLD, BaseColor.DARK_GRAY));
            parrafo.add("Reporte de Proveedores \n\n");

            documento.open();
            //agregamos los datos
            documento.add(header);
            documento.add(parrafo);

            PdfPTable tabla = new PdfPTable(5);
            tabla.addCell("ID");
            tabla.addCell("Nombres");
            tabla.addCell("CURP");
            tabla.addCell("Telefono");
            tabla.addCell("Direccion");

            try {
                Connection cn = Conexion.conectar();
                PreparedStatement pst = cn.prepareStatement(
                        "select id, concat(nombre, ' ', apellido) as nombres, cedula, telefono, direccion from proveedores");
                ResultSet rs = pst.executeQuery();
                if (rs.next()) {
                    do {
                        tabla.addCell(rs.getString(1));
                        tabla.addCell(rs.getString(2));
                        tabla.addCell(rs.getString(3));
                        tabla.addCell(rs.getString(4));
                        tabla.addCell(rs.getString(5));
                    } while (rs.next());
                    documento.add(tabla);
                }

            } catch (SQLException e) {
                System.out.println("Error 4 en: " + e);
            }
            documento.close();
            
            JOptionPane.showMessageDialog(null, "Reporte creado");

        } catch (DocumentException e) {
            System.out.println("Error 1 en: " + e);
        } catch (FileNotFoundException ex) {
            System.out.println("Error 2 en: " + ex);
        } catch (IOException ex) {
            System.out.println("Error 3 en: " + ex);
        }
    }
    
    /* ********************************************************************
    * metodo para crear reportes de los productos registrados en el sistema
    *********************************************************************** */
    public void ReportesProductos() {
        Document documento = new Document();
        // Usa la ruta del escritorio para guardar el archivo PDF
        String ruta = System.getProperty("user.home") + "/Desktop/Reporte_Productos.pdf";
        
        try {
            // Verifica si la carpeta del escritorio existe, si no, créala
            File folder = new File(System.getProperty("user.home") + "/Desktop");
            if (!folder.exists()) {
                if (folder.mkdirs()) {
                    System.out.println("Carpeta creada: " + folder.getAbsolutePath());
                } else {
                    System.out.println("No se pudo crear la carpeta.");
                    return; // Sale del método si no puede crear la carpeta
                }
            }

            // Crea el archivo PDF
            PdfWriter.getInstance(documento, new FileOutputStream(ruta));
            Image header = Image.getInstance("src/com/images/header1.jpg");
            header.scaleToFit(650, 1000);
            header.setAlignment(Chunk.ALIGN_CENTER);
            
            // Formato al texto
            Paragraph parrafo = new Paragraph();
            parrafo.setAlignment(Paragraph.ALIGN_CENTER);
            parrafo.add("Reporte creado por \nEstilo Urbano © S.A. de C.V.\n\n");
            parrafo.setFont(FontFactory.getFont("Tahoma", 18, Font.BOLD, BaseColor.DARK_GRAY));
            parrafo.add("Reporte de Productos \n\n");

            documento.open();
            // Agregamos los datos
            documento.add(header);
            documento.add(parrafo);
            
            float[] columnsWidths = {3, 5, 4, 5, 7};

            PdfPTable tabla = new PdfPTable(columnsWidths);
            tabla.addCell("Codigo");
            tabla.addCell("Descripcion");
            tabla.addCell("Categoria");
            tabla.addCell("Cantidad");
            tabla.addCell("Precio unitario");

            try {
                Connection cn = Conexion.conectar();
                PreparedStatement pst = cn.prepareStatement(
                        "SELECT codigo, descripcion, categoria, cantidad, precio "
                                + "FROM productos");
                ResultSet rs = pst.executeQuery();
                if (rs.next()) {
                    do {
                        tabla.addCell(rs.getString(1));
                        tabla.addCell(rs.getString(2));
                        tabla.addCell(rs.getString(3));
                        tabla.addCell(rs.getString(4));
                        tabla.addCell(rs.getString(5));
                    } while (rs.next());
                    documento.add(tabla);
                }

            } catch (SQLException e) {
                System.out.println("Error 4 en: " + e);
            }
            
            // Cierra el documento
            documento.close();
            
            // Verifica si el archivo se creó
            File file = new File(ruta);
            if (file.exists()) {
                System.out.println("Archivo creado exitosamente: " + file.getAbsolutePath());
            } else {
                System.out.println("No se pudo crear el archivo.");
            }

            JOptionPane.showMessageDialog(null, "Reporte creado");

        } catch (DocumentException e) {
            System.out.println("Error 1 en: " + e);
        } catch (FileNotFoundException ex) {
            System.out.println("Error 2 en: " + ex);
        } catch (IOException ex) {
            System.out.println("Error 3 en: " + ex);
        } finally {
            if (documento.isOpen()) {
                documento.close();
            }
        }
    }
    
        /* ********************************************************************
    * metodo para crear reportes de las ventas registrados en el sistema
    *********************************************************************** */
    public void ReportesProveedores() {
        Document documento = new Document();
        // Usa la ruta del escritorio para guardar el archivo PDF
        String ruta = System.getProperty("user.home") + "/Desktop/Reporte_Proveedores.pdf";
        
        try {
            // Verifica si la carpeta del escritorio existe, si no, créala
            File folder = new File(System.getProperty("user.home") + "/Desktop");
            if (!folder.exists()) {
                if (folder.mkdirs()) {
                    System.out.println("Carpeta creada: " + folder.getAbsolutePath());
                } else {
                    System.out.println("No se pudo crear la carpeta.");
                    return; // Sale del método si no puede crear la carpeta
                }
            }

            // Crea el archivo PDF
            PdfWriter.getInstance(documento, new FileOutputStream(ruta));
            Image header = Image.getInstance("src/com/images/header1.jpg");
            header.scaleToFit(650, 1000);
            header.setAlignment(Chunk.ALIGN_CENTER);
            
            // Formato al texto
            Paragraph parrafo = new Paragraph();
            parrafo.setAlignment(Paragraph.ALIGN_CENTER);
            parrafo.add("Reporte creado por \nEstilo Urbano © S.A. de C.V.\n\n");
            parrafo.setFont(FontFactory.getFont("Tahoma", 18, Font.BOLD, BaseColor.DARK_GRAY));
            parrafo.add("Reporte de Proveedores \n\n");

            documento.open();
            // Agregamos los datos
            documento.add(header);
            documento.add(parrafo);
            
            PdfPTable tabla = new PdfPTable(6);
            tabla.addCell("Nombre");
            tabla.addCell("Apellidos");
            tabla.addCell("RFC");
            tabla.addCell("Telefono");
            tabla.addCell("Direccion");
            tabla.addCell("Producto");

            try {
                Connection cn = Conexion.conectar();
                PreparedStatement pst = cn.prepareStatement(
                        "SELECT nombre, apellido, RFC, telefono, direccion, producto "
                                + "FROM proveedores");
                ResultSet rs = pst.executeQuery();
                if (rs.next()) {
                    do {
                        tabla.addCell(rs.getString(1));
                        tabla.addCell(rs.getString(2));
                        tabla.addCell(rs.getString(3));
                        tabla.addCell(rs.getString(4));
                        tabla.addCell(rs.getString(5));
                        tabla.addCell(rs.getString(6));
                    } while (rs.next());
                    documento.add(tabla);
                }

            } catch (SQLException e) {
                System.out.println("Error 4 en: " + e);
            }
            
            // Cierra el documento
            documento.close();
            
            // Verifica si el archivo se creó
            File file = new File(ruta);
            if (file.exists()) {
                System.out.println("Archivo creado exitosamente: " + file.getAbsolutePath());
            } else {
                System.out.println("No se pudo crear el archivo.");
            }

            JOptionPane.showMessageDialog(null, "Reporte creado");

        } catch (DocumentException e) {
            System.out.println("Error 1 en: " + e);
        } catch (FileNotFoundException ex) {
            System.out.println("Error 2 en: " + ex);
        } catch (IOException ex) {
            System.out.println("Error 3 en: " + ex);
        } finally {
            if (documento.isOpen()) {
                documento.close();
            }
        }
    }
}
