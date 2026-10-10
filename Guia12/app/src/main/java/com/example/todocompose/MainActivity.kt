package com.example.todocompose
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.todocompose.data.model.network.TodoApiService
import com.example.todocompose.repository.TodoRepository
import com.example.todocompose.ui.theme.TodoComposeTheme
import com.example.todocompose.ui.theme.composables.TodoScreen
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
class MainActivity : ComponentActivity() {
 override fun onCreate(savedInstanceState: Bundle?) {
  super.onCreate(savedInstanceState)
  enableEdgeToEdge()
  val api = Retrofit.Builder()
   .baseUrl("https://6aca87c73115bcc95820eb0a.mockapi.io/academic/v1/")
   .addConverterFactory(GsonConverterFactory.create()).build().create(TodoApiService::class.java)
  val repository = TodoRepository(api)
  val preferences = getSharedPreferences("created_todos", MODE_PRIVATE)
  val factory = object : ViewModelProvider.Factory {
   @Suppress("UNCHECKED_CAST")
   override fun <T : ViewModel> create(modelClass: Class<T>): T = TodoViewModel(repository, preferences) as T
  }
  val viewModel = ViewModelProvider(this, factory)[TodoViewModel::class.java]
  setContent { TodoComposeTheme { TodoScreen(viewModel) } }
 }
}
