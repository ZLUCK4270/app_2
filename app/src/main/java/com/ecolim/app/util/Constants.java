package com.ecolim.app.util;

/**
 * Constantes globales de la aplicación Ecolim.
 * app/src/main/java/com/ecolim/app/util/Constants.java
 */
public final class Constants {
    private Constants() {}

    public static final String PREF_NAME = "ecolim_session_pref";
    public static final String KEY_USER_ID = "pref_user_id";
    public static final String KEY_USER_NAME = "pref_user_name";
    public static final String KEY_USER_ROLE = "pref_user_role";
    public static final String KEY_USER_TURNO = "pref_user_turno";
    public static final String KEY_USER_DNI = "pref_user_dni";
    public static final String KEY_USER_SEDE = "pref_user_sede";
    public static final String KEY_USER_FOTO = "pref_user_foto";
    public static final String KEY_IS_LOGGED_IN = "pref_is_logged_in";

    public static final String DEFAULT_OPERADOR_ID = "";
    public static final String DEFAULT_OPERADOR_NOMBRE = "";
    public static final String DEFAULT_BASCULA_ID = "B-04";
    public static final String DISPOSITIVO_ID = "ACT-709";

    // URL hacia el servidor Python de sincronización
    public static final String PYTHON_BACKEND_URL = "http://10.0.2.2:8080/api";
}
