package com.example.rutana.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(
        context,
        "rutana.db",
        null,
        1
    ) {

    override fun onCreate(db: SQLiteDatabase) {
        val query = """
            CREATE TABLE usuarios(
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            nombre TEXT,
            email TEXT UNIQUE,
            password TEXT,
            rol TEXT
            )
        """.trimIndent()

        db.execSQL(query)

        // Usuario de prueba (se crea una sola vez, al crear la base de datos)
        val datos = ContentValues()
        datos.put("nombre", "Juan Pérez")
        datos.put("email", "admin@rutana.com")
        datos.put("password", "123456")
        datos.put("rol", "Administrator")
        db.insert("usuarios", null, datos)
    }

    override fun onUpgrade(
        db: SQLiteDatabase,
        oldVersion: Int,
        newVersion: Int
    ) {
        db.execSQL("DROP TABLE IF EXISTS usuarios")
        onCreate(db)
    }

    fun validarUsuario(email: String, password: String): String? {
        val db = readableDatabase

        val cursor = db.rawQuery(
            "SELECT nombre FROM usuarios WHERE email = ? AND password = ?",
            arrayOf(email, password)
        )

        var nombre: String? = null
        if (cursor.moveToFirst()) {
            nombre = cursor.getString(0)
        }

        cursor.close()
        db.close()

        return nombre
    }
}
