package com.example.sqliteapp

import android.content.Intent
import android.database.sqlite.SQLiteException
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.sqliteapp.model.Categoria
import com.example.sqliteapp.model.Productos
import java.util.Locale

class MainActivity : BaseActivity(), View.OnClickListener {
    private lateinit var managerCategoria: Categoria
    private lateinit var managerProductos: Productos
    private lateinit var txtId: EditText
    private lateinit var txtNombre: EditText
    private lateinit var txtPrecio: EditText
    private lateinit var txtCantidad: EditText
    private lateinit var cmbCategorias: Spinner
    private lateinit var estado: TextView
    private var categorias = listOf<Pair<Int, String>>()
    private var aviso: Toast? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Sesion.nick == null) { irLogin(); return }
        preparar(R.layout.activity_main)
        txtId = findViewById(R.id.txtId)
        txtNombre = findViewById(R.id.txtNombre)
        txtPrecio = findViewById(R.id.txtPrecio)
        txtCantidad = findViewById(R.id.txtCantidad)
        cmbCategorias = findViewById(R.id.cmbCategorias)
        estado = findViewById(R.id.estadoProducto)
        managerCategoria = Categoria(this)
        managerProductos = Productos(this)
        try { setSpinnerCategorias() }
        catch (_: SQLiteException) { mostrar(getString(R.string.error_bd)) }
        listOf(R.id.btnAgregar, R.id.btnActualizar, R.id.btnEliminar, R.id.btnBuscar).forEach {
            findViewById<Button>(it).setOnClickListener(this)
        }
        findViewById<Button>(R.id.btnCerrarSesion).setOnClickListener { Sesion.nick = null; irLogin() }
    }
    fun setSpinnerCategorias() {
        managerCategoria.insertValuesDefault()
        val datos = mutableListOf<Pair<Int, String>>()
        managerCategoria.showAllCategoria().use { cursor ->
            while (cursor.moveToNext()) datos.add(cursor.getInt(0) to cursor.getString(1))
        }
        categorias = datos
        cmbCategorias.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item,
            datos.map { it.second }).apply { setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }
    }
    override fun onClick(view: View) {
        try {
            when (view.id) {
                R.id.btnAgregar -> agregar()
                R.id.btnBuscar -> buscar()
                R.id.btnActualizar -> actualizar()
                R.id.btnEliminar -> confirmarEliminar()
            }
        } catch (_: SQLiteException) { mostrar(getString(R.string.error_bd)) }
    }
    private data class Formulario(val categoria: Int, val nombre: String, val precio: Double, val cantidad: Int)
    private fun formulario(): Formulario? {
        txtNombre.error = null; txtPrecio.error = null; txtCantidad.error = null
        val nombre = txtNombre.text.toString().trim()
        val precio = ValidacionProducto.precio(txtPrecio.text.toString())
        val cantidad = ValidacionProducto.cantidad(txtCantidad.text.toString())
        if (nombre.isEmpty()) { txtNombre.error = getString(R.string.nombre_obligatorio); txtNombre.requestFocus(); return null }
        if (precio == null) { txtPrecio.error = getString(R.string.precio_invalido); txtPrecio.requestFocus(); return null }
        if (cantidad == null) { txtCantidad.error = getString(R.string.cantidad_invalida); txtCantidad.requestFocus(); return null }
        val categoria = categorias.getOrNull(cmbCategorias.selectedItemPosition)?.first
        if (categoria == null) { mostrar(getString(R.string.categoria_obligatoria)); return null }
        return Formulario(categoria, nombre, precio, cantidad)
    }
    private fun codigo(): Int? {
        val id = ValidacionProducto.codigo(txtId.text.toString())
        txtId.error = if (id == null) getString(R.string.codigo_invalido) else null
        return id
    }
    private fun agregar() {
        val datos = formulario() ?: return
        val id = managerProductos.addNewProducto(datos.categoria, datos.nombre, datos.precio, datos.cantidad)
        txtId.setText(id.toString())
        findViewById<TextView>(R.id.txtIdDB).text = id.toString()
        mostrar(getString(R.string.producto_agregado, id))
    }
    private fun buscar() {
        val id = codigo() ?: return
        managerProductos.searchProducto(id).use { cursor ->
            if (!cursor.moveToFirst()) {
                limpiarDatos()
                mostrar(getString(R.string.producto_no_existe, id))
                return
            }
            txtNombre.setText(cursor.getString(cursor.getColumnIndexOrThrow(Productos.COL_DESCRIPCION)))
            txtPrecio.setText(String.format(Locale.US, "%.2f", cursor.getDouble(cursor.getColumnIndexOrThrow(Productos.COL_PRECIO))))
            txtCantidad.setText(cursor.getInt(cursor.getColumnIndexOrThrow(Productos.COL_CANTIDAD)).toString())
            val idCategoria = cursor.getInt(cursor.getColumnIndexOrThrow(Productos.COL_IDCATEGORIA))
            val posicion = categorias.indexOfFirst { it.first == idCategoria }
            if (posicion >= 0) cmbCategorias.setSelection(posicion)
            findViewById<TextView>(R.id.txtIdDB).text = id.toString()
            mostrar(getString(R.string.producto_encontrado))
        }
    }
    private fun actualizar() {
        val id = codigo() ?: return
        val datos = formulario() ?: return
        val filas = managerProductos.updateProducto(id, datos.categoria, datos.nombre, datos.precio, datos.cantidad)
        mostrar(if (filas == 1) getString(R.string.producto_actualizado) else getString(R.string.producto_no_existe, id))
    }
    private fun confirmarEliminar() {
        val id = codigo() ?: return
        val existe = managerProductos.searchProducto(id).use { it.moveToFirst() }
        if (!existe) { mostrar(getString(R.string.producto_no_existe, id)); return }
        AlertDialog.Builder(this).setTitle(R.string.eliminar)
            .setMessage(getString(R.string.confirmar_eliminar, id))
            .setNegativeButton(R.string.cancelar, null)
            .setPositiveButton(R.string.eliminar) { _, _ ->
                try {
                    val filas = managerProductos.deleteProducto(id)
                    if (filas == 1) {
                        txtId.text.clear()
                        limpiarDatos()
                        mostrar(getString(R.string.producto_eliminado))
                    } else mostrar(getString(R.string.producto_no_existe, id))
                } catch (_: SQLiteException) { mostrar(getString(R.string.error_bd)) }
            }.show()
    }
    private fun limpiarDatos() {
        txtNombre.text.clear(); txtPrecio.text.clear(); txtCantidad.text.clear()
        findViewById<TextView>(R.id.txtIdDB).text = ""
        if (categorias.isNotEmpty()) cmbCategorias.setSelection(0)
    }
    private fun mostrar(mensaje: String) {
        estado.text = mensaje
        aviso?.cancel()
        aviso = Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).also { it.show() }
    }
    private fun irLogin() {
        startActivity(Intent(this, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        })
        finish()
    }
    override fun onDestroy() {
        aviso?.cancel()
        if (::managerCategoria.isInitialized) managerCategoria.close()
        if (::managerProductos.isInitialized) managerProductos.close()
        super.onDestroy()
    }
}
