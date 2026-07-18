package com.example.guia3

import android.os.Bundle
import android.content.Intent
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ThirdActivity : AppCompatActivity() {

    lateinit var btnRegresarMain: Button
    lateinit var btnRegresarSecond: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_third)

        btnRegresarMain = findViewById(R.id.btnRegresarMain)
        btnRegresarSecond = findViewById(R.id.btnRegresarSecond)

        lifecycle.addObserver(MyLifeCycleObserver("ThirdActivity"))

        btnRegresarMain.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        btnRegresarSecond.setOnClickListener {
            val intent = Intent(this, SecondActivity::class.java)
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
            "ThirdActivity -> $mensaje",
            duracion
        ).show()
    }
}
