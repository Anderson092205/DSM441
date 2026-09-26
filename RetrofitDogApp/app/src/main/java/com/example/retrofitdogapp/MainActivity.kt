package com.example.retrofitdogapp

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.retrofitdogapp.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Locale

class MainActivity : AppCompatActivity(), SearchView.OnQueryTextListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var dogAdapter: DogAdapter
    private val images: MutableList<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(
                WindowInsetsCompat.Type.systemBars()
            )
            v.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )
            insets
        }

        initRecyclerView()
        binding.searchDogs.setOnQueryTextListener(this)
    }

    private fun initRecyclerView() {
        dogAdapter = DogAdapter(images)
        binding.listDogs.layoutManager = LinearLayoutManager(this)
        binding.listDogs.adapter = dogAdapter
    }

    private fun searchByName(raza: String) {
        val batch: Call<DogsResponse?>? =
            RetrofitClient.instance.getDogsByBreed(raza)

        if (batch == null) {
            showError()
            return
        }

        batch.enqueue(object : Callback<DogsResponse?> {
            override fun onResponse(
                call: Call<DogsResponse?>,
                response: Response<DogsResponse?>
            ) {
                val body = response.body()

                if (response.isSuccessful && body != null) {
                    val responseImages = body.getImages()
                        ?.filterNotNull()
                        .orEmpty()

                    images.clear()
                    images.addAll(responseImages)
                    dogAdapter.notifyDataSetChanged()
                } else {
                    showError()
                }
            }

            override fun onFailure(
                call: Call<DogsResponse?>,
                t: Throwable
            ) {
                showError()
            }
        })
    }

    private fun showError() {
        Toast.makeText(
            this,
            "Ocurrió un error",
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun onQueryTextChange(query: String?): Boolean {
        return true
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        val raza = query?.trim().orEmpty()

        if (raza.isNotEmpty()) {
            searchByName(raza.lowercase(Locale.ROOT))
            binding.searchDogs.clearFocus()
        }

        return true
    }

}
