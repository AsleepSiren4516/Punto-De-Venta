/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.constructor;

/**
 *
 * @author bobby
 */
public class proveedor {
    String nombre;
    String apellidos;
    String rfc;
    Long telefono;
    String domicilio;
    String producto;
    
    public proveedor(String nombre, String apellidos, String rfc, Long telefono, String domicilio, String producto){
        this.nombre=nombre;
        this.apellidos=apellidos;
        this.rfc=rfc;
        this.telefono=telefono;
        this.domicilio=domicilio;
        this.producto=producto;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getRfc() {
        return rfc;
    }

    public void setRfc(String rfc) {
        this.rfc = rfc;
    }

    public Long getTelefono() {
        return telefono;
    }

    public void setTelefono(Long telefono) {
        this.telefono = telefono;
    }

    public String getDomicilio() {
        return domicilio;
    }

    public void setDomicilio(String domicilio) {
        this.domicilio = domicilio;
    }

    public String getProducto() {
        return producto;
    }

    public void setProducto(String producto) {
        this.producto = producto;
    }
    
}
