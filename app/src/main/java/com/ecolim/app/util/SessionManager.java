package com.ecolim.app.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.ecolim.app.data.model.Usuario;

/**
 * Gestor de sesión SharedPreferences para el operador activo.
 * app/src/main/java/com/ecolim/app/util/SessionManager.java
 */
public class SessionManager {

    private final SharedPreferences prefs;

    public SessionManager(Context context) {
        this.prefs = context.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
    }

    public void guardarSesion(Usuario usuario) {
        prefs.edit()
                .putBoolean(Constants.KEY_IS_LOGGED_IN, true)
                .putString(Constants.KEY_USER_ID, usuario.getId())
                .putString(Constants.KEY_USER_NAME, usuario.getNombre())
                .putString(Constants.KEY_USER_ROLE, usuario.getRol())
                .putString(Constants.KEY_USER_TURNO, usuario.getTurno())
                .putString(Constants.KEY_USER_DNI, usuario.getDni())
                .putString(Constants.KEY_USER_SEDE, usuario.getSede())
                .putString(Constants.KEY_USER_FOTO, usuario.getFotoPerfil())
                .apply();
    }

    public boolean estaLogueado() {
        return prefs.getBoolean(Constants.KEY_IS_LOGGED_IN, false);
    }

    public String getOperadorId() {
        return prefs.getString(Constants.KEY_USER_ID, Constants.DEFAULT_OPERADOR_ID);
    }

    public String getOperadorNombre() {
        return prefs.getString(Constants.KEY_USER_NAME, Constants.DEFAULT_OPERADOR_NOMBRE);
    }

    public String getOperadorRol() {
        return prefs.getString(Constants.KEY_USER_ROLE, "Técnico Ambiental");
    }

    public String getOperadorTurno() {
        return prefs.getString(Constants.KEY_USER_TURNO, "Turno Matutino");
    }

    public String getOperadorDni() {
        return prefs.getString(Constants.KEY_USER_DNI, "");
    }

    public String getOperadorSede() {
        return prefs.getString(Constants.KEY_USER_SEDE, "");
    }

    public String getOperadorFoto() {
        return prefs.getString(Constants.KEY_USER_FOTO, "");
    }

    public void cerrarSesion() {
        prefs.edit().clear().apply();
    }
}
