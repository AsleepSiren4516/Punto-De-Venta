/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.constructor;

/**
 *
 * @author bobby
 */
public class Apuntadores {
    private double totalDinero;
    private int ultimoId;
    public Apuntadores(double totalDinero, int ultimoId){
        this.totalDinero = totalDinero;
        this.ultimoId = ultimoId;
    }

    public double gettotalDinero() {
        return totalDinero;
    }

    public int getultimoId() {
        return ultimoId;
    }
    
}
