package com.example.realtimedatabase.datos

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Persona (
    var dui: String? = null,
    var nombre: String? = null,
    var fechaNacimiento: String? = null,
    var genero: String? = null,
    var peso: String? = null,
    var altura: String? = null,
    @get:Exclude
    var key: String? = null
){
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "dui" to (dui ?: ""),
            "nombre" to (nombre ?: ""),
            "fechaNacimiento" to (fechaNacimiento ?: ""),
            "genero" to (genero ?: ""),
            "peso" to (peso ?: ""),
            "altura" to (altura ?: "")
        )
    }
}