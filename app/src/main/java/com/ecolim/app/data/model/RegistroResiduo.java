package com.ecolim.app.data.model;

import java.io.Serializable;

/**
 * Entidad POJO de Registro de Residuo.
 * app/src/main/java/com/ecolim/app/data/model/RegistroResiduo.java
 */
public class RegistroResiduo implements Serializable {
    private String id;
    private String codigoLote;
    private String material;
    private String subtipo;
    private double pesoKg;
    private String contenedor;
    private String puntoAcopio;
    private String operadorId;
    private String basculaId;
    private Double latitud;
    private Double longitud;
    private String rfidTag;
    private String fotoPath;
    private boolean sincronizado;
    private String fechaCreacion;

    public RegistroResiduo() {}

    public RegistroResiduo(String id, String codigoLote, String material, String subtipo, double pesoKg,
                          String contenedor, String puntoAcopio, String operadorId, String basculaId,
                          Double latitud, Double longitud, String rfidTag, String fotoPath,
                          boolean sincronizado, String fechaCreacion) {
        this.id = id;
        this.codigoLote = codigoLote;
        this.material = material;
        this.subtipo = subtipo;
        this.pesoKg = pesoKg;
        this.contenedor = contenedor;
        this.puntoAcopio = puntoAcopio;
        this.operadorId = operadorId;
        this.basculaId = basculaId;
        this.latitud = latitud;
        this.longitud = longitud;
        this.rfidTag = rfidTag;
        this.fotoPath = fotoPath;
        this.sincronizado = sincronizado;
        this.fechaCreacion = fechaCreacion;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getCodigoLote() { return codigoLote; }
    public void setCodigoLote(String codigoLote) { this.codigoLote = codigoLote; }

    public String getMaterial() { return material; }
    public void setMaterial(String material) { this.material = material; }

    public String getSubtipo() { return subtipo; }
    public void setSubtipo(String subtipo) { this.subtipo = subtipo; }

    public double getPesoKg() { return pesoKg; }
    public void setPesoKg(double pesoKg) { this.pesoKg = pesoKg; }

    public String getContenedor() { return contenedor; }
    public void setContenedor(String contenedor) { this.contenedor = contenedor; }

    public String getPuntoAcopio() { return puntoAcopio; }
    public void setPuntoAcopio(String puntoAcopio) { this.puntoAcopio = puntoAcopio; }

    public String getOperadorId() { return operadorId; }
    public void setOperadorId(String operadorId) { this.operadorId = operadorId; }

    public String getBasculaId() { return basculaId; }
    public void setBasculaId(String basculaId) { this.basculaId = basculaId; }

    public Double getLatitud() { return latitud; }
    public void setLatitud(Double latitud) { this.latitud = latitud; }

    public Double getLongitud() { return longitud; }
    public void setLongitud(Double longitud) { this.longitud = longitud; }

    public String getRfidTag() { return rfidTag; }
    public void setRfidTag(String rfidTag) { this.rfidTag = rfidTag; }

    public String getFotoPath() { return fotoPath; }
    public void setFotoPath(String fotoPath) { this.fotoPath = fotoPath; }

    public boolean isSincronizado() { return sincronizado; }
    public void setSincronizado(boolean sincronizado) { this.sincronizado = sincronizado; }

    public String getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(String fechaCreacion) { this.fechaCreacion = fechaCreacion; }
}
