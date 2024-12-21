package com.liuxing.todo.repository

import android.app.Application
import androidx.lifecycle.LiveData
import com.liuxing.todo.data.Todo
import com.liuxing.todo.data.TodoDatabase

/**
 * Author：流星
 * DateTime：2024/12/19 10:41
 * Description：TodoRepository
 */
class TodoRepository(application: Application) {

    private val todoDao = TodoDatabase.getDatabase(application).getTodoDao()

    suspend fun insertTodo(todo: Todo) {
        todoDao.insertTodo(todo)
    }

    suspend fun deleteTodo(todo: Todo) {
        todoDao.deleteTodo(todo)
    }

    suspend fun updateTodo(todo: Todo) {
        todoDao.updateTodo(todo)
    }

    fun queryAllTodo(): LiveData<List<Todo>> {
        return todoDao.queryAllTodo()
    }
}