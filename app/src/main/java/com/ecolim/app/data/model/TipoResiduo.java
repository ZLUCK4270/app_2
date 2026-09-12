package com.ecolim.app.data.model;

/**
 * Catálogo de Tipo de Residuo.
 * app/src/main/java/com/ecolim/app/data/model/TipoResiduo.java
 */
public class TipoResiduo {
    private String id;
    private String nombre;
    private String colorHex;
    private double factorCo2;

    public TipoResiduo(String id, String nombre, String colorHex, double factorCo2) {
        this.id = id;
        this.nombre = nombre;
        this.colorHex = colorHex;
        this.factorCo2 = factorCo2;
    }

    public String getId() { return id; }
    public String getNombre() { return nombre; }
    public String getColorHex() { return colorHex; }
    public double getFactorCo2() { return factorCo2; }
}
