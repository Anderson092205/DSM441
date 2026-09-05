package com.example.guia6

import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class RegisterActivity : AppCompatActivity() {

    private lateinit var etNombre: EditText
    private lateinit var etApellido: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var rgGenero: RadioGroup
    private lateinit var cbTerminos: CheckBox
    private lateinit var btnRegistrar: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.constraintRoot)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )
            insets
        }

        // Inicializar vistas
        etNombre = findViewById(R.id.etNombre)
        etApellido = findViewById(R.id.etApellido)
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        rgGenero = findViewById(R.id.rgGenero)
        cbTerminos = findViewById(R.id.cbTerminos)
        btnRegistrar = findViewById(R.id.btnRegistrar)

        // Configurar listener para el botón de registro
        btnRegistrar.setOnClickListener {
            validarYRegistrar()
        }
    }

    private fun validarYRegistrar() {
        val nombre = etNombre.text.toString().trim()
        val apellido = etApellido.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString().trim()
        val selectedGenderId = rgGenero.checkedRadioButtonId
        val terminosAceptados = cbTerminos.isChecked

        if (nombre.isEmpty()) {
            etNombre.error = "Ingrese su nombre"
            etNombre.requestFocus()
            return
        }

        if (apellido.isEmpty()) {
            etApellido.error = "Ingrese su apellido"
            etApellido.requestFocus()
            return
        }

        if (email.isEmpty()) {
            etEmail.error = "Ingrese su correo electrónico"
            etEmail.requestFocus()
            return
        }

        if (password.isEmpty()) {
            etPassword.error = "Ingrese su contraseña"
            etPassword.requestFocus()
            return
        }

        if (selectedGenderId == -1) {
            Toast.makeText(this, "Por favor seleccione un género", Toast.LENGTH_SHORT).show()
            return
        }

        if (!terminosAceptados) {
            Toast.makeText(this, "Debe aceptar los términos y condiciones", Toast.LENGTH_SHORT).show()
            return
        }

        // Registro exitoso
        Toast.makeText(
            this,
            "¡Registro exitoso! Bienvenido $nombre $apellido",
            Toast.LENGTH_LONG
        ).show()
    }
}
