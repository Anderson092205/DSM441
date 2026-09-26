package com.example.retrofitdogapp

import com.google.gson.annotations.SerializedName

class DogsResponse {

    //Campo que representa el estado de la respuesta de la API
    @SerializedName("status")
    private var status: String? = null

    //Campo que representa la lista de URLs de imágenes
    @SerializedName("message")
    private var images: List<String?>? = null

    //obtienes el estado de la respuesta
    fun getStatus(): String? {
        return status
    }

    fun setStatus(status: String?){
        this.status = status
    }

    fun getImages(): List<String?>? {
        return images
    }

    fun setImages(images: List<String?>?){
        this.images = images
    }

}