package com.example.retrofitcrudapp

class ActualizarAlumnoActivity : FormularioPersonaActivity<Alumno>() {
    override val esProfesor = false
    override val esEdicion = true
    override fun enviar(id: Int, datos: DatosPersona) = RetrofitClient.instance.actualizarAlumno(id, datos)
}
