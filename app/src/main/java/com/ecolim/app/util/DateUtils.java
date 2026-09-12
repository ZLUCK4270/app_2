package com.ecolim.app.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Utilidades para formateo de fechas y marcas de tiempo de recolección.
 * app/src/main/java/com/ecolim/app/util/DateUtils.java
 */
public class DateUtils {

    private static final SimpleDateFormat FORMATO_DB = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
    private static final SimpleDateFormat FORMATO_LEGIBLE = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
    private static final SimpleDateFormat FORMATO_HORA = new SimpleDateFormat("HH:mm", Locale.getDefault());

    public static String ahora() {
        return FORMATO_DB.format(new Date());
    }

    public static String formatearHora(String timestampStr) {
        try {
            Date d = FORMATO_DB.parse(timestampStr);
            return d != null ? FORMATO_HORA.format(d) : timestampStr;
        } catch (Exception e) {
            return timestampStr;
        }
    }

    public static String formatearLegible(String timestampStr) {
        try {
            Date d = FORMATO_DB.parse(timestampStr);
            return d != null ? FORMATO_LEGIBLE.format(d) : timestampStr;
        } catch (Exception e) {
            return timestampStr;
        }
    }
}
