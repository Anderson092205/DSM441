package com.example.sqliteapp

import android.content.Context
import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteDatabase
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.sqliteapp.db.HelperDB
import com.example.sqliteapp.model.Categoria
import com.example.sqliteapp.model.Productos
import com.example.sqliteapp.model.Usuario
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BaseDatosTest {
    private val context = ApplicationProvider.getApplicationContext<Context>()
    private val nombre = "pruebas.sqlite"
    @Before fun preparar() { context.deleteDatabase(nombre) }
    @After fun limpiar() { context.deleteDatabase(nombre); context.deleteDatabase("migracion.sqlite") }

    @Test fun categoriasSeCarganUnaVezSinDuplicados() {
        Categoria(context, nombre).use { categorias ->
            categorias.insertValuesDefault()
            categorias.insertValuesDefault()
            categorias.showAllCategoria().use { assertEquals(10, it.count) }
            val id = categorias.searchID("Abarrotes")!!
            assertEquals("Abarrotes", categorias.searchNombre(id))
            assertNull(categorias.searchID("no existe"))
            assertNull(categorias.searchNombre(999))
        }
    }
    @Test fun productosPersistenYElCrudDevuelveFilasReales() {
        val categoria = Categoria(context, nombre).use {
            it.insertValuesDefault(); it.searchID("Abarrotes")!!
        }
        val id = Productos(context, nombre).use {
            it.addNewProducto(categoria, "Atún de 'prueba'", 2.50, 10).toInt()
        }
        Productos(context, nombre).use { productos ->
            productos.searchProducto(id).use {
                assertTrue(it.moveToFirst())
                assertEquals("Atún de 'prueba'", it.getString(2))
                assertEquals(2.50, it.getDouble(3), 0.0)
                assertEquals(10, it.getInt(4))
            }
            assertEquals(1, productos.updateProducto(id, categoria, "Atún actualizado", 3.75, 0))
            productos.searchProducto(id).use { assertTrue(it.moveToFirst()); assertEquals(0, it.getInt(4)) }
            assertEquals(0, productos.updateProducto(999, categoria, "No existe", 1.0, 1))
            assertEquals(1, productos.deleteProducto(id))
            assertEquals(0, productos.deleteProducto(id))
            productos.searchProducto(id).use { assertFalse(it.moveToFirst()) }
        }
    }
    @Test(expected = SQLiteConstraintException::class)
    fun claveForaneaRechazaCategoriaInexistente() {
        Productos(context, nombre).use { it.addNewProducto(999, "Inválido", 2.5, 1) }
    }
    @Test fun loginPersistenteEvitaDuplicadosYNoInterpretaSqlEnNick() {
        Usuario(context, nombre).use { it.registrar("Estudiante", "Guia09#2026") }
        Usuario(context, nombre).use {
            assertTrue(it.autenticar("estudiante", "Guia09#2026"))
            assertFalse(it.autenticar("estudiante", "incorrecta"))
            assertFalse(it.autenticar("' OR 1=1 --", "Guia09#2026"))
            try { it.registrar("ESTUDIANTE", "otraClave"); fail("Nick duplicado aceptado") }
            catch (_: SQLiteConstraintException) { }
        }
        HelperDB(context, nombre).use { helper ->
            helper.readableDatabase.rawQuery("SELECT contrasena FROM Usuario", null).use {
                assertTrue(it.moveToFirst()); assertTrue(it.getString(0).startsWith("pbkdf2_sha1:"))
            }
        }
    }
    @Test fun migrarVersionUnoAgregaUsuarioSinBorrarLosProductos() {
        val archivo = context.getDatabasePath("migracion.sqlite")
        archivo.parentFile!!.mkdirs()
        SQLiteDatabase.openOrCreateDatabase(archivo, null).use { db ->
            db.execSQL(Categoria.CREATE_TABLE_CATEGORIA)
            db.execSQL(Productos.CREATE_TABLE_PRODUCTOS)
            db.execSQL("INSERT INTO categoria(nombre) VALUES ('Abarrotes')")
            db.execSQL("INSERT INTO productos(idcategoria,descripcion,precio,cantidad) VALUES (1,'Arroz',1.5,5)")
            db.version = 1
        }
        HelperDB(context, "migracion.sqlite").use { helper ->
            val db = helper.writableDatabase
            assertEquals(2, db.version)
            db.rawQuery("SELECT descripcion FROM productos", null).use {
                assertTrue(it.moveToFirst()); assertEquals("Arroz", it.getString(0))
            }
            db.rawQuery("SELECT count(*) FROM Usuario", null).use { assertTrue(it.moveToFirst()); assertEquals(0, it.getInt(0)) }
        }
    }
}
