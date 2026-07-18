package com.example.guia3

import android.os.Bundle
import android.content.Intent
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SecondActivity : AppCompatActivity() {

    lateinit var  btnRegresar: Button
    lateinit var  btnAbrirThird: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //1. Referencia al archivo layout
        setContentView(R.layout.activity_second)
        //2. Reerenca al boton
        btnRegresar = findViewById(R.id.btnRegresar)
        btnAbrirThird = findViewById(R.id.btnAbrirThird)
        //3. Registro del observador
        lifecycle.addObserver(MyLifeCycleObserver("SecondActivity"))

        //4. Listener del boton
        btnRegresar.setOnClickListener {
            //5. Uso de un intent explicito para iniciar una nueva activity
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        btnAbrirThird.setOnClickListener {
            val intent = Intent(this, ThirdActivity::class.java)
            startActivity(intent)
        }
        mostrarToast("onCreate")
    }

    override fun onStart(){
        super.onStart()
        mostrarToast("onStart")
    }

    override fun onResume() {
        super.onResume()
        mostrarToast("onResume")
    }
    override fun onPause() {
        super.onPause()
        mostrarToast("onPause")
    }
    override fun onStop() {
        super.onStop()
        mostrarToast("onStop")
    }
    override fun onRestart() {
        super.onRestart()
        mostrarToast("onRestart")
    }
    override fun onDestroy() {
        super.onDestroy()
        mostrarToast("onDestroy")
    }

    private fun mostrarToast(
        mensaje: String,
        duracion: Int = Toast.LENGTH_SHORT
    ){
        Toast.makeText(
            this,
            "SecondActivity -> $mensaje",
            duracion
        ).show()
    }
}