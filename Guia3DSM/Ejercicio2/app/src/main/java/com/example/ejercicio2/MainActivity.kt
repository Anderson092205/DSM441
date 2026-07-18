package com.example.ejercicio2

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.textfield.TextInputEditText

class MainActivity : AppCompatActivity() {

    private lateinit var etNombres: TextInputEditText
    private lateinit var etApellidos: TextInputEditText
    private lateinit var etCorreo: TextInputEditText
    private lateinit var etTelefono: TextInputEditText
    private lateinit var etEdad: TextInputEditText
    private lateinit var tvResumen: TextView
    private lateinit var btnValidar: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        etNombres = findViewById(R.id.etNombres)
        etApellidos = findViewById(R.id.etApellidos)
        etCorreo = findViewById(R.id.etCorreo)
        etTelefono = findViewById(R.id.etTelefono)
        etEdad = findViewById(R.id.etEdad)
        tvResumen = findViewById(R.id.tvResumen)
        btnValidar = findViewById(R.id.btnValidar)

        if (savedInstanceState != null) {
            val savedSummary = savedInstanceState.getString("summary", "")
            tvResumen.text = savedSummary
        }

        btnValidar.setOnClickListener {
            validarInformacion()
        }
    }

    private fun validarInformacion() {
        val nombres = etNombres.text.toString().trim()
        val apellidos = etApellidos.text.toString().trim()
        val correo = etCorreo.text.toString().trim()
        val telefono = etTelefono.text.toString().trim()
        val edadStr = etEdad.text.toString().trim()

        if (nombres.isEmpty() || apellidos.isEmpty() || correo.isEmpty() || telefono.isEmpty() || edadStr.isEmpty()) {
            Toast.makeText(this, "Por favor complete todos los campos.", Toast.LENGTH_SHORT).show()
            tvResumen.text = ""
            return
        }

        val edad = edadStr.toIntOrNull()
        if (edad == null) {
            Toast.makeText(this, "Por favor ingrese una edad válida.", Toast.LENGTH_SHORT).show()
            tvResumen.text = ""
            return
        }

        val esMayor = if (edad >= 18) "Es mayor de edad" else "Es menor de edad"

        val resumen = """
            Resumen de Información:
            Nombres: $nombres
            Apellidos: $apellidos
            Correo: $correo
            Teléfono: $telefono
            Edad: $edad
            Resultado: $esMayor
        """.trimIndent()

        tvResumen.text = resumen
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("summary", tvResumen.text.toString())
    }

    override fun onStart() {
        super.onStart()
        mostrarToast("onStart")
    }

    override fun onResume() {
        super.onResume()
        mostrarToast("onResume")
    }

    override fun onPause() {
        super.onPause()
        mostrarToast("onPause")
    }

    override fun onStop() {
        super.onStop()
        mostrarToast("onStop")
    }

    override fun onRestart() {
        super.onRestart()
        mostrarToast("onRestart")
    }

    override fun onDestroy() {
        super.onDestroy()
        mostrarToast("onDestroy")
    }

    private fun mostrarToast(
        mensaje: String,
        duracion: Int = Toast.LENGTH_SHORT
    ) {
        val texto = "MainActivity -> $mensaje"
        Toast.makeText(this, texto, duracion).show()
        Log.d("CicloDeVida", texto)
    }
}