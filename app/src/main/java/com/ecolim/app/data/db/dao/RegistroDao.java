package com.ecolim.app.data.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.ecolim.app.data.db.DatabaseHelper;
import com.ecolim.app.data.model.RegistroResiduo;
import com.ecolim.app.data.model.ResumenTipo;

import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object para la tabla registros_residuos en SQLite.
 * app/src/main/java/com/ecolim/app/data/db/dao/RegistroDao.java
 */
public class RegistroDao {

    private final DatabaseHelper dbHelper;

    public RegistroDao(Context context) {
        this.dbHelper = new DatabaseHelper(context);
    }

    public long insertar(RegistroResiduo r) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(DatabaseHelper.COL_REG_ID, r.getId());
        cv.put(DatabaseHelper.COL_REG_CODIGO_LOTE, r.getCodigoLote());
        cv.put(DatabaseHelper.COL_REG_MATERIAL, r.getMaterial());
        cv.put(DatabaseHelper.COL_REG_SUBTIPO, r.getSubtipo());
        cv.put(DatabaseHelper.COL_REG_PESO_KG, r.getPesoKg());
        cv.put(DatabaseHelper.COL_REG_CONTENEDOR, r.getContenedor());
        cv.put(DatabaseHelper.COL_REG_PUNTO_ACOPIO, r.getPuntoAcopio());
        cv.put(DatabaseHelper.COL_REG_OPERADOR_ID, r.getOperadorId());
        cv.put(DatabaseHelper.COL_REG_BASCULA_ID, r.getBasculaId());
        cv.put(DatabaseHelper.COL_REG_LATITUD, r.getLatitud());
        cv.put(DatabaseHelper.COL_REG_LONGITUD, r.getLongitud());
        cv.put(DatabaseHelper.COL_REG_RFID_TAG, r.getRfidTag());
        cv.put(DatabaseHelper.COL_REG_FOTO_PATH, r.getFotoPath());
        cv.put(DatabaseHelper.COL_REG_SINCRONIZADO, r.isSincronizado() ? 1 : 0);

