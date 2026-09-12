package com.ecolim.app.data.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Gestor SQLiteOpenHelper para la base de datos local de Ecolim.
 * Ruta en tu repositorio: app/src/main/java/com/ecolim/app/data/db/DatabaseHelper.java
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "ecolim.db";
    public static final int DATABASE_VERSION = 4;

    // Tabla Usuarios
    public static final String TABLE_USUARIOS = "usuarios";
    public static final String COL_USER_ID = "id";
    public static final String COL_USER_NOMBRE = "nombre";
    public static final String COL_USER_EMAIL = "email";
    public static final String COL_USER_PASSWORD_HASH = "password_hash";
    public static final String COL_USER_ROL = "rol";
    public static final String COL_USER_TURNO = "turno";
    public static final String COL_USER_DNI = "dni";
    public static final String COL_USER_SEDE = "sede";
    public static final String COL_USER_FOTO = "foto_perfil";

    // Tabla Tipos de Residuos
    public static final String TABLE_TIPOS_RESIDUOS = "tipos_residuos";
    public static final String COL_TIPO_ID = "id";
    public static final String COL_TIPO_NOMBRE = "nombre";
    public static final String COL_TIPO_COLOR_HEX = "color_hex";
    public static final String COL_TIPO_FACTOR_CO2 = "factor_co2";

    // Tabla Registros de Residuos
    public static final String TABLE_REGISTROS = "registros_residuos";
    public static final String COL_REG_ID = "id";
    public static final String COL_REG_CODIGO_LOTE = "codigo_lote";
    public static final String COL_REG_MATERIAL = "material";
    public static final String COL_REG_SUBTIPO = "subtipo";
    public static final String COL_REG_PESO_KG = "peso_kg";
    public static final String COL_REG_CONTENEDOR = "contenedor";
    public static final String COL_REG_PUNTO_ACOPIO = "punto_acopio";
    public static final String COL_REG_OPERADOR_ID = "operador_id";
    public static final String COL_REG_BASCULA_ID = "bascula_id";
    public static final String COL_REG_LATITUD = "latitud";
    public static final String COL_REG_LONGITUD = "longitud";
    public static final String COL_REG_RFID_TAG = "rfid_tag";
    public static final String COL_REG_FOTO_PATH = "foto_path";
    public static final String COL_REG_SINCRONIZADO = "sincronizado";
    public static final String COL_REG_FECHA = "fecha_creacion";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 1. Crear tabla usuarios
        db.execSQL("CREATE TABLE " + TABLE_USUARIOS + " (" +
                COL_USER_ID + " TEXT PRIMARY KEY, " +
                COL_USER_NOMBRE + " TEXT NOT NULL, " +
                COL_USER_EMAIL + " TEXT NOT NULL UNIQUE, " +
                COL_USER_PASSWORD_HASH + " TEXT NOT NULL, " +
                COL_USER_ROL + " TEXT NOT NULL, " +
                COL_USER_TURNO + " TEXT NOT NULL, " +
                COL_USER_DNI + " TEXT, " +
                COL_USER_SEDE + " TEXT, " +
                COL_USER_FOTO + " TEXT);");

        // 2. Crear tabla tipos de residuos
        db.execSQL("CREATE TABLE " + TABLE_TIPOS_RESIDUOS + " (" +
                COL_TIPO_ID + " TEXT PRIMARY KEY, " +
                COL_TIPO_NOMBRE + " TEXT NOT NULL, " +
                COL_TIPO_COLOR_HEX + " TEXT NOT NULL, " +
                COL_TIPO_FACTOR_CO2 + " REAL NOT NULL);");

        // 3. Crear tabla principal de registros de residuos
        db.execSQL("CREATE TABLE " + TABLE_REGISTROS + " (" +
                COL_REG_ID + " TEXT PRIMARY KEY, " +
                COL_REG_CODIGO_LOTE + " TEXT NOT NULL UNIQUE, " +
                COL_REG_MATERIAL + " TEXT NOT NULL, " +
                COL_REG_SUBTIPO + " TEXT NOT NULL, " +
                COL_REG_PESO_KG + " REAL NOT NULL, " +
                COL_REG_CONTENEDOR + " TEXT NOT NULL, " +
                COL_REG_PUNTO_ACOPIO + " TEXT NOT NULL, " +
                COL_REG_OPERADOR_ID + " TEXT NOT NULL, " +
                COL_REG_BASCULA_ID + " TEXT NOT NULL DEFAULT 'B-04', " +
                COL_REG_LATITUD + " REAL, " +
                COL_REG_LONGITUD + " REAL, " +
                COL_REG_RFID_TAG + " TEXT, " +
                COL_REG_FOTO_PATH + " TEXT, " +
                COL_REG_SINCRONIZADO + " INTEGER NOT NULL DEFAULT 0, " +
                COL_REG_FECHA + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP);");

        // Datos iniciales de catálogo
        db.execSQL("INSERT INTO " + TABLE_TIPOS_RESIDUOS + " VALUES ('T-01', 'Plástico', '#E57A00', 1.85);");
        db.execSQL("INSERT INTO " + TABLE_TIPOS_RESIDUOS + " VALUES ('T-02', 'Cartón', '#8A5100', 1.10);");
        db.execSQL("INSERT INTO " + TABLE_TIPOS_RESIDUOS + " VALUES ('T-03', 'Vidrio', '#006D38', 0.90);");
        db.execSQL("INSERT INTO " + TABLE_TIPOS_RESIDUOS + " VALUES ('T-04', 'Metal', '#4A6572', 2.50);");
        db.execSQL("INSERT INTO " + TABLE_TIPOS_RESIDUOS + " VALUES ('T-05', 'Orgánico', '#7CB342', 0.50);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_REGISTROS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TIPOS_RESIDUOS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USUARIOS);
        onCreate(db);
    }
}
