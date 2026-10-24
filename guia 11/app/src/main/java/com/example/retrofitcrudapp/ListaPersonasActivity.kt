package com.example.retrofitcrudapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

abstract class ListaPersonasActivity<T : Persona> : BaseActivity() {
    protected abstract val esProfesor: Boolean
    protected abstract fun obtener(): Call<List<T>>
    protected abstract fun eliminar(id: Int): Call<Void>
    protected abstract fun crearAdapter(onClick: (T) -> Unit): PersonaAdapter<T>
    private lateinit var adapter: PersonaAdapter<T>
    private lateinit var progreso: ProgressBar
    private lateinit var estado: TextView
    private lateinit var recargar: Button
    private lateinit var agregar: FloatingActionButton
    private lateinit var lista: RecyclerView
    private var consulta: Call<List<T>>? = null
    private var borrado: Call<Void>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prepararPantalla(R.layout.activity_main)
        findViewById<TextView>(R.id.titulo).setText(if (esProfesor) R.string.profesores else R.string.alumnos)
        findViewById<Button>(R.id.btnCambiar).apply {
            setText(if (esProfesor) R.string.ver_alumnos else R.string.ver_profesores)
            setOnClickListener {
                if (esProfesor) finish()
                else startActivity(Intent(this@ListaPersonasActivity, ProfesoresActivity::class.java))
            }
        }
        progreso = findViewById(R.id.progreso)
        estado = findViewById(R.id.estado)
        recargar = findViewById(R.id.btnRecargar)
        agregar = findViewById(R.id.fab_agregar)
        lista = findViewById(R.id.recyclerView)
        adapter = crearAdapter(::mostrarOpciones)
        lista.layoutManager = LinearLayoutManager(this)
        lista.adapter = adapter
        recargar.setOnClickListener { cargarDatos() }
        agregar.contentDescription = getString(if (esProfesor) R.string.agregar_profesor else R.string.agregar_alumno)
        agregar.setOnClickListener {
            startActivity(Intent(this, if (esProfesor) CrearProfesorActivity::class.java else CrearAlumnoActivity::class.java))
        }
    }
    override fun onResume() {
        super.onResume()
        cargarDatos()
    }
    private fun cargarDatos() {
        if (borrado != null) return
        consulta?.cancel()
        progreso.visibility = View.VISIBLE
        recargar.isEnabled = false
        estado.visibility = View.GONE
        val llamada = obtener()
        consulta = llamada
        llamada.enqueue(object : Callback<List<T>> {
            override fun onResponse(call: Call<List<T>>, response: Response<List<T>>) {
                if (call !== consulta || isFinishing || isDestroyed) return
                terminarCarga()
                val datos = response.body()
                if (response.isSuccessful && datos != null) {
                    adapter.actualizar(datos)
                    lista.visibility = View.VISIBLE
                    estado.text = getString(R.string.sin_registros)
                    estado.visibility = if (datos.isEmpty()) View.VISIBLE else View.GONE
                } else mostrarError(getString(R.string.error_http, response.code()))
            }
            override fun onFailure(call: Call<List<T>>, t: Throwable) {
                if (call.isCanceled || call !== consulta || isFinishing || isDestroyed) return
                terminarCarga()
                mostrarError(getString(R.string.error_conexion))
            }
        })
    }
    private fun terminarCarga() {
        consulta = null
        progreso.visibility = View.GONE
        recargar.isEnabled = true
    }
    private fun mostrarError(mensaje: String) {
        lista.visibility = View.GONE
        estado.text = mensaje
        estado.visibility = View.VISIBLE
    }
    private fun mostrarOpciones(persona: T) {
        if (borrado != null) return
        AlertDialog.Builder(this).setTitle("${persona.nombre} ${persona.apellido}")
            .setItems(R.array.opciones_persona) { _, opcion ->
                if (opcion == 0) {
                    val pantalla = if (esProfesor) ActualizarProfesorActivity::class.java else ActualizarAlumnoActivity::class.java
                    startActivity(Intent(this, pantalla).apply {
                        putExtra("persona_id", persona.id)
                        putExtra("nombre", persona.nombre)
                        putExtra("apellido", persona.apellido)
                        persona.edad?.let { putExtra("edad", it) }
                    })
                } else confirmarEliminacion(persona)
            }.setNegativeButton(R.string.cancelar, null).show()
    }
    private fun confirmarEliminacion(persona: T) {
        AlertDialog.Builder(this).setTitle(R.string.eliminar)
            .setMessage(getString(R.string.confirmar_eliminar, persona.nombre, persona.apellido))
            .setNegativeButton(R.string.cancelar, null)
            .setPositiveButton(R.string.eliminar) { _, _ -> eliminarPersona(persona) }.show()
    }
    private fun eliminarPersona(persona: T) {
        if (borrado != null) return
        consulta?.cancel()
        consulta = null
        progreso.visibility = View.VISIBLE
        agregar.isEnabled = false
        recargar.isEnabled = false
        val llamada = eliminar(persona.id)
        borrado = llamada
        llamada.enqueue(object : Callback<Void> {
            private fun terminar() {
                borrado = null
                progreso.visibility = View.GONE
                agregar.isEnabled = true
                recargar.isEnabled = true
            }
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (isFinishing || isDestroyed) return
                terminar()
                if (response.isSuccessful) {
                    Toast.makeText(this@ListaPersonasActivity, R.string.registro_eliminado, Toast.LENGTH_SHORT).show()
                    cargarDatos()
                } else Toast.makeText(this@ListaPersonasActivity, getString(R.string.error_http, response.code()), Toast.LENGTH_LONG).show()
            }
            override fun onFailure(call: Call<Void>, t: Throwable) {
                if (call.isCanceled || isFinishing || isDestroyed) return
                terminar()
                Toast.makeText(this@ListaPersonasActivity, R.string.error_conexion, Toast.LENGTH_LONG).show()
            }
        })
    }
    override fun onDestroy() {
        consulta?.cancel()
        borrado?.cancel()
        super.onDestroy()
    }
}
