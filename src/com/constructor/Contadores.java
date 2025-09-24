/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.constructor;
/**
 *
 * @author bobby
 */
public class Contadores {
    private int totalFilas;
    private int filaInicio;
    private int filaFinal;

    public Contadores(int totalFilas, int filaInicio, int filaFinal) {
        this.totalFilas = totalFilas;
        this.filaInicio = filaInicio;
        this.filaFinal = filaFinal;
    }

    public int getTotalFilas() {
        return totalFilas;
    }

    public void setTotalFilas(int totalFilas) {
        this.totalFilas = totalFilas;
    }

    public int getFilaInicio() {
        return filaInicio;
    }

    public void setFilaInicio(int filaInicio) {
        this.filaInicio = filaInicio;
    }

    public int getFilaFinal() {
        return filaFinal;
    }

    public void setFilaFinal(int filaFinal) {
        this.filaFinal = filaFinal;
    }
}
