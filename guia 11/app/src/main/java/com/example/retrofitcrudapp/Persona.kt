package com.example.retrofitcrudapp

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type

interface Persona {
    val id: Int
    val nombre: String
    val apellido: String
    val edad: Int?
}

// En POST no enviamos el ID: MockAPI lo asigna automáticamente.
data class DatosPersona(val nombre: String, val apellido: String, val edad: Int)

// Hay registros de ejemplo con edades como "edad 1" en el servidor de la guía.
class EdadDeserializer : JsonDeserializer<Int?> {
    override fun deserialize(json: JsonElement, type: Type, context: JsonDeserializationContext): Int? =
        if (json.isJsonPrimitive) json.asString.toIntOrNull() else null
}
