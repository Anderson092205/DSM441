package com.example.retrofitdogapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class DogAdapter(private val images: List<String>?) : RecyclerView.Adapter<DogViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DogViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_dog, parent, false)
            return DogViewHolder(view)
    }
    // Se llama para mostrar los datos en una posición específica
    override fun onBindViewHolder(holder: DogViewHolder, position: Int) {
        holder.bind(images!![position])
    }
    // Devuelve el número total de elementos en el conjunto de datos
    override fun getItemCount(): Int {
        return images?.size ?: 0
    }

}