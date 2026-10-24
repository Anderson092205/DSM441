package com.example.retrofitcrudapp

import com.google.gson.annotations.JsonAdapter

data class Profesor(
    override val id: Int,
    override val nombre: String,
    override val apellido: String,
    @field:JsonAdapter(EdadDeserializer::class) override val edad: Int?
) : Persona