        return db.insertWithOnConflict(DatabaseHelper.TABLE_REGISTROS, null, cv, SQLiteDatabase.CONFLICT_REPLACE);
    }

    public List<RegistroResiduo> obtenerTodos(String materialFiltro, String queryBusqueda) {
        List<RegistroResiduo> lista = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        StringBuilder sql = new StringBuilder("SELECT * FROM " + DatabaseHelper.TABLE_REGISTROS + " WHERE 1=1 ");
        List<String> args = new ArrayList<>();

        if (materialFiltro != null && !materialFiltro.equalsIgnoreCase("Todos")) {
            sql.append(" AND " + DatabaseHelper.COL_REG_MATERIAL + " = ? ");
            args.add(materialFiltro);
        }

        if (queryBusqueda != null && !queryBusqueda.trim().isEmpty()) {
            sql.append(" AND (" + DatabaseHelper.COL_REG_CODIGO_LOTE + " LIKE ? OR " +
                       DatabaseHelper.COL_REG_CONTENEDOR + " LIKE ? OR " +
                       DatabaseHelper.COL_REG_SUBTIPO + " LIKE ?) ");
            String likeArg = "%" + queryBusqueda.trim() + "%";
            args.add(likeArg);
            args.add(likeArg);
            args.add(likeArg);
        }

        sql.append(" ORDER BY " + DatabaseHelper.COL_REG_FECHA + " DESC");

        Cursor c = db.rawQuery(sql.toString(), args.toArray(new String[0]));
        if (c.moveToFirst()) {
            do {
                lista.add(mapearCursor(c));
            } while (c.moveToNext());
        }
        c.close();
        return lista;
    }

    public List<RegistroResiduo> obtenerPendientesSync() {
        List<RegistroResiduo> lista = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_REGISTROS + " WHERE " +
                DatabaseHelper.COL_REG_SINCRONIZADO + " = 0 ORDER BY " + DatabaseHelper.COL_REG_FECHA + " ASC", null);
        if (c.moveToFirst()) {
            do {
                lista.add(mapearCursor(c));
            } while (c.moveToNext());
        }
        c.close();
        return lista;
    }

    public double obtenerTotalKgHoy() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT COALESCE(SUM(" + DatabaseHelper.COL_REG_PESO_KG + "), 0.0) FROM " + DatabaseHelper.TABLE_REGISTROS, null);
        double total = 0.0;
        if (c.moveToFirst()) {
            total = c.getDouble(0);
        }
        c.close();
        return total;
    }

    public List<ResumenTipo> obtenerResumenPorTipo() {
        List<ResumenTipo> resumen = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        double totalGeneral = obtenerTotalKgHoy();

        String sql = "SELECT " + DatabaseHelper.COL_REG_MATERIAL + ", " +
                     "SUM(" + DatabaseHelper.COL_REG_PESO_KG + ") AS total_kg, " +
                     "COUNT(*) AS cantidad " +
                     "FROM " + DatabaseHelper.TABLE_REGISTROS + " " +
                     "GROUP BY " + DatabaseHelper.COL_REG_MATERIAL + " " +
                     "ORDER BY total_kg DESC";

        Cursor c = db.rawQuery(sql, null);
        if (c.moveToFirst()) {
            do {
                String mat = c.getString(0);
                double kg = c.getDouble(1);
                int cant = c.getInt(2);
                double pct = totalGeneral > 0 ? (kg * 100.0 / totalGeneral) : 0.0;

                String color = "#006D38";
                if ("Plástico".equalsIgnoreCase(mat)) color = "#E57A00";
                else if ("Cartón".equalsIgnoreCase(mat)) color = "#8A5100";
                else if ("Vidrio".equalsIgnoreCase(mat)) color = "#006D38";
                else if ("Metal".equalsIgnoreCase(mat)) color = "#4A6572";
                else if ("Orgánico".equalsIgnoreCase(mat)) color = "#7CB342";

                resumen.add(new ResumenTipo(mat, kg, pct, cant, color));
            } while (c.moveToNext());
        }
        c.close();
        return resumen;
    }

    public int actualizarPeso(String id, double nuevoPeso) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(DatabaseHelper.COL_REG_PESO_KG, nuevoPeso);
        cv.put(DatabaseHelper.COL_REG_SINCRONIZADO, 0); // Re-marca como pendiente para sync
        return db.update(DatabaseHelper.TABLE_REGISTROS, cv, DatabaseHelper.COL_REG_ID + " = ?", new String[]{id});
    }

    public int eliminar(String id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        return db.delete(DatabaseHelper.TABLE_REGISTROS, DatabaseHelper.COL_REG_ID + " = ?", new String[]{id});
    }

    public void marcarComoSincronizados(List<String> ids) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues cv = new ContentValues();
            cv.put(DatabaseHelper.COL_REG_SINCRONIZADO, 1);
            for (String id : ids) {
                db.update(DatabaseHelper.TABLE_REGISTROS, cv, DatabaseHelper.COL_REG_ID + " = ?", new String[]{id});
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    private RegistroResiduo mapearCursor(Cursor c) {
        return new RegistroResiduo(
            c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COL_REG_ID)),
            c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COL_REG_CODIGO_LOTE)),
            c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COL_REG_MATERIAL)),
            c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COL_REG_SUBTIPO)),
            c.getDouble(c.getColumnIndexOrThrow(DatabaseHelper.COL_REG_PESO_KG)),
            c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COL_REG_CONTENEDOR)),
            c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COL_REG_PUNTO_ACOPIO)),
            c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COL_REG_OPERADOR_ID)),
            c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COL_REG_BASCULA_ID)),
            c.isNull(c.getColumnIndexOrThrow(DatabaseHelper.COL_REG_LATITUD)) ? null : c.getDouble(c.getColumnIndexOrThrow(DatabaseHelper.COL_REG_LATITUD)),
            c.isNull(c.getColumnIndexOrThrow(DatabaseHelper.COL_REG_LONGITUD)) ? null : c.getDouble(c.getColumnIndexOrThrow(DatabaseHelper.COL_REG_LONGITUD)),
            c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COL_REG_RFID_TAG)),
            c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COL_REG_FOTO_PATH)),
            c.getInt(c.getColumnIndexOrThrow(DatabaseHelper.COL_REG_SINCRONIZADO)) == 1,
            c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COL_REG_FECHA))
        );
    }
}
