package com.example.retrofitcrudapp

class MainActivity : ListaPersonasActivity<Alumno>() {
    override val esProfesor = false
    override fun obtener() = RetrofitClient.instance.obtenerAlumnos()
    override fun eliminar(id: Int) = RetrofitClient.instance.eliminarAlumno(id)
    override fun crearAdapter(onClick: (Alumno) -> Unit) = AlumnoAdapter(onClick)
}
