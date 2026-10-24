package com.example.retrofitcrudapp

class ProfesoresActivity : ListaPersonasActivity<Profesor>() {
    override val esProfesor = true
    override fun obtener() = RetrofitClient.profesores.obtenerProfesores()
    override fun eliminar(id: Int) = RetrofitClient.profesores.eliminarProfesor(id)
    override fun crearAdapter(onClick: (Profesor) -> Unit) = ProfesorAdapter(onClick)
}
