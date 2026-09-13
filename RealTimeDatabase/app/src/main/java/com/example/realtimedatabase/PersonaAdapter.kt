package com.example.realtimedatabase

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.realtimedatabase.datos.Persona

class PersonaAdapter (private val context: Activity, var personas: List<Persona>):
ArrayAdapter<Persona>(context, R.layout.persona_layout, personas){

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View{
        // Metodo invocado tantas veces como elementos tenga la colleción personas
        // para formar cada item que se visualizara en la lista personalizada
        val layoutInflater = context.layoutInflater
        val rowView: View = convertView ?: layoutInflater.inflate(R.layout.persona_layout, parent , false)

        // Obtener las vistas del diseño
        val tvNombre = rowView.findViewById<TextView>(R.id.tvNombre)
        val tvDUI = rowView.findViewById<TextView>(R.id.tvDUI)
        val tvFechaNacimiento = rowView.findViewById<TextView>(R.id.tvFechaNacimiento)
        val tvGenero = rowView.findViewById<TextView>(R.id.tvGenero)
        val tvPeso = rowView.findViewById<TextView>(R.id.tvPeso)
        val tvAltura = rowView.findViewById<TextView>(R.id.tvAltura)

        val persona = personas[position]

        tvNombre.text = persona.nombre ?: ""
        tvDUI.text = "DUI: ${persona.dui ?: ""}"
        tvFechaNacimiento.text = "F. Nac: ${persona.fechaNacimiento ?: "N/D"}"
        tvGenero.text = "Género: ${persona.genero ?: "N/D"}"
        tvPeso.text = "Peso: ${if (!persona.peso.isNullOrEmpty()) "${persona.peso} kg" else "N/D"}"
        tvAltura.text = "Altura: ${if (!persona.altura.isNullOrEmpty()) "${persona.altura} m" else "N/D"}"

        return rowView
    }

}