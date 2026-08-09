package com.example.todo

import android.app.Application
import androidx.room.Room
import com.example.todo.data.TodoDatabase
import com.example.todo.data.TodoRepository

class TodoApplication : Application() {
    private lateinit var database: TodoDatabase
    lateinit var repository: TodoRepository

    override fun onCreate() {
        super.onCreate()

        // Initialize Room Database
        database = Room.databaseBuilder(
            this,
            TodoDatabase::class.java,
            "todo_database"
        ).build()

        // Initialize Repository
        repository = TodoRepository(database.todoDao())
    }
}
