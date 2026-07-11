package com.example.micalculadora

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlin.math.sqrt

class MainActivity : AppCompatActivity() {

    // Variables globales para la operación acumulativa
    private var primerNumero: Double? = null
    private var operacionPendiente: String = ""

    // Lista para almacenar estrictamente las últimas 5 operaciones
    private val historialLista = mutableListOf<String>()

    lateinit var etNumero: EditText
    lateinit var tvResultado: TextView
    lateinit var tvHistorial: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Referencias a los componentes de la interfaz
        etNumero = findViewById(R.id.etNumero)
        tvResultado = findViewById(R.id.tvResultado)
        tvHistorial = findViewById(R.id.tvHistorial)

        val btnSuma: Button = findViewById(R.id.btnSuma)
        val btnResta: Button = findViewById(R.id.btnResta)
        val btnMulti: Button = findViewById(R.id.btnMulti)
        val btnDiv: Button = findViewById(R.id.btnDiv)

        val btnPorcentaje: Button = findViewById(R.id.btnPorcentaje)
        val btnCuadrado: Button = findViewById(R.id.btnCuadrado)
        val btnRaiz: Button = findViewById(R.id.btnRaiz)
        val btnLimpiarTodo: Button = findViewById(R.id.btnLimpiarTodo)

        // --- LISTENERS OPERACIONES BÁSICAS ---
        btnSuma.setOnClickListener { prepararOperacion("+") }
        btnResta.setOnClickListener { prepararOperacion("-") }
        btnMulti.setOnClickListener { prepararOperacion("×") }
        btnDiv.setOnClickListener { prepararOperacion("÷") }

        // --- LISTENERS OPERACIONES DE UN SOLO NÚMERO ---
        btnPorcentaje.setOnClickListener {
            val num = obtenerNumero() ?: return@setOnClickListener
            val res = num / 100
            tvResultado.text = res.toString()
            agregarAlHistorial("$num / 100 = $res")
            etNumero.text.clear()
        }

        btnCuadrado.setOnClickListener {
            val num = obtenerNumero() ?: return@setOnClickListener
            val res = num * num
            tvResultado.text = res.toString()
            agregarAlHistorial("$num² = $res")
            etNumero.text.clear()
        }

        btnRaiz.setOnClickListener {
            val num = obtenerNumero() ?: return@setOnClickListener
            if (num < 0) {
                Toast.makeText(this, "Error: Raíz negativa", Toast.LENGTH_SHORT).show()
            } else {
                val res = sqrt(num)
                tvResultado.text = res.toString()
                agregarAlHistorial("√($num) = $res")
                etNumero.text.clear()
            }
        }

        // --- BOTÓN LIMPIAR TODO ---
        btnLimpiarTodo.setOnClickListener {
            primerNumero = null
            operacionPendiente = ""
            etNumero.text.clear()
            tvResultado.text = "0"
            historialLista.clear()
            tvHistorial.text = ""
        }
    }

    // Procesa el encadenamiento de operaciones básicas de dos números
    private fun prepararOperacion(op: String) {
        val num = obtenerNumero() ?: return

        if (primerNumero == null) {
            primerNumero = num
            operacionPendiente = op
            tvResultado.text = "$primerNumero $operacionPendiente"
            etNumero.text.clear()
        } else {
            val segundoNumero = num
            val res = calcularResultado(primerNumero!!, segundoNumero, operacionPendiente)

            // Registra en el historial usando el formato solicitado: 1 + 1 = 2
            agregarAlHistorial("$primerNumero $operacionPendiente $segundoNumero = $res")

            primerNumero = res
            operacionPendiente = op
            tvResultado.text = "$primerNumero $operacionPendiente"
            etNumero.text.clear()
        }
    }

    private fun calcularResultado(n1: Double, n2: Double, op: String): Double {
        return when (op) {
            "+" -> n1 + n2
            "-" -> n1 - n2
            "×" -> n1 * n2
            "÷" -> if (n2 != 0.0) n1 / n2 else 0.0
            else -> 0.0
        }
    }

    private fun obtenerNumero(): Double? {
        val texto = etNumero.text.toString().trim()
        if (texto.isEmpty()) {
            Toast.makeText(this, "Por favor ingrese un número", Toast.LENGTH_SHORT).show()
            return null
        }
        return texto.toDoubleOrNull()
    }

    // Gestiona dinámicamente el historial usando "\n" limitado a 5 líneas
    private fun agregarAlHistorial(lineaOperacion: String) {
        // Inserta al principio de la lista para que la última operación aparezca arriba
        historialLista.add(0, lineaOperacion)

        // Si la lista supera las 5 líneas, elimina la más antigua
        if (historialLista.size > 5) {
            historialLista.removeAt(5)
        }

        // Concatena todos los elementos usando el salto de línea matemático "\n"
        tvHistorial.text = historialLista.joinToString("\n")
    }
}