package com.example.todo.data

import kotlinx.coroutines.flow.Flow

class TodoRepository(private val todoDao: TodoDao) {
    val allTodos: Flow<List<TodoEntity>> = todoDao.getAllTodos()
    val activeTodos: Flow<List<TodoEntity>> = todoDao.getActiveTodos()
    val completedTodos: Flow<List<TodoEntity>> = todoDao.getCompletedTodos()

    suspend fun insertTodo(title: String, description: String = "", dueDate: Long? = null) {
        val todo = TodoEntity(
            title = title,
            description = description,
            dueDate = dueDate
        )
        todoDao.insertTodo(todo)
    }

    suspend fun updateTodo(todo: TodoEntity) {
        todoDao.updateTodo(todo)
    }

    suspend fun toggleTodoCompletion(todo: TodoEntity) {
        todoDao.updateTodo(todo.copy(isCompleted = !todo.isCompleted))
    }

    suspend fun deleteTodo(todo: TodoEntity) {
        todoDao.deleteTodo(todo)
    }

    suspend fun clearCompletedTodos() {
        todoDao.clearCompletedTodos()
    }

    suspend fun getTodoById(id: Int): TodoEntity? {
        return todoDao.getTodoById(id)
    }
}
