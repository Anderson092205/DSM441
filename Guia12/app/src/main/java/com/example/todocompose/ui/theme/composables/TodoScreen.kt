package com.example.todocompose.ui.theme.composables
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.todocompose.TodoViewModel
import com.example.todocompose.data.model.Todo
@Composable
fun TodoScreen(viewModel: TodoViewModel) {
 val todos by viewModel.todos.collectAsState()
 val loading by viewModel.loading.collectAsState()
 val error by viewModel.error.collectAsState()
 val ownedIds by viewModel.ownedIds.collectAsState()
 var titleInput by rememberSaveable { mutableStateOf("") }
 var descriptionInput by rememberSaveable { mutableStateOf("") }
 var editing by remember { mutableStateOf<Todo?>(null) }
 var deleting by remember { mutableStateOf<Todo?>(null) }
 LaunchedEffect(Unit) { viewModel.loadTodos() }
 Surface(Modifier.fillMaxSize()) {
  Column(Modifier.fillMaxSize().safeDrawingPadding().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
   Text("Mis Tareas", style = MaterialTheme.typography.headlineMedium, modifier = Modifier.padding(vertical = 16.dp))
   TextField(titleInput, { titleInput = it }, label = { Text("Titulo") }, modifier = Modifier.fillMaxWidth(), enabled = !loading)
   Spacer(Modifier.height(8.dp))
   TextField(descriptionInput, { descriptionInput = it }, label = { Text("Descripcion") }, modifier = Modifier.fillMaxWidth(), enabled = !loading)
   Spacer(Modifier.height(8.dp))
   Button(onClick = { viewModel.addTodo(titleInput, descriptionInput) { titleInput = ""; descriptionInput = "" } },
    modifier = Modifier.fillMaxWidth(), enabled = !loading && titleInput.isNotBlank()) { Text("Agregar") }
   Spacer(Modifier.height(16.dp))
   error?.let {
    Text(it, color = MaterialTheme.colorScheme.error)
    TextButton(onClick = { viewModel.loadTodos() }, enabled = !loading) { Text("Reintentar") }
   }
   if (loading) CircularProgressIndicator()
   Box(Modifier.weight(1f)) {
    TodoList(todos, ownedIds, !loading, onComplete = { viewModel.updateTodo(it) }, onEdit = { editing = it }, onDelete = { deleting = it })
   }
  }
 }
 editing?.let { todo ->
  var title by remember(todo.id) { mutableStateOf(todo.title) }
  var description by remember(todo.id) { mutableStateOf(todo.description) }
  AlertDialog(onDismissRequest = { if (!loading) editing = null }, title = { Text("Modificar tarea") },
   text = { Column {
    TextField(title, { title = it }, label = { Text("Titulo") }, enabled = !loading)
    Spacer(Modifier.height(8.dp))
    TextField(description, { description = it }, label = { Text("Descripcion") }, enabled = !loading)
    error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
   } }, confirmButton = {
    TextButton(onClick = { viewModel.updateTodo(todo.copy(title = title, description = description)) { editing = null } },
     enabled = !loading && title.isNotBlank()) { Text("Guardar") }
   }, dismissButton = { TextButton(onClick = { editing = null }, enabled = !loading) { Text("Cancelar") } })
 }
 deleting?.let { todo ->
  AlertDialog(onDismissRequest = { if (!loading) deleting = null }, title = { Text("Eliminar tarea") },
   text = { Column { Text("¿Eliminar ${todo.title}?"); error?.let { Text(it, color = MaterialTheme.colorScheme.error) } } },
   confirmButton = { TextButton(onClick = { viewModel.deleteTodo(todo) { deleting = null } }, enabled = !loading) { Text("Eliminar") } },
   dismissButton = { TextButton(onClick = { deleting = null }, enabled = !loading) { Text("Cancelar") } })
 }
}
