package com.example.retrofitcrudapp

import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.closeSoftKeyboard
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CrudPantallasTest {
    @Test fun crearAlumnoValidaCamposYEdadSinEnviarDatos() {
        ActivityScenario.launch(CrearAlumnoActivity::class.java).use {
            onView(withId(R.id.btnGuardar)).perform(click())
            onView(withId(R.id.editTextNombre)).check(matches(hasErrorText("Este campo es obligatorio")))
            onView(withId(R.id.editTextNombre)).perform(replaceText("Ana"))
            closeSoftKeyboard()
            onView(withId(R.id.btnGuardar)).perform(click())
            onView(withId(R.id.editTextApellido)).check(matches(hasErrorText("Este campo es obligatorio")))
            onView(withId(R.id.editTextApellido)).perform(replaceText("Perez"))
            onView(withId(R.id.editTextEdad)).perform(replaceText("0"))
            closeSoftKeyboard()
            onView(withId(R.id.btnGuardar)).perform(click())
            onView(withId(R.id.editTextEdad)).check(matches(hasErrorText("Ingresa una edad entre 1 y 120 años")))
        }
    }

    @Test fun crearProfesorMuestraSuTituloYValidaCampos() {
        ActivityScenario.launch(CrearProfesorActivity::class.java).use {
            onView(withId(R.id.tituloFormulario)).check(matches(withText(R.string.agregar_profesor)))
            onView(withId(R.id.btnGuardar)).perform(click())
            onView(withId(R.id.editTextNombre)).check(matches(hasErrorText("Este campo es obligatorio")))
        }
    }

    @Test fun editarAlumnoRecibeElRegistroSeleccionado() {
        val intent = Intent(ApplicationProvider.getApplicationContext(), ActualizarAlumnoActivity::class.java)
            .putExtra("persona_id", 42).putExtra("nombre", "Ana").putExtra("apellido", "Perez").putExtra("edad", 25)
        ActivityScenario.launch<ActualizarAlumnoActivity>(intent).use {
            onView(withId(R.id.nombreEditText)).check(matches(withText("Ana")))
            onView(withId(R.id.apellidoEditText)).check(matches(withText("Perez")))
            onView(withId(R.id.edadEditText)).check(matches(withText("25")))
            it.recreate()
            onView(withId(R.id.edadEditText)).check(matches(withText("25")))
        }
    }

    @Test fun editarProfesorConEdadInvalidaPermiteCorregirla() {
        val intent = Intent(ApplicationProvider.getApplicationContext(), ActualizarProfesorActivity::class.java)
            .putExtra("persona_id", 42).putExtra("nombre", "Luis").putExtra("apellido", "Perez")
        ActivityScenario.launch<ActualizarProfesorActivity>(intent).use {
            onView(withId(R.id.tituloFormulario)).check(matches(withText(R.string.editar_profesor)))
            onView(withId(R.id.edadEditText)).check(matches(withText("")))
            onView(withId(R.id.actualizarButton)).perform(click())
            onView(withId(R.id.edadEditText)).check(matches(hasErrorText("Ingresa una edad entre 1 y 120 años")))
        }
    }
}
