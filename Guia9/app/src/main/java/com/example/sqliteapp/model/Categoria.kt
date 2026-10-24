package com.example.sqliteapp.model

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.DatabaseUtils
import com.example.sqliteapp.db.HelperDB
import java.io.Closeable

class Categoria(context: Context, nombreDB: String = HelperDB.DB_NAME) : Closeable {
    private val helper = HelperDB(context, nombreDB)
    companion object {
        const val TABLE_NAME_CATEGORIA = "categoria"
        const val COL_ID = "idcategoria"
        const val COL_NOMBRE = "nombre"
        const val CREATE_TABLE_CATEGORIA = "CREATE TABLE IF NOT EXISTS categoria (" +
            "idcategoria INTEGER PRIMARY KEY AUTOINCREMENT, nombre VARCHAR(50) NOT NULL UNIQUE)"
        val PREDETERMINADAS = listOf("Abarrotes", "Carnes", "Embutidos", "Mariscos", "Pescado",
            "Bebidas", "Verduras", "Frutas", "Bebidas Carbonatadas", "Bebidas no carbonatadas")
    }
    fun generarContentValues(nombre: String) = ContentValues().apply { put(COL_NOMBRE, nombre) }
    fun insertValuesDefault() {
        val db = helper.writableDatabase
        db.beginTransaction()
        try {
            if (DatabaseUtils.queryNumEntries(db, TABLE_NAME_CATEGORIA) == 0L) {
                PREDETERMINADAS.forEach { db.insertOrThrow(TABLE_NAME_CATEGORIA, null, generarContentValues(it)) }
            }
            db.setTransactionSuccessful()
        } finally { db.endTransaction() }
    }
    fun showAllCategoria(): Cursor = helper.readableDatabase.query(TABLE_NAME_CATEGORIA,
        arrayOf(COL_ID, COL_NOMBRE), null, null, null, null, "$COL_NOMBRE ASC")
    fun searchID(nombre: String): Int? = helper.readableDatabase.query(TABLE_NAME_CATEGORIA,
        arrayOf(COL_ID), "$COL_NOMBRE=?", arrayOf(nombre), null, null, null).use {
            if (it.moveToFirst()) it.getInt(0) else null
        }
    fun searchNombre(id: Int): String? = helper.readableDatabase.query(TABLE_NAME_CATEGORIA,
        arrayOf(COL_NOMBRE), "$COL_ID=?", arrayOf(id.toString()), null, null, null).use {
            if (it.moveToFirst()) it.getString(0) else null
        }
    override fun close() = helper.close()
}
