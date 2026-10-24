package com.example.sqliteapp.model

import android.content.ContentValues
import android.content.Context
import com.example.sqliteapp.db.HelperDB
import com.example.sqliteapp.PasswordHash
import java.io.Closeable

class Usuario(context: Context, nombreDB: String = HelperDB.DB_NAME) : Closeable {
    private val helper = HelperDB(context, nombreDB)
    companion object {
        const val CREATE_TABLE_USUARIO = "CREATE TABLE IF NOT EXISTS Usuario (" +
            "idusuario INTEGER PRIMARY KEY AUTOINCREMENT, nick VARCHAR(50) NOT NULL COLLATE NOCASE UNIQUE, " +
            "contrasena TEXT NOT NULL)"
    }
    fun registrar(nick: String, contrasena: String): Long {
        val valores = ContentValues().apply {
            put("nick", nick.trim())
            put("contrasena", PasswordHash.crear(contrasena))
        }
        return helper.writableDatabase.insertOrThrow("Usuario", null, valores)
    }
    fun autenticar(nick: String, contrasena: String): Boolean = helper.readableDatabase.query("Usuario",
        arrayOf("contrasena"), "nick=?", arrayOf(nick.trim()), null, null, null).use {
            it.moveToFirst() && PasswordHash.verificar(contrasena, it.getString(0))
        }
    override fun close() = helper.close()
}
