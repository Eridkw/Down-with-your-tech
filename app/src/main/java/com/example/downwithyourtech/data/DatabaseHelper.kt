package com.example.downwithyourtech.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, "TechStore.db", null, 2) {

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = "CREATE TABLE usuarios (id INTEGER PRIMARY KEY AUTOINCREMENT, usuario TEXT, correo TEXT, password TEXT)"
        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS usuarios")
        onCreate(db)
    }

    // 1. REGISTRAR
    fun registrarUsuario(usuario: String, correo: String, pass: String): Boolean {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put("usuario", usuario)
        contentValues.put("correo", correo)
        contentValues.put("password", pass)
        val resultado = db.insert("usuarios", null, contentValues)
        return resultado != -1L
    }

    // 2. VALIDAR DUPLICADOS
    fun existeUsuarioOCorreo(usuario: String, correo: String): Boolean {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM usuarios WHERE usuario = ? OR correo = ?", arrayOf(usuario, correo))
        val existe = cursor.count > 0
        cursor.close()
        return existe
    }

    // --- 3. LOGIN MEJORADO (Devuelve el NOMBRE REAL o null) ---
    fun validarLogin(inputUserOrEmail: String, pass: String): String? {
        val db = this.readableDatabase
        // Seleccionamos específicamente la columna 'usuario'
        val query = "SELECT usuario FROM usuarios WHERE (usuario = ? OR correo = ?) AND password = ?"
        val cursor = db.rawQuery(query, arrayOf(inputUserOrEmail, inputUserOrEmail, pass))

        var nombreReal: String? = null

        if (cursor.moveToFirst()) {
            // Si encontramos coincidencia, sacamos el nombre de la columna 0
            nombreReal = cursor.getString(0)
        }

        cursor.close()
        return nombreReal // Devolvemos el nombre (ej. "Dabi777") o null si falló
    }
}