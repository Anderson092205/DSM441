package com.example.todocompose.repository
import com.example.todocompose.data.model.Todo
import com.example.todocompose.data.model.network.TodoApiService
class TodoRepository(private val api: TodoApiService) {
 suspend fun fetchTodos(): List<Todo> = api.getTodos()
 suspend fun addTodo(todo: Todo): Todo = api.addTodo(todo)
 suspend fun updateTodo(todo: Todo): Todo = api.updateTodo(todo.id, todo)
 suspend fun deleteTodo(id: String) {
  val response = api.deleteTodo(id)
  if (!response.isSuccessful) throw retrofit2.HttpException(response)
 }
}
