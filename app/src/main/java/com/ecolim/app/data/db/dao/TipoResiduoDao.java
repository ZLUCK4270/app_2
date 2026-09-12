package com.ecolim.app.data.db.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.ecolim.app.data.db.DatabaseHelper;
import com.ecolim.app.data.model.TipoResiduo;

import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object para la tabla tipos_residuos en SQLite.
 * app/src/main/java/com/ecolim/app/data/db/dao/TipoResiduoDao.java
 */
public class TipoResiduoDao {

    private final DatabaseHelper dbHelper;

    public TipoResiduoDao(Context context) {
        this.dbHelper = new DatabaseHelper(context);
    }

    public List<TipoResiduo> obtenerTodos() {
        List<TipoResiduo> lista = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_TIPOS_RESIDUOS + " ORDER BY " + DatabaseHelper.COL_TIPO_NOMBRE, null);
        if (c.moveToFirst()) {
            do {
                lista.add(new TipoResiduo(
                    c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COL_TIPO_ID)),
                    c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COL_TIPO_NOMBRE)),
                    c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COL_TIPO_COLOR_HEX)),
                    c.getDouble(c.getColumnIndexOrThrow(DatabaseHelper.COL_TIPO_FACTOR_CO2))
                ));
            } while (c.moveToNext());
        }
        c.close();
        return lista;
    }
}
