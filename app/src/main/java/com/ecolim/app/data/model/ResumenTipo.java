package com.ecolim.app.data.model;

/**
 * Agrupación estadística para gráficas y métricas por tipo de residuo.
 * app/src/main/java/com/ecolim/app/data/model/ResumenTipo.java
 */
public class ResumenTipo {
    private String material;
    private double totalKg;
    private double porcentaje;
    private int cantidadLotes;
    private String colorHex;

    public ResumenTipo(String material, double totalKg, double porcentaje, int cantidadLotes, String colorHex) {
        this.material = material;
        this.totalKg = totalKg;
        this.porcentaje = porcentaje;
        this.cantidadLotes = cantidadLotes;
        this.colorHex = colorHex;
    }

    public String getMaterial() { return material; }
    public double getTotalKg() { return totalKg; }
    public double getPorcentaje() { return porcentaje; }
    public int getCantidadLotes() { return cantidadLotes; }
    public String getColorHex() { return colorHex; }
}
