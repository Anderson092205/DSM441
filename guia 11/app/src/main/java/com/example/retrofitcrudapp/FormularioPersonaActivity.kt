package com.example.retrofitcrudapp

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

abstract class FormularioPersonaActivity<T : Persona> : BaseActivity() {
    protected abstract val esProfesor: Boolean
    protected abstract val esEdicion: Boolean
    protected abstract fun enviar(id: Int, datos: DatosPersona): Call<T>
    private lateinit var nombre: EditText
    private lateinit var apellido: EditText
    private lateinit var edad: EditText
    private lateinit var guardar: Button
    private lateinit var progreso: ProgressBar
    private lateinit var error: TextView
    private var solicitud: Call<T>? = null
    private var id = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prepararPantalla(if (esEdicion) R.layout.activity_actualizar_alumno else R.layout.activity_crear_alumno)
        nombre = findViewById(if (esEdicion) R.id.nombreEditText else R.id.editTextNombre)
        apellido = findViewById(if (esEdicion) R.id.apellidoEditText else R.id.editTextApellido)
        edad = findViewById(if (esEdicion) R.id.edadEditText else R.id.editTextEdad)
        guardar = findViewById(if (esEdicion) R.id.actualizarButton else R.id.btnGuardar)
        progreso = findViewById(R.id.progreso)
        error = findViewById(R.id.errorFormulario)
        findViewById<TextView>(R.id.tituloFormulario).setText(
            if (esProfesor) { if (esEdicion) R.string.editar_profesor else R.string.agregar_profesor }
            else { if (esEdicion) R.string.editar_alumno else R.string.agregar_alumno })
        findViewById<Button>(R.id.btnCancelar).setOnClickListener { finish() }
        id = intent.getIntExtra("persona_id", -1)
        if (esEdicion && id <= 0) {
            Toast.makeText(this, R.string.registro_invalido, Toast.LENGTH_LONG).show()
            finish()
            return
        }
        if (esEdicion && savedInstanceState == null) {
            nombre.setText(intent.getStringExtra("nombre").orEmpty())
            apellido.setText(intent.getStringExtra("apellido").orEmpty())
            edad.setText(if (intent.hasExtra("edad")) intent.getIntExtra("edad", 0).toString() else "")
        }
        guardar.setOnClickListener { guardarDatos() }
    }
    private fun guardarDatos() {
        if (solicitud != null) return
        val datos = validar() ?: return
        ocupado(true)
        error.visibility = View.GONE
        val llamada = enviar(id, datos)
        solicitud = llamada
        llamada.enqueue(object : Callback<T> {
            override fun onResponse(call: Call<T>, response: Response<T>) {
                if (isFinishing || isDestroyed) return
                solicitud = null
                ocupado(false)
                if (response.isSuccessful && response.body() != null) {
                    Toast.makeText(this@FormularioPersonaActivity,
                        if (esEdicion) R.string.registro_actualizado else R.string.registro_creado, Toast.LENGTH_SHORT).show()
                    setResult(RESULT_OK)
                    finish()
                } else mostrarError(getString(R.string.error_http, response.code()))
            }
            override fun onFailure(call: Call<T>, t: Throwable) {
                if (call.isCanceled || isFinishing || isDestroyed) return
                solicitud = null
                ocupado(false)
                mostrarError(getString(R.string.error_conexion))
            }
        })
    }
    private fun validar(): DatosPersona? {
        nombre.error = null
        apellido.error = null
        edad.error = null
        val n = nombre.text.toString().trim()
        val a = apellido.text.toString().trim()
        val e = edad.text.toString().toIntOrNull()
        if (n.isBlank()) { nombre.error = getString(R.string.campo_obligatorio); nombre.requestFocus(); return null }
        if (a.isBlank()) { apellido.error = getString(R.string.campo_obligatorio); apellido.requestFocus(); return null }
        if (e == null || e !in 1..120) { edad.error = getString(R.string.edad_invalida); edad.requestFocus(); return null }
        return DatosPersona(n, a, e)
    }
    private fun ocupado(valor: Boolean) {
        guardar.isEnabled = !valor
        nombre.isEnabled = !valor
        apellido.isEnabled = !valor
        edad.isEnabled = !valor
        findViewById<Button>(R.id.btnCancelar).isEnabled = !valor
        progreso.visibility = if (valor) View.VISIBLE else View.GONE
    }
    private fun mostrarError(mensaje: String) {
        error.text = mensaje
        error.visibility = View.VISIBLE
    }
    override fun onDestroy() {
        solicitud?.cancel()
        super.onDestroy()
    }
}
