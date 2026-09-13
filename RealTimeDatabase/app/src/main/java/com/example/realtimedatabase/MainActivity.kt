package com.example.realtimedatabase

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ListView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.realtimedatabase.datos.Persona
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener

class MainActivity : AppCompatActivity() {

    private val consultaOrdenada: Query = refPersonas.orderByChild("nombre")
    private var personas: MutableList<Persona>? = null
    private var listaPersonas: ListView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        inicializar()
    }
    private fun inicializar() {
        val fabAgregar: FloatingActionButton = findViewById(R.id.fab_agregar)
        listaPersonas = findViewById(R.id.ListaPersonas)
        // Cuando el usuario haga clic en la lista (para editar registro)
        listaPersonas!!.setOnItemClickListener { adapterView, view, i, l ->
            val intent = Intent(this, AddPersonaActivity::class.java)
            intent.putExtra("accion", "e") // Editar
            val persona = personas!![i]
            intent.putExtra("key", persona.key)
            intent.putExtra("nombre", persona.nombre)
            intent.putExtra("dui", persona.dui)
            intent.putExtra("fechaNacimiento", persona.fechaNacimiento)
            intent.putExtra("genero", persona.genero)
            intent.putExtra("peso", persona.peso)
            intent.putExtra("altura", persona.altura)
            startActivity(intent)
        }
        // Cuando el usuario hace un LongClick (clic sin soltar elemento por más de 2 segundos)
        // Es porque el usuario quiere eliminar el registro
        listaPersonas!!.onItemLongClickListener = AdapterView.OnItemLongClickListener { adapterView, view, position, l ->
            // Preparando cuadro de diálogo para preguntar al usuario
            // Si está seguro de eliminar o no el registro
            val ad = AlertDialog.Builder(this@MainActivity)
            ad.setMessage("¿Está seguro de eliminar el registro?")
                .setTitle("Confirmación")
            ad.setPositiveButton("Sí") { dialog, id ->
                personas!![position].key?.let {
                    refPersonas.child(it).removeValue()
                }
                Toast.makeText(this@MainActivity,
                    "Registro borrado!",
                    Toast.LENGTH_SHORT
                ).show()
            }
            ad.setNegativeButton("No") { dialog, id ->
                Toast.makeText(
                    this@MainActivity,
                    "Operación de borrado cancelada!",
                    Toast.LENGTH_SHORT
                ).show()
            }
            ad.show()
            true
        }
        fabAgregar.setOnClickListener {
            // Cuando el usuario quiere agregar un nuevo registro
            val intent = Intent(this, AddPersonaActivity::class.java)
            intent.putExtra("accion", "a") // Agregar
            intent.putExtra("key", "")
            intent.putExtra("nombre", "")
            intent.putExtra("dui", "")
            intent.putExtra("fechaNacimiento", "")
            intent.putExtra("genero", "")
            intent.putExtra("peso", "")
            intent.putExtra("altura", "")
            startActivity(intent)
        }
        personas = ArrayList()
        // Cambiarlo refPersonas a consultaOrdenada para ordenar lista
        consultaOrdenada.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Procedimiento que se ejecuta cuando hubo algún cambio
                // en la base de datos
                // Se actualiza la colección de personas
                personas!!.clear()
                for (dato in dataSnapshot.children) {
                    try {
                        val persona: Persona? = dato.getValue(Persona::class.java)
                        if (persona != null) {
                            persona.key = dato.key
                            personas!!.add(persona)
                        }
                    } catch (e: Exception) {
                        val map = dato.value as? Map<*, *>
                        if (map != null) {
                            val persona = Persona(
                                dui = map["dui"]?.toString(),
                                nombre = map["nombre"]?.toString(),
                                fechaNacimiento = map["fechaNacimiento"]?.toString(),
                                genero = map["genero"]?.toString(),
                                peso = map["peso"]?.toString(),
                                altura = map["altura"]?.toString(),
                                key = dato.key
                            )
                            personas!!.add(persona)
                        }
                    }
                }
                val adapter = PersonaAdapter(
                    this@MainActivity,
                    personas as ArrayList<Persona>
                )
                listaPersonas!!.adapter = adapter
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(
                    this@MainActivity,
                    "Error al cargar datos: ${databaseError.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }
    companion object {
        private val database: FirebaseDatabase = FirebaseDatabase.getInstance("https://fir-dbrealtime-86f34-default-rtdb.firebaseio.com")
        private val refPersonas: DatabaseReference = database
            .getReference("personas")
    }
}