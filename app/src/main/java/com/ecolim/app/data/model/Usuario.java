package com.ecolim.app.data.model;

/**
 * Modelo de Usuario / Técnico de Campo en SQLite.
 * app/src/main/java/com/ecolim/app/data/model/Usuario.java
 */
public class Usuario {
    private String id;
    private String nombre;
    private String email;
    private String rol;
    private String turno;
    private String dni;
    private String sede;
    private String fotoPerfil;

    public Usuario(String id, String nombre, String email, String rol, String turno, String dni, String sede, String fotoPerfil) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.rol = rol;
        this.turno = turno;
        this.dni = dni;
        this.sede = sede;
        this.fotoPerfil = fotoPerfil;
    }

    public String getId() { return id; }
    public String getNombre() { return nombre; }
    public String getEmail() { return email; }
    public String getRol() { return rol; }
    public String getTurno() { return turno; }
    public String getDni() { return dni; }
    public String getSede() { return sede; }
    public String getFotoPerfil() { return fotoPerfil; }

    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setDni(String dni) { this.dni = dni; }
    public void setSede(String sede) { this.sede = sede; }
    public void setFotoPerfil(String fotoPerfil) { this.fotoPerfil = fotoPerfil; }
}
