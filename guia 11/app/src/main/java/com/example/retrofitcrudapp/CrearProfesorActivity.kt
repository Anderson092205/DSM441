package com.example.retrofitcrudapp

class CrearProfesorActivity : FormularioPersonaActivity<Profesor>() {
    override val esProfesor = true
    override val esEdicion = false
    override fun enviar(id: Int, datos: DatosPersona) = RetrofitClient.profesores.crearProfesor(datos)
}
