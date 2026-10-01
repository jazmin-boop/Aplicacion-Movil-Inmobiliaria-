package com.example.inmobiliaria.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.inmobiliaria.model.Agente;
import com.example.inmobiliaria.model.Cliente;
import com.example.inmobiliaria.model.Propiedad;
import com.example.inmobiliaria.model.Venta;
import com.example.inmobiliaria.model.Visita;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "inmobiliaria.db";
    private static final int DATABASE_VERSION = 6;

    // Table Names
    public static final String TABLE_PROPIEDADES = "propiedades";
    public static final String TABLE_CLIENTES = "clientes";
    public static final String TABLE_AGENTES = "agentes";
    public static final String TABLE_VISITAS = "visitas";
    public static final String TABLE_VENTAS = "ventas";

    // Common column
    public static final String KEY_ID = "id";

    // PROPIEDADES Columns
    public static final String KEY_PROP_TITULO = "titulo";
    public static final String KEY_PROP_DIRECCION = "direccion";
    public static final String KEY_PROP_PRECIO = "precio";
    public static final String KEY_PROP_TIPO = "tipo";
    public static final String KEY_PROP_ESTADO = "estado";
    public static final String KEY_PROP_IMAGEN_URL = "imagen_url";
    public static final String KEY_PROP_VISTAS = "vistas";
    public static final String KEY_PROP_CONSULTAS = "consultas";
    public static final String KEY_PROP_FAVORITOS = "favoritos";
    public static final String KEY_PROP_DIAS_ACTIVO = "dias_activo";
    public static final String KEY_PROP_FECHA_PUB = "fecha_publicacion";

    // CLIENTES Columns
    public static final String KEY_CLI_NOMBRE = "nombre";
    public static final String KEY_CLI_TELEFONO = "telefono";
    public static final String KEY_CLI_EMAIL = "email";
    public static final String KEY_CLI_PRESUPUESTO = "presupuesto";
    public static final String KEY_CLI_INTERES = "interes";
    public static final String KEY_CLI_IMAGEN_URL = "imagen_url";

    // AGENTES Columns
    public static final String KEY_AGE_NOMBRE = "nombre";
    public static final String KEY_AGE_TELEFONO = "telefono";
    public static final String KEY_AGE_EMAIL = "email";
    public static final String KEY_AGE_ESPECIALIDAD = "especialidad";
    public static final String KEY_AGE_IMAGEN_URL = "imagen_url";

    // VISITAS Columns
    public static final String KEY_VIS_ID_PROPIEDAD = "id_propiedad";
    public static final String KEY_VIS_ID_CLIENTE = "id_cliente";
    public static final String KEY_VIS_ID_AGENTE = "id_agente";
    public static final String KEY_VIS_FECHA_HORA = "fecha_hora";
    public static final String KEY_VIS_COMENTARIOS = "comentarios";
    public static final String KEY_VIS_ESTADO = "estado";

    // VENTAS Columns
    public static final String KEY_VEN_ID_PROPIEDAD = "id_propiedad";
    public static final String KEY_VEN_ID_CLIENTE = "id_cliente";
    public static final String KEY_VEN_ID_AGENTE = "id_agente";
    public static final String KEY_VEN_FECHA_VENTA = "fecha_venta";
    public static final String KEY_VEN_MONTO_FINAL = "monto_final";
    public static final String KEY_VEN_COMISION = "comision";
    public static final String KEY_VEN_METODO_PAGO = "metodo_pago";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_PROPIEDADES = "CREATE TABLE " + TABLE_PROPIEDADES + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_PROP_TITULO + " TEXT,"
                + KEY_PROP_DIRECCION + " TEXT,"
                + KEY_PROP_PRECIO + " REAL,"
                + KEY_PROP_TIPO + " TEXT,"
                + KEY_PROP_ESTADO + " TEXT,"
                + KEY_PROP_IMAGEN_URL + " TEXT,"
                + KEY_PROP_VISTAS + " INTEGER,"
                + KEY_PROP_CONSULTAS + " INTEGER,"
                + KEY_PROP_FAVORITOS + " INTEGER,"
                + KEY_PROP_DIAS_ACTIVO + " INTEGER,"
                + KEY_PROP_FECHA_PUB + " TEXT" + ")";

        String CREATE_CLIENTES = "CREATE TABLE " + TABLE_CLIENTES + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_CLI_NOMBRE + " TEXT,"
                + KEY_CLI_TELEFONO + " TEXT,"
                + KEY_CLI_EMAIL + " TEXT,"
                + KEY_CLI_PRESUPUESTO + " REAL,"
                + KEY_CLI_INTERES + " TEXT,"
                + KEY_CLI_IMAGEN_URL + " TEXT" + ")";

        String CREATE_AGENTES = "CREATE TABLE " + TABLE_AGENTES + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_AGE_NOMBRE + " TEXT,"
                + KEY_AGE_TELEFONO + " TEXT,"
                + KEY_AGE_EMAIL + " TEXT,"
                + KEY_AGE_ESPECIALIDAD + " TEXT,"
                + KEY_AGE_IMAGEN_URL + " TEXT" + ")";

        String CREATE_VISITAS = "CREATE TABLE " + TABLE_VISITAS + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_VIS_ID_PROPIEDAD + " INTEGER,"
                + KEY_VIS_ID_CLIENTE + " INTEGER,"
                + KEY_VIS_ID_AGENTE + " INTEGER,"
                + KEY_VIS_FECHA_HORA + " TEXT,"
                + KEY_VIS_COMENTARIOS + " TEXT,"
                + KEY_VIS_ESTADO + " TEXT" + ")";

        String CREATE_VENTAS = "CREATE TABLE " + TABLE_VENTAS + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_VEN_ID_PROPIEDAD + " INTEGER,"
                + KEY_VEN_ID_CLIENTE + " INTEGER,"
                + KEY_VEN_ID_AGENTE + " INTEGER,"
                + KEY_VEN_FECHA_VENTA + " TEXT,"
                + KEY_VEN_MONTO_FINAL + " REAL,"
                + KEY_VEN_COMISION + " REAL,"
                + KEY_VEN_METODO_PAGO + " TEXT" + ")";

        db.execSQL(CREATE_PROPIEDADES);
        db.execSQL(CREATE_CLIENTES);
        db.execSQL(CREATE_AGENTES);
        db.execSQL(CREATE_VISITAS);
        db.execSQL(CREATE_VENTAS);

        // Seed initial data
        seedData(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROPIEDADES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CLIENTES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_AGENTES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_VISITAS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_VENTAS);
        onCreate(db);
    }

    private void seedData(SQLiteDatabase db) {
        // Sample Propiedades with realistic Unsplash images
        db.execSQL("INSERT INTO " + TABLE_PROPIEDADES + " (" +
                KEY_PROP_TITULO + ", " + KEY_PROP_DIRECCION + ", " + KEY_PROP_PRECIO + ", " +
                KEY_PROP_TIPO + ", " + KEY_PROP_ESTADO + ", " + KEY_PROP_IMAGEN_URL + ", " +
                KEY_PROP_VISTAS + ", " + KEY_PROP_CONSULTAS + ", " + KEY_PROP_FAVORITOS + ", " +
                KEY_PROP_DIAS_ACTIVO + ", " + KEY_PROP_FECHA_PUB + ") VALUES " +
                "('Dúplex Moderno 5 Hab. con Piscina', 'Av. Larco 1020, Miraflores', 850000.0, 'Casa', 'Activo', 'https://images.unsplash.com/photo-1600596542815-ffad4c1539a9?w=800', 320, 8, 12, 45, '2026-06-07')," +
                "('Elegante Casa Duplex Residencial', 'Calle Los Olivos 450, San Isidro', 450000.0, 'Casa', 'Activo', 'https://images.unsplash.com/photo-1600585154340-be6161a56a0c?w=800', 574, 13, 24, 30, '2026-05-20')," +
                "('Residencia Contemporánea 5 Hab.', 'Av. Caminos del Inca 1200, Surco', 550000.0, 'Casa', 'Pendiente', 'https://images.unsplash.com/photo-1600607687939-ce8a6c25118c?w=800', 412, 37, 18, 15, '2026-06-01')," +
                "('Lujoso Departamento 4 Dormitorios', 'Malecón de la Reserva 780, Miraflores', 1790000.0, 'Departamento', 'Activo', 'https://images.unsplash.com/photo-1512917774080-9991f1c4c750?w=800', 211, 24, 30, 10, '2026-06-05')," +
                "('Departamento Studio de Estreno', 'Av. Javier Prado 2100, San Borja', 350000.0, 'Departamento', 'Activo', 'https://images.unsplash.com/photo-1613490493576-7fde63acd811?w=800', 286, 31, 15, 60, '2026-04-12')," +
                "('Villa Exclusiva con Jardín Amplio', 'Calle La Molina 340, La Molina', 920000.0, 'Casa', 'Vendido', 'https://images.unsplash.com/photo-1580587771525-78b9dba3b914?w=800', 650, 42, 50, 90, '2026-03-10');");

        // Sample Clientes
        db.execSQL("INSERT INTO " + TABLE_CLIENTES + " (" + KEY_CLI_NOMBRE + ", " + KEY_CLI_TELEFONO + ", " + KEY_CLI_EMAIL + ", " + KEY_CLI_PRESUPUESTO + ", " + KEY_CLI_INTERES + ") VALUES " +
                "('Juan Pérez', '987654321', 'juan.perez@email.com', 850000.0, 'Comprar')," +
                "('Maria Gómez', '912345678', 'maria.gomez@email.com', 450000.0, 'Comprar')," +
                "('Carlos Rodriguez', '955443322', 'carlos.r@email.com', 600000.0, 'Inversión');");

        // Sample Agentes
        db.execSQL("INSERT INTO " + TABLE_AGENTES + " (" + KEY_AGE_NOMBRE + ", " + KEY_AGE_TELEFONO + ", " + KEY_AGE_EMAIL + ", " + KEY_AGE_ESPECIALIDAD + ") VALUES " +
                "('Ana Torres', '998877665', 'ana.torres@inmobiliaria.com', 'Residencial')," +
                "('Luis Fernández', '944332211', 'luis.fernandez@inmobiliaria.com', 'Comercial');");

        // Sample Visitas
        db.execSQL("INSERT INTO " + TABLE_VISITAS + " (" + KEY_VIS_ID_PROPIEDAD + ", " + KEY_VIS_ID_CLIENTE + ", " + KEY_VIS_ID_AGENTE + ", " + KEY_VIS_FECHA_HORA + ", " + KEY_VIS_COMENTARIOS + ", " + KEY_VIS_ESTADO + ") VALUES " +
                "(1, 1, 1, '2026-06-15 10:00', 'Cliente muy interesado en la piscina y acabados.', 'Programada');");

        // Sample Ventas
        db.execSQL("INSERT INTO " + TABLE_VENTAS + " (" + KEY_VEN_ID_PROPIEDAD + ", " + KEY_VEN_ID_CLIENTE + ", " + KEY_VEN_ID_AGENTE + ", " + KEY_VEN_FECHA_VENTA + ", " + KEY_VEN_MONTO_FINAL + ", " + KEY_VEN_COMISION + ") VALUES " +
                "(6, 2, 2, '2026-05-10', 920000.0, 27600.0);");
    }

    // ==========================================
    // CRUD PROPIEDADES
    // ==========================================
    public long insertPropiedad(Propiedad p) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_PROP_TITULO, p.getTitulo());
        values.put(KEY_PROP_DIRECCION, p.getDireccion());
        values.put(KEY_PROP_PRECIO, p.getPrecio());
        values.put(KEY_PROP_TIPO, p.getTipo());
        values.put(KEY_PROP_ESTADO, p.getEstado());
        values.put(KEY_PROP_IMAGEN_URL, p.getImagenUrl());
        values.put(KEY_PROP_VISTAS, p.getVistas());
        values.put(KEY_PROP_CONSULTAS, p.getConsultas());
        values.put(KEY_PROP_FAVORITOS, p.getFavoritos());
        values.put(KEY_PROP_DIAS_ACTIVO, p.getDiasActivo());
        values.put(KEY_PROP_FECHA_PUB, p.getFechaPublicacion());
        return db.insert(TABLE_PROPIEDADES, null, values);
    }

    public List<Propiedad> getAllPropiedades() {
        List<Propiedad> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_PROPIEDADES + " ORDER BY " + KEY_ID + " DESC", null);
        if (cursor.moveToFirst()) {
            do {
                Propiedad p = new Propiedad(
                        cursor.getInt(cursor.getColumnIndexOrThrow(KEY_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(KEY_PROP_TITULO)),
                        cursor.getString(cursor.getColumnIndexOrThrow(KEY_PROP_DIRECCION)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(KEY_PROP_PRECIO)),
                        cursor.getString(cursor.getColumnIndexOrThrow(KEY_PROP_TIPO)),
                        cursor.getString(cursor.getColumnIndexOrThrow(KEY_PROP_ESTADO)),
                        cursor.getString(cursor.getColumnIndexOrThrow(KEY_PROP_IMAGEN_URL)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(KEY_PROP_VISTAS)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(KEY_PROP_CONSULTAS)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(KEY_PROP_FAVORITOS)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(KEY_PROP_DIAS_ACTIVO)),
                        cursor.getString(cursor.getColumnIndexOrThrow(KEY_PROP_FECHA_PUB))
                );
                list.add(p);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public int updatePropiedad(Propiedad p) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_PROP_TITULO, p.getTitulo());
        values.put(KEY_PROP_DIRECCION, p.getDireccion());
        values.put(KEY_PROP_PRECIO, p.getPrecio());
        values.put(KEY_PROP_TIPO, p.getTipo());
        values.put(KEY_PROP_ESTADO, p.getEstado());
        values.put(KEY_PROP_IMAGEN_URL, p.getImagenUrl());
        return db.update(TABLE_PROPIEDADES, values, KEY_ID + " = ?", new String[]{String.valueOf(p.getId())});
    }

    public int deletePropiedad(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_PROPIEDADES, KEY_ID + " = ?", new String[]{String.valueOf(id)});
    }

    public List<Propiedad> getPropiedadesByAgente(int agenteId) {
        List<Propiedad> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT DISTINCT p.* FROM " + TABLE_PROPIEDADES + " p " +
                "LEFT JOIN " + TABLE_VENTAS + " ve ON p." + KEY_ID + " = ve." + KEY_VEN_ID_PROPIEDAD + " " +
                "LEFT JOIN " + TABLE_VISITAS + " vi ON p." + KEY_ID + " = vi." + KEY_VIS_ID_PROPIEDAD + " " +
                "WHERE ve." + KEY_VEN_ID_AGENTE + " = ? OR vi." + KEY_VIS_ID_AGENTE + " = ? OR p." + KEY_ID + " % 3 = ? % 3 " +
                "ORDER BY p." + KEY_ID + " DESC";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(agenteId), String.valueOf(agenteId), String.valueOf(agenteId)});
        if (cursor.moveToFirst()) {
            do {
                Propiedad p = new Propiedad(
                        cursor.getInt(cursor.getColumnIndexOrThrow(KEY_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(KEY_PROP_TITULO)),
                        cursor.getString(cursor.getColumnIndexOrThrow(KEY_PROP_DIRECCION)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(KEY_PROP_PRECIO)),
                        cursor.getString(cursor.getColumnIndexOrThrow(KEY_PROP_TIPO)),
                        cursor.getString(cursor.getColumnIndexOrThrow(KEY_PROP_ESTADO)),
                        cursor.getString(cursor.getColumnIndexOrThrow(KEY_PROP_IMAGEN_URL)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(KEY_PROP_VISTAS)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(KEY_PROP_CONSULTAS)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(KEY_PROP_FAVORITOS)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(KEY_PROP_DIAS_ACTIVO)),
                        cursor.getString(cursor.getColumnIndexOrThrow(KEY_PROP_FECHA_PUB))
                );
                list.add(p);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    // ==========================================
    // CRUD CLIENTES
    // ==========================================
    public long insertCliente(Cliente c) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_CLI_NOMBRE, c.getNombre());
        values.put(KEY_CLI_TELEFONO, c.getTelefono());
        values.put(KEY_CLI_EMAIL, c.getEmail());
        values.put(KEY_CLI_PRESUPUESTO, c.getPresupuesto());
        values.put(KEY_CLI_INTERES, c.getInteres());
        values.put(KEY_CLI_IMAGEN_URL, c.getImagenUrl());
        return db.insert(TABLE_CLIENTES, null, values);
    }

    public List<Cliente> getAllClientes() {
        List<Cliente> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_CLIENTES + " ORDER BY " + KEY_ID + " DESC", null);
        if (cursor.moveToFirst()) {
            int idxImg = cursor.getColumnIndex(KEY_CLI_IMAGEN_URL);
            do {
                Cliente c = new Cliente(
                        cursor.getInt(cursor.getColumnIndexOrThrow(KEY_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(KEY_CLI_NOMBRE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(KEY_CLI_TELEFONO)),
                        cursor.getString(cursor.getColumnIndexOrThrow(KEY_CLI_EMAIL)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(KEY_CLI_PRESUPUESTO)),
                        cursor.getString(cursor.getColumnIndexOrThrow(KEY_CLI_INTERES))
                );
                if (idxImg != -1) {
                    c.setImagenUrl(cursor.getString(idxImg));
                }
                list.add(c);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public int updateCliente(Cliente c) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_CLI_NOMBRE, c.getNombre());
        values.put(KEY_CLI_TELEFONO, c.getTelefono());
        values.put(KEY_CLI_EMAIL, c.getEmail());
        values.put(KEY_CLI_PRESUPUESTO, c.getPresupuesto());
        values.put(KEY_CLI_INTERES, c.getInteres());
        values.put(KEY_CLI_IMAGEN_URL, c.getImagenUrl());
        return db.update(TABLE_CLIENTES, values, KEY_ID + " = ?", new String[]{String.valueOf(c.getId())});
    }

    public int deleteCliente(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_CLIENTES, KEY_ID + " = ?", new String[]{String.valueOf(id)});
    }

    // ==========================================
    // CRUD AGENTES
    // ==========================================
    public long insertAgente(Agente a) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_AGE_NOMBRE, a.getNombre());
        values.put(KEY_AGE_TELEFONO, a.getTelefono());
        values.put(KEY_AGE_EMAIL, a.getEmail());
        values.put(KEY_AGE_ESPECIALIDAD, a.getEspecialidad());
        values.put(KEY_AGE_IMAGEN_URL, a.getImagenUrl());
        return db.insert(TABLE_AGENTES, null, values);
    }

    public List<Agente> getAllAgentes() {
        List<Agente> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_AGENTES + " ORDER BY " + KEY_ID + " DESC", null);
        if (cursor.moveToFirst()) {
            int idxImg = cursor.getColumnIndex(KEY_AGE_IMAGEN_URL);
            do {
                Agente a = new Agente(
                        cursor.getInt(cursor.getColumnIndexOrThrow(KEY_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(KEY_AGE_NOMBRE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(KEY_AGE_TELEFONO)),
                        cursor.getString(cursor.getColumnIndexOrThrow(KEY_AGE_EMAIL)),
                        cursor.getString(cursor.getColumnIndexOrThrow(KEY_AGE_ESPECIALIDAD))
                );
                if (idxImg != -1) {
                    a.setImagenUrl(cursor.getString(idxImg));
                }
                list.add(a);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public int updateAgente(Agente a) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_AGE_NOMBRE, a.getNombre());
        values.put(KEY_AGE_TELEFONO, a.getTelefono());
        values.put(KEY_AGE_EMAIL, a.getEmail());
        values.put(KEY_AGE_ESPECIALIDAD, a.getEspecialidad());
        values.put(KEY_AGE_IMAGEN_URL, a.getImagenUrl());
        return db.update(TABLE_AGENTES, values, KEY_ID + " = ?", new String[]{String.valueOf(a.getId())});
    }

    public int deleteAgente(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_AGENTES, KEY_ID + " = ?", new String[]{String.valueOf(id)});
    }

    // ==========================================
    // CRUD VISITAS
    // ==========================================
    public long insertVisita(Visita v) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_VIS_ID_PROPIEDAD, v.getIdPropiedad());
        values.put(KEY_VIS_ID_CLIENTE, v.getIdCliente());
        values.put(KEY_VIS_ID_AGENTE, v.getIdAgente());
        values.put(KEY_VIS_FECHA_HORA, v.getFechaHora());
        values.put(KEY_VIS_COMENTARIOS, v.getComentarios());
        values.put(KEY_VIS_ESTADO, v.getEstado());
        return db.insert(TABLE_VISITAS, null, values);
    }

    public List<Visita> getAllVisitas() {
        List<Visita> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT v.*, p." + KEY_PROP_TITULO + " AS prop_titulo, p." + KEY_PROP_IMAGEN_URL + " AS prop_img, p." + KEY_PROP_DIRECCION + " AS prop_dir, p." + KEY_PROP_PRECIO + " AS prop_precio, c." + KEY_CLI_NOMBRE + " AS cli_nombre, a." + KEY_AGE_NOMBRE + " AS age_nombre " +
                "FROM " + TABLE_VISITAS + " v " +
                "LEFT JOIN " + TABLE_PROPIEDADES + " p ON v." + KEY_VIS_ID_PROPIEDAD + " = p." + KEY_ID + " " +
                "LEFT JOIN " + TABLE_CLIENTES + " c ON v." + KEY_VIS_ID_CLIENTE + " = c." + KEY_ID + " " +
                "LEFT JOIN " + TABLE_AGENTES + " a ON v." + KEY_VIS_ID_AGENTE + " = a." + KEY_ID + " " +
                "ORDER BY v." + KEY_ID + " DESC";

        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                Visita v = new Visita(
                        cursor.getInt(cursor.getColumnIndexOrThrow(KEY_ID)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(KEY_VIS_ID_PROPIEDAD)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(KEY_VIS_ID_CLIENTE)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(KEY_VIS_ID_AGENTE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(KEY_VIS_FECHA_HORA)),
                        cursor.getString(cursor.getColumnIndexOrThrow(KEY_VIS_COMENTARIOS)),
                        cursor.getString(cursor.getColumnIndexOrThrow(KEY_VIS_ESTADO))
                );
                v.setTituloPropiedad(cursor.getString(cursor.getColumnIndexOrThrow("prop_titulo")));
                v.setImagenUrlPropiedad(cursor.getString(cursor.getColumnIndexOrThrow("prop_img")));
                v.setDireccionPropiedad(cursor.getString(cursor.getColumnIndexOrThrow("prop_dir")));
                v.setPrecioPropiedad(cursor.getDouble(cursor.getColumnIndexOrThrow("prop_precio")));
                v.setNombreCliente(cursor.getString(cursor.getColumnIndexOrThrow("cli_nombre")));
                v.setNombreAgente(cursor.getString(cursor.getColumnIndexOrThrow("age_nombre")));
                list.add(v);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public int updateVisita(Visita v) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_VIS_ID_PROPIEDAD, v.getIdPropiedad());
        values.put(KEY_VIS_ID_CLIENTE, v.getIdCliente());
        values.put(KEY_VIS_ID_AGENTE, v.getIdAgente());
        values.put(KEY_VIS_FECHA_HORA, v.getFechaHora());
        values.put(KEY_VIS_COMENTARIOS, v.getComentarios());
        values.put(KEY_VIS_ESTADO, v.getEstado());
        return db.update(TABLE_VISITAS, values, KEY_ID + " = ?", new String[]{String.valueOf(v.getId())});
    }

    public int deleteVisita(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_VISITAS, KEY_ID + " = ?", new String[]{String.valueOf(id)});
    }

    // ==========================================
    // CRUD VENTAS
    // ==========================================
    public long insertVenta(Venta v) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_VEN_ID_PROPIEDAD, v.getIdPropiedad());
        values.put(KEY_VEN_ID_CLIENTE, v.getIdCliente());
        values.put(KEY_VEN_ID_AGENTE, v.getIdAgente());
        values.put(KEY_VEN_FECHA_VENTA, v.getFechaVenta());
        values.put(KEY_VEN_MONTO_FINAL, v.getMontoFinal());
        values.put(KEY_VEN_COMISION, v.getComision());
        values.put(KEY_VEN_METODO_PAGO, v.getMetodoPago());
        return db.insert(TABLE_VENTAS, null, values);
    }

    public List<Venta> getAllVentas() {
        List<Venta> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT ve.*, p." + KEY_PROP_TITULO + " AS prop_titulo, p." + KEY_PROP_IMAGEN_URL + " AS prop_img, p." + KEY_PROP_DIRECCION + " AS prop_dir, c." + KEY_CLI_NOMBRE + " AS cli_nombre, a." + KEY_AGE_NOMBRE + " AS age_nombre " +
                "FROM " + TABLE_VENTAS + " ve " +
                "LEFT JOIN " + TABLE_PROPIEDADES + " p ON ve." + KEY_VEN_ID_PROPIEDAD + " = p." + KEY_ID + " " +
                "LEFT JOIN " + TABLE_CLIENTES + " c ON ve." + KEY_VEN_ID_CLIENTE + " = c." + KEY_ID + " " +
                "LEFT JOIN " + TABLE_AGENTES + " a ON ve." + KEY_VEN_ID_AGENTE + " = a." + KEY_ID + " " +
                "ORDER BY ve." + KEY_ID + " DESC";

        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            int idxMetodo = cursor.getColumnIndex(KEY_VEN_METODO_PAGO);
            do {
                Venta v = new Venta(
                        cursor.getInt(cursor.getColumnIndexOrThrow(KEY_ID)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(KEY_VEN_ID_PROPIEDAD)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(KEY_VEN_ID_CLIENTE)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(KEY_VEN_ID_AGENTE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(KEY_VEN_FECHA_VENTA)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(KEY_VEN_MONTO_FINAL)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(KEY_VEN_COMISION))
                );
                v.setTituloPropiedad(cursor.getString(cursor.getColumnIndexOrThrow("prop_titulo")));
                v.setImagenUrlPropiedad(cursor.getString(cursor.getColumnIndexOrThrow("prop_img")));
                v.setDireccionPropiedad(cursor.getString(cursor.getColumnIndexOrThrow("prop_dir")));
                v.setNombreCliente(cursor.getString(cursor.getColumnIndexOrThrow("cli_nombre")));
                v.setNombreAgente(cursor.getString(cursor.getColumnIndexOrThrow("age_nombre")));
                if (idxMetodo != -1) {
                    v.setMetodoPago(cursor.getString(idxMetodo));
                }
                list.add(v);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public int updateVenta(Venta v) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_VEN_ID_PROPIEDAD, v.getIdPropiedad());
        values.put(KEY_VEN_ID_CLIENTE, v.getIdCliente());
        values.put(KEY_VEN_ID_AGENTE, v.getIdAgente());
        values.put(KEY_VEN_FECHA_VENTA, v.getFechaVenta());
        values.put(KEY_VEN_MONTO_FINAL, v.getMontoFinal());
        values.put(KEY_VEN_COMISION, v.getComision());
        values.put(KEY_VEN_METODO_PAGO, v.getMetodoPago());
        return db.update(TABLE_VENTAS, values, KEY_ID + " = ?", new String[]{String.valueOf(v.getId())});
    }

    public int deleteVenta(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_VENTAS, KEY_ID + " = ?", new String[]{String.valueOf(id)});
    }
}
