package com.example.sqliteapp

object ValidacionProducto {
    fun precio(texto: String): Double? {
        val normalizado = texto.trim().replace(',', '.')
        if (!Regex("[0-9]+(?:\\.[0-9]{1,2})?").matches(normalizado)) return null
        return normalizado.toDoubleOrNull()?.takeIf { it.isFinite() && it <= 99999999.99 }
    }
    fun cantidad(texto: String): Int? = texto.trim().toIntOrNull()?.takeIf { it >= 0 }
    fun codigo(texto: String): Int? = texto.trim().toIntOrNull()?.takeIf { it > 0 }
}
