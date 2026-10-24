package com.example.retrofitcrudapp

import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import okio.Buffer
import org.junit.Assert.*
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiContratoTest {
    private val requests = mutableListOf<Request>()
    private var respuesta = """{"id":"42","nombre":"Ana","apellido":"PÃ©rez","edad":"30"}"""
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://example.test/api/")
        .client(OkHttpClient.Builder().addInterceptor(Interceptor { chain ->
            val request = chain.request()
            requests.add(request)
            val body = if (request.method == "GET" && request.url.pathSegments.last() != "42")
                "[$respuesta]" else respuesta
            Response.Builder().request(request).protocol(Protocol.HTTP_1_1).code(200).message("OK")
                .body(body.toResponseBody("application/json".toMediaType())).build()
        }).build())
        .addConverterFactory(GsonConverterFactory.create()).build()

    @Test fun alumnosUsanLosVerbosRutasYDatosCorrectos() {
        val api = retrofit.create(AlumnoApi::class.java)
        val datos = DatosPersona("Ana", "PÃ©rez", 30)
        assertEquals(42, api.obtenerAlumnos().execute().body()!!.single().id)
        assertEquals(30, api.obtenerAlumnoPorId(42).execute().body()!!.edad)
        assertTrue(api.crearAlumno(datos).execute().isSuccessful)
        assertTrue(api.actualizarAlumno(42, datos).execute().isSuccessful)
        assertTrue(api.eliminarAlumno(42).execute().isSuccessful)
        verificarContrato("alumno")
    }

    @Test fun profesoresUsanLosVerbosRutasYDatosCorrectos() {
        val api = retrofit.create(ProfesorApi::class.java)
        val datos = DatosPersona("Ana", "PÃ©rez", 30)
        assertEquals(42, api.obtenerProfesores().execute().body()!!.single().id)
        assertEquals(30, api.obtenerProfesorPorId(42).execute().body()!!.edad)
        assertTrue(api.crearProfesor(datos).execute().isSuccessful)
        assertTrue(api.actualizarProfesor(42, datos).execute().isSuccessful)
        assertTrue(api.eliminarProfesor(42).execute().isSuccessful)
        verificarContrato("profesor")
    }

    @Test fun edadesInvalidasDelServidorNoImpidenCargarProfesores() {
        respuesta = """{"id":"42","nombre":"Ana","apellido":"PÃ©rez","edad":"edad 1"}"""
        val api = retrofit.create(ProfesorApi::class.java)
        assertNull(api.obtenerProfesores().execute().body()!!.single().edad)
        respuesta = """{"id":"42","nombre":"Ana","apellido":"PÃ©rez","edad":30}"""
        assertEquals(30, api.obtenerProfesores().execute().body()!!.single().edad)
    }

    private fun verificarContrato(recurso: String) {
        assertEquals(listOf("GET", "GET", "POST", "PUT", "DELETE"), requests.map { it.method })
        val ruta = "/api/escuela/$recurso"
        assertEquals(listOf(ruta, "$ruta/42", ruta, "$ruta/42", "$ruta/42"), requests.map { it.url.encodedPath })
        for (request in requests.filter { it.method == "POST" || it.method == "PUT" }) {
            val buffer = Buffer()
            request.body!!.writeTo(buffer)
            val json = com.google.gson.Gson().fromJson(buffer.readUtf8(), com.google.gson.JsonObject::class.java)
            assertFalse("El ID se asigna en el servidor", json.has("id"))
            assertEquals("Ana", json["nombre"].asString)
            assertEquals("PÃ©rez", json["apellido"].asString)
            assertEquals(30, json["edad"].asInt)
        }
    }
}
