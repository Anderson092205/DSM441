package com.example.guia3

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast

class MainActivity : AppCompatActivity() {

    lateinit var  btnAbrir: Button
    lateinit var  btnAbrirThird: Button
    lateinit var  btnIncrementar: Button
    lateinit var  tvContador: TextView
    var contador: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnAbrir = findViewById(R.id.btnAbrir)
        btnAbrirThird = findViewById(R.id.btnAbrirThird)
        btnIncrementar = findViewById(R.id.btnIncrementar)
        tvContador = findViewById(R.id.tvContador)

        lifecycle.addObserver(MyLifeCycleObserver("MainActivity"))

        btnAbrir.setOnClickListener {
            val intent = Intent(this, SecondActivity::class.java)
            startActivity(intent)
        }
        btnAbrirThird.setOnClickListener {
            val intent = Intent(this, ThirdActivity::class.java)
            startActivity(intent)
        }


        btnIncrementar.setOnClickListener{
            tvContador.text = "Valor contador: ${++contador}"
        }

        if(savedInstanceState != null){
            contador = savedInstanceState.getInt("CONTADOR")
            tvContador.text = "Valor contador: ${contador}"
        }

        mostrarToast("onCreate")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("CONTADOR", contador)
    }

    override fun onStart(){
        super.onStart()
        mostrarToast("onStart")
    }

    override fun onResume() {
        super.onResume()
        mostrarToast("onResume")
    }

    override fun onPause(){
        super.onPause()
        mostrarToast("onPause")
    }

    override fun onStop(){
        super.onStop()
        mostrarToast("onStop")
    }

    override fun onRestart(){
        super.onRestart()
        mostrarToast("onRestart")
    }

    override fun onDestroy(){
        super.onDestroy()
        mostrarToast("onDestroy")
    }

    private fun mostrarToast(
        mensaje: String,
        duracion: Int = Toast.LENGTH_SHORT
    ) {
        Toast.makeText(
            this,
            "MainActivity -> $mensaje",
            duracion
        ).show()
    }

}

