package com.example.sqliteapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import com.example.sqliteapp.model.Usuario
import java.util.concurrent.Executors

class LoginActivity : BaseActivity() {
    private val executor = Executors.newSingleThreadExecutor()
    private var ocupado = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preparar(R.layout.activity_login)
        Sesion.nick = null
        val nick = findViewById<EditText>(R.id.txtNick)
        val password = findViewById<EditText>(R.id.txtContrasena)
        val entrar = findViewById<Button>(R.id.btnIngresar)
        val registrar = findViewById<Button>(R.id.btnRegistrarse)
        val mensaje = findViewById<TextView>(R.id.estadoLogin)
        val progreso = findViewById<ProgressBar>(R.id.progresoLogin)
        registrar.setOnClickListener { startActivity(Intent(this, RegistroActivity::class.java)) }
        entrar.setOnClickListener {
            if (ocupado) return@setOnClickListener
            val nombre = nick.text.toString().trim()
            val clave = password.text.toString()
            nick.error = null; password.error = null
            if (nombre.isEmpty()) { nick.error = getString(R.string.nick_obligatorio); return@setOnClickListener }
            if (clave.isEmpty()) { password.error = getString(R.string.clave_obligatoria); return@setOnClickListener }
            ocupado = true
            entrar.isEnabled = false; registrar.isEnabled = false
            progreso.visibility = View.VISIBLE
            mensaje.text = ""
            executor.execute {
                val resultado = runCatching { Usuario(applicationContext).use { it.autenticar(nombre, clave) } }
                runOnUiThread {
                    if (isFinishing || isDestroyed) return@runOnUiThread
                    ocupado = false
                    entrar.isEnabled = true; registrar.isEnabled = true
                    progreso.visibility = View.GONE
                    if (resultado.getOrDefault(false)) {
                        Sesion.nick = nombre
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    } else mensaje.setText(if (resultado.isFailure) R.string.error_bd else R.string.credenciales_invalidas)
                }
            }
        }
    }
    override fun onDestroy() { executor.shutdown(); super.onDestroy() }
}
