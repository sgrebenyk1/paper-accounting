# To-Do List Application

A complete to-do list application with local storage functionality built with Kotlin and Jetpack Compose.

## Features

✅ **Local Storage** - All todos are saved locally using Room Database
✅ **CRUD Operations** - Create, Read, Update, and Delete todos
✅ **Todo Status** - Mark todos as completed or active
✅ **Multiple Views** - View all, active, or completed todos
✅ **Search & Filter** - Filter todos by status
✅ **Due Dates** - Optional due date for each todo
✅ **Descriptions** - Add descriptions to todos
✅ **Clear Completed** - Quick action to clear all completed todos

## Architecture

### Data Layer
- **TodoEntity.kt** - Data class representing a todo item
- **TodoDao.kt** - Room DAO for database operations
- **TodoDatabase.kt** - Room Database definition
- **TodoRepository.kt** - Repository pattern for data management

### UI Layer
- **TodoViewModel.kt** - ViewModel for managing UI state
- **TodoScreen.kt** - Main Composable with tab navigation
- **TodoItem** - Composable for displaying individual todos
- **AddTodoDialog** - Composable dialog for adding new todos

## Database Schema

```sql
CREATE TABLE todos (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    title TEXT NOT NULL,
    description TEXT,
    isCompleted BOOLEAN DEFAULT 0,
    createdAt LONG NOT NULL,
    dueDate LONG
)
```

## Usage

### Adding a Todo
```kotlin
viewModel.addTodo(
    title = "Buy groceries",
    description = "Milk, eggs, bread",
    dueDate = System.currentTimeMillis() + (24 * 60 * 60 * 1000) // Tomorrow
)
```

### Toggling Todo Completion
```kotlin
viewModel.toggleTodoCompletion(todo)
```

### Deleting a Todo
```kotlin
viewModel.deleteTodo(todo)
```

### Clearing Completed Todos
```kotlin
viewModel.clearCompletedTodos()
```

## Dependencies Required

Add to `build.gradle.kts`:

```kotlin
// Room Database
implementation(libs.androidx.room.ktx)
implementation(libs.androidx.room.runtime)
ksp(libs.androidx.room.compiler)

// Lifecycle
implementation(libs.androidx.lifecycle.runtime.ktx)
implementation(libs.androidx.lifecycle.runtime.compose)
implementation(libs.androidx.lifecycle.viewmodel.compose)

// Compose
implementation(libs.androidx.compose.material3)
implementation(libs.androidx.compose.ui)
implementation(libs.androidx.compose.ui.tooling.preview)
```

## Setup Instructions

1. **Copy files** from this package to your Android project
2. **Update AndroidManifest.xml** to use TodoApplication:
   ```xml
   <application
       android:name=".TodoApplication"
       ...
   />
   ```
3. **Add Room dependency** to your build.gradle
4. **Integrate TodoScreen** into your main Activity or Navigation

## Storage

All data is stored locally in the device's SQLite database:
- Location: `/data/data/com.aistudio.paperstock.printshop/databases/todo_database`
- Persists across app restarts
- No internet connection required

## Future Enhancements

- 📅 Calendar date picker for due dates
- 🏷️ Categories/Tags for todos
- 🔔 Notifications for due dates
- 📊 Statistics and productivity tracking
- 🔄 Sync with cloud storage
- 🎨 Custom themes and colors
- 📱 Widget support

## License

MIT License
