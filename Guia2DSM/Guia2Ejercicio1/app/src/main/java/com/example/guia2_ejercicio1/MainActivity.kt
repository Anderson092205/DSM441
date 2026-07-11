package com.example.guia2_ejercicio1

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.util.Calendar

class MainActivity : AppCompatActivity() {

    // 1. Variables de UI
    lateinit var btnSaludar: Button
    lateinit var btnLimpiar: Button
    lateinit var etNombre: EditText
    lateinit var etApellido: EditText // Nuevo campo
    lateinit var tvSaludo: TextView
    lateinit var tvMensaje: TextView

    // 2. Método del ciclo de vida del activity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 3. Establecer archivo layout
        setContentView(R.layout.activity_main)

        // 4. Referencias a elementos de la UI
        btnSaludar = findViewById(R.id.btnSaludar)
        btnLimpiar = findViewById(R.id.btnLimpiar)
        etNombre = findViewById(R.id.etNombre)
        etApellido = findViewById(R.id.etApellido) // Enlace del nuevo campo
        tvSaludo = findViewById(R.id.tvSaludo)
        tvMensaje = findViewById(R.id.tvMensaje)

        // Inicializar textos vacíos
        tvSaludo.text = ""
        tvMensaje.text = ""

        // 5. Listeners de los botones
        btnSaludar.setOnClickListener {
            val nombre = etNombre.text.toString().trim()
            val apellido = etApellido.text.toString().trim()

            // Validación de ambos campos vacíos
            if (nombre.isEmpty() || apellido.isEmpty()){
                mostrarToast("Error, por favor completa nombre y apellido.")
            }
            else {
                // REEMPLAZADO CON TUS VARIABLES:
                val calendar = Calendar.getInstance()
                val hora = calendar.get(Calendar.HOUR_OF_DAY)
                val esDia = hora in 6..17

                val prefijoSaludo = if (esDia) {
                    "Hola buenos días"
                } else {
                    "Hola buenas noches"
                }

                val saludoCompleto = "$prefijoSaludo, $nombre $apellido"
                val mensaje = "Bienvenido al laboratorio 2 de DSM441"

                tvSaludo.text = saludoCompleto
                tvMensaje.text = mensaje

                mostrarToast("saludo generado exitosamente")
            }
        }

        btnLimpiar.setOnClickListener {
            etNombre.text.clear()
            etApellido.text.clear() // Limpiar el nuevo campo de apellido

            tvSaludo.text = ""
            tvMensaje.text = ""

            etNombre.requestFocus() // Regresa el foco al primer campo

            mostrarToast("Pantalla reiniciada")
        }
    }

    private fun mostrarToast(mensaje: String, duracion: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(this, mensaje, duracion).show()
    }
}