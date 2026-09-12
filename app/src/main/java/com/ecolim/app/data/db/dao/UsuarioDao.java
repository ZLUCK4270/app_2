package com.ecolim.app.data.db.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.ecolim.app.data.db.DatabaseHelper;
import com.ecolim.app.data.model.Usuario;

/**
 * Data Access Object para autenticación y usuarios en SQLite.
 * app/src/main/java/com/ecolim/app/data/db/dao/UsuarioDao.java
 */
public class UsuarioDao {

    private final DatabaseHelper dbHelper;

    public UsuarioDao(Context context) {
        this.dbHelper = new DatabaseHelper(context);
    }

    public Usuario autenticar(String email, String passwordHash) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_USUARIOS +
                " WHERE " + DatabaseHelper.COL_USER_EMAIL + " = ? AND " + DatabaseHelper.COL_USER_PASSWORD_HASH + " = ?",
                new String[]{email, passwordHash});

        Usuario u = null;
        if (c.moveToFirst()) {
            u = new Usuario(
                c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COL_USER_ID)),
                c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COL_USER_NOMBRE)),
                c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COL_USER_EMAIL)),
                c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COL_USER_ROL)),
                c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COL_USER_TURNO)),
                c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COL_USER_DNI)),
                c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COL_USER_SEDE)),
                c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COL_USER_FOTO))
            );
        }
        c.close();
        return u;
    }

    public Usuario obtenerPorId(String id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_USUARIOS +
                " WHERE " + DatabaseHelper.COL_USER_ID + " = ?", new String[]{id});

        Usuario u = null;
        if (c.moveToFirst()) {
            u = new Usuario(
                c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COL_USER_ID)),
                c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COL_USER_NOMBRE)),
                c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COL_USER_EMAIL)),
                c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COL_USER_ROL)),
                c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COL_USER_TURNO)),
                c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COL_USER_DNI)),
                c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COL_USER_SEDE)),
                c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COL_USER_FOTO))
            );
        }
        c.close();
        return u;
    }

    /** Verifica si ya existe un usuario con ese correo. */
    public boolean emailExiste(String email) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT 1 FROM " + DatabaseHelper.TABLE_USUARIOS +
                " WHERE " + DatabaseHelper.COL_USER_EMAIL + " = ?", new String[]{email});
        boolean existe = c.moveToFirst();
        c.close();
        return existe;
    }

    /**
     * Registra un nuevo usuario en SQLite.
     * @return el Usuario creado, o null si el email ya existe.
     */
    public Usuario registrar(String nombre, String email, String passwordHash, String rol, String turno, String dni, String sede, String fotoPerfil) {
        if (emailExiste(email)) return null;

        // Generar ID único tipo "OP-XXXX"
        String id = "OP-" + String.format("%04d", (int)(Math.random() * 9000) + 1000);

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("INSERT INTO " + DatabaseHelper.TABLE_USUARIOS + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)",
                new Object[]{id, nombre, email, passwordHash, rol, turno, dni, sede, fotoPerfil});

        return new Usuario(id, nombre, email, rol, turno, dni, sede, fotoPerfil);
    }

    public boolean actualizarPerfil(Usuario usuario, String nuevaContrasenaHash) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            if (nuevaContrasenaHash != null && !nuevaContrasenaHash.isEmpty()) {
                db.execSQL("UPDATE " + DatabaseHelper.TABLE_USUARIOS + " SET " +
                        DatabaseHelper.COL_USER_NOMBRE + " = ?, " +
                        DatabaseHelper.COL_USER_DNI + " = ?, " +
                        DatabaseHelper.COL_USER_SEDE + " = ?, " +
                        DatabaseHelper.COL_USER_FOTO + " = ?, " +
                        DatabaseHelper.COL_USER_PASSWORD_HASH + " = ? WHERE " + DatabaseHelper.COL_USER_ID + " = ?",
                        new Object[]{usuario.getNombre(), usuario.getDni(), usuario.getSede(), usuario.getFotoPerfil(), nuevaContrasenaHash, usuario.getId()});
            } else {
                db.execSQL("UPDATE " + DatabaseHelper.TABLE_USUARIOS + " SET " +
                        DatabaseHelper.COL_USER_NOMBRE + " = ?, " +
                        DatabaseHelper.COL_USER_DNI + " = ?, " +
                        DatabaseHelper.COL_USER_SEDE + " = ?, " +
                        DatabaseHelper.COL_USER_FOTO + " = ? WHERE " + DatabaseHelper.COL_USER_ID + " = ?",
                        new Object[]{usuario.getNombre(), usuario.getDni(), usuario.getSede(), usuario.getFotoPerfil(), usuario.getId()});
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
