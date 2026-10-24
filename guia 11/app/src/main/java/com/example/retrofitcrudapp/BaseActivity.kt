package com.example.retrofitcrudapp

import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

abstract class BaseActivity : AppCompatActivity() {
    protected fun prepararPantalla(layout: Int) {
        enableEdgeToEdge()
        setContentView(layout)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById<View>(R.id.main)) { view, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.ime())
            view.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }
    }
}
