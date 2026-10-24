package com.example.retrofitdogapp

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.retrofitdogapp.databinding.ActivityGithubBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GitHubActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGithubBinding
    private val adapter = RepositoryAdapter(::abrirRepositorio)
    private var solicitud: Call<List<GitHubRepository>>? = null
    private var usuario = ""
    private var pagina = 0
    private var hayMas = false
    private val repositorios = mutableListOf<GitHubRepository>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityGithubBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.ime())
            view.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }
        binding.repositories.layoutManager = LinearLayoutManager(this)
        binding.repositories.adapter = adapter
        binding.searchRepositories.setOnClickListener { buscar() }
        binding.githubUsername.setOnEditorActionListener { _, action, _ ->
            if (action == EditorInfo.IME_ACTION_SEARCH) { buscar(); true } else false
        }
        binding.loadMoreRepositories.setOnClickListener { cargarPagina(pagina + 1) }
        binding.backToDogs.setOnClickListener { finish() }
        val anterior = savedInstanceState?.getString("usuario_buscado").orEmpty()
        if (anterior.isNotEmpty()) {
            usuario = anterior
            cargarPagina(1)
        }
    }
    private fun buscar() {
        val nombre = binding.githubUsername.text.toString().trim()
        if (!Regex("[A-Za-z0-9](?:[A-Za-z0-9-]{0,37}[A-Za-z0-9])?").matches(nombre) || "--" in nombre) {
            binding.githubUsername.error = getString(R.string.github_invalid_username)
            binding.githubUsername.requestFocus()
            return
        }
        binding.githubUsername.error = null
        binding.githubUsername.clearFocus()
        (getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager)
            .hideSoftInputFromWindow(binding.githubUsername.windowToken, 0)
        usuario = nombre
        cargarPagina(1)
    }
    private fun cargarPagina(numero: Int) {
        solicitud?.cancel()
        if (numero == 1) {
            repositorios.clear()
            adapter.submitList(emptyList())
            pagina = 0
            hayMas = false
        }
        binding.githubProgress.visibility = View.VISIBLE
        binding.githubStatus.visibility = View.GONE
        binding.loadMoreRepositories.visibility = if (hayMas) View.VISIBLE else View.GONE
        binding.loadMoreRepositories.isEnabled = false
        val llamada = GitHubClient.instance.getRepositories(usuario, numero)
        solicitud = llamada
        llamada.enqueue(object : Callback<List<GitHubRepository>> {
            override fun onResponse(call: Call<List<GitHubRepository>>, response: Response<List<GitHubRepository>>) {
                if (call !== solicitud || isFinishing || isDestroyed) return
                terminarCarga()
                val datos = response.body()
                if (response.isSuccessful && datos != null) {
                    pagina = numero
                    repositorios.addAll(datos)
                    adapter.submitList(repositorios.distinctBy { it.id })
                    hayMas = response.headers()["Link"]?.contains("rel=\"next\"") == true
                    binding.loadMoreRepositories.visibility = if (hayMas) View.VISIBLE else View.GONE
                    binding.githubStatus.text = if (repositorios.isEmpty())
                        getString(R.string.github_empty, usuario)
                    else getString(R.string.github_results, usuario, repositorios.size)
                    binding.githubStatus.visibility = View.VISIBLE
                } else {
                    val mensaje = when (response.code()) {
                        404 -> getString(R.string.github_not_found, usuario)
                        403, 429 -> if (response.code() == 429 || response.headers()["X-RateLimit-Remaining"] == "0")
                            getString(R.string.github_rate_limit) else getString(R.string.github_forbidden)
                        else -> getString(R.string.github_http_error, response.code())
                    }
                    mostrarError(mensaje)
                }
            }
            override fun onFailure(call: Call<List<GitHubRepository>>, t: Throwable) {
                if (call.isCanceled || call !== solicitud || isFinishing || isDestroyed) return
                terminarCarga()
                mostrarError(getString(R.string.github_connection_error))
            }
        })
    }
    private fun terminarCarga() {
        solicitud = null
        binding.githubProgress.visibility = View.GONE
        binding.loadMoreRepositories.isEnabled = true
    }
    private fun mostrarError(mensaje: String) {
        binding.githubStatus.text = mensaje
        binding.githubStatus.visibility = View.VISIBLE
    }
    private fun abrirRepositorio(repo: GitHubRepository) {
        try {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(repo.htmlUrl)))
        } catch (_: ActivityNotFoundException) {
            Toast.makeText(this, R.string.github_no_browser, Toast.LENGTH_LONG).show()
        }
    }
    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString("usuario_buscado", usuario)
        super.onSaveInstanceState(outState)
    }
    override fun onDestroy() {
        solicitud?.cancel()
        super.onDestroy()
    }
}
