package com.example.sqliteapp

import android.content.Context
import android.graphics.Bitmap
import android.os.SystemClock
import android.view.View
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.closeSoftKeyboard
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.NoMatchingViewException
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.hamcrest.Matcher
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

@RunWith(AndroidJUnit4::class)
class FlujoGuiaTest {
    private val context = ApplicationProvider.getApplicationContext<Context>()
    @Test fun registroLoginDiezProductosBuscarActualizarEliminarYCerrarSesion() {
        context.deleteDatabase("tienda.sqlite")
        Sesion.nick = null
        ActivityScenario.launch(LoginActivity::class.java).use {
            captura("01-login.png")
            onView(withId(R.id.btnRegistrarse)).perform(click())
            onView(withId(R.id.txtNick)).perform(replaceText("estudiante"))
            onView(withId(R.id.txtContrasena)).perform(replaceText("Guia09#2026"))
            onView(withId(R.id.txtConfirmarContrasena)).perform(replaceText("Guia09#2026"))
            closeSoftKeyboard()
            onView(withId(R.id.btnCrearCuenta)).perform(scrollTo(), click())
            esperar(withId(R.id.btnIngresar))
            onView(withId(R.id.txtNick)).perform(replaceText("estudiante"))
            onView(withId(R.id.txtContrasena)).perform(replaceText("Guia09#2026"))
            closeSoftKeyboard()
            onView(withId(R.id.btnIngresar)).perform(click())
            esperar(withId(R.id.btnAgregar))
            val nombres = listOf("Arroz", "Frijoles", "Azúcar", "Sal", "Aceite", "Harina", "Café", "Pasta", "Avena", "Maíz")
            nombres.forEachIndexed { index, nombre ->
                llenar(nombre, "2.50", "10")
                onView(withId(R.id.btnAgregar)).perform(scrollTo(), click())
                onView(withId(R.id.txtId)).check(matches(withText((index + 1).toString())))
            }
            // El producto temporal permite probar eliminar sin reducir los 10 registros pedidos.
            llenar("Producto temporal", "1.25", "2")
            onView(withId(R.id.btnAgregar)).perform(scrollTo(), click())
            onView(withId(R.id.btnEliminar)).perform(scrollTo(), click())
            onView(withId(android.R.id.button1)).inRoot(isDialog()).perform(click())
            onView(withId(R.id.estadoProducto)).check(matches(withText(R.string.producto_eliminado)))
            onView(withId(R.id.txtId)).perform(scrollTo(), replaceText("1"))
            onView(withId(R.id.btnBuscar)).perform(scrollTo(), click())
            onView(withId(R.id.txtNombre)).check(matches(withText("Arroz")))
            onView(withId(R.id.txtPrecio)).perform(scrollTo(), replaceText("4.50"))
            onView(withId(R.id.txtCantidad)).perform(scrollTo(), replaceText("25"))
            closeSoftKeyboard()
            onView(withId(R.id.btnActualizar)).perform(scrollTo(), click())
            onView(withId(R.id.estadoProducto)).check(matches(withText(R.string.producto_actualizado)))
            onView(withId(R.id.btnBuscar)).perform(scrollTo(), click())
            onView(withId(R.id.txtPrecio)).check(matches(withText("4.50")))
            onView(withId(R.id.txtCantidad)).check(matches(withText("25")))
            onView(withText(R.string.titulo_productos)).perform(scrollTo())
            SystemClock.sleep(2500)
            captura("02-productos.png")
            onView(withId(R.id.txtId)).perform(scrollTo(), replaceText("99999"))
            onView(withId(R.id.btnBuscar)).perform(scrollTo(), click())
            onView(withId(R.id.estadoProducto)).check(matches(withText("No existe un producto con el código 99999")))
            onView(withId(R.id.txtNombre)).check(matches(withText("")))
            onView(withId(R.id.btnCerrarSesion)).perform(scrollTo(), click())
            esperar(withId(R.id.btnIngresar))
            onView(withId(R.id.txtNick)).perform(replaceText("estudiante"))
            onView(withId(R.id.txtContrasena)).perform(replaceText("incorrecta"))
            closeSoftKeyboard()
            onView(withId(R.id.btnIngresar)).perform(click())
            esperar(withText(R.string.credenciales_invalidas))
        }
        com.example.sqliteapp.db.HelperDB(context).use { helper ->
            helper.readableDatabase.rawQuery("SELECT COUNT(*) FROM productos", null).use {
                assertTrue(it.moveToFirst()); org.junit.Assert.assertEquals(10, it.getInt(0))
            }
        }
    }
    private fun llenar(nombre: String, precio: String, cantidad: String) {
        onView(withId(R.id.txtNombre)).perform(scrollTo(), replaceText(nombre))
        onView(withId(R.id.txtPrecio)).perform(scrollTo(), replaceText(precio))
        onView(withId(R.id.txtCantidad)).perform(scrollTo(), replaceText(cantidad))
        closeSoftKeyboard()
    }
    private fun esperar(matcher: Matcher<View>) {
        val limite = SystemClock.uptimeMillis() + 120000
        while (SystemClock.uptimeMillis() < limite) {
            try { onView(matcher).check(matches(isDisplayed())); return }
            catch (_: NoMatchingViewException) { }
            catch (_: AssertionError) { }
            SystemClock.sleep(150)
        }
        onView(matcher).check(matches(isDisplayed()))
    }
    private fun captura(nombre: String) {
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()
        val bitmap = InstrumentationRegistry.getInstrumentation().uiAutomation.takeScreenshot()
        File(context.filesDir, nombre).outputStream().use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
        bitmap.recycle()
    }
}
