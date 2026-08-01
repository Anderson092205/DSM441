package com.example.guia4

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {
    private val TAG = "Guia4-Permisos"
    private val CODIGO_SOLICITUD_GRABAR = 102

    lateinit var btnCamera: Button
    lateinit var btnGrabar: Button
    lateinit var tvEstado: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvEstado = findViewById(R.id.tvEstado)
        btnGrabar = findViewById(R.id.btnGrabar)

        //Comprobacion de permisos
        comprobarEstadoPermiso()
        configurarPermiso()

        btnCamera = findViewById(R.id.btnCamara)
        btnCamera.setOnClickListener {
            val intent = Intent(this, CamaraActivity::class.java)
            startActivity(intent)
        }

        btnGrabar.setOnClickListener {
            val estadoPermiso = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            )
            if (estadoPermiso == PackageManager.PERMISSION_GRANTED) {
                tvEstado.text = "Grabando"
            } else {
                configurarPermiso()
            }
        }
    }

    private fun comprobarEstadoPermiso() {
        val estadoPermiso = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.RECORD_AUDIO
        )
        if (estadoPermiso != PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "Permiso para grabar denegado")
            tvEstado.text = "Permiso denegado"
        }
    }

    private fun configurarPermiso() {
        val estadoPermiso = ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.RECORD_AUDIO
        )

        if (estadoPermiso != PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, getString(R.string.permiso_audio_denegado))

            val mostrarRequest = ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                android.Manifest.permission.RECORD_AUDIO
            );

            if (mostrarRequest) {
                val builder = AlertDialog.Builder(this)
                builder.setMessage(getString(R.string.permiso_audio_requerido))
                    .setTitle("Permiso requirido")

                builder.setPositiveButton("OK") { _, _ ->
                    Log.i(TAG, "Seleccionado")
                    solicitudPermiso()
                }
                builder.setNegativeButton("Cancelar", null)
                val dialog = builder.create()
                dialog.show()
            } else {
                solicitudPermiso()
            }
        }
    }

    private fun solicitudPermiso() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(android.Manifest.permission.RECORD_AUDIO),
            CODIGO_SOLICITUD_GRABAR
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            CODIGO_SOLICITUD_GRABAR -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Log.i(TAG, getString(R.string.permiso_audio_denegado_usuario))
                    tvEstado.text = "Permiso denegado"
                    Toast.makeText(
                        applicationContext,
                        getString(R.string.permiso_audio_denegado_usuario),
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Log.i(TAG, getString(R.string.permiso_audio_concedido_usuario))
                    // Could set to "Esperando permiso" or "Permiso concedido", wait, the state text in UI was requested: "Esperando permiso, Grabando, Permiso denegado". 
                    // When granted, it is just "Esperando permiso" until the user clicks the button, 
                    // but since they just granted it, maybe leave it as "Esperando permiso" or change it if they requested to record.
                    // We'll reset it to "Esperando permiso" because it's no longer denied, and not yet recording.
                    tvEstado.text = "Esperando permiso"
                    Toast.makeText(
                        applicationContext,
                        getString(R.string.permiso_audio_concedido_usuario),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}