package com.example.retrofitcrudapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

open class PersonaAdapter<T : Persona>(private val onClick: (T) -> Unit) :
    RecyclerView.Adapter<PersonaAdapter.ViewHolder>() {
    private var personas: List<T> = emptyList()

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nombre: TextView = view.findViewById(R.id.tvNombre)
        val apellido: TextView = view.findViewById(R.id.tvApellido)
        val edad: TextView = view.findViewById(R.id.tvEdad)
    }
    fun actualizar(datos: List<T>) {
        personas = datos
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_alumno, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val persona = personas[position]
        holder.nombre.text = persona.nombre
        holder.apellido.text = persona.apellido
        holder.edad.text = persona.edad?.let { holder.itemView.context.getString(R.string.edad_valor, it) }
            ?: holder.itemView.context.getString(R.string.edad_sin_dato)
        holder.itemView.setOnClickListener { onClick(persona) }
    }
    override fun getItemCount() = personas.size
}
