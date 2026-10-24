package com.example.retrofitcrudapp

class CrearAlumnoActivity : FormularioPersonaActivity<Alumno>() {
    override val esProfesor = false
    override val esEdicion = false
    override fun enviar(id: Int, datos: DatosPersona) = RetrofitClient.instance.crearAlumno(datos)
}
