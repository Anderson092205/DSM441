package com.example.retrofitdogapp

import okhttp3.Interceptor
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody
import org.junit.Assert.*
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class GitHubApiServiceTest {
    private var codigo = 200
    private var cuerpo = """[{"id":1296269,"name":"Hello-World","full_name":"octocat/Hello-World","description":null,"language":null,"stargazers_count":80,"html_url":"https://github.com/octocat/Hello-World"}]"""
    private lateinit var solicitud: Request
    private val api = Retrofit.Builder().baseUrl("https://api.github.com/")
        .client(OkHttpClient.Builder().addInterceptor(Interceptor { chain ->
            solicitud = chain.request()
            Response.Builder().request(solicitud).protocol(Protocol.HTTP_1_1)
                .code(codigo).message("Test")
                .header("Link", "<https://api.github.com/users/octocat/repos?page=3>; rel=\"next\"")
                .body(ResponseBody.create(MediaType.parse("application/json"), cuerpo)).build()
        }).build()).addConverterFactory(GsonConverterFactory.create()).build()
        .create(GitHubApiService::class.java)

    @Test fun consultaUsuarioConPaginacionYConvierteCamposJson() {
        val response = api.getRepositories("octocat", 2).execute()
        assertEquals("GET", solicitud.method())
        assertEquals("/users/octocat/repos", solicitud.url().encodedPath())
        assertEquals("2", solicitud.url().queryParameter("page"))
        assertEquals("100", solicitud.url().queryParameter("per_page"))
        assertEquals("full_name", solicitud.url().queryParameter("sort"))
        assertEquals("application/vnd.github+json", solicitud.header("Accept"))
        assertNotNull(solicitud.header("User-Agent"))
        val repo = response.body()!!.single()
        assertEquals(1296269L, repo.id)
        assertEquals("octocat/Hello-World", repo.fullName)
        assertEquals("https://github.com/octocat/Hello-World", repo.htmlUrl)
        assertEquals(80, repo.stars)
        assertNull(repo.description)
        assertNull(repo.language)
        assertTrue(response.headers()["Link"]!!.contains("rel=\"next\""))
    }

    @Test fun conservaRespuesta404ParaDistinguirUsuarioInexistente() {
        codigo = 404
        cuerpo = """{"message":"Not Found"}"""
        val response = api.getRepositories("usuario-inexistente").execute()
        assertFalse(response.isSuccessful)
        assertEquals(404, response.code())
        assertNull(response.body())
    }

    @Test fun aceptaListadoVacioDeUsuarioSinRepositorios() {
        cuerpo = "[]"
        assertTrue(api.getRepositories("octocat").execute().body()!!.isEmpty())
    }
}
