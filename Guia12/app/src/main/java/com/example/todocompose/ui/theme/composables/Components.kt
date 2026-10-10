package com.example.todocompose.ui.theme.composables
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.todocompose.data.model.Todo
import com.example.todocompose.ui.theme.TodoComposeTheme
import java.util.UUID
@Composable
fun TodoList(todos: List<Todo>, ownedIds: Set<String> = emptySet(), enabled: Boolean = true,
 onComplete: (Todo) -> Unit = {}, onEdit: (Todo) -> Unit = {}, onDelete: (Todo) -> Unit = {}) {
 LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(vertical = 8.dp),
  verticalArrangement = Arrangement.spacedBy(8.dp)) {
  items(todos, key = { it.id }) { todo -> TodoItem(todo, todo.id in ownedIds, enabled, onComplete, onEdit, onDelete) }
 }
}
@Composable
fun TodoItem(todo: Todo, editable: Boolean = false, enabled: Boolean = true,
 onComplete: (Todo) -> Unit = {}, onEdit: (Todo) -> Unit = {}, onDelete: (Todo) -> Unit = {}) {
 Card(elevation = CardDefaults.cardElevation(4.dp), modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
  Column(Modifier.padding(16.dp)) {
   Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
    Text("ID: ${todo.id}", style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(1f))
    Text(if (todo.done) "Completada" else "", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
   }
   Spacer(Modifier.height(4.dp))
   Text("Titulo: ${todo.title}", style = MaterialTheme.typography.bodyMedium)
   Spacer(Modifier.height(2.dp))
   Text("Descripcion: ${todo.description}", style = MaterialTheme.typography.bodySmall)
   if (editable) {
    Row {
     Checkbox(todo.done, { onComplete(todo.copy(done = it)) }, enabled = enabled)
     Text("Completada", modifier = Modifier.padding(top = 12.dp))
    }
    Row {
     TextButton(onClick = { onEdit(todo) }, enabled = enabled) { Text("Modificar") }
     TextButton(onClick = { onDelete(todo) }, enabled = enabled) { Text("Eliminar") }
    }
   }
  }
 }
}
@Preview(showBackground = true)
@Composable
fun TodoPreviewScreen() {
 val sampleTodos = listOf(
  Todo(UUID.randomUUID().toString(), "Comprar leche", "Ir al supermercado antes de las 6 PM.", false),
  Todo(UUID.randomUUID().toString(), "Estudiar Kotlin", "Repasar funciones de alto orden y lambdas.", true),
  Todo(UUID.randomUUID().toString(), "Hacer ejercicio", "Salir a correr al parque por 30 minutos.", false)
 )
 TodoComposeTheme { TodoList(sampleTodos) }
}
