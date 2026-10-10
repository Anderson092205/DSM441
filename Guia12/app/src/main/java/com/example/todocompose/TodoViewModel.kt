package com.example.todocompose
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todocompose.data.model.Todo
import com.example.todocompose.repository.TodoRepository
import java.util.UUID
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
class TodoViewModel(private val repository: TodoRepository, private val preferences: SharedPreferences) : ViewModel() {
 private val _todos = MutableStateFlow<List<Todo>>(emptyList())
 val todos: StateFlow<List<Todo>> = _todos
 private val _loading = MutableStateFlow(false)
 val loading: StateFlow<Boolean> = _loading
 private val _error = MutableStateFlow<String?>(null)
 val error: StateFlow<String?> = _error
 private val _ownedIds = MutableStateFlow(preferences.getStringSet("ids", emptySet())!!.toSet())
 val ownedIds: StateFlow<Set<String>> = _ownedIds
 private fun saveOwned(ids: Set<String>) {
  preferences.edit().putStringSet("ids", ids).apply()
  _ownedIds.value = ids
 }
 private fun request(action: suspend () -> Unit) {
  if (_loading.value) return
  _loading.value = true
  _error.value = null
  viewModelScope.launch {
   try { action() }
   catch (e: CancellationException) { throw e }
   catch (e: Exception) { _error.value = "No se pudo completar la solicitud. Verifica tu conexión e intenta nuevamente." }
   finally { _loading.value = false }
  }
 }
 fun loadTodos() = request { _todos.value = repository.fetchTodos() }
 fun addTodo(title: String, description: String, onSuccess: () -> Unit) {
  if (title.isBlank()) return
  request {
   val result = repository.addTodo(Todo(UUID.randomUUID().toString(), title, description, false))
   saveOwned(_ownedIds.value + result.id)
   _todos.value += result
   onSuccess()
  }
 }
 fun updateTodo(todo: Todo, onSuccess: () -> Unit = {}) {
  if (todo.id !in _ownedIds.value || todo.title.isBlank()) return
  request {
   val result = repository.updateTodo(todo)
   _todos.value = _todos.value.map { if (it.id == result.id) result else it }
   onSuccess()
  }
 }
 fun deleteTodo(todo: Todo, onSuccess: () -> Unit) {
  if (todo.id !in _ownedIds.value) return
  request {
   repository.deleteTodo(todo.id)
   _todos.value = _todos.value.filterNot { it.id == todo.id }
   saveOwned(_ownedIds.value - todo.id)
   onSuccess()
  }
 }
}
