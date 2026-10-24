package com.example.sqliteapp

import android.database.sqlite.SQLiteConstraintException
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.example.sqliteapp.model.Usuario
import java.util.concurrent.Executors

class RegistroActivity : BaseActivity() {
    private val executor = Executors.newSingleThreadExecutor()
    private var ocupado = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preparar(R.layout.activity_registro)
        val nick = findViewById<EditText>(R.id.txtNick)
        val clave = findViewById<EditText>(R.id.txtContrasena)
        val confirmar = findViewById<EditText>(R.id.txtConfirmarContrasena)
        val guardar = findViewById<Button>(R.id.btnCrearCuenta)
        val volver = findViewById<Button>(R.id.btnVolverLogin)
        val estado = findViewById<TextView>(R.id.estadoRegistro)
        val progreso = findViewById<ProgressBar>(R.id.progresoRegistro)
        volver.setOnClickListener { finish() }
        guardar.setOnClickListener {
            if (ocupado) return@setOnClickListener
            nick.error = null; clave.error = null; confirmar.error = null
            val nombre = nick.text.toString().trim()
            val password = clave.text.toString()
            when {
                nombre.isBlank() -> nick.error = getString(R.string.nick_obligatorio)
                password.length < 6 -> clave.error = getString(R.string.clave_corta)
                password != confirmar.text.toString() -> confirmar.error = getString(R.string.claves_diferentes)
                else -> {
                    ocupado = true
                    guardar.isEnabled = false; volver.isEnabled = false
                    progreso.visibility = View.VISIBLE
                    estado.text = ""
                    executor.execute {
                        val resultado = runCatching { Usuario(applicationContext).use { it.registrar(nombre, password) } }
                        runOnUiThread {
                            if (isFinishing || isDestroyed) return@runOnUiThread
                            ocupado = false
                            guardar.isEnabled = true; volver.isEnabled = true
                            progreso.visibility = View.GONE
                            if (resultado.isSuccess) {
                                Toast.makeText(this, R.string.cuenta_creada, Toast.LENGTH_LONG).show()
                                finish()
                            } else estado.setText(if (resultado.exceptionOrNull() is SQLiteConstraintException)
                                R.string.nick_duplicado else R.string.error_bd)
                        }
                    }
                }
            }
        }
    }
    override fun onDestroy() { executor.shutdown(); super.onDestroy() }
}
