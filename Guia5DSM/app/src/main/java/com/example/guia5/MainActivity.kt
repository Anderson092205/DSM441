package com.example.guia5

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.content.Intent
import kotlin.time.Duration
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        enableEdgeToEdge()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_muestra, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.opcion1 -> {
                startActivity(Intent(this, Activity1::class.java))
                true
            }
            R.id.opcion2 -> {
                startActivity(Intent(this, Activity2::class.java))
                true
            }
            R.id.opcion3 -> {
                startActivity(Intent(this, Activity3::class.java))
                true
            }
            R.id.opcion4 -> {
                startActivity(Intent(this, Activity4::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}