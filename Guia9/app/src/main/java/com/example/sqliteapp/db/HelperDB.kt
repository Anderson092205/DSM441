package com.example.sqliteapp.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.sqliteapp.model.Categoria
import com.example.sqliteapp.model.Productos
import com.example.sqliteapp.model.Usuario

class HelperDB(context: Context, nombre: String = DB_NAME) :
    SQLiteOpenHelper(context.applicationContext, nombre, null, DB_VERSION) {
    companion object {
        const val DB_NAME = "tienda.sqlite"
        const val DB_VERSION = 2
    }
    override fun onConfigure(db: SQLiteDatabase) {
        super.onConfigure(db)
        db.setForeignKeyConstraintsEnabled(true)
    }
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(Categoria.CREATE_TABLE_CATEGORIA)
        db.execSQL(Productos.CREATE_TABLE_PRODUCTOS)
        db.execSQL(Usuario.CREATE_TABLE_USUARIO)
    }
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // La versión 2 agrega el login sin borrar categorías ni productos de la versión 1.
        if (oldVersion < 2) db.execSQL(Usuario.CREATE_TABLE_USUARIO)
    }
}
