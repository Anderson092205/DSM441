package com.example.retrofitcrudapp

class ActualizarProfesorActivity : FormularioPersonaActivity<Profesor>() {
    override val esProfesor = true
    override val esEdicion = true
    override fun enviar(id: Int, datos: DatosPersona) = RetrofitClient.profesores.actualizarProfesor(id, datos)
}
