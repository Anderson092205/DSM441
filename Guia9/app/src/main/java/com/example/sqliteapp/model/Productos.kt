package com.example.sqliteapp.model

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import com.example.sqliteapp.db.HelperDB
import java.io.Closeable

class Productos(context: Context, nombreDB: String = HelperDB.DB_NAME) : Closeable {
    private val helper = HelperDB(context, nombreDB)
    companion object {
        const val TABLE_NAME_PRODUCTOS = "productos"
        const val COL_ID = "idproductos"
        const val COL_IDCATEGORIA = "idcategoria"
        const val COL_DESCRIPCION = "descripcion"
        const val COL_PRECIO = "precio"
        const val COL_CANTIDAD = "cantidad"
        const val CREATE_TABLE_PRODUCTOS = "CREATE TABLE IF NOT EXISTS productos (" +
            "idproductos INTEGER PRIMARY KEY AUTOINCREMENT, idcategoria INTEGER NOT NULL, " +
            "descripcion VARCHAR(150) NOT NULL, precio DECIMAL(10,2) NOT NULL CHECK(precio >= 0), " +
            "cantidad INTEGER NOT NULL CHECK(cantidad >= 0), " +
            "FOREIGN KEY(idcategoria) REFERENCES categoria(idcategoria))"
        private val COLUMNAS = arrayOf(COL_ID, COL_IDCATEGORIA, COL_DESCRIPCION, COL_PRECIO, COL_CANTIDAD)
    }
    fun generarContentValues(idcategoria: Int, descripcion: String, precio: Double, cantidad: Int) =
        ContentValues().apply {
            put(COL_IDCATEGORIA, idcategoria); put(COL_DESCRIPCION, descripcion)
            put(COL_PRECIO, precio); put(COL_CANTIDAD, cantidad)
        }
    fun addNewProducto(idcategoria: Int, descripcion: String, precio: Double, cantidad: Int): Long =
        helper.writableDatabase.insertOrThrow(TABLE_NAME_PRODUCTOS, null,
            generarContentValues(idcategoria, descripcion, precio, cantidad))
    fun deleteProducto(id: Int): Int = helper.writableDatabase.delete(TABLE_NAME_PRODUCTOS,
        "$COL_ID=?", arrayOf(id.toString()))
    fun updateProducto(id: Int, idcategoria: Int, descripcion: String, precio: Double, cantidad: Int): Int =
        helper.writableDatabase.update(TABLE_NAME_PRODUCTOS,
            generarContentValues(idcategoria, descripcion, precio, cantidad), "$COL_ID=?", arrayOf(id.toString()))
    fun searchProducto(id: Int): Cursor = helper.readableDatabase.query(TABLE_NAME_PRODUCTOS,
        COLUMNAS, "$COL_ID=?", arrayOf(id.toString()), null, null, null)
    fun searchProductosAll(): Cursor = helper.readableDatabase.query(TABLE_NAME_PRODUCTOS,
        COLUMNAS, null, null, null, null, "$COL_DESCRIPCION ASC")
    override fun close() = helper.close()
}
