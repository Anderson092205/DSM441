package com.example.realtimedatabase

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.realtimedatabase.datos.Persona
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.Calendar

class AddPersonaActivity : AppCompatActivity() {
    private var edtDUI: EditText? = null
    private var edtNombre: EditText? = null
    private var edtFechaNacimiento: EditText? = null
    private var spnGenero: Spinner? = null
    private var edtPeso: EditText? = null
    private var edtAltura: EditText? = null
    private var tvTituloForm: TextView? = null
    private var key: String = ""
    private var accion: String = ""
    private lateinit var database: DatabaseReference
    private val opcionesGenero = arrayOf("Seleccione género", "Masculino", "Femenino", "Otro")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_persona)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        inicializar()
    }

    private fun inicializar() {
        tvTituloForm = findViewById(R.id.tvTituloForm)
        edtNombre = findViewById(R.id.edtNombre)
        edtDUI = findViewById(R.id.edtDUI)
        edtFechaNacimiento = findViewById(R.id.edtFechaNacimiento)
        spnGenero = findViewById(R.id.spnGenero)
        edtPeso = findViewById(R.id.edtPeso)
        edtAltura = findViewById(R.id.edtAltura)

        // Configurar Spinner de Género
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, opcionesGenero)
        spnGenero?.adapter = spinnerAdapter

        // Configurar DatePickerDialog para Fecha de Nacimiento
        edtFechaNacimiento?.setOnClickListener {
            mostrarDatePicker()
        }

        // Obtención de datos que envía la actividad anterior
        val datos: Bundle? = intent.extras
        datos?.let {
            key = it.getString("key", "")
            edtDUI?.setText(it.getString("dui", ""))
            edtNombre?.setText(it.getString("nombre", ""))
            edtFechaNacimiento?.setText(it.getString("fechaNacimiento", ""))
            edtPeso?.setText(it.getString("peso", ""))
            edtAltura?.setText(it.getString("altura", ""))
            
            val generoExtra = it.getString("genero", "")
            if (generoExtra.isNotEmpty()) {
                val pos = opcionesGenero.indexOf(generoExtra)
                if (pos >= 0) {
                    spnGenero?.setSelection(pos)
                }
            }

            accion = it.getString("accion", "a")
        }

        if (accion == "e") {
            tvTituloForm?.text = "Editar Persona"
        } else {
            accion = "a"
            tvTituloForm?.text = "Agregar Persona"
        }

        database = FirebaseDatabase.getInstance("https://fir-dbrealtime-86f34-default-rtdb.firebaseio.com").getReference("personas")
    }

    private fun mostrarDatePicker() {
        val calendario = Calendar.getInstance()
        val anio = calendario.get(Calendar.YEAR)
        val mes = calendario.get(Calendar.MONTH)
        val dia = calendario.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, monthOfYear, dayOfMonth ->
                val fechaFormateada = String.format("%02d/%02d/%d", dayOfMonth, monthOfYear + 1, year)
                edtFechaNacimiento?.setText(fechaFormateada)
            },
            anio,
            mes,
            dia
        )
        datePickerDialog.show()
    }

    fun guardar(v: View?) {
        val nombre: String = edtNombre?.text?.toString()?.trim() ?: ""
        val dui: String = edtDUI?.text?.toString()?.trim() ?: ""
        val fechaNacimiento: String = edtFechaNacimiento?.text?.toString()?.trim() ?: ""
        val peso: String = edtPeso?.text?.toString()?.trim() ?: ""
        val altura: String = edtAltura?.text?.toString()?.trim() ?: ""
        val generoPos = spnGenero?.selectedItemPosition ?: 0
        val genero: String = if (generoPos > 0) opcionesGenero[generoPos] else ""

        if (nombre.isEmpty() || dui.isEmpty()) {
            Toast.makeText(this, "Por favor complete al menos el Nombre y DUI", Toast.LENGTH_SHORT).show()
            return
        }

        // Se forma objeto persona con los nuevos atributos
        val persona = Persona(
            dui = dui,
            nombre = nombre,
            fechaNacimiento = fechaNacimiento,
            genero = genero,
            peso = peso,
            altura = altura
        )

        if (accion == "e") { // Editar registro
            if (key.isNotEmpty()) {
                database.child(key).setValue(persona.toMap())
                    .addOnSuccessListener {
                        Toast.makeText(this, "Se actualizó con éxito", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    .addOnFailureListener { error ->
                        Toast.makeText(this, "Error al actualizar: ${error.message}", Toast.LENGTH_LONG).show()
                    }
            } else {
                Toast.makeText(this, "No se encontró la clave del registro", Toast.LENGTH_SHORT).show()
            }
        } else { // Agregar registro (por defecto)
            val newKey = database.push().key // Generar una nueva clave
            if (newKey != null) {
                database.child(newKey).setValue(persona.toMap())
                    .addOnSuccessListener {
                        Toast.makeText(this, "Se guardó con éxito", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    .addOnFailureListener { error ->
                        Toast.makeText(this, "Error al guardar: ${error.message}", Toast.LENGTH_LONG).show()
                    }
            } else {
                Toast.makeText(this, "No se pudo generar una clave", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun cancelar(v: View?) {
        finish()
    }
}